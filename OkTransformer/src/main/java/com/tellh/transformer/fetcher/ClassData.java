package com.tellh.transformer.fetcher;

/**
 * Created by tlh on 2018/8/22.
 */

public class ClassData {
    byte[] classBytes;
    String relativePath;

    public ClassData(byte[] classBytes, String relativePath) {
        this.classBytes = classBytes;
        this.relativePath = relativePath;
    }

    public byte[] getClassBytes() {
        return classBytes;
    }

    public void setClassBytes(byte[] classBytes) {
        this.classBytes = classBytes;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }
}
