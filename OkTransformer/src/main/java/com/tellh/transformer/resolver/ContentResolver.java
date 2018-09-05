package com.tellh.transformer.resolver;

import com.android.build.api.transform.QualifiedContent;
import com.tellh.transformer.fetcher.ContentFetcher;

import java.io.IOException;
import java.util.List;

/**
 * Created by tlh on 2018/8/21.
 */

public interface ContentResolver {
    boolean accepted(QualifiedContent content);

    void handle(QualifiedContent content, List<ContentFetcher> fetchers) throws IOException;

    void traverseOnly(QualifiedContent content, List<ContentFetcher> fetchers) throws IOException;
}
