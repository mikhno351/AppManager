package com.mikhno.appmanager.model;

import android.view.View;

import androidx.annotation.NonNull;

public class MenuModel {

    String _title;
    int _icon;
    View.OnClickListener _click;

    public MenuModel(@NonNull String _title, int _icon, View.OnClickListener _click) {
        this._title = _title;
        this._icon = _icon;
        this._click = _click;
    }

    public String get_title() {
        return _title;
    }

    public int get_icon() {
        return _icon;
    }

    public View.OnClickListener get_click() {
        return _click;
    }
}
