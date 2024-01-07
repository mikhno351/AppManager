package com.mixno35.appmanager;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.jaredrummler.apkparser.ApkParser;
import com.mixno35.appmanager.widget.SyntaxHighlightTextView;

import java.util.Objects;

public class ManifestActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    SyntaxHighlightTextView textView;

    String packageName;

    PackageManager packageManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manifest);

        toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.textView);

        packageManager = getPackageManager();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (textView != null) {
            textView.setHorizontallyScrolling(true);
            textView.setVerticalScrollBarEnabled(true);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.linearLayoutContent), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, 0, insets.right, insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        try {
            packageName = getIntent().getStringExtra("packageName");
        } catch (Exception e) {
            e.printStackTrace();
            packageName = getPackageName();
        }

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(Objects.requireNonNull(packageName), 0);

            ApkParser apkParser = ApkParser.create(packageInfo);

            if (textView != null) textView.post(() -> {
                try {
                    textView.setTextHighlightSyntax(apkParser.getManifestXml().trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
