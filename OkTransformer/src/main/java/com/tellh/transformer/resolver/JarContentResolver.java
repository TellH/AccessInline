package com.tellh.transformer.resolver;

import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.google.common.io.ByteStreams;
import com.tellh.transformer.TransformContext;
import com.tellh.transformer.fetcher.ContentFetcher;
import com.tellh.transformer.fetcher.ContentFetcherChain;
import com.tellh.transformer.fetcher.Input;
import com.tellh.transformer.fetcher.Output;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by tlh on 2018/8/21.
 */

public class JarContentResolver extends BaseContentResolver {

    public JarContentResolver(TransformContext context) {
        super(context);
    }

    @Override
    public boolean accepted(QualifiedContent content) {
        return content instanceof JarInput;
    }

    @Override
    public void resolve(QualifiedContent content, List<ContentFetcher> fetchers) throws IOException {
        File root = content.getFile();
        File outputFile = context.getOutputFile(content);
        ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(root)));
        JarOutputStream jos = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
        ZipEntry zipEntry;
        try {
            while ((zipEntry = zin.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    continue;
                }
                String relativePath = zipEntry.getName();
                byte[] raw = ByteStreams.toByteArray(zin);
                Input input = new Input(content, relativePath, raw);
                ContentFetcherChain chain = new ContentFetcherChain(fetchers, input, 0);
                Output output = chain.proceed(input);
                if (output != null && output.bytes != null && output.bytes.length > 0) {
                    ZipEntry entry = new ZipEntry(relativePath);
                    jos.putNextEntry(entry);
                    jos.write(output.bytes);
                }
            }
        } finally {
            zin.close();
            jos.close();
        }
    }

}
