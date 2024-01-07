package com.mixno35.appmanager.model;

import android.view.View;

import androidx.annotation.NonNull;

public class DetailInfoModel {

    String _title, _value, _action_title;
    int _icon;
    View.OnClickListener _click;

    public DetailInfoModel(@NonNull String _title, @NonNull String _value, int _icon, View.OnClickListener _click, String _action_title) {
        this._title = _title;
        this._value = _value;
        this._icon = _icon;
        this._click = _click;
        this._action_title = _action_title;
    }

    public String get_title() {
        return _title;
    }

    public String get_value() {
        return _value;
    }

    public String get_action_title() {
        return _action_title;
    }

    public int get_icon() {
        return _icon;
    }

    public View.OnClickListener get_click() {
        return _click;
    }
}
