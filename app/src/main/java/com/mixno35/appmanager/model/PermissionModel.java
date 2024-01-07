package com.mixno35.appmanager.model;

import androidx.annotation.NonNull;

import java.util.Comparator;

public class PermissionModel implements Comparable<PermissionModel> {

    String _title, _value;
    boolean _granded;

    public PermissionModel(@NonNull String _title, @NonNull String _value, boolean _granded) {
        this._title = _title;
        this._value = _value;
        this._granded = _granded;
    }

    public String get_title() {
        return _title;
    }

    public String get_value() {
        return _value;
    }

    public boolean isGranded() {
        return _granded;
    }

    @Override
    public int compareTo(PermissionModel another) {
        return this._value.compareTo(another._value);
    }

    public static final Comparator<PermissionModel> TitleComparator = Comparator.comparing(PermissionModel::get_title);
}
