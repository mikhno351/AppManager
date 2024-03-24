package com.mixno35.app_manager.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.mixno35.app_manager.R;

import java.util.Objects;

public class PermissionUtils {

    public static String extractPermissionName(@NonNull String permission) {
        String[] parts = permission.split("\\.");

        return (parts.length > 0) ? parts[parts.length - 1] : "";
    }

    public static String getName(@NonNull PackageManager packageManager, @NonNull String permission) {
        String name = "";
        String extract = extractPermissionName(permission).replace("_", " ");

        try {
            name = packageManager.getPermissionInfo(permission, PackageManager.GET_META_DATA).loadLabel(packageManager).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (name.equalsIgnoreCase(permission)) return extract;

        return name.trim().isEmpty() ? extract : name;
    }

    public static String getDescription(@NonNull Context context, @NonNull PackageManager packageManager, @NonNull String permission) {
        String description = "";
        String default_str = context.getString(R.string.message_no_detail_info_permission, permission);

        try {
            description = Objects.requireNonNull(packageManager.getPermissionInfo(permission, PackageManager.GET_META_DATA).loadDescription(packageManager)).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return description.trim().isEmpty() ? default_str : description;
    }

    public static int getIcon(@NonNull String permission) {
        if      (permission.equalsIgnoreCase("com.android.vending.BILLING")) return R.drawable.outline_paid_24;
        else if (permission.equalsIgnoreCase("android.permission.INTERNET")) return R.drawable.baseline_network_cell_24;
        else if (permission.equalsIgnoreCase("android.permission.WAKE_LOCK")) return R.drawable.outline_lock_24;
        else if (permission.equalsIgnoreCase("android.permission.CAMERA")) return R.drawable.outline_camera_alt_24;
        else if (permission.equalsIgnoreCase("android.permission.VIBRATE")) return R.drawable.baseline_vibration_24;
        else if (permission.equalsIgnoreCase("android.permission.USE_FINGERPRINT")) return R.drawable.baseline_fingerprint_24;
        else if (permission.equalsIgnoreCase("android.permission.QUERY_ALL_PACKAGE")) return R.drawable.baseline_android_24;
        else if (permission.equalsIgnoreCase("android.permission.READ_MEDIA_AUDIO")) return R.drawable.baseline_music_note_24;
        else if (permission.equalsIgnoreCase("android.permission.READ_MEDIA_IMAGES")) return R.drawable.outline_image_24;
        else if (permission.equalsIgnoreCase("android.permission.READ_MEDIA_VIDEO")) return R.drawable.outline_subscriptions_24;
        else if (permission.equalsIgnoreCase("android.permission.RECORD_AUDIO")) return R.drawable.outline_mic_24;
        else if (permission.equalsIgnoreCase("android.permission.USE_BIOMETRIC")) return R.drawable.baseline_accessibility_24;
        else if (permission.equalsIgnoreCase("android.permission.NFC")) return R.drawable.baseline_nfc_24;
        else if (permission.equalsIgnoreCase("android.permission.GET_PACKAGE_SIZE")) return R.drawable.outline_sd_storage_24;
        else if (permission.contains("NOTIFICATION")) return R.drawable.outline_notifications_active_24;
        else if (permission.contains("ACCOUNTS")) return R.drawable.outline_person_24;
        else if (permission.contains("LOCATION")) return R.drawable.outline_place_24;
        else if (permission.contains("EXTERNAL_STORAGE")) return R.drawable.outline_folder_24;
        else if (permission.contains("BLUETOOTH")) return R.drawable.baseline_bluetooth_24;
        else if (permission.contains("PACKAGES")) return R.drawable.baseline_android_24;
        else if (permission.contains("CONTACTS")) return R.drawable.outline_contacts_24;
        else if (permission.contains("WIFI")) return R.drawable.baseline_network_wifi_3_bar_24;
        else if (permission.contains("NETWORK")) return R.drawable.baseline_network_wifi_3_bar_24;
        else if (permission.equalsIgnoreCase("com.google.android.c2dm.permission.RECEIVE")) return R.drawable.outline_cloud_download_24;
        else if (permission.equalsIgnoreCase("com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE")) return R.drawable.baseline_shop_24;
        else if (permission.equalsIgnoreCase("com.android.launcher.permission.INSTALL_SHORTCUT")) return R.drawable.baseline_open_in_new_24;
        else return R.drawable.baseline_security_24;
    }
}
