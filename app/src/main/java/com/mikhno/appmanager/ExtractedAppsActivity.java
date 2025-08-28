package com.mikhno.appmanager;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.mikhno.appmanager.adapter.AppExtractAdapter;
import com.mikhno.appmanager.decoration.ListExpressiveDecorate;
import com.mikhno.appmanager.model.AppExtractModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExtractedAppsActivity extends AppCompatActivity {

    MaterialToolbar toolbar;

    RecyclerView recyclerView;
    CircularProgressIndicator progressBar;
    MaterialTextView nothingTextView;

    @SuppressLint("StaticFieldLeak")
    public static AppExtractAdapter adapter;

    ExecutorService executorSingle;

    public static ArrayList<AppExtractModel> list = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            EdgeToEdge.enable(this);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extracted_apps);

        toolbar = findViewById(R.id.toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        nothingTextView = findViewById(R.id.nothingTextView);

        adapter = new AppExtractAdapter(list, this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setTitle(getString(R.string.action_extracted_apps));
        }

        if (recyclerView != null) {
            recyclerView.addItemDecoration(new ListExpressiveDecorate(getApplicationContext(), 2, 16, 16));
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerView.setAdapter(adapter);

            ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(insets.left, 0, insets.right, insets.bottom);

                return WindowInsetsCompat.CONSUMED;
            });
        } if (nothingTextView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(nothingTextView, (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(insets.left, 0, insets.right, insets.bottom);

                return WindowInsetsCompat.CONSUMED;
            });
        }

        File mainPath = new File(getExternalFilesDir(null), "");

        executorSingle = Executors.newSingleThreadExecutor();
        executorSingle.submit(() -> {
            runOnUiThread(() -> {
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.GONE);
                    recyclerView.setAlpha(0);
                } if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                } if (nothingTextView != null) {
                    nothingTextView.setVisibility(View.GONE);
                }
            });

            list.clear();

            if (mainPath.isDirectory()) {
                File[] files = mainPath.listFiles((dir, name) -> name.toLowerCase().endsWith(".apk"));

                assert files != null;

                for (File file : files) {
                    PackageManager packageManager = getPackageManager();
                    PackageInfo packageInfo = packageManager.getPackageArchiveInfo(file.getAbsolutePath(), 0);

                    assert packageInfo != null;

                    ApplicationInfo applicationInfo = packageInfo.applicationInfo;

                    list.add(new AppExtractModel(
                            file,
                            applicationInfo.loadLabel(packageManager).toString(),
                            applicationInfo.packageName,
                            applicationInfo.loadIcon(packageManager),
                            file.length()));
                }
            }

            runOnUiThread(() -> {
                Log.i("ARR", Arrays.toString(list.toArray()));
                try {
                    adapter.setList(list);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                if (recyclerView != null) {
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.animate().alpha(1).start();
                } if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                } if (nothingTextView != null && list.isEmpty()) {
                    nothingTextView.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        adapter = null;

        if (executorSingle != null && !executorSingle.isShutdown()) {
            executorSingle.shutdownNow();
        }
    }
}
