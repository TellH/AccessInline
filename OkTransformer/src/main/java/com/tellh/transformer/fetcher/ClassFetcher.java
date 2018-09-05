package com.tellh.transformer.fetcher;

import java.io.IOException;

/**
 * Created by tlh on 2018/8/22.
 */

public final class ClassFetcher implements ContentFetcher {
    private ClassTransformer transformer;

    private ClassFetcher(ClassTransformer transformer) {
        this.transformer = transformer;
    }

    public static ClassFetcher newInstance(ClassTransformer transformer) {
        return new ClassFetcher(transformer);
    }

    @Override
    public Output process(Chain chain) throws IOException {
        Input input = chain.input();
        if (input.filePath.endsWith(".class")) {
            byte[] data = input.bytes;
            ClassData classData = transformer.transform(data, input.filePath);
            input.bytes = classData.getClassBytes();
            input.filePath = classData.getRelativePath();
        }
        return chain.proceed(input);
    }
}
