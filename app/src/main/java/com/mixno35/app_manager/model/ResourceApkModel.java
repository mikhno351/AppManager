package com.mixno35.app_manager.model;

import androidx.annotation.NonNull;

public class ResourceApkModel {

    String _path, _name;
    long _size;

    public ResourceApkModel(@NonNull String _path, @NonNull String _name, long _size) {
        this._path = _path;
        this._name = _name;
        this._size = _size;
    }

    public String get_path() {
        return _path;
    }

    public String get_name() {
        return _name;
    }

    public long get_size() {
        return _size;
    }
}
