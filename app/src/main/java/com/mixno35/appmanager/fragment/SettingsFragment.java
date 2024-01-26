package com.mixno35.appmanager.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.mixno35.appmanager.BuildConfig;
import com.mixno35.appmanager.R;
import com.mixno35.appmanager.dialog.AppDetailDialog;

public class SettingsFragment extends PreferenceFragmentCompat {

    Preference keyAboutApp, keyPrivacy, keyTerms;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        keyAboutApp = findPreference("keyAboutApp");
        keyPrivacy = findPreference("keyPrivacy");
        keyTerms = findPreference("keyTerms");

        if (keyAboutApp != null) keyAboutApp.setSummary(String.format("%1s\nv.%2s (%3s)", BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        if (keyPrivacy != null && preference.getKey().equals(keyPrivacy.getKey())) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://doc-hosting.flycricket.io/app-manager-privacy-policy/054a6c1b-521f-4e73-979d-ac6dc56172df/privacy")));
            return true;
        } if (keyTerms != null && preference.getKey().equals(keyTerms.getKey())) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://doc-hosting.flycricket.io/app-manager-terms-of-use/86d949cf-4ada-4d98-8484-a00a85cb2a0a/terms")));
            return true;
        } if (keyAboutApp != null && preference.getKey().equals(keyAboutApp.getKey())) {
            new AppDetailDialog(requireActivity(), requireActivity().getPackageName());
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }
}
