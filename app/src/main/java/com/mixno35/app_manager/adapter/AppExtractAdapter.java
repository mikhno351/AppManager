package com.mixno35.app_manager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.mixno35.app_manager.R;
import com.mixno35.app_manager.dialog.AppExtractMenuDialog;
import com.mixno35.app_manager.model.AppExtractModel;

import java.util.ArrayList;

public class AppExtractAdapter extends RecyclerView.Adapter<AppExtractAdapter.AdapterHolder> {

    ArrayList<AppExtractModel> list;
    Context context;

    @SuppressLint("NotifyDataSetChanged")
    public void setList(@NonNull ArrayList<AppExtractModel> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    public AppExtractAdapter(@NonNull ArrayList<AppExtractModel> list, @NonNull Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public AppExtractAdapter.AdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_app_extract, parent, false);
        return new AdapterHolder(v);
    }

    @Override
    @SuppressLint({"RecyclerView", "SetTextI18n"})
    public void onBindViewHolder(@NonNull final AppExtractAdapter.AdapterHolder holder, int position) {
        final AppExtractModel model = list.get(position);

        try {
            holder.appName.post(() -> holder.appName.setText(model.getName()));
        } catch (Exception ignored) {} try {
            holder.appPackage.post(() -> holder.appPackage.setText(model.getPkg()));
        } catch (Exception ignored) {} try {
            holder.appSize.post(() -> holder.appSize.setText(Formatter.formatFileSize(context, model.getSize())));
        } catch (Exception ignored) {} try {
            holder.appIcon.post(() -> holder.appIcon.setImageDrawable(model.getIcon()));
        } catch (Exception ignored) {}

        holder.itemView.setOnClickListener(v -> new AppExtractMenuDialog(context, position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AdapterHolder extends RecyclerView.ViewHolder {

        protected MaterialTextView appPackage, appName, appSize;
        protected ImageView appIcon;

        public AdapterHolder(View item) {
            super(item);
            appPackage = item.findViewById(R.id.appPackage);
            appName = item.findViewById(R.id.appName);
            appSize = item.findViewById(R.id.appSize);
            appIcon = item.findViewById(R.id.appIcon);
        }
    }
}
