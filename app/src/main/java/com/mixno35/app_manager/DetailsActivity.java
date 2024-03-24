package com.mixno35.app_manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.mixno35.app_manager.adapter.fragment.DetailsAdapter;
import com.mixno35.app_manager.data.AppData;
import com.mixno35.app_manager.data.Data;

import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {

    MaterialToolbar toolbar;

    String packageName = "";

    AppBarLayout appBarLayout;
    AppCompatImageView appIcon, imageVerified;
    MaterialTextView appName, appPackage, appVersion;
    TabLayout tabLayout;
    ViewPager2 viewPager;

    SharedPreferences prefs;
    PackageManager packageManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
        appIcon = findViewById(R.id.appIcon);
        appName = findViewById(R.id.appName);
        appVersion = findViewById(R.id.appVersion);
        appPackage = findViewById(R.id.appPackage);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        imageVerified = findViewById(R.id.imageVerified);

        packageManager = getPackageManager();
        prefs = getSharedPreferences(Data.PREFS_NAME(getApplicationContext()), Context.MODE_PRIVATE);

        if (appBarLayout != null) appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

            findViewById(R.id.contentDetail).post(() -> findViewById(R.id.contentDetail).setAlpha(1 - percentage));
        });

        if (tabLayout != null) {
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_basic)));
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_details)));

            ViewCompat.setOnApplyWindowInsetsListener(tabLayout, (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                layoutParams.setMargins(insets.left, 0, insets.right, 0);

                return WindowInsetsCompat.CONSUMED;
            });

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (viewPager != null) viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });
        }

        if (viewPager != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            DetailsAdapter adapter = new DetailsAdapter(fragmentManager, getLifecycle());

            viewPager.setAdapter(adapter);

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    tabLayout.selectTab(tabLayout.getTabAt(position));
                }
            });
        }

        try {
            packageName = getIntent().getStringExtra("packageName");
        } catch (Exception ignore) {
            packageName = getPackageName();
        }

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(Objects.requireNonNull(packageName), 0);

            ViewCompat.setTransitionName(appIcon, "sharedImage");

            if (new AppData().isAppInstalledFromPlayStore(getApplicationContext(), packageName)) imageVerified.post(() -> imageVerified.setVisibility(View.VISIBLE));

            appIcon.post(() -> appIcon.setImageDrawable(packageInfo.applicationInfo.loadIcon(packageManager)));
            appName.post(() -> appName.setText(packageInfo.applicationInfo.loadLabel(packageManager)));
            appPackage.post(() -> appPackage.setText(packageInfo.applicationInfo.packageName));
            appVersion.post(() -> appVersion.setText(String.format("%s (%s)", packageInfo.versionName, packageInfo.versionCode)));
        } catch (Exception ignore) {}

        setTitle("");

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
