package com.mixno35.appmanager.fragment.details;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.mixno35.appmanager.ManifestActivity;
import com.mixno35.appmanager.R;
import com.mixno35.appmanager.ResourcesActivity;
import com.mixno35.appmanager.adapter.DetailInfoAdapter;
import com.mixno35.appmanager.adapter.MenuAdapter;
import com.mixno35.appmanager.data.AppData;
import com.mixno35.appmanager.data.Data;
import com.mixno35.appmanager.decoration.GridSpacingItemDecoration;
import com.mixno35.appmanager.dialog.PermissionsDialog;
import com.mixno35.appmanager.model.DetailInfoModel;
import com.mixno35.appmanager.model.MenuModel;

import java.util.ArrayList;
import java.util.Objects;

public class BaseFragment extends Fragment {

    String packageName = "";

    RecyclerView recyclerView, recyclerViewMenu;
    LinearLayoutCompat containerUsageStatsSize, containerSize;
    MaterialButton buttonProvide;
    MaterialTextView textSizeApp, textSizeData, textSizeCache;

    SharedPreferences prefs;
    PackageManager packageManager;

    DetailInfoAdapter adapter;
    MenuAdapter adapter_menu;

    ArrayList<DetailInfoModel> list = new ArrayList<>();
    ArrayList<MenuModel> list_menu = new ArrayList<>();

    ActivityResultLauncher<Intent> usageStatsLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);

        packageManager = requireActivity().getPackageManager();
        prefs = requireActivity().getSharedPreferences(Data.PREFS_NAME(requireActivity()), Context.MODE_PRIVATE);

        try {
            packageName = requireActivity().getIntent().getStringExtra("packageName");
        } catch (Exception e) {
            e.printStackTrace();
            packageName = requireActivity().getPackageName();
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerViewMenu = view.findViewById(R.id.recyclerViewMenu);
        containerUsageStatsSize = view.findViewById(R.id.containerUsageStatsSize);
        containerSize = view.findViewById(R.id.containerSize);
        buttonProvide = view.findViewById(R.id.buttonProvide);
        textSizeApp = view.findViewById(R.id.textSizeApp);
        textSizeData = view.findViewById(R.id.textSizeData);
        textSizeCache = view.findViewById(R.id.textSizeCache);

        if (recyclerView != null) recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        if (recyclerViewMenu != null) {
            recyclerViewMenu.setLayoutManager(new GridLayoutManager(requireActivity(), 4));
            recyclerViewMenu.addItemDecoration(new GridSpacingItemDecoration(60, 4));
        }

        adapter = new DetailInfoAdapter(list, requireActivity());
        adapter_menu = new MenuAdapter(list_menu, requireActivity());

        buttonProvide.setOnClickListener(v -> Data.requestUsageStatsPermission(usageStatsLauncher));

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(Objects.requireNonNull(packageName), 0);

            list.clear();
            list_menu.clear();

            list_menu.add(new MenuModel(getString(R.string.action_permissions_app), R.drawable.baseline_security_24, v -> new PermissionsDialog(requireActivity(), packageManager, packageName)));
            list_menu.add(new MenuModel(getString(R.string.action_resources_app), R.drawable.outline_extension_24, v -> startActivity(new Intent(requireActivity(), ResourcesActivity.class).putExtra("packageName", packageName))));
            list_menu.add(new MenuModel(getString(R.string.action_manifest_app), R.drawable.baseline_android_24, v -> startActivity(new Intent(requireActivity(), ManifestActivity.class).putExtra("packageName", packageName))));

            String app_name = Objects.requireNonNull(packageInfo.applicationInfo.loadLabel(packageManager).toString());
            String app_package = Objects.requireNonNull(packageInfo.applicationInfo.packageName);

            list.add(new DetailInfoModel(getString(R.string.text_app_name), app_name, R.drawable.baseline_content_copy_24, v -> Data.copyToClipboard(requireActivity(), app_name), requireActivity().getString(R.string.text_copy_to_clipboard)));
            list.add(new DetailInfoModel(getString(R.string.text_app_package), app_package, R.drawable.baseline_content_copy_24, v -> Data.copyToClipboard(requireActivity(), app_package), requireActivity().getString(R.string.text_copy_to_clipboard)));
            list.add(new DetailInfoModel(getString(R.string.text_app_version), Objects.requireNonNull(packageInfo.versionName), 0, null, null));
            list.add(new DetailInfoModel(getString(R.string.text_app_version_code), Objects.requireNonNull(String.valueOf(packageInfo.versionCode)), 0, null, null));
            list.add(new DetailInfoModel(getString(R.string.text_app_install_time), Objects.requireNonNull(Data.convertTimestampToDateTime(requireActivity(), packageInfo.firstInstallTime)), 0, null, null));
            list.add(new DetailInfoModel(getString(R.string.text_app_update_time), Objects.requireNonNull(Data.convertTimestampToDateTime(requireActivity(), packageInfo.lastUpdateTime)), 0, null, null));
            list.add(new DetailInfoModel(getString(R.string.text_app_provenance), Objects.requireNonNull(new AppData().getProvenance(requireActivity(), packageManager, packageName)), 0, null, null));

            recyclerView.setAdapter(adapter);
            recyclerViewMenu.setAdapter(adapter_menu);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, 0, insets.right, insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        usageStatsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Data.hasUsageStatsPermission(requireActivity());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        containerUsageStatsSize.post(() -> containerUsageStatsSize.setVisibility(Data.hasUsageStatsPermission(requireActivity()) ? View.GONE : View.VISIBLE));
        containerSize.post(() -> containerSize.setVisibility(Data.hasUsageStatsPermission(requireActivity()) ? View.VISIBLE : View.GONE));

        if (Data.hasUsageStatsPermission(requireActivity())) {
            ArrayList<Long> appSize = new AppData().getAppSize(requireActivity(), packageName);

            if (textSizeApp != null) textSizeApp.post(() -> textSizeApp.setText(Data.convertBytes(appSize.get(2))));
            if (textSizeData != null) textSizeData.post(() -> textSizeData.setText(Data.convertBytes(appSize.get(1))));
            if (textSizeCache != null) textSizeCache.post(() -> textSizeCache.setText(Data.convertBytes(appSize.get(0))));
        }
    }
}