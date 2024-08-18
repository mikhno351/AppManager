package com.mixno35.app_manager.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mixno35.app_manager.BuildConfig;
import com.mixno35.app_manager.R;
import com.mixno35.app_manager.dialog.AppDetailDialog;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    Preference keyAboutApp, keyPrivacy, keyTerms;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        keyAboutApp = findPreference("keyAboutApp");
        keyPrivacy = findPreference("keyPrivacy");
        keyTerms = findPreference("keyTerms");

        if (keyAboutApp != null) {
            keyAboutApp.setSummary(String.format("%1s\nv.%2s (%3s)", BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        if (keyPrivacy != null && preference.getKey().equals(keyPrivacy.getKey())) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://doc-hosting.flycricket.io/app-manager-privacy-policy/054a6c1b-521f-4e73-979d-ac6dc56172df/privacy")));
            return true;
        } if (keyTerms != null && preference.getKey().equals(keyTerms.getKey())) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://doc-hosting.flycricket.io/app-manager-terms-of-use/86d949cf-4ada-4d98-8484-a00a85cb2a0a/terms")));
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        assert key != null;

        if (key.equals("keyLanguage")) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
            builder.setTitle(getString(R.string.text_prefs_changed));
            builder.setMessage(getString(R.string.message_prefs_changed));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.action_restart), (dialog, which) -> System.exit(0));
            builder.setNegativeButton(getString(R.string.action_cancel), (dialog, which) -> dialog.dismiss());
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
