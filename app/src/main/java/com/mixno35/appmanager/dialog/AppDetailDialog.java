package com.mixno35.appmanager.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;
import com.mixno35.appmanager.DetailsActivity;
import com.mixno35.appmanager.R;
import com.mixno35.appmanager.adapter.MenuAdapter;
import com.mixno35.appmanager.data.ApkExtractor;
import com.mixno35.appmanager.data.AppData;
import com.mixno35.appmanager.data.Data;
import com.mixno35.appmanager.decoration.GridSpacingItemDecoration;
import com.mixno35.appmanager.model.MenuModel;

import java.util.ArrayList;

public class AppDetailDialog {

    BottomSheetDialog dialog;
    public static boolean isOpened = false;

    AppCompatImageView iconImageView;
    MaterialTextView nameTextView, packageTextView, versionTextView;
    RecyclerView recyclerView;
    LinearLayoutCompat mainContent, errorContent;

    Context context;
    String packageName;
    PackageManager packageManager;

    ArrayList<MenuModel> list;

    @SuppressLint("InflateParams, MissingInflatedId")
    public AppDetailDialog(@NonNull Context context, @NonNull String packageName) {
        this.context = context;
        this.packageName = packageName;

        if (isOpened) return;
        else isOpened = true;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_app_detail, null);

        iconImageView = view.findViewById(R.id.iconImageView);
        nameTextView = view.findViewById(R.id.nameTextView);
        packageTextView = view.findViewById(R.id.packageTextView);
        versionTextView = view.findViewById(R.id.versionTextView);
        recyclerView = view.findViewById(R.id.recyclerView);
        mainContent = view.findViewById(R.id.mainContent);
        errorContent = view.findViewById(R.id.errorContent);

        list = new ArrayList<>();

        packageManager = context.getPackageManager();

        try {
            if (new AppData().isLaunchable(packageManager, packageName)) {
                list.add(new MenuModel(context.getString(R.string.action_open_app), R.drawable.baseline_open_in_new_24, v -> new AppData().launchApp(context, packageManager, packageName)));
            } if (new AppData().isSettings(packageManager, packageName)) {
                list.add(new MenuModel(context.getString(R.string.action_settings_app), R.drawable.outline_settings_24, v -> new AppData().openAppSettings(context, packageName)));
            } if (!new AppData().isSystem(packageManager, packageName)) {
                list.add(new MenuModel(context.getString(R.string.action_delete_app), R.drawable.baseline_delete_outline_24, v -> {
                    new AppData().uninstallApp(packageName);
                    dialog.dismiss();
                }));
            }
            list.add(new MenuModel(context.getString(R.string.action_share_app), R.drawable.baseline_share_24, v -> new ApkExtractor(context).shareApkFile(packageManager, packageName)));
            list.add(new MenuModel(context.getString(R.string.action_find_in_gp_app), R.drawable.baseline_shop_24, v -> Data.openInGooglePlay(context, packageName)));
            list.add(new MenuModel(context.getString(R.string.action_details_app), R.drawable.outline_info_24, v -> {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(((Activity) context), iconImageView, "sharedImage");
                context.startActivity(new Intent(context, DetailsActivity.class).putExtra("packageName", packageName), options.toBundle());
            }));

            recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(60, 4));

            MenuAdapter adapter = new MenuAdapter(list, context);
            recyclerView.setAdapter(adapter);

            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);

                iconImageView.post(() -> iconImageView.setImageDrawable(applicationInfo.loadIcon(packageManager)));
                nameTextView.post(() -> nameTextView.setText(applicationInfo.loadLabel(packageManager)));
                packageTextView.post(() -> packageTextView.setText(applicationInfo.packageName));
                versionTextView.post(() -> versionTextView.setText(String.format("%s (%s)", packageInfo.versionName, packageInfo.versionCode)));
            } catch (Exception e) {
                mainContent.post(() -> mainContent.setVisibility(View.GONE));
                errorContent.post(() -> errorContent.setVisibility(View.VISIBLE));
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setOnDismissListener((dialog1) -> isOpened = false);
        dialog.show();
    }
}
