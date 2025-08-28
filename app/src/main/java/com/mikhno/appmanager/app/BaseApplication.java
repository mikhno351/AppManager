package com.mikhno.appmanager.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.color.DynamicColors;

import java.util.Locale;

public class BaseApplication extends Application {

    @SuppressLint("HardwareIds")
    @Override
    public void onCreate() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String appLanguage = prefs.getString("keyLanguage", "en");

        if (!prefs.getString("keyLanguage", "0").equals("0")) {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.create(Locale.forLanguageTag(appLanguage))
            );
        }

        super.onCreate();

        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
