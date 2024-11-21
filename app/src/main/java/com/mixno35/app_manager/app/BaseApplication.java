package com.mixno35.app_manager.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.color.DynamicColors;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mixno35.app_manager.BuildConfig;
import com.mixno35.app_manager.data.AppData;

import java.util.Locale;

public class BaseApplication extends Application {

    Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

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

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        FirebaseCrashlytics.getInstance().setUserId(Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        FirebaseCrashlytics.getInstance().setCustomKey("Device", Build.MODEL);
        FirebaseCrashlytics.getInstance().setCustomKey("Manufacturer", Build.MANUFACTURER);
        FirebaseCrashlytics.getInstance().setCustomKey("Android", Build.VERSION.RELEASE);
        FirebaseCrashlytics.getInstance().setCustomKey("Language", Locale.getDefault().getLanguage());
        FirebaseCrashlytics.getInstance().setCustomKey("App_Version", BuildConfig.VERSION_NAME);
        FirebaseCrashlytics.getInstance().setCustomKey("App_Build", BuildConfig.VERSION_CODE);
        FirebaseCrashlytics.getInstance().setCustomKey("App_Package", BuildConfig.APPLICATION_ID);
        FirebaseCrashlytics.getInstance().setCustomKey("App_Language", appLanguage);

        DynamicColors.applyToActivitiesIfAvailable(this);

        if (new AppData().isAppInstalledFromPlayStore(getApplicationContext(), getPackageName()) && !BuildConfig.DEBUG && !BuildConfig.IS_RUSTORE) {
            System.exit(0);
        }
    }
}
