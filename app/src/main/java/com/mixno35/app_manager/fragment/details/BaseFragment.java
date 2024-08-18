package com.mixno35.app_manager.fragment.details;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.mixno35.app_manager.R;
import com.mixno35.app_manager.adapter.DetailInfoAdapter;
import com.mixno35.app_manager.adapter.MenuHorizontalAdapter;
import com.mixno35.app_manager.data.AppData;
import com.mixno35.app_manager.data.Data;
import com.mixno35.app_manager.decoration.GridSpacingItemDecoration;
import com.mixno35.app_manager.dialog.ActivitiesDialog;
import com.mixno35.app_manager.dialog.PermissionsDialog;
import com.mixno35.app_manager.dialog.ResourcesDialog;
import com.mixno35.app_manager.model.DetailInfoModel;
import com.mixno35.app_manager.model.MenuModel;
import com.mixno35.app_manager.utils.AndroidUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseFragment extends Fragment {

    String packageName = "";

    RecyclerView recyclerView, recyclerViewMenu;
    LinearLayoutCompat containerUsageStatsSize;
    ConstraintLayout containerSize;
    MaterialButton buttonProvide;
    MaterialTextView textSizeApp, textSizeData, textSizeCache;
    NestedScrollView nestedScrollMain;

    SharedPreferences prefs;
    PackageManager packageManager;

    DetailInfoAdapter adapter;
    MenuHorizontalAdapter adapter_menu;

    ArrayList<DetailInfoModel> list = new ArrayList<>();
    ArrayList<MenuModel> list_menu = new ArrayList<>();

    ActivityResultLauncher<Intent> usageStatsLauncher;

    ExecutorService executorSingleSizeApp;
    ExecutorService executorSingleInfoApp;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);

        packageManager = requireActivity().getPackageManager();
        prefs = requireActivity().getSharedPreferences(Data.PREFS_NAME(requireActivity()), Context.MODE_PRIVATE);

        try {
            packageName = requireActivity().getIntent().getStringExtra("packageName");
        } catch (Exception ignore) {
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
        nestedScrollMain = view.findViewById(R.id.nestedScrollMain);

        if (recyclerView != null) recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        if (recyclerViewMenu != null) {
            recyclerViewMenu.setLayoutManager(new GridLayoutManager(requireActivity(), 4));
            recyclerViewMenu.addItemDecoration(new GridSpacingItemDecoration(60, 4));
        }

        adapter = new DetailInfoAdapter(list, requireActivity());
        adapter_menu = new MenuHorizontalAdapter(list_menu, requireActivity());
        buttonProvide.setOnClickListener(v -> Data.requestUsageStatsPermission(usageStatsLauncher));

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(Objects.requireNonNull(packageName), 0);

            list_menu.clear();
            list.clear();

            if (recyclerViewMenu != null) {
                list_menu.add(new MenuModel(getString(R.string.action_permissions_app), R.drawable.baseline_security_24, v -> new PermissionsDialog(requireActivity(), packageManager, packageName)));
                list_menu.add(new MenuModel(getString(R.string.action_resources_app), R.drawable.outline_extension_24, v -> new ResourcesDialog(requireActivity(), packageManager, packageName)));
                list_menu.add(new MenuModel(getString(R.string.action_activities_app), R.drawable.outline_account_tree_24, v -> new ActivitiesDialog(requireActivity(), packageManager, packageName)));

                recyclerViewMenu.setAdapter(adapter_menu);
            }

            if (recyclerView != null) {
                executorSingleInfoApp = Executors.newSingleThreadExecutor();
                executorSingleInfoApp.submit(() -> {
                    String app_name = Objects.requireNonNull(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                    String app_package = Objects.requireNonNull(packageInfo.applicationInfo.packageName);
                    String app_developer = new AppData().getAppDeveloper(requireContext(), app_package);

                    int appMinSDK = packageInfo.applicationInfo.minSdkVersion;
                    int appTargetSDK = packageInfo.applicationInfo.targetSdkVersion;

                    File file_executed_apk = new File(requireActivity().getExternalFilesDir(null), app_package + ".apk");

                    try {
                        list.add(new DetailInfoModel(getString(R.string.text_app_name), app_name, R.drawable.baseline_content_copy_24, v -> Data.copyToClipboard(requireActivity(), app_name), requireActivity().getString(R.string.text_copy_to_clipboard)));
                    } catch (Exception ignored) {} try {
                        list.add(new DetailInfoModel(getString(R.string.text_app_package), app_package, R.drawable.baseline_content_copy_24, v -> Data.copyToClipboard(requireActivity(), app_package), requireActivity().getString(R.string.text_copy_to_clipboard)));
                    } catch (Exception ignored) {} try {
                        list.add(new DetailInfoModel(getString(R.string.text_app_version), Objects.requireNonNull(packageInfo.versionName), 0, null, null));
                    } catch (Exception ignored) {} try {
                        list.add(new DetailInfoModel(getString(R.string.text_app_version_code), Objects.requireNonNull(String.valueOf(packageInfo.versionCode)), 0, null, null));
                    } catch (Exception ignored) {} try {
                        list.add(new DetailInfoModel(getString(R.string.text_app_install_time), Objects.requireNonNull(Data.convertTimestampToDateTime(requireActivity(), packageInfo.firstInstallTime)), 0, null, null));
                    } catch (Exception ignored) {} try {
                        list.add(new DetailInfoModel(getString(R.string.text_app_update_time), Objects.requireNonNull(Data.convertTimestampToDateTime(requireActivity(), packageInfo.lastUpdateTime)), 0, null, null));
                    } catch (Exception ignored) {} try {
                        list.add(new DetailInfoModel(getString(R.string.text_app_provenance), Objects.requireNonNull(new AppData().getProvenance(requireActivity(), packageManager, packageName)), 0, null, null));
                    } catch (Exception ignored) {} try {
                        list.add(new DetailInfoModel(getString(R.string.text_app_developer), app_developer, R.drawable.baseline_content_copy_24, v -> Data.copyToClipboard(requireActivity(), app_developer), requireActivity().getString(R.string.text_copy_to_clipboard)));
                    } catch (Exception ignored) {} try {
                        if (file_executed_apk.exists()) {
                            list.add(new DetailInfoModel(getString(R.string.text_executed_apk), file_executed_apk.getAbsolutePath(), R.drawable.baseline_share_24, v -> Data.shareFile(requireActivity(), file_executed_apk), requireActivity().getString(R.string.text_share_apk)));
                        }
                    } catch (Exception ignored) {} try {
                        list.add(new DetailInfoModel(getString(R.string.text_min_sdk), Objects.requireNonNull(AndroidUtils.getName(appMinSDK, "%a %v %c (%s)")), 0, null, null));
                    } catch (Exception ignored) {} try {
                        list.add(new DetailInfoModel(getString(R.string.text_target_sdk), Objects.requireNonNull(AndroidUtils.getName(appTargetSDK, "%a %v %c (%s)")), 0, null, null));
                    } catch (Exception ignored) {}

                    requireActivity().runOnUiThread(() -> recyclerView.setAdapter(adapter));
                });
            }
        } catch (Exception ignore) {}

        usageStatsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Data.hasUsageStatsPermission(requireActivity());
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (executorSingleSizeApp != null && !executorSingleSizeApp.isShutdown()) {
            executorSingleSizeApp.shutdownNow();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (executorSingleInfoApp != null && !executorSingleInfoApp.isShutdown()) {
            executorSingleInfoApp.shutdownNow();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        executorSingleSizeApp = Executors.newSingleThreadExecutor();
        executorSingleSizeApp.submit(() -> {
            requireActivity().runOnUiThread(() -> {
                if (containerUsageStatsSize != null) {
                    containerUsageStatsSize.setVisibility(Data.hasUsageStatsPermission(requireActivity()) ? View.GONE : View.VISIBLE);
                } if (containerSize != null) {
                    containerSize.setVisibility(Data.hasUsageStatsPermission(requireActivity()) ? View.VISIBLE : View.GONE);
                }
            });

            ArrayList<Long> appSize = new AppData().getAppSize(requireActivity(), packageName);

            requireActivity().runOnUiThread(() -> {
                if (textSizeApp != null) {
                    textSizeApp.post(() -> textSizeApp.setText(Data.convertBytes(requireContext(), appSize.get(2))));
                } if (textSizeData != null) {
                    textSizeData.post(() -> textSizeData.setText(Data.convertBytes(requireContext(), appSize.get(1))));
                } if (textSizeCache != null) {
                    textSizeCache.post(() -> textSizeCache.setText(Data.convertBytes(requireContext(), appSize.get(0))));
                }
            });
        });
    }
}