package com.tellh.transformer;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class TransformContext {
    private TransformInvocation invocation;
    private Collection<JarInput> allJars;
    private Collection<DirectoryInput> allDirs;

    public TransformContext(TransformInvocation invocation) {
        this.invocation = invocation;
        init();
    }

    private void init() {
        allJars = new ArrayList<>(invocation.getInputs().size());
        allDirs = new ArrayList<>(invocation.getInputs().size());
        invocation.getInputs().forEach(input -> {
            allJars.addAll(input.getJarInputs());
            allDirs.addAll(input.getDirectoryInputs());
        });
    }

    public Collection<DirectoryInput> getAllDirs() {
        return Collections.unmodifiableCollection(allDirs);
    }

    public Collection<JarInput> getAllJars() {
        return Collections.unmodifiableCollection(allJars);
    }

    public File getOutputFile(QualifiedContent content) throws IOException {
        File target = invocation.getOutputProvider().getContentLocation(content.getName(), content.getContentTypes(), content.getScopes(),
                content instanceof JarInput ? Format.JAR : Format.DIRECTORY);
        Files.createParentDirs(target);
        return target;
    }

    public static File getOutputTarget(File root, String relativePath) throws IOException {
        File target = new File(root, relativePath.replace('/', File.separatorChar));
        Files.createParentDirs(target);
        return target;
    }

    public File getOutputFile(String affinity, String relativePath, Set<QualifiedContent.ContentType> contentTypes) throws IOException {
        File root = invocation.getOutputProvider().getContentLocation(affinity, contentTypes, TransformManager.SCOPE_FULL_PROJECT, Format.DIRECTORY);
        File target = new File(root, relativePath.replace('/', File.separatorChar));
        Files.createParentDirs(target);
        return target;
    }
}
