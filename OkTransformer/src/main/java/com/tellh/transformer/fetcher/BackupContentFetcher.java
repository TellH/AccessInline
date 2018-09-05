package com.tellh.transformer.fetcher;

import java.io.IOException;

/**
 * Created by tlh on 2018/8/21.
 */

public class BackupContentFetcher implements ContentFetcher {
    @Override
    public Output process(Chain chain) throws IOException {
        Input input = chain.input();
        return new Output(input.bytes);
    }
}
