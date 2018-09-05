package com.tellh.inline.plugin.fetcher;

import com.tellh.transformer.fetcher.ClassData;
import com.tellh.transformer.fetcher.ClassTransformer;

import org.objectweb.asm.ClassReader;

import java.util.concurrent.atomic.AtomicInteger;

public class AndroidJarProcessor implements ClassTransformer {
    private final Context context;
    private AtomicInteger count = new AtomicInteger(0);

    public AndroidJarProcessor(Context context) {
        this.context = context;
    }

    @Override
    public ClassData transform(byte[] raw, String relativePath) {
        ClassReader cr = new ClassReader(raw);
        PreProcessClassVisitor cv = new PreProcessClassVisitor(context, true);
        cr.accept(cv, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES | ClassReader.SKIP_CODE);
        count.getAndIncrement();
        context.addEntity(cv.getEntity());
        return new ClassData(raw, relativePath);
    }

    public AtomicInteger getCount() {
        return count;
    }
}
