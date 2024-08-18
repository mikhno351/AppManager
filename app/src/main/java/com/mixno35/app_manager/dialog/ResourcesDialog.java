package com.mixno35.app_manager.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.mixno35.app_manager.R;
import com.mixno35.app_manager.adapter.ResourceAdapter;
import com.mixno35.app_manager.model.ResourceApkModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourcesDialog {

    BottomSheetDialog dialog;
    ExecutorService executorSingle;

    @SuppressLint({"InflateParams", "MissingInflatedId"})
    public ResourcesDialog(@NonNull Context context, @NonNull PackageManager packageManager, @NonNull String packageName) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_resources, null);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        CircularProgressIndicator progressBar = view.findViewById(R.id.progressBar);
        MaterialTextView titleTextView = view.findViewById(R.id.titleTextView);

        ArrayList<ResourceApkModel> list = new ArrayList<>();
        ResourceAdapter adapter = new ResourceAdapter(list, context);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        executorSingle = Executors.newSingleThreadExecutor();
        executorSingle.submit(() -> {
            ((Activity) context).runOnUiThread(() -> {
                if (progressBar != null) {
                    progressBar.post(() -> progressBar.animate().alpha(1f).setDuration(200).start());
                }
                recyclerView.post(() -> {
                    recyclerView.animate().alpha(0f).setDuration(0).start();
                    recyclerView.scrollToPosition(0);
                });
            });

            try {
                String pathApk;
                if (new File(packageName).isFile()) {
                    pathApk = new File(packageName).getAbsolutePath();
                } else {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                    pathApk = applicationInfo.sourceDir;
                }

                ZipFile apkZipFile = new ZipFile(new File(pathApk));

                Enumeration<? extends ZipEntry> entries = apkZipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String path = entry.getName();
                    String name = new File(path).getName();

                    if (!path.toLowerCase().startsWith("meta-inf") && !entry.isDirectory() && !path.contains("$")) {
                        list.add(new ResourceApkModel(path, name, entry.getSize()));
                    }
                }

                apkZipFile.close();
            } catch (Exception ignore) {}

            ((Activity) context).runOnUiThread(() -> {
                adapter.setList(list);

                titleTextView.post(() -> titleTextView.append(" (" + list.size() + ")"));

                new Handler().postDelayed(() -> {
                    progressBar.post(() -> progressBar.animate().alpha(0f).setDuration(200).start());
                    recyclerView.post(() -> recyclerView.animate().alpha(1f).setDuration(200).start());
                }, 200);
            });
        });

        dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setOnDismissListener((dialog1) -> {
            dialog = null;
            if (executorSingle != null && !executorSingle.isShutdown()) {
                executorSingle.shutdownNow();
            }
        });
        dialog.show();
    }
}
