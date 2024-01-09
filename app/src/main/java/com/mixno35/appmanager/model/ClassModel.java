package com.mixno35.appmanager.model;

import androidx.annotation.NonNull;

import java.util.Comparator;

public class ClassModel implements Comparable<ClassModel> {
    
    String _title, _value;
    
    public ClassModel(@NonNull String _title, @NonNull String _value) {
        this._title = _title;
        this._value = _value;
    }

    public String get_title() {
        return _title;
    }

    public String get_value() {
        return _value;
    }

    @Override
    public int compareTo(ClassModel another) {
        return this._value.compareTo(another._value);
    }

    public static final Comparator<ClassModel> TitleComparator = Comparator.comparing(ClassModel::get_title);
}
