package com.mikhno.appmanager.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;
import com.mikhno.appmanager.R;
import com.mikhno.appmanager.utils.PermissionUtils;

public class PermissionDetailDialog {

    BottomSheetDialog dialog;

    @SuppressLint({"InflateParams", "MissingInflatedId"})
    public PermissionDetailDialog(@NonNull Context context, @NonNull PackageManager packageManager, @NonNull String permission) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_permission_detail, null);

        MaterialTextView titleText = view.findViewById(R.id.titleText);
        MaterialTextView descriptionText = view.findViewById(R.id.descriptionText);
        AppCompatImageView iconPermission = view.findViewById(R.id.iconPermission);

        iconPermission.post(() -> iconPermission.setImageResource(PermissionUtils.getIcon(permission)));

        titleText.post(() -> titleText.setText(PermissionUtils.getName(packageManager, permission)));
        descriptionText.post(() -> descriptionText.setText(PermissionUtils.getDescription(context, packageManager, permission)));

        dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setOnDismissListener((dialog1) -> dialog = null);
        dialog.show();
    }
}
