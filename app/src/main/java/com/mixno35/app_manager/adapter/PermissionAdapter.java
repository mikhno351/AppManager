package com.mixno35.app_manager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.mixno35.app_manager.R;
import com.mixno35.app_manager.dialog.PermissionDetailDialog;
import com.mixno35.app_manager.model.PermissionModel;

import java.util.ArrayList;

public class PermissionAdapter extends RecyclerView.Adapter<PermissionAdapter.AdapterHolder> {

    ArrayList<PermissionModel> list;
    Context context;
    PackageManager packageManager;

    @SuppressLint("NotifyDataSetChanged")
    public void setList(@NonNull ArrayList<PermissionModel> list) {
        this.list = list;
        this.list.sort(PermissionModel.TitleComparator);
        this.notifyDataSetChanged();
    }

    public PermissionAdapter(@NonNull ArrayList<PermissionModel> list, @NonNull Context context, @NonNull PackageManager packageManager) {
        this.list = list;
        this.context = context;
        this.packageManager = packageManager;
    }

    @NonNull
    @Override
    public PermissionAdapter.AdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_permission, parent, false);
        return new AdapterHolder(v);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull final PermissionAdapter.AdapterHolder holder, int position) {
        final PermissionModel model = list.get(position);

        String value = "-";

        try {
            if (model.get_title() != null) {
                holder.titleTextView.post(() -> holder.titleTextView.setText(model.get_title()));
            } else {
                holder.titleTextView.post(() -> holder.titleTextView.setText("-"));
            }
        } catch (Exception ignore) {}
        try {
            if (model.get_value() != null) {
                value = (model.get_value().trim().isEmpty()) ? "-" : model.get_value();
            } else {
                value = "-";
            }
        } catch (Exception ignore) {}
        try {
            holder.detailTextView.post(() -> holder.detailTextView.setText(String.format(context.getString(R.string.text_permission_granded), model.isGranded() ? context.getString(R.string.text_yes) : context.getString(R.string.text_no))));
        } catch (Exception ignore) {}

        String finalValue = value;

        holder.titleTextView.post(() -> holder.titleTextView.setText(model.get_title()));
        holder.valueTextView.post(() -> holder.valueTextView.setText(finalValue));

        if (!finalValue.equals("-")) {
            holder.itemView.setOnClickListener(v -> new PermissionDetailDialog(context, packageManager, finalValue));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AdapterHolder extends RecyclerView.ViewHolder {

        protected MaterialTextView titleTextView, valueTextView, detailTextView;

        public AdapterHolder(View item) {
            super(item);
            titleTextView = item.findViewById(R.id.titleTextView);
            valueTextView = item.findViewById(R.id.valueTextView);
            detailTextView = item.findViewById(R.id.detailTextView);
        }
    }
}
