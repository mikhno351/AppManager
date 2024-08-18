package com.mixno35.app_manager.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mixno35.app_manager.ExtractedAppsActivity;
import com.mixno35.app_manager.R;
import com.mixno35.app_manager.adapter.MenuVerticalAdapter;
import com.mixno35.app_manager.data.AppData;
import com.mixno35.app_manager.data.Data;
import com.mixno35.app_manager.model.MenuModel;

import java.io.File;
import java.util.ArrayList;

public class AppExtractMenuDialog {

    BottomSheetDialog dialog;

    @SuppressLint({"InflateParams, MissingInflatedId", "NotifyDataSetChanged"})
    public AppExtractMenuDialog(@NonNull Context context, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_app_extract_menu, null);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        ArrayList<MenuModel> list = new ArrayList<>();

        String appName = ExtractedAppsActivity.list.get(position).getName();
        String appPackage = ExtractedAppsActivity.list.get(position).getPkg();
        File appFile = ExtractedAppsActivity.list.get(position).getFile();

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
        list.add(new MenuModel(context.getString(R.string.action_delete_app), R.drawable.baseline_delete_outline_24, v -> {
            if (appFile.delete()) {
                Toast.makeText(context, String.format(context.getString(R.string.toast_app_deleted), appName), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                ExtractedAppsActivity.list.remove(position);
                ExtractedAppsActivity.adapter.notifyItemRemoved(position);
                new Handler().postDelayed(() -> ExtractedAppsActivity.adapter.notifyDataSetChanged(), 350);
            }
        }));

        MenuVerticalAdapter adapter = new MenuVerticalAdapter(list, context);

        if (recyclerView != null) {
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
