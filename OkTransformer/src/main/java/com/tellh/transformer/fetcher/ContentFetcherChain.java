package com.tellh.transformer.fetcher;

import java.io.IOException;
import java.util.List;

/**
 * Created by tlh on 2018/8/21.
 */

public class ContentFetcherChain implements ContentFetcher.Chain {
    private List<ContentFetcher> fetchers;
    private Input input;
    private int index;

    public ContentFetcherChain(List<ContentFetcher> fetchers, Input input, int index) {
        this.fetchers = fetchers;
        this.input = input;
        this.index = index;
    }

    @Override
    public Input input() {
        return input;
    }

    @Override
    public Output proceed(Input input) throws IOException {
        if (index >= fetchers.size()) throw new AssertionError();
        ContentFetcher next = fetchers.get(index);
        return next.process(new ContentFetcherChain(fetchers, input, index + 1));
    }
}
