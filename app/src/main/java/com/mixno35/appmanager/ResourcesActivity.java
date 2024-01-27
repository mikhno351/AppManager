package com.mixno35.appmanager;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.mixno35.appmanager.adapter.ResourceAdapter;
import com.mixno35.appmanager.model.ResourceApkModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourcesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MaterialToolbar toolbar;
    CircularProgressIndicator progressBar;

    ResourceAdapter adapter;

    String packageName;
    File sourceDir;

    ArrayList<ResourceApkModel> list = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        PackageManager packageManager = getPackageManager();

        if (recyclerView != null) {
            adapter = new ResourceAdapter(list, this);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(insets.left, 0, insets.right, insets.bottom);

                return WindowInsetsCompat.CONSUMED;
            });
        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setTitle(getString(R.string.action_resources_app));
        }

        try {
            packageName = getIntent().getStringExtra("packageName");
        } catch (Exception e) {
            e.printStackTrace();
            packageName = getPackageName();
        }

        Executors.newSingleThreadExecutor().submit(() -> {
            runOnUiThread(() -> {
                if (progressBar != null) progressBar.post(() -> progressBar.animate().alpha(1f).setDuration(200).start());
                if (recyclerView != null) recyclerView.post(() -> {
                    recyclerView.animate().alpha(0f).setDuration(0).start();
                    recyclerView.scrollToPosition(0);
                });
            });

            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                sourceDir = new File(applicationInfo.sourceDir);

                list.addAll(getResourcesInApk(sourceDir));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            runOnUiThread(() -> {
                adapter.setList(list);
                setTitle(getString(R.string.action_resources_app) + String.format(" (%s)", list.size()));
                if (progressBar != null) progressBar.post(() -> progressBar.animate().alpha(0f).setDuration(200).start());
                if (recyclerView != null) recyclerView.post(() -> recyclerView.animate().alpha(1f).setDuration(200).start());
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    public ArrayList<ResourceApkModel> getResourcesInApk(@NonNull File apkPath) {
        ArrayList<ResourceApkModel> arrayList = new ArrayList<>();

        try {
            ZipFile apkZipFile = new ZipFile(apkPath);

            Enumeration<? extends ZipEntry> entries = apkZipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String path = entry.getName();
                String name = new File(path).getName();

                if (!path.toLowerCase().startsWith("meta-inf") && !entry.isDirectory() && !path.contains("$")) arrayList.add(new ResourceApkModel(path, name, entry.getSize()));
            }

            apkZipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }
}
