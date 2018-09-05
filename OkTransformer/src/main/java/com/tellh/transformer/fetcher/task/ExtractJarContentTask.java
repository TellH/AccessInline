package com.tellh.transformer.fetcher.task;

import com.google.common.io.ByteStreams;
import com.tellh.transformer.fetcher.BackupContentFetcher;
import com.tellh.transformer.fetcher.ClassData;
import com.tellh.transformer.fetcher.ContentFetcher;
import com.tellh.transformer.fetcher.ContentFetcherChain;
import com.tellh.transformer.fetcher.Input;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExtractJarContentTask implements Callable<List<ClassData>> {
    private final List<ClassData> dataList;
    private final List<ContentFetcher> fetchers;
    private File jar;

    public ExtractJarContentTask(File jar) {
        this(jar, null);
    }

    public ExtractJarContentTask(File jar, List<ContentFetcher> fetchers) {
        this.jar = jar;
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
    public List<ClassData> call() throws IOException {
        ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(jar)));
        ZipEntry zipEntry;
        try {
            while ((zipEntry = zin.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    continue;
                }
                if (dataList != null) {
                    dataList.add(new ClassData(ByteStreams.toByteArray(zin), zipEntry.getName()));
                } else {
                    Input input = new Input(null, zipEntry.getName(), ByteStreams.toByteArray(zin));
                    ContentFetcherChain chain = new ContentFetcherChain(fetchers, input, 0);
                    chain.proceed(input);
                }
            }
        } finally {
            zin.close();
        }
        return dataList;
    }
}