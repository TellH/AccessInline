package com.tellh.transformer.fetcher.task;

import com.tellh.transformer.fetcher.BackupContentFetcher;
import com.tellh.transformer.fetcher.ClassData;
import com.tellh.transformer.fetcher.ContentFetcher;
import com.tellh.transformer.fetcher.ContentFetcherChain;
import com.tellh.transformer.fetcher.Input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class ClassVisitTask implements Callable<Void> {

    private ClassData classData;
    private List<ContentFetcher> fetcherList;

    public ClassVisitTask(ClassData classData, ContentFetcher[] fetchers) {
        this.classData = classData;
        this.fetcherList = new ArrayList<>(fetchers.length + 1);
        this.fetcherList.addAll(Arrays.asList(fetchers));
        this.fetcherList.add(new BackupContentFetcher());
    }

    @Override
    public Void call() throws Exception {
        Input input = new Input(null, classData.getRelativePath(), classData.getClassBytes());
        ContentFetcherChain chain = new ContentFetcherChain(fetcherList, input, 0);
        chain.proceed(input);
        return null;
    }
}
