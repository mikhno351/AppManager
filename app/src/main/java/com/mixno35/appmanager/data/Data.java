package com.mixno35.appmanager.data;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.text.format.DateFormat;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.mixno35.appmanager.BuildConfig;
import com.mixno35.appmanager.R;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Data {

    public static String PREFS_NAME(Context context) {
            return context.getPackageName() + "_preferences";
    }
    public static String PREFS_KEY_TAP_TARGET_MAIN = "tap_target_main";
    public static String PREFS_KEY_TAP_TARGET_DETAILS = "tap_target_details";
    public static String PREFS_KEY_LIST_FILTER = "list_filter";
    public static String PREFS_KEY_APP_USE_PERMISSION = "app_use_perms_hide";

    @SuppressLint("DefaultLocale")
    public static String formatMillisToDHMS(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        StringBuilder result = new StringBuilder();
        if (days > 0) result.append(days).append("д. ");
        if (hours > 0) result.append(hours).append("ч. ");
        if (minutes > 0) result.append(minutes).append("м. ");
        if (seconds > 0) result.append(seconds).append("с.");

        return result.toString();
    }

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) return input;

        char firstChar = Character.toUpperCase(input.charAt(0));
        String restOfString = input.substring(1);

        return firstChar + restOfString;
    }

    public static void copyToClipboard(@NonNull Context context, @NonNull String text) {
        ClipData clipData = ClipData.newPlainText(context.getPackageName() + ".clipboard", text);

        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(clipData);

        Toast.makeText(context, context.getString(R.string.text_copied_to_clipboard), Toast.LENGTH_SHORT).show();
    }

    public static void shareFile(@NonNull Context context, @NonNull File file) {
        Uri apkUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", file);

        context.grantUriPermission(context.getPackageName(), apkUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.setType("application/vnd.android.package-archive");
        shareIntent.putExtra(Intent.EXTRA_STREAM, apkUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.text_share_apk)));
    }

    public static String convertTimestampToDateTime(@NonNull Context context, long timestamp) {
        try {
            Date date = new Date(timestamp);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM yyyy HH:mm:ss", context.getResources().getConfiguration().getLocales().get(0));

            return simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void writeToLog(@NonNull Context context, @NonNull String message) {
        String message_result = "\n";
        message_result += DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date());
        message_result += "\nAndroid: " + Build.VERSION.RELEASE;
        message_result += "\nSDK: " + Build.VERSION.SDK_INT;
        message_result += "\nBASE-OS: " + Build.VERSION.BASE_OS;
        message_result += "\nCODENAME: " + Build.VERSION.CODENAME;
        message_result += "\nAPP-VERSION: " + BuildConfig.VERSION_NAME;
        message_result += "\nAPP-VERSION-CODE: " + BuildConfig.VERSION_CODE;
        message_result += "\nAPP-PACKAGE: " + BuildConfig.APPLICATION_ID;
        message_result += "\nAPP-BUILD: " + BuildConfig.BUILD_TYPE;
        message_result += "\n> ";
        message_result += message;
        message_result += "\n\n";

        File logFile = new File(context.getExternalFilesDir(null), "log.txt");
        FileWriter writer;
        try {
            if (!logFile.exists()) {
                if (logFile.createNewFile()) {
                    writer = new FileWriter(logFile, true);
                    writer.append(message_result);
                    writer.close();
                }
            } if (logFile.exists()) {
                writer = new FileWriter(logFile, true);
                writer.append(message_result);
                writer.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isDarkThemeEnabled(@NonNull Context context) {
        int nightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightMode == Configuration.UI_MODE_NIGHT_YES;
    }
}
