package com.tellh.inline.plugin;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;

public class AccessInlinePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        AppExtension android = project.getExtensions().getByType(AppExtension.class);
        android.registerTransform(new InlineAccessTransform(new GlobalContext(project, getSdkJarDir(android))));
    }

    private String getSdkJarDir(AppExtension android) {
        return String.join(File.separator, android.getSdkDirectory().getAbsolutePath(), "platforms", "android-27");
    }
}
