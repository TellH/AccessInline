package com.tellh.transformer.fetcher;

import java.io.IOException;

public interface ContentFetcher {

    Output process(Chain chain) throws IOException;

    interface Chain {
        Input input();

        Output proceed(Input input) throws IOException;
    }
}