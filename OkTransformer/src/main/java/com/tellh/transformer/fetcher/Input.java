package com.tellh.transformer.fetcher;

import com.android.build.api.transform.QualifiedContent;

/**
 * Created by tlh on 2018/8/21.
 */

public class Input {
    public QualifiedContent content;
    public String filePath;
    public byte[] bytes;

    public Input(QualifiedContent content, String filePath, byte[] bytes) {
        this.content = content;
        this.filePath = filePath;
        this.bytes = bytes;
    }
}
