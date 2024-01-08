package com.mixno35.appmanager.model;

import androidx.annotation.NonNull;

public class AndroidModel {

    String _version, _codename;

    public AndroidModel(@NonNull String _version, @NonNull String _codename) {
        this._version = _version;
        this._codename = _codename;
    }

    public String get_version() {
        return _version;
    }

    public String get_codename() {
        return _codename;
    }
}
