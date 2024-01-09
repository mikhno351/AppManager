package com.mixno35.appmanager;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.mixno35.appmanager.adapter.ClassAdapter;
import com.mixno35.appmanager.model.ClassModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClassActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MaterialToolbar toolbar;
    CircularProgressIndicator progressBar;

    ArrayList<ClassModel> list = new ArrayList<>();
    ClassAdapter adapter;

    String packageName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        if (recyclerView != null) {
            adapter = new ClassAdapter(list, this);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
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

            if (recyclerView != null) {
                PackageManager packageManager = getPackageManager();

                try {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                    File sourceDir = new File(applicationInfo.sourceDir);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            runOnUiThread(() -> {
                adapter.setList(list);
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
}
