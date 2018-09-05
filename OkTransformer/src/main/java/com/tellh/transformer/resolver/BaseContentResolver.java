package com.tellh.transformer.resolver;

import com.android.build.api.transform.QualifiedContent;
import com.tellh.transformer.TransformContext;
import com.tellh.transformer.fetcher.BackupContentFetcher;
import com.tellh.transformer.fetcher.ContentFetcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tlh on 2018/8/21.
 */

public abstract class BaseContentResolver implements ContentResolver {
    protected TransformContext context;

    BaseContentResolver(TransformContext context) {
        this.context = context;
    }

    @Override
    public final void handle(QualifiedContent content, List<ContentFetcher> fetchers) throws IOException {
        List<ContentFetcher> realFetcherList = new ArrayList<>(fetchers.size() + 1);
        realFetcherList.addAll(fetchers);
        realFetcherList.add(new BackupContentFetcher());
        resolve(content, Collections.unmodifiableList(realFetcherList));
    }

    protected abstract void resolve(QualifiedContent content, List<ContentFetcher> fetchers) throws IOException;

}
