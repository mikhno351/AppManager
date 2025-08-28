package com.mikhno.appmanager.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonGroup;
import com.google.android.material.textview.MaterialTextView;
import com.mikhno.appmanager.DetailsActivity;
import com.mikhno.appmanager.R;
import com.mikhno.appmanager.adapter.MenuVerticalAdapter;
import com.mikhno.appmanager.data.ApkExtractor;
import com.mikhno.appmanager.data.AppData;
import com.mikhno.appmanager.data.Data;
import com.mikhno.appmanager.decoration.ListExpressiveDecorate;
import com.mikhno.appmanager.model.MenuModel;

import java.util.ArrayList;

public class AppDetailDialog {

    BottomSheetDialog dialog;
    public static boolean isOpened = false;

    AppCompatImageView iconImageView;
    MaterialTextView nameTextView, packageTextView;
    RecyclerView recyclerView;
    LinearLayoutCompat mainContent;
    MaterialButtonGroup materialButtonGroup;
    MaterialButton actionOpenApp, actionDeleteApp;

    Context context;
    String packageName;
    PackageManager packageManager;

    ArrayList<MenuModel> list;

    @SuppressLint("InflateParams, MissingInflatedId")
    public AppDetailDialog(@NonNull Context context, @NonNull String packageName) {
        this.context = context;
        this.packageName = packageName;

        if (isOpened) {
            return;
        } else isOpened = true;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_app_detail, null);

        iconImageView = view.findViewById(R.id.iconImageView);
        nameTextView = view.findViewById(R.id.nameTextView);
        packageTextView = view.findViewById(R.id.packageTextView);
        recyclerView = view.findViewById(R.id.recyclerView);
        mainContent = view.findViewById(R.id.mainContent);
        materialButtonGroup = view.findViewById(R.id.materialButtonGroup);
        actionOpenApp = view.findViewById(R.id.actionOpenApp);
        actionDeleteApp = view.findViewById(R.id.actionDeleteApp);

        list = new ArrayList<>();

        packageManager = context.getPackageManager();

        boolean isLaunchable = new AppData().isLaunchable(packageManager, packageName);
        boolean isSystem = new AppData().isSystem(packageManager, packageName);

        actionOpenApp.setOnClickListener(v -> {
            new AppData().launchApp(context, packageManager, packageName);
        });
        actionDeleteApp.setOnClickListener(v -> {
            new AppData().uninstallApp(packageName);
            dialog.dismiss();
        });

        if (!isLaunchable && isSystem) {
            materialButtonGroup.post(() -> materialButtonGroup.setVisibility(View.GONE));
        }

        if (!isLaunchable) {
            actionOpenApp.post(() -> actionOpenApp.setVisibility(View.GONE));
        }
        if (isSystem) {
            actionDeleteApp.post(() -> actionDeleteApp.setVisibility(View.GONE));
        }

        try {
//            if (new AppData().isLaunchable(packageManager, packageName)) {
//                list.add(new MenuModel(context.getString(R.string.action_open_app), R.drawable.baseline_open_in_new_24, v -> new AppData().launchApp(context, packageManager, packageName)));
//            }
            if (new AppData().isSettings(packageManager, packageName)) {
                list.add(new MenuModel(context.getString(R.string.action_settings_app), R.drawable.outline_settings_24, v -> new AppData().openAppSettings(context, packageName)));
            }
//            if (!new AppData().isSystem(packageManager, packageName)) {
//                list.add(new MenuModel(context.getString(R.string.action_delete_app), R.drawable.baseline_delete_outline_24, v -> {
//                    new AppData().uninstallApp(packageName);
//                    dialog.dismiss();
//                }));
//            }
            list.add(new MenuModel(context.getString(R.string.action_share_app), R.drawable.baseline_upload_24, v -> new ApkExtractor(context).shareApkFile(packageManager, packageName)));
            list.add(new MenuModel(context.getString(R.string.action_find_in_gp_app), R.drawable.baseline_shop_24, v -> Data.openInGooglePlay(context, packageName)));
            list.add(new MenuModel(context.getString(R.string.action_details_app), R.drawable.outline_info_24, v -> {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(((Activity) context), iconImageView, "appTransitionSharedImage");
                context.startActivity(new Intent(context, DetailsActivity.class).putExtra("packageName", packageName), options.toBundle());
            }));

            recyclerView.addItemDecoration(new ListExpressiveDecorate(context, 2, 16, 0));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            MenuVerticalAdapter adapter = new MenuVerticalAdapter(list, context);
            recyclerView.setAdapter(adapter);

            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);

                iconImageView.post(() -> iconImageView.setImageDrawable(applicationInfo.loadIcon(packageManager)));
                nameTextView.post(() -> nameTextView.setText(applicationInfo.loadLabel(packageManager)));
                packageTextView.post(() -> packageTextView.setText(applicationInfo.packageName));
            } catch (Exception e) {
                mainContent.post(() -> mainContent.setVisibility(View.GONE));
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ignored) {}

        dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setOnDismissListener((dialog1) -> {
            dialog = null;
            isOpened = false;
        });
        dialog.show();
    }
}
