package com.mikhno.appmanager.model;

import androidx.annotation.NonNull;

import java.util.Comparator;

public class ActivityModel implements Comparable<ActivityModel> {
    
    String _title, _value;
    Boolean _exported, _enabled;
    
    public ActivityModel(@NonNull String _title, @NonNull String _value, @NonNull Boolean _exported, @NonNull Boolean _enabled) {
        this._title = _title;
        this._value = _value;
        this._exported = _exported;
        this._enabled = _enabled;
    }

    public String get_title() {
        return _title;
    }

    public String get_value() {
        return _value;
    }

    public Boolean isExported() {
        return _exported;
    }

    public Boolean isEnabled() {
        return _enabled;
    }

    @Override
    public int compareTo(ActivityModel another) {
        return this._value.compareTo(another._value);
    }

    public static final Comparator<ActivityModel> TitleComparator = Comparator.comparing(ActivityModel::get_title);
}
