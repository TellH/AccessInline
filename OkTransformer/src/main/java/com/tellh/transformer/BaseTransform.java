package com.tellh.transformer;

import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.tellh.transformer.fetcher.ContentFetcher;

import java.io.IOException;

/**
 * Created by tlh on 2018/8/22.
 */

public abstract class BaseTransform extends Transform {
    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        TransformContext context = new TransformContext(transformInvocation);
        Transformer transformer = new Transformer(context);
        transformer.resolve(getContentFetchers());
    }

    protected abstract ContentFetcher[] getContentFetchers();
}
