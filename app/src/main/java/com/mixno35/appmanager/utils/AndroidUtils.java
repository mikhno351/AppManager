package com.mixno35.appmanager.utils;

import androidx.annotation.NonNull;

import com.mixno35.appmanager.data.Data;

import java.util.Objects;

public class AndroidUtils {

    /**
     * @param sdk Version Android SDK
     * @param format Format Value | %a - Text "Android", %c - Internal Code Name, %s - SDK, %v - Android Version | "%a %v %c (%s)"
     */
    public static String getName(int sdk, @NonNull String format) {
        String version = Objects.requireNonNull(Data.ANDROID_VERSIONS.get(sdk).get_version());
        String codename = Objects.requireNonNull(Data.ANDROID_VERSIONS.get(sdk).get_codename());

        version = version.trim().isEmpty() ? "0.0" : version;
        codename = codename.trim().isEmpty() ? "-" : codename;

        String name = format
                .replaceAll("%a", "Android")
                .replaceAll("%v", version)
                .replaceAll("%c", codename)
                .replaceAll("%s", String.valueOf(sdk));

        return name.trim();
    }
}
