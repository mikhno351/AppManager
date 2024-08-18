package com.mixno35.app_manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.mixno35.app_manager.data.AppData;
import com.mixno35.app_manager.data.Data;
import com.mixno35.app_manager.fragment.details.BaseFragment;

import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {

    MaterialToolbar toolbar;

    String packageName = "";

    AppBarLayout appBarLayout;
    AppCompatImageView appIcon;

    SharedPreferences prefs;
    PackageManager packageManager;

    FrameLayout frameLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            EdgeToEdge.enable(this);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
        appIcon = findViewById(R.id.appIcon);
        frameLayout = findViewById(R.id.frameLayout);

        packageManager = getPackageManager();
        prefs = getSharedPreferences(Data.PREFS_NAME(getApplicationContext()), Context.MODE_PRIVATE);

        if (appBarLayout != null) {
            appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                int maxScroll = appBarLayout.getTotalScrollRange();
                float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

                float alpha = 1 - (percentage * 1.86f);

                if (appIcon != null) {
                    appIcon.post(() -> appIcon.setAlpha(alpha));
                }
            });
        }

        if (frameLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(frameLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
                return insets;
            });

            openFragment(new BaseFragment());
        }

        try {
            packageName = getIntent().getStringExtra("packageName");
        } catch (Exception ignore) {
            packageName = getPackageName();
        }

        try {
            PackageInfo packageInfo = AppData.getPackageInfo(packageManager, packageName, 0);

            ViewCompat.setTransitionName(appIcon, "sharedImage");

            appIcon.post(() -> appIcon.setImageDrawable(packageInfo.applicationInfo.loadIcon(packageManager)));

            setTitle(packageInfo.applicationInfo.loadLabel(packageManager));
        } catch (Exception ignore) {}

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

    private void openFragment(Fragment fragment) {
        if (frameLayout != null && fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        }
    }
}
