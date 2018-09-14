package com.tellh.transformer;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.utils.FileUtils;
import com.google.common.io.Files;
import com.tellh.transformer.fetcher.ClassData;
import com.tellh.transformer.fetcher.ContentFetcher;
import com.tellh.transformer.fetcher.task.ClassVisitTask;
import com.tellh.transformer.fetcher.task.ExtractDirContentTask;
import com.tellh.transformer.fetcher.task.ExtractJarContentTask;
import com.tellh.transformer.fetcher.task.QualifiedContentTask;
import com.tellh.transformer.resolver.DirContentResolver;
import com.tellh.transformer.resolver.JarContentResolver;
import com.tellh.transformer.resolver.QualifiedContentResolverImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by tlh on 2018/8/21.
 */

public class Transformer {
    private ExecutorService service = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), (r, executor) -> {
    });

    private TransformContext context;

    private QualifiedContentResolverImpl resolver;

    public Transformer(TransformContext context) {
        this.context = context;
        this.resolver = QualifiedContentResolverImpl.newInstance(new DirContentResolver(context), new JarContentResolver(context));
    }

    public void resolve(ContentFetcher... fetchers) throws IOException, InterruptedException {
        List<Future<Void>> tasks = Stream.concat(context.getAllJars().stream(), context.getAllDirs().stream())
                .map(q -> new QualifiedContentTask(q, fetchers, resolver))
                .map(t -> service.submit(t))
                .collect(Collectors.toList());

        // block until all task has finish.
        try {
            for (Future<Void> future : tasks) {
                future.get();
            }
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof InterruptedException) {
                throw (InterruptedException) cause;
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    public void skip() throws InterruptedException, IOException {
        List<Future<Void>> tasks = Stream.concat(context.getAllJars().stream(), context.getAllDirs().stream())
                .map(q -> (Callable<Void>) () -> {
                    File dest = context.getOutputFile(q);
                    if (q instanceof DirectoryInput) {
                        FileUtils.copyDirectory(q.getFile(), dest);
                    } else {
                        FileUtils.copyFile(q.getFile(), dest);
                    }
                    return null;
                })
                .map(t -> service.submit(t))
                .collect(Collectors.toList());
        try {
            for (Future<Void> future : tasks) {
                future.get();
            }
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof InterruptedException) {
                throw (InterruptedException) cause;
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    public void traverseOnly(ContentFetcher... fetchers) throws IOException, InterruptedException {
        traverseV1(fetchers);
    }

    private void traverseV1(ContentFetcher[] fetchers) throws InterruptedException, IOException {
        List<Future<List<ClassData>>> tasks = Stream.concat(context.getAllJars().stream(), context.getAllDirs().stream())
                .map(q -> q instanceof JarInput ? new ExtractJarContentTask(q.getFile(), Arrays.asList(fetchers)) :
                        new ExtractDirContentTask(q.getFile(), Arrays.asList(fetchers)))
                .map(t -> service.submit(t))
                .collect(Collectors.toList());

        // block until all task has finish.
        try {
            for (Future<List<ClassData>> future : tasks) {
                future.get();
            }
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof InterruptedException) {
                throw (InterruptedException) cause;
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    private void traverseV2(ContentFetcher... fetchers) throws IOException, InterruptedException {
        List<Future<List<ClassData>>> tasks = Stream.concat(context.getAllJars().stream(), context.getAllDirs().stream())
                .map(q -> q instanceof JarInput ? new ExtractJarContentTask(q.getFile()) : new ExtractDirContentTask(q.getFile()))
                .map(t -> service.submit(t))
                .collect(Collectors.toList());

        // block until all task has finish.
        try {
            List<ClassData> collectDataList = new ArrayList<>();
            for (Future<List<ClassData>> future : tasks) {
                collectDataList.addAll(future.get());
            }

            List<Future<Void>> visitTasks = collectDataList.stream()
                    .map(data -> new ClassVisitTask(data, fetchers))
                    .map(t -> service.submit(t))
                    .collect(Collectors.toList());
            for (Future<Void> future : visitTasks) {
                future.get();
            }
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof InterruptedException) {
                throw (InterruptedException) cause;
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    public void traverseAndroidJar(File jar, ContentFetcher... fetchers) throws IOException, InterruptedException {
        new ExtractJarContentTask(jar, Arrays.asList(fetchers)).call();
    }

    public void addFile(byte[] data, String relativePath, Set<QualifiedContent.ContentType> contentTypes) throws IOException {
        addFile(data, relativePath, relativePath, contentTypes);
    }

    /**
     * put files with the same affinity together
     *
     * @param data
     * @param affinity
     * @param relativePath
     * @param contentTypes
     * @throws IOException
     */
    public void addFile(byte[] data, String affinity, String relativePath, Set<QualifiedContent.ContentType> contentTypes) throws IOException {
        File file = context.getOutputFile(affinity, relativePath, contentTypes);
        Files.write(data, file);
    }
}
