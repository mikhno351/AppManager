package com.mixno35.appmanager.data;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.mixno35.appmanager.BuildConfig;
import com.mixno35.appmanager.R;
import com.mixno35.appmanager.model.AndroidModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    static String GOOGLE_PLAY_APP_LINK = "https://play.google.com/store/apps/details?id=%20";

    public static ArrayList<AndroidModel> ANDROID_VERSIONS = new ArrayList<>();

    @SuppressLint("DefaultLocale")
    public static String formatMillisToDHMS(Context context, long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        StringBuilder result = new StringBuilder();
        if (days > 0) result.append(days).append(context.getString(R.string.text_time_day)).append(". ");
        if (hours > 0) result.append(hours).append(context.getString(R.string.text_time_hour)).append(". ");
        if (minutes > 0) result.append(minutes).append(context.getString(R.string.text_time_minute)).append(". ");
        if (seconds > 0) result.append(seconds).append(context.getString(R.string.text_time_second)).append(". ");

        return result.toString().trim();
    }

    public static boolean hasUsageStatsPermission(@NonNull Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public static String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public static void requestUsageStatsPermission(@NonNull ActivityResultLauncher<Intent> usageStatsLauncher) {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        usageStatsLauncher.launch(intent);
    }

    @SuppressLint("DefaultLocale")
    public static String convertBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";

        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char unit = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), unit);
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

    public static void openInGooglePlay(@NonNull Context context, @NonNull String packageName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(GOOGLE_PLAY_APP_LINK.replace("%20", packageName))));
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
