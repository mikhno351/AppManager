package com.mixno35.appmanager.data;

import android.annotation.SuppressLint;
import android.app.Application;

import com.google.android.material.color.DynamicColors;
import com.mixno35.appmanager.BuildConfig;

public class MyApplication extends Application {

    Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    @Override
    @SuppressLint("UnspecifiedImmutableFlag")
    public void onCreate() {
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
