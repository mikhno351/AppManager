package com.mixno35.app_manager.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.mixno35.app_manager.R;
import com.mixno35.app_manager.adapter.PermissionAdapter;
import com.mixno35.app_manager.data.AppData;
import com.mixno35.app_manager.model.PermissionModel;
import com.mixno35.app_manager.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PermissionsDialog {

    BottomSheetDialog dialog;
    ExecutorService executorSingle;

    @SuppressLint({"InflateParams", "MissingInflatedId"})
    public PermissionsDialog(@NonNull Context context, @NonNull PackageManager packageManager, @NonNull String packageName) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_permissions, null);

        LinearLayoutCompat errorContent = view.findViewById(R.id.errorContent);
        LinearLayoutCompat mainContent = view.findViewById(R.id.mainContent);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        CircularProgressIndicator progressBar = view.findViewById(R.id.progressBar);
        MaterialTextView titleTextView = view.findViewById(R.id.titleTextView);

        ArrayList<PermissionModel> list = new ArrayList<>();
        PermissionAdapter adapter = new PermissionAdapter(list, context, packageManager);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        executorSingle = Executors.newSingleThreadExecutor();
        executorSingle.submit(() -> {
            ((Activity) context).runOnUiThread(() -> {
                progressBar.post(() -> progressBar.animate().alpha(1f).setDuration(200).start());
                recyclerView.post(() -> {
                    recyclerView.animate().alpha(0f).setDuration(0).start();
                    recyclerView.scrollToPosition(0);
                });
            });

            try {
                list.clear();

                PackageInfo packageInfo = AppData.getPackageInfo(packageManager, packageName, PackageManager.GET_PERMISSIONS);
                String[] permissions = packageInfo.requestedPermissions;

                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    boolean granded = (packageInfo.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0;
                    list.add(new PermissionModel(PermissionUtils.getName(packageManager, permission), permission, granded));
                }
            } catch (Exception ignore) {}

            ((Activity) context).runOnUiThread(() -> {
                if (list.isEmpty()) {
                    mainContent.post(() -> mainContent.setVisibility(View.GONE));
                    errorContent.post(() -> errorContent.setVisibility(View.VISIBLE));
                }

                titleTextView.post(() -> titleTextView.append(" (" + list.size() + ")"));

                adapter.setList(list);
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
