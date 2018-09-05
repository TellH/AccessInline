package com.tellh.transformer.fetcher.task;

import com.android.build.api.transform.QualifiedContent;
import com.tellh.transformer.fetcher.ContentFetcher;
import com.tellh.transformer.resolver.ContentResolver;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class QualifiedContentTask implements Callable<Void> {

    private QualifiedContent content;
    private List<ContentFetcher> fetchers;
    private ContentResolver resolver;

    public QualifiedContentTask(QualifiedContent content, ContentFetcher[] fetcher, ContentResolver resolver) {
        this.content = content;
        this.fetchers = Arrays.asList(fetcher);
        this.resolver = resolver;
    }

    @Override
    public Void call() throws Exception {
        if (resolver.accepted(content)) {
            resolver.handle(content, Collections.unmodifiableList(fetchers));
        }
        return null;
    }
}