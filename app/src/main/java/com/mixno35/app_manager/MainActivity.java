package com.mixno35.app_manager;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.mixno35.app_manager.adapter.AppAdapter;
import com.mixno35.app_manager.adapter.RecentAdapter;
import com.mixno35.app_manager.data.AppData;
import com.mixno35.app_manager.data.Data;
import com.mixno35.app_manager.dialog.AppDetailDialog;
import com.mixno35.app_manager.model.AndroidModel;
import com.mixno35.app_manager.model.AppModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    ArrayList<AppModel> list = new ArrayList<>();
    AppAdapter adapter;
    CircularProgressIndicator progressBar;
    MaterialToolbar toolbar;
    SearchBar searchBar;
    SearchView searchView;
    AppBarLayout appBarLayout;

    ListView listViewRecent;

    TabLayout tabLayout;

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

    SharedPreferences prefs;

    RecentAdapter adapterRecent;
    List<String> listRecent = new ArrayList<>();

    public static Boolean APK_SHARE_HIDDEN = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) EdgeToEdge.enable(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        APP_PACKAGE_REMOVE = "";
        LIST_FILTER = prefs.getInt(Data.PREFS_KEY_LIST_FILTER, 0);
        LOADING_APPS = false;
        RECYCLER_SCROLLING = false;
        AppDetailDialog.isOpened = false;

        appBarLayout = findViewById(R.id.appBarLayout);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        searchBar = findViewById(R.id.searchBar);
        searchView = findViewById(R.id.searchView);
        listViewRecent = findViewById(R.id.listViewRecent);
        tabLayout = findViewById(R.id.tabLayout);
        
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

        if (listViewRecent != null) {
            adapterRecent = new RecentAdapter(this, listRecent);
            listViewRecent.setAdapter(adapterRecent);
            listViewRecent.setOnItemClickListener((parent, view, position, id) -> {
                String text = Objects.requireNonNull(adapterRecent.getItem(position)).trim();

                if (searchView != null) {
                    searchView.setText(text);
                    searchView.hide();
                } if (searchBar != null) {
                    searchBar.setText(text);
                }

                adapter.getFilter().filter(text);
            });
        }

        updateList();

        if (toolbar != null) setSupportActionBar(toolbar);

        if (searchView != null) {
            searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
                String text = String.valueOf(searchView.getText()).trim();

                if (searchBar != null) {
                    searchBar.setText(text);
                }

                adapter.getFilter().filter(text);
                searchView.hide();

                if (searchView.getText().length() >= 2) {
                    addToRecent(text);
                }

                return false;
            });

            searchView.addTransitionListener((searchView, previousState, newState) -> {
                if (newState == SearchView.TransitionState.SHOWING) {
                    adapterRecent.notifyDataSetChanged();
                }
            });
        }

        if (searchView != null && searchBar != null) {
            searchView.setupWithSearchBar(searchBar);
        }

        if (tabLayout != null) {
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.text_user_apps)));
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.text_system_apps)));
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.text_google_play)));
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.text_all_apps)));

            ViewCompat.setOnApplyWindowInsetsListener(tabLayout, (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                layoutParams.setMargins(insets.left, 0, insets.right, 0);

                return WindowInsetsCompat.CONSUMED;
            });

            Objects.requireNonNull(tabLayout.getTabAt(LIST_FILTER)).select();
            tabLayout.setScrollPosition(LIST_FILTER, 0f, true);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (LOADING_APPS) return;
                    if (RECYCLER_SCROLLING) recyclerView.post(() -> recyclerView.stopScroll());

                    LIST_FILTER = tab.getPosition();

                    prefs.edit().putInt(Data.PREFS_KEY_LIST_FILTER, LIST_FILTER).apply();

                    new Handler().postDelayed(updateList(), 50);
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
                    }

                    return false;
                });
            }
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

        uninstallAppLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (!APP_PACKAGE_REMOVE.trim().isEmpty()) {
                    Snackbar.make(recyclerView, String.format(getString(R.string.toast_app_deleted), APP_PACKAGE_REMOVE), Snackbar.LENGTH_LONG).show();
                    runOnUiThread(() -> adapter.notifyItemRemoved(new AppData().removeAppByPackage(list, APP_PACKAGE_REMOVE)));
                }
            } else Snackbar.make(recyclerView, getString(R.string.toast_app_not_deleted), Snackbar.LENGTH_LONG).show();

            APP_PACKAGE_REMOVE = "";
        });

        loadAndroidVersions(Data.readInputStream(getResources().openRawResource(R.raw.android_versions)));
    }

    void loadAndroidVersions(String json) {
        Executors.newSingleThreadExecutor().submit(() -> {
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

    private void addToRecent(String string) {
        if (!adapterRecent.isItemContains(string)) {
            listRecent.add(string);
        }
    }

    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        if (searchView != null && searchView.getCurrentTransitionState() == SearchView.TransitionState.SHOWING) searchView.hide();
        return super.getOnBackInvokedDispatcher();
    }

    Runnable updateList() {
        Executors.newSingleThreadExecutor().submit(() -> {
            runOnUiThread(() -> {
                progressBar.post(() -> progressBar.animate().alpha(1f).setDuration(200).start());
                recyclerView.post(() -> {
                    recyclerView.animate().alpha(0f).setDuration(0).start();
                    recyclerView.scrollToPosition(0);
                });

                LOADING_APPS = true;
            });

            list.clear();

            try {
                PackageManager packageManager = getPackageManager();

                if (LIST_FILTER == 0) list.addAll(new AppData().get_arrayAppsUser(getApplicationContext(), packageManager));
                else if (LIST_FILTER == 1) list.addAll(new AppData().get_arrayAppsSystem(getApplicationContext(), packageManager));
                else if (LIST_FILTER == 2) list.addAll(new AppData().get_arrayAppsGooglePlay(getApplicationContext(), packageManager));
                else if (LIST_FILTER == 3) list.addAll(new AppData().get_arrayAppsAll(getApplicationContext(), packageManager));
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            runOnUiThread(() -> {
                Log.i("APPS", list.toString());
                adapter.setList(list);
                progressBar.post(() -> progressBar.animate().alpha(0f).setDuration(200).start());
                recyclerView.post(() -> recyclerView.animate().alpha(1f).setDuration(200).start());

                LOADING_APPS = false;
            });
        });

        return null;
    }
}