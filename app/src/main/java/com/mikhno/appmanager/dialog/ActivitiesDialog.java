package com.mikhno.appmanager.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.mikhno.appmanager.R;
import com.mikhno.appmanager.adapter.ActivityAdapter;
import com.mikhno.appmanager.data.AppData;
import com.mikhno.appmanager.model.ActivityModel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivitiesDialog {

    BottomSheetDialog dialog;
    ExecutorService executorSingle;

    @SuppressLint({"InflateParams", "MissingInflatedId"})
    public ActivitiesDialog(@NonNull Context context, @NonNull PackageManager packageManager, @NonNull String packageName) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_activities, null);

        LinearLayoutCompat errorContent = view.findViewById(R.id.errorContent);
        LinearLayoutCompat mainContent = view.findViewById(R.id.mainContent);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        CircularProgressIndicator progressBar = view.findViewById(R.id.progressBar);
        MaterialTextView titleTextView = view.findViewById(R.id.titleTextView);

        ArrayList<ActivityModel> list = new ArrayList<>();
        ActivityAdapter adapter = new ActivityAdapter(list, context);

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
                PackageInfo packageInfo = AppData.getPackageInfo(packageManager, packageName, PackageManager.GET_ACTIVITIES);

                for (ActivityInfo activity : packageInfo.activities) {
                    if (activity != null) {
                        String activityName = activity.name;
                        Boolean activityExported = activity.exported;
                        Boolean activityEnabled = activity.enabled;

                        list.add(new ActivityModel(activity.loadLabel(packageManager).toString(), activityName, activityExported, activityEnabled));
                    }
                }
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            ((Activity) context).runOnUiThread(() -> {
                adapter.setList(list);

                if (list.isEmpty()) {
                    mainContent.post(() -> mainContent.setVisibility(View.GONE));
                    errorContent.post(() -> errorContent.setVisibility(View.VISIBLE));
                }

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
