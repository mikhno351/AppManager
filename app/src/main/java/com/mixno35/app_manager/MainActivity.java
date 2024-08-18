package com.mixno35.app_manager;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.mixno35.app_manager.adapter.AppAdapter;
import com.mixno35.app_manager.data.AppData;
import com.mixno35.app_manager.data.Data;
import com.mixno35.app_manager.dialog.AppDetailDialog;
import com.mixno35.app_manager.fragment.AppsFragment;
import com.mixno35.app_manager.model.AndroidModel;
import com.mixno35.app_manager.model.AppModel;
import com.mixno35.app_manager.tab.Tab;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    ArrayList<AppModel> listSearch = new ArrayList<>();

    AppAdapter adapterSearch;

    CircularProgressIndicator progressBarSearch;
    MaterialToolbar toolbar;
    SearchBar searchBar;
    SearchView searchView;
    AppBarLayout appBarLayout;
    MaterialTextView nothingSearchTextView;

    FrameLayout frameLayout;

    TabLayout tabLayout;

    ExecutorService executorSingleAndroidVersions;
    ExecutorService executorSingleSearchApps;

    RecyclerView recyclerViewSearch;

    private final ArrayList<Tab> arrayTabs = new ArrayList<>();

    public static String APP_PACKAGE_REMOVE = "";
    public static ActivityResultLauncher<Intent> uninstallAppLauncher;

    SharedPreferences prefs;

    public static Boolean APK_SHARE_HIDDEN = false;

    @SuppressLint({"MissingInflatedId", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            EdgeToEdge.enable(this);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        APP_PACKAGE_REMOVE = "";
        AppDetailDialog.isOpened = false;

        appBarLayout = findViewById(R.id.appBarLayout);
        recyclerViewSearch = findViewById(R.id.recyclerViewSearch);
        progressBarSearch = findViewById(R.id.progressBarSearch);
        searchBar = findViewById(R.id.searchBar);
        searchView = findViewById(R.id.searchView);
        tabLayout = findViewById(R.id.tabLayout);
        nothingSearchTextView = findViewById(R.id.nothingSearchTextView);
        frameLayout = findViewById(R.id.frameLayout);

        adapterSearch = new AppAdapter(listSearch, this);

        if (recyclerViewSearch != null) {
            recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerViewSearch.setAdapter(adapterSearch);
        }

        openFragment(new AppsFragment("default"));
        loadAndroidVersions(Data.readInputStream(getResources().openRawResource(R.raw.android_versions)));

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if (searchView != null) {
            if (searchBar != null) {
                searchView.setupWithSearchBar(searchBar);
            }

            searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = String.valueOf(v.getText()).trim();
                    if (!TextUtils.isEmpty(text)) {
                        if (executorSingleSearchApps != null && !executorSingleSearchApps.isShutdown()) {
                            executorSingleSearchApps.submit(() -> {
                                runOnUiThread(() -> {
                                    if (recyclerViewSearch != null) {
                                        recyclerViewSearch.setVisibility(View.GONE);
                                        recyclerViewSearch.setAlpha(0);
                                    } if (progressBarSearch != null) {
                                        progressBarSearch.setVisibility(View.VISIBLE);
                                        progressBarSearch.setAlpha(1);
                                    } if (nothingSearchTextView != null) {
                                        nothingSearchTextView.setVisibility(View.GONE);
                                        nothingSearchTextView.setAlpha(0);
                                    }
                                });

                                PackageManager packageManager = getPackageManager();

                                listSearch.clear();

                                listSearch.addAll(new AppData().get_arrayAppsSearch(packageManager, text));

                                runOnUiThread(() -> {
                                    try {
                                        adapterSearch.setList(listSearch);
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    if (recyclerViewSearch != null) {
                                        recyclerViewSearch.setVisibility(View.VISIBLE);
                                        recyclerViewSearch.animate().alpha(1).start();
                                    } if (progressBarSearch != null) {
                                        progressBarSearch.setVisibility(View.GONE);
                                        progressBarSearch.setAlpha(0);
                                    } if (nothingSearchTextView != null && adapterSearch.getItemCount() == 0) {
                                        nothingSearchTextView.setVisibility(View.VISIBLE);
                                        nothingSearchTextView.animate().alpha(1).start();
                                    }
                                });
                            });
                        }
                    }
                }

                return true;
            });

            searchView.addTransitionListener((searchView, previousState, newState) -> {
                if (newState == SearchView.TransitionState.SHOWN) {
                    executorSingleSearchApps = Executors.newSingleThreadExecutor();
                } if (newState == SearchView.TransitionState.HIDDEN) {
                    listSearch.clear();
                    adapterSearch.notifyDataSetChanged();
                    if (executorSingleSearchApps != null && !executorSingleSearchApps.isShutdown()) {
                        executorSingleSearchApps.shutdownNow();
                    } if (progressBarSearch != null) {
                        progressBarSearch.setVisibility(View.GONE);
                        progressBarSearch.setAlpha(0);
                    } if (recyclerViewSearch != null) {
                        recyclerViewSearch.setVisibility(View.GONE);
                        recyclerViewSearch.setAlpha(0);
                    } if (nothingSearchTextView != null) {
                        nothingSearchTextView.setVisibility(View.GONE);
                        nothingSearchTextView.setAlpha(0);
                    }
                }
            });
        }

        if (recyclerViewSearch != null) {
            ViewCompat.setOnApplyWindowInsetsListener(recyclerViewSearch, (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                recyclerViewSearch.setPadding(insets.left, 0, insets.right, insets.bottom);

                return WindowInsetsCompat.CONSUMED;
            });
        }

        if (tabLayout != null) {
            arrayTabs.clear();

            arrayTabs.add(new Tab(tabLayout, getString(R.string.text_user_apps), "default"));
            arrayTabs.add(new Tab(tabLayout, getString(R.string.text_system_apps), "system"));
            if (!BuildConfig.IS_RUSTORE) {
                arrayTabs.add(new Tab(tabLayout, getString(R.string.text_google_play), "google_play"));
            }
            arrayTabs.add(new Tab(tabLayout, getString(R.string.text_all_apps), "all"));

            for (Tab tab : arrayTabs) {
                tabLayout.addTab(tab.getTab());
            }

            ViewCompat.setOnApplyWindowInsetsListener(tabLayout, (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                layoutParams.setMargins(insets.left, 0, insets.right, 0);

                return WindowInsetsCompat.CONSUMED;
            });

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    new Handler().postDelayed(() -> openFragment(new AppsFragment(arrayTabs.get(tab.getPosition()).getRunnable())), 100);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });
        }

        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

            if (searchBar != null) {
                searchBar.post(() -> searchBar.setAlpha(1 - (percentage * 1.3f)));

                searchBar.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();

                    if (id == R.id.menuActionSettings) {
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        return true;
                    } if (id == R.id.menuActionExtractedApps) {
                        startActivity(new Intent(getApplicationContext(), ExtractedAppsActivity.class));
                        return true;
                    }

                    return false;
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (executorSingleAndroidVersions != null && !executorSingleAndroidVersions.isShutdown()) {
            executorSingleAndroidVersions.shutdownNow();
        }
    }

    void loadAndroidVersions(String json) {
        executorSingleAndroidVersions = Executors.newSingleThreadExecutor();
        executorSingleAndroidVersions.submit(() -> {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String version = Objects.requireNonNull(jsonArray.getJSONArray(i).getString(0));
                    String codename = Objects.requireNonNull(jsonArray.getJSONArray(i).getString(1));

                    version = version.trim().isEmpty() ? "0.0" : version;
                    codename = codename.trim().isEmpty() ? "-" : codename;

                    Data.ANDROID_VERSIONS.add(new AndroidModel(version, codename));
                }
            } catch (Exception ignore) {}
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        if (searchView != null && searchView.getCurrentTransitionState() == SearchView.TransitionState.SHOWN) {
            Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_LONG).show();
        } return super.getOnBackInvokedDispatcher();
    }

    void openFragment(@NotNull Fragment fragment) {
        if (frameLayout != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        }
    }
}