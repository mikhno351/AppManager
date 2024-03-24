package com.mixno35.app_manager.data;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.color.DynamicColors;
import com.mixno35.app_manager.BuildConfig;

import java.util.Locale;

public class MyApplication extends Application {

    Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    @Override
    @SuppressLint("UnspecifiedImmutableFlag")
    public void onCreate() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getString("keyLanguage", "0").equals("0")) AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(Locale.forLanguageTag(prefs.getString("keyLanguage", "en"))));

        this.uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            Data.writeToLog(getApplicationContext(), new TryMe().getStackTrace(ex));

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(2);

            uncaughtExceptionHandler.uncaughtException(thread, ex);
        });
        super.onCreate();

        DynamicColors.applyToActivitiesIfAvailable(this);

        if (new AppData().isAppInstalledFromPlayStore(getApplicationContext(), getPackageName()) && !BuildConfig.DEBUG) System.exit(0);
    }
}
