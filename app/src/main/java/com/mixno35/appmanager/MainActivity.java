package com.mixno35.appmanager;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.search.SearchBar;
import com.google.android.material.snackbar.Snackbar;
import com.mixno35.appmanager.adapter.AppAdapter;
import com.mixno35.appmanager.data.AppData;
import com.mixno35.appmanager.data.Data;
import com.mixno35.appmanager.dialog.AppDetailDialog;
import com.mixno35.appmanager.model.AndroidModel;
import com.mixno35.appmanager.model.AppModel;
import com.mixno35.appmanager.service.AppUsageService;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    ArrayList<AppModel> list = new ArrayList<>();
    AppAdapter adapter;
    CircularProgressIndicator progressBar;
    MaterialToolbar toolbar;
    SearchBar searchBar;
    ChipGroup chipGroup;
    AppBarLayout appBarLayout;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
        @Override
        public int scrollVerticallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
            return super.scrollVerticallyBy(dx, recycler, state);
        }
    };

    boolean LOADING_APPS = false;
    boolean RECYCLER_SCROLLING = false;

    int LIST_FILTER = 0;
    public static String APP_PACKAGE_REMOVE = "";
    public static ActivityResultLauncher<Intent> uninstallAppLauncher;
    ActivityResultLauncher<Intent> usageStatsLauncher;

    SharedPreferences prefs;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(Data.PREFS_NAME(getApplicationContext()), Context.MODE_PRIVATE);

        APP_PACKAGE_REMOVE = "";
        LIST_FILTER = prefs.getInt(Data.PREFS_KEY_LIST_FILTER, 0);
        LOADING_APPS = false;
        RECYCLER_SCROLLING = false;
        AppDetailDialog.isOpened = false;

        appBarLayout = findViewById(R.id.appBarLayout);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        searchBar = findViewById(R.id.searchBar);
        chipGroup = findViewById(R.id.chipGroup);
        
        adapter = new AppAdapter(list, this, prefs);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RECYCLER_SCROLLING = !(newState == RecyclerView.SCROLL_STATE_IDLE);
            }
        });

        if (!Data.hasUsageStatsPermission(this) && !prefs.getBoolean(Data.PREFS_KEY_APP_USE_PERMISSION, false)) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle(getString(R.string.text_package_usage_stats));
            builder.setMessage(getString(R.string.toast_package_usage_stats));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.action_provide), (dialog, which) -> Data.requestUsageStatsPermission(usageStatsLauncher));
            builder.setNegativeButton(getString(R.string.action_hide), (dialog, which) -> prefs.edit().putBoolean(Data.PREFS_KEY_APP_USE_PERMISSION, true).apply());
            builder.setNeutralButton(getString(R.string.action_cancel), (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        } else startAppUsageService();

        updateList();

        if (toolbar != null) setSupportActionBar(toolbar);

        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

            searchBar.post(() -> searchBar.setAlpha(1 - (percentage * 1.3f)));
        });

        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, 0, insets.right, insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contentSearchBar), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            layoutParams.setMargins(insets.left, 0, insets.right, 0);

            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contentChips), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            layoutParams.setMargins(insets.left, 0, insets.right, 0);

            return WindowInsetsCompat.CONSUMED;
        });

        if (!prefs.getBoolean(Data.PREFS_KEY_TAP_TARGET_MAIN, false)) new TapTargetSequence(this).targets(
                    TapTarget.forView(searchBar, getString(R.string.hint_search), getString(R.string.message_tap_target_search_bar))
                            .transparentTarget(true)
                            .targetRadius(60)
                            .cancelable(false)
                            .dimColor(com.google.android.material.R.color.background_floating_material_dark),
                    TapTarget.forView(findViewById(R.id.contentChips), String.format("%s, %s, %s, %s",
                                    getString(R.string.text_user_apps),
                                    getString(R.string.text_system_apps),
                                    getString(R.string.text_google_play),
                                    getString(R.string.text_all_apps)
                            ), getString(R.string.message_tap_target_filter))
                            .transparentTarget(true)
                            .targetRadius(60)
                            .cancelable(false)
                            .dimColor(com.google.android.material.R.color.background_floating_material_dark)
            )
            .listener(new TapTargetSequence.Listener() {
                @Override
                public void onSequenceFinish() {
                    prefs.edit().putBoolean(Data.PREFS_KEY_TAP_TARGET_MAIN, true).apply();
                }

                @Override
                public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {}

                @Override
                public void onSequenceCanceled(TapTarget lastTarget) {}
            }).start();

        Chip activeChip = ((Chip) chipGroup.getChildAt(LIST_FILTER));

        if (activeChip != null) activeChip.setChecked(true);

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (LOADING_APPS) return;
            if (RECYCLER_SCROLLING) recyclerView.post(() -> recyclerView.stopScroll());

            if (group.getCheckedChipId() == R.id.chipBy0) LIST_FILTER = 0;
            if (group.getCheckedChipId() == R.id.chipBy1) LIST_FILTER = 1;
            if (group.getCheckedChipId() == R.id.chipBy2) LIST_FILTER = 2;
            if (group.getCheckedChipId() == R.id.chipBy3) LIST_FILTER = 3;

            prefs.edit().putInt(Data.PREFS_KEY_LIST_FILTER, LIST_FILTER).apply();

            new Handler().postDelayed(this::updateList, 50);
        });

        uninstallAppLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (!APP_PACKAGE_REMOVE.trim().isEmpty()) {
                            Snackbar.make(recyclerView, String.format(getString(R.string.toast_app_deleted), APP_PACKAGE_REMOVE), Snackbar.LENGTH_LONG).show();
                            runOnUiThread(() -> adapter.notifyItemRemoved(new AppData().removeAppByPackage(list, APP_PACKAGE_REMOVE)));
                        }
                    } else Snackbar.make(recyclerView, getString(R.string.toast_app_not_deleted), Snackbar.LENGTH_LONG).show();

                    APP_PACKAGE_REMOVE = "";
                });

        usageStatsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK)
                        if (Data.hasUsageStatsPermission(MainActivity.this)) startAppUsageService();
                });

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                JSONArray jsonArray = new JSONArray(Data.readInputStream(getResources().openRawResource(R.raw.android_versions)));
                for (int i = 0; i < jsonArray.length(); i++) {
                    String version = Objects.requireNonNull(jsonArray.getJSONArray(i).getString(0));
                    String codename = Objects.requireNonNull(jsonArray.getJSONArray(i).getString(1));

                    version = version.trim().isEmpty() ? "0.0" : version;
                    codename = codename.trim().isEmpty() ? "-" : codename;

                    Data.ANDROID_VERSIONS.add(new AndroidModel(version, codename));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



    private void startAppUsageService() {
        startService(new Intent(this, AppUsageService.class));
    }

    void updateList() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(this::loadInstalledApps);
    }

    @SuppressLint("QueryPermissionsNeeded") void loadInstalledApps() {
        runOnUiThread(() -> {
            progressBar.post(() -> progressBar.animate().alpha(1f).setDuration(200).start());
            recyclerView.post(() -> {
                recyclerView.animate().alpha(0f).setDuration(0).start();
                recyclerView.scrollToPosition(0);
            });
            chipGroup.post(() -> {
                chipGroup.setEnabled(false);
                chipGroup.setClickable(false);
            });
            LOADING_APPS = true;
        });

        list.clear();

        try {
            PackageManager packageManager = getPackageManager();

            if (LIST_FILTER == 0) list.addAll(new AppData().get_arrayAppsUser(getApplicationContext(), packageManager));
            if (LIST_FILTER == 1) list.addAll(new AppData().get_arrayAppsSystem(getApplicationContext(), packageManager));
            if (LIST_FILTER == 2) list.addAll(new AppData().get_arrayAppsGooglePlay(getApplicationContext(), packageManager));
            if (LIST_FILTER == 3) list.addAll(new AppData().get_arrayAppsAll(getApplicationContext(), packageManager));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        runOnUiThread(() -> {
            Log.i("APPS", list.toString());
            adapter.setList(list);
            progressBar.post(() -> progressBar.animate().alpha(0f).setDuration(200).start());
            recyclerView.post(() -> recyclerView.animate().alpha(1f).setDuration(200).start());
            chipGroup.post(() -> {
                chipGroup.setEnabled(true);
                chipGroup.setClickable(true);
            });
            LOADING_APPS = false;
        });
    }
}