package com.tellh.inline.plugin;

import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.tellh.inline.plugin.fetcher.AndroidJarProcessor;
import com.tellh.inline.plugin.fetcher.CollectClassInfoProcessor;
import com.tellh.inline.plugin.fetcher.Context;
import com.tellh.inline.plugin.fetcher.ShrinkAccessProcessor;
import com.tellh.inline.plugin.log.Log;
import com.tellh.inline.plugin.log.Timer;
import com.tellh.transformer.TransformContext;
import com.tellh.transformer.Transformer;
import com.tellh.transformer.fetcher.ClassFetcher;

import java.io.IOException;
import java.util.Set;

/**
 * Created by tlh on 2018/8/29.
 */

public class InlineAccessTransform extends Transform {
    private GlobalContext globalContext;

    public InlineAccessTransform(GlobalContext context) {
        this.globalContext = context;
    }

    @Override
    public String getName() {
        return InlineAccessTransform.class.getSimpleName();
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        globalContext.init();
        Timer timer = new Timer();
        Transformer transformer = new Transformer(new TransformContext(transformInvocation));
        Context context = new Context();

        timer.startRecord("PRE_PROCESS");
        if (globalContext.enable()) {
            timer.startRecord("PROJECT_CLASS");
            transformer.traverseOnly(ClassFetcher.newInstance(new CollectClassInfoProcessor(context)));
            timer.stopRecord("PROJECT_CLASS", "Process project all .class files cost time = [%s ms]");
        }

        if (globalContext.enable()) {
            timer.startRecord("ANDROID");
            AndroidJarProcessor androidJarProcessor = new AndroidJarProcessor(context);
            transformer.traverseAndroidJar(globalContext.androidJar(), ClassFetcher.newInstance(androidJarProcessor));
            Log.i(String.format("Collect android class count = [%s]", androidJarProcessor.getCount()));
            timer.stopRecord("ANDROID", "Process android jar cost time = [%s ms]");
        }

        timer.stopRecord("PRE_PROCESS", "Collect info cost time = [%s ms]");
        Log.i("access$ method count : " + context.methodCount());

        if (globalContext.enable()) {
            timer.startRecord("PROCESS");
            transformer.resolve(ClassFetcher.newInstance(new ShrinkAccessProcessor(context)));
            timer.stopRecord("PROCESS", "Shrink access$ cost time = [%s ms]");
        } else {
            transformer.skip();
        }

        timer.record("Inline access$ transform total cost time = [%s ms]");
    }
}
