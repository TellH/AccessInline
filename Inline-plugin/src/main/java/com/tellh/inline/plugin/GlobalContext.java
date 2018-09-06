package com.tellh.inline.plugin;

import com.tellh.inline.plugin.log.Impl.FileLoggerImpl;
import com.tellh.inline.plugin.log.Log;

import org.gradle.api.Project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GlobalContext {
    private Project project;
    private String androidTargetPlatformDir;

    public GlobalContext(Project project, String sdkDir) {
        this.project = project;
        this.androidTargetPlatformDir = sdkDir;
    }

    public File buildDir() {
        return new File(project.getBuildDir(), "access$inline");
    }

    public File androidJar() throws FileNotFoundException {
        File jar = new File(androidTargetPlatformDir, "android.jar");
        if (!jar.exists()) {
            throw new FileNotFoundException("Android jar not found!");
        }
        return jar;
    }


    public void init() {
        try {
            Log.setLevel(Log.Level.DEBUG);
            Log.setImpl(FileLoggerImpl.of(String.join(File.separator, buildDir().getAbsolutePath(), "transform_log.txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
