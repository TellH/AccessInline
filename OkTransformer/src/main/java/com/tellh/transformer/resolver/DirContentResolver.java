package com.tellh.transformer.resolver;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.QualifiedContent;
import com.google.common.io.Files;
import com.tellh.transformer.TransformContext;
import com.tellh.transformer.fetcher.ContentFetcher;
import com.tellh.transformer.fetcher.ContentFetcherChain;
import com.tellh.transformer.fetcher.Input;
import com.tellh.transformer.fetcher.Output;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Created by tlh on 2018/8/21.
 */

public class DirContentResolver extends BaseContentResolver {

    public DirContentResolver(TransformContext context) {
        super(context);
    }

    @Override
    public boolean accepted(QualifiedContent content) {
        return content instanceof DirectoryInput;
    }

    @Override
    public void resolve(QualifiedContent content, List<ContentFetcher> fetchers) throws IOException {
        File root = content.getFile();
        URI base = root.toURI();
        File outputFile = context.getOutputFile(content);

        for (File file : Files.fileTreeTraverser().preOrderTraversal(root)) {
            if (file.isFile() && !file.getName().equalsIgnoreCase(".DS_Store")) {
                String relativePath = base.relativize(file.toURI()).toString();
                byte[] raw = Files.toByteArray(file);
                handleRawFile(content, fetchers, raw, relativePath, outputFile);
            }
        }
    }

    protected void handleRawFile(QualifiedContent content, List<ContentFetcher> fetchers, byte[] raw, String relativePath, File outputFile) throws IOException {
        Input input = new Input(content, relativePath, raw);
        ContentFetcherChain chain = new ContentFetcherChain(fetchers, input, 0);
        Output output = chain.proceed(input);
        if (output != null && output.bytes != null && output.bytes.length > 0) {
            File target = TransformContext.getOutputTarget(outputFile, relativePath);
            Files.write(output.bytes, target);
        }
    }
}
