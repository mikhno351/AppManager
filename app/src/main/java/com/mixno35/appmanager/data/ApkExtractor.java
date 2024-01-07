package com.mixno35.appmanager.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.mixno35.appmanager.R;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApkExtractor {

    MaterialAlertDialogBuilder builder;
    AlertDialog dialog;
    Context context;
    ExecutorService executorService;

    LinearProgressIndicator progressIndicator;
    MaterialTextView textPercent;

    public ApkExtractor(Context context) {
        this.context = context;
    }

    @SuppressLint({"InflateParams", "MissingInflatedId"})
    public void shareApkFile(@NonNull PackageManager packageManager, @NonNull String packageName) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_extract_apk, null);

        progressIndicator = view.findViewById(R.id.progressIndicator);
        textPercent = view.findViewById(R.id.textPercent);

        progressIndicator.setMax(100);

        builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(context.getString(R.string.text_extracting_apk));
        builder.setMessage("base.apk");
        builder.setCancelable(false);
        builder.setView(view);

        builder.setPositiveButton(context.getString(R.string.action_hide), (dialog1, which) -> {
            Toast.makeText(context, context.getString(R.string.toast_extracting_apk_background), Toast.LENGTH_SHORT).show();
            dialog1.dismiss();
        });

        dialog = builder.create();
        dialog.show();

        ((Activity) context).runOnUiThread(() -> publishProgress(0));

        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                File apkFile = new File(applicationInfo.publicSourceDir);
                if (apkFile.exists()) {
                    String apkName = applicationInfo.packageName + ".apk";
                    dialog.setMessage(apkName);
                    File destFile = new File(context.getExternalFilesDir(null), apkName);
                    if (!destFile.exists()) copyFileWithProgress(apkFile, destFile);

                    Data.shareFile(context, destFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executorService.shutdown();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> dialog.dismiss());
            }
        });
    }

    private void copyFileWithProgress(@NonNull File source, @NonNull File dest) throws Exception {
        int totalSize = (int) source.length();
        int bufferSize = 1024;
        int bytesRead;
        int totalRead = 0;
        byte[] buffer = new byte[bufferSize];

        try (InputStream in = context.getContentResolver().openInputStream(Uri.fromFile(source));
             OutputStream out = context.getContentResolver().openOutputStream(Uri.fromFile(dest))) {

            while ((bytesRead = Objects.requireNonNull(in).read(buffer)) != -1) {
                Objects.requireNonNull(out).write(buffer, 0, bytesRead);
                totalRead += bytesRead;

                if (totalRead % 1000 == 0) {
                    int progress = (int) ((totalRead / (float) totalSize) * 100);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> publishProgress(progress));
                }
            }
        }

        ((Activity) context).runOnUiThread(() -> publishProgress(100));
    }

    @SuppressLint("SetTextI18n")
    private void publishProgress(int progress) {
        progressIndicator.setProgress(progress);
        textPercent.setText(progress + "%");
    }
}
