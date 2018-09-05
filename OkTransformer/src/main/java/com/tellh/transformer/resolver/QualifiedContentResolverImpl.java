package com.tellh.transformer.resolver;

import com.android.build.api.transform.QualifiedContent;
import com.tellh.transformer.fetcher.ContentFetcher;

import java.io.IOException;
import java.util.List;

/**
 * Created by tlh on 2018/8/21.
 */

public class QualifiedContentResolverImpl implements ContentResolver {
    private ContentResolver[] resolvers;

    private QualifiedContentResolverImpl(ContentResolver[] resolvers) {
        this.resolvers = resolvers;
    }

    public static QualifiedContentResolverImpl newInstance(ContentResolver... resolvers) {
        return new QualifiedContentResolverImpl(resolvers);
    }

    @Override
    public boolean accepted(QualifiedContent content) {
        return resolvers != null && resolvers.length > 0;
    }

    @Override
    public void handle(QualifiedContent content, List<ContentFetcher> fetchers) throws IOException {
        for (ContentResolver resolver : resolvers) {
            if (resolver.accepted(content)) {
                resolver.handle(content, fetchers);
            }
        }
    }

    @Override
    public void traverseOnly(QualifiedContent content, List<ContentFetcher> fetchers) throws IOException {
        for (ContentResolver resolver : resolvers) {
            if (resolver.accepted(content)) {
                resolver.traverseOnly(content, fetchers);
            }
        }
    }
}
