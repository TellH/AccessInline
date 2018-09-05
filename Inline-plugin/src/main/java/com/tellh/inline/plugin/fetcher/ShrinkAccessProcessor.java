package com.tellh.inline.plugin.fetcher;


import com.tellh.transformer.fetcher.ClassData;
import com.tellh.transformer.fetcher.ClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class ShrinkAccessProcessor implements ClassTransformer {

    private final Context context;

    public ShrinkAccessProcessor(Context context) {
        this.context = context;
        context.graph();
    }

    @Override
    public ClassData transform(byte[] raw, String relativePath) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassReader cr = new ClassReader(raw);
        cr.accept(new ShrinkAccessClassVisitor(cw, context), ClassReader.EXPAND_FRAMES);
        byte[] data = cw.toByteArray();
        return new ClassData(data, relativePath);
    }
}
