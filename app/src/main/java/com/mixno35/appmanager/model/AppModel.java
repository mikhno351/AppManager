package com.mixno35.appmanager.model;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import java.util.Comparator;

public class AppModel implements Comparable<AppModel> {

    String _package, _name;
    Drawable _icon;

    public AppModel(@NonNull String _package, @NonNull String _name, @NonNull Drawable _icon) {
        this._package = _package;
        this._name = _name;
        this._icon = _icon;
    }

    public String get_package() {
        return _package;
    }

    public String get_name() {
        return _name;
    }

    public Drawable get_icon() {
        return _icon;
    }

    @Override
    public int compareTo(AppModel another) {
        return this._name.compareTo(another._name);
    }

    public static final Comparator<AppModel> NameComparator = Comparator.comparing(AppModel::get_name);
}
