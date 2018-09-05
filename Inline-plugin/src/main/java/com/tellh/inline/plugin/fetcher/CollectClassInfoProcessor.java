package com.tellh.inline.plugin.fetcher;


import com.tellh.transformer.fetcher.ClassData;
import com.tellh.transformer.fetcher.ClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

/**
 * Created by tlh on 2018/8/29.
 */

public class CollectClassInfoProcessor implements ClassTransformer {
    private final Context context;

    public CollectClassInfoProcessor(Context context) {
        this.context = context;
    }

    @Override
    public ClassData transform(byte[] raw, String relativePath) {
        ClassReader cr = new ClassReader(raw);
        PreProcessClassVisitor cv = new PreProcessClassVisitor(context);
        cr.accept(cv, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        context.addEntity(cv.getEntity());
        return new ClassData(raw, relativePath);
    }
}
