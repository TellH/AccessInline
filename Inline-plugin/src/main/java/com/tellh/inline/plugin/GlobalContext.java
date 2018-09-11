package com.tellh.inline.plugin;

import com.android.build.gradle.AppExtension;
import com.tellh.inline.plugin.log.Impl.FileLoggerImpl;
import com.tellh.inline.plugin.log.Log;

import org.gradle.api.Project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GlobalContext {
    private Project project;
    private final AccessInlineExtension accessInlineExtension;
    private final AppExtension android;

    public GlobalContext(Project project, AccessInlineExtension accessInlineExtension, AppExtension android) {
        this.project = project;
        this.android = android;
        this.accessInlineExtension = accessInlineExtension;
    }

    private String getSdkJarDir() {
        String compileSdkVersion = android.getCompileSdkVersion();
        return String.join(File.separator, android.getSdkDirectory().getAbsolutePath(), "platforms", compileSdkVersion);
    }

    public File buildDir() {
        return new File(project.getBuildDir(), "access$inline");
    }

    public File androidJar() throws FileNotFoundException {
        File jar = new File(getSdkJarDir(), "android.jar");
        if (!jar.exists()) {
            throw new FileNotFoundException("Android jar not found!");
        }
        return jar;
    }


    public void init() {
        try {
            Log.setLevel(accessInlineExtension.getLogLevel());
            Log.setImpl(FileLoggerImpl.of(String.join(File.separator, buildDir().getAbsolutePath(), "transform_log.txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
