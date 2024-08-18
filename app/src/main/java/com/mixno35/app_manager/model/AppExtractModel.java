package com.mixno35.app_manager.model;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class AppExtractModel {

    File file;
    String name, pkg;
    Drawable icon;
    long size;

    public AppExtractModel(@NotNull File file, @NonNull String name, @NonNull String pkg, @NonNull Drawable icon, long size) {
        this.file = file;
        this.name = name;
        this.pkg = pkg;
        this.icon = icon;
        this.size = size;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public String getPkg() {
        return pkg;
    }

    public Drawable getIcon() {
        return icon;
    }

    public long getSize() {
        return size;
    }
}
