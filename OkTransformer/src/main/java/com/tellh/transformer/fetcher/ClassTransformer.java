package com.tellh.transformer.fetcher;

/**
 * Created by tlh on 2018/8/22.
 */

public interface ClassTransformer {
    ClassData transform(byte[] raw, String relativePath);
}
