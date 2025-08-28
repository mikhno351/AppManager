package com.mikhno.appmanager.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;
import com.mikhno.appmanager.DetailsActivity;
import com.mikhno.appmanager.ExtractedAppsActivity;
import com.mikhno.appmanager.R;
import com.mikhno.appmanager.adapter.MenuVerticalAdapter;
import com.mikhno.appmanager.data.AppData;
import com.mikhno.appmanager.data.Data;
import com.mikhno.appmanager.decoration.ListExpressiveDecorate;
import com.mikhno.appmanager.model.MenuModel;

import java.io.File;
import java.util.ArrayList;

public class AppExtractMenuDialog {

    BottomSheetDialog dialog;

    @SuppressLint({"InflateParams, MissingInflatedId", "NotifyDataSetChanged"})
    public AppExtractMenuDialog(@NonNull Context context, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_app_extract_menu, null);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        AppCompatImageView iconImageView = view.findViewById(R.id.iconImageView);
        MaterialTextView nameTextView = view.findViewById(R.id.nameTextView);
        MaterialTextView packageTextView = view.findViewById(R.id.packageTextView);

        ArrayList<MenuModel> list = new ArrayList<>();

        String appName = ExtractedAppsActivity.list.get(position).getName();
        String appPackage = ExtractedAppsActivity.list.get(position).getPkg();
        File appFile = ExtractedAppsActivity.list.get(position).getFile();

        iconImageView.post(() -> iconImageView.setImageDrawable(ExtractedAppsActivity.list.get(position).getIcon()));

        nameTextView.post(() -> nameTextView.setText(appName));
        packageTextView.post(() -> packageTextView.setText(appPackage));

        if (!AppData.isAppInstalled(context, appPackage)) {
            list.add(new MenuModel(context.getString(R.string.action_install_app), R.drawable.baseline_download_24, v -> {
                if (!context.getPackageManager().canRequestPackageInstalls()) {
                    ((Activity) context).startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", context.getPackageName()))), 1);
                    Toast.makeText(context, context.getString(R.string.toast_allow_unknown_sources), Toast.LENGTH_LONG).show();
                    return;
                }

                new AppData().installApk(context, appFile);
            }));
        }
        list.add(new MenuModel(context.getString(R.string.action_share), R.drawable.baseline_share_24, v -> {
            dialog.dismiss();
            Data.shareFile(context, appFile);
        }));
        list.add(new MenuModel(context.getString(R.string.action_find_in_gp_app), R.drawable.baseline_shop_24, v -> Data.openInGooglePlay(context, appPackage)));
        list.add(new MenuModel(context.getString(R.string.action_delete_extracted_apk), R.drawable.baseline_delete_outline_24, v -> {
            if (appFile.delete()) {
                Toast.makeText(context, String.format(context.getString(R.string.toast_app_deleted), appName), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                ExtractedAppsActivity.list.remove(position);
                ExtractedAppsActivity.adapter.notifyItemRemoved(position);
                new Handler().postDelayed(() -> ExtractedAppsActivity.adapter.notifyDataSetChanged(), 350);
            }
        }));
        list.add(new MenuModel(context.getString(R.string.action_details_app), R.drawable.outline_info_24, v -> {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(((Activity) context), iconImageView, "appTransitionSharedImage");
            context.startActivity(new Intent(context, DetailsActivity.class).putExtra("packageName", appFile.getAbsolutePath()), options.toBundle());
        }));

        MenuVerticalAdapter adapter = new MenuVerticalAdapter(list, context);

        if (recyclerView != null) {
            recyclerView.addItemDecoration(new ListExpressiveDecorate(context, 2, 16, 0));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }

        dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setOnDismissListener((dialog1) -> dialog = null);
        dialog.show();
    }
}
