package com.mixno35.appmanager.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.mixno35.appmanager.R;

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
        if (permission.contains("com.android.vending.BILLING")) return R.drawable.outline_paid_24;
        else if (permission.contains("android.permission.ACCESS_WIFI_STATE")) return R.drawable.baseline_network_wifi_3_bar_24;
        else if (permission.contains("android.permission.INTERNET")) return R.drawable.baseline_network_cell_24;
        else if (permission.contains("android.permission.WAKE_LOCK")) return R.drawable.outline_lock_24;
        else if (permission.contains("android.permission.CHANGE_WIFI_STATE")) return R.drawable.baseline_network_wifi_3_bar_24;
        else if (permission.contains("android.permission.ACCESS_NETWORK_STATE")) return R.drawable.baseline_network_wifi_3_bar_24;
        else if (permission.contains("android.permission.CAMERA")) return R.drawable.outline_camera_alt_24;
        else if (permission.contains("com.google.android.c2dm.permission.RECEIVE")) return R.drawable.outline_cloud_download_24;
        else return R.drawable.baseline_security_24;
    }
}
