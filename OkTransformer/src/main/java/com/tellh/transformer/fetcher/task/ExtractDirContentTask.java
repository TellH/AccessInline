package com.tellh.transformer.fetcher.task;

import com.google.common.io.Files;
import com.tellh.transformer.fetcher.BackupContentFetcher;
import com.tellh.transformer.fetcher.ClassData;
import com.tellh.transformer.fetcher.ContentFetcher;
import com.tellh.transformer.fetcher.ContentFetcherChain;
import com.tellh.transformer.fetcher.Input;

import org.apache.commons.io.input.NullInputStream;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class ExtractDirContentTask implements Callable<List<ClassData>> {
    private final List<ClassData> dataList;
    private final List<ContentFetcher> fetchers;
    private File file;

    public ExtractDirContentTask(File file) {
        this(file, null);
    }

    public ExtractDirContentTask(File file, List<ContentFetcher> fetchers) {
        this.file = file;
        if (fetchers == null) {
            dataList = new ArrayList<>();
            this.fetchers = null;
        } else {
            List<ContentFetcher> realFetcherList = new ArrayList<>(fetchers.size() + 1);
            realFetcherList.addAll(fetchers);
            realFetcherList.add(new BackupContentFetcher());
            this.fetchers = Collections.unmodifiableList(realFetcherList);
            dataList = null;
        }
    }

    @Override
    public List<ClassData> call() throws Exception {
        URI base = file.toURI();
        for (File file : Files.fileTreeTraverser().preOrderTraversal(file)) {
            if (file.isFile() && !file.getName().equalsIgnoreCase(".DS_Store")) {
                String relativePath = base.relativize(file.toURI()).toString();
                byte[] raw = Files.toByteArray(file);
                if (dataList != null) {
                    dataList.add(new ClassData(raw, relativePath));
                } else {
                    Input input = new Input(null, relativePath, raw);
                    ContentFetcherChain chain = new ContentFetcherChain(fetchers, input, 0);
                    chain.proceed(input);
                }
            }
        }
        return dataList;
    }
}
