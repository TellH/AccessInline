package com.tellh.transformer;

import com.android.build.api.transform.QualifiedContent;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.tellh.transformer.fetcher.BackupContentFetcher;
import com.tellh.transformer.fetcher.ClassData;
import com.tellh.transformer.fetcher.ContentFetcher;
import com.tellh.transformer.fetcher.ContentFetcherChain;
import com.tellh.transformer.fetcher.Input;
import com.tellh.transformer.resolver.DirContentResolver;
import com.tellh.transformer.resolver.JarContentResolver;
import com.tellh.transformer.resolver.QualifiedContentResolverImpl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
        forEach(false, fetchers);
    }

    public void traverseOnly(ContentFetcher... fetchers) throws IOException, InterruptedException {
        forEach(true, fetchers);
    }

    public void traverseAndroidJar(File jar, ContentFetcher... fetchers) {
        try {
            List<ContentFetcher> realFetcherList = new ArrayList<>(fetchers.length + 1);
            realFetcherList.addAll(Arrays.asList(fetchers));
            realFetcherList.add(new BackupContentFetcher());
            List<Future<Void>> tasks = new ResolveJarContentTask(jar).call()
                    .stream()
                    .map(classData -> (Callable<Void>) () -> {
                        Input input = new Input(null, classData.getRelativePath(), classData.getClassBytes());
                        ContentFetcherChain chain = new ContentFetcherChain(realFetcherList, input, 0);
                        chain.proceed(input);
                        return null;
                    })
                    .map(t -> service.submit(t))
                    .collect(Collectors.toList());

            // block until all task has finish.
            for (Future<Void> future : tasks) {
                try {
                    future.get();
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void forEach(boolean traverseOnly, ContentFetcher... fetchers) throws IOException, InterruptedException {
        List<Future<Void>> tasks = Stream.concat(context.getAllJars().stream(), context.getAllDirs().stream())
                .map(q -> new QualifiedContentTask(q, fetchers, traverseOnly))
                .map(t -> service.submit(t))
                .collect(Collectors.toList());

        // block until all task has finish.
        for (Future<Void> future : tasks) {
            try {
                future.get();
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


    private class QualifiedContentTask implements Callable<Void> {

        private QualifiedContent content;
        private List<ContentFetcher> fetchers;
        private boolean traverseOnly;

        QualifiedContentTask(QualifiedContent content, ContentFetcher[] fetcher, boolean traverseOnly) {
            this.content = content;
            this.fetchers = Arrays.asList(fetcher);
            this.traverseOnly = traverseOnly;
        }

        QualifiedContentTask(QualifiedContent content, ContentFetcher[] fetcher) {
            this(content, fetcher, false);
        }

        @Override
        public Void call() throws Exception {
            if (resolver.accepted(content)) {
                if (traverseOnly) {
                    resolver.traverseOnly(content, Collections.unmodifiableList(fetchers));
                } else {
                    resolver.handle(content, Collections.unmodifiableList(fetchers));
                }
            }
            return null;
        }
    }

    private static class ResolveJarContentTask implements Callable<List<ClassData>> {
        private final List<ClassData> zipEntryList;
        private File jar;

        public ResolveJarContentTask(File jar) {
            this.jar = jar;
            this.zipEntryList = new ArrayList<>();
        }

        @Override
        public List<ClassData> call() throws Exception {
            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(jar)));
            ZipEntry zipEntry;
            try {
                while ((zipEntry = zin.getNextEntry()) != null) {
                    if (zipEntry.isDirectory()) {
                        continue;
                    }
                    zipEntryList.add(new ClassData(ByteStreams.toByteArray(zin), zipEntry.getName()));
                }
            } finally {
                zin.close();
            }
            return zipEntryList;
        }
    }

}
