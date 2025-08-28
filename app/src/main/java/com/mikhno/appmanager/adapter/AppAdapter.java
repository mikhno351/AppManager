package com.mikhno.appmanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.textview.MaterialTextView;
import com.mikhno.appmanager.R;
import com.mikhno.appmanager.decoration.holder.ListExpressionHolder;
import com.mikhno.appmanager.dialog.AppDetailDialog;
import com.mikhno.appmanager.model.AppModel;

import java.util.ArrayList;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AdapterHolder> {

    ArrayList<AppModel> list;
    Context context;

    @SuppressLint("NotifyDataSetChanged")
    public void setList(@NonNull ArrayList<AppModel> list) {
        this.list = list;
        this.list.sort(AppModel.NameComparator);
        this.notifyDataSetChanged();
    }

    public AppAdapter(@NonNull ArrayList<AppModel> list, @NonNull Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public AppAdapter.AdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_app, parent, false);
        return new AdapterHolder(v);
    }

    @Override
    @SuppressLint({"RecyclerView", "SetTextI18n"})
    public void onBindViewHolder(@NonNull final AppAdapter.AdapterHolder holder, int position) {
        final AppModel model = list.get(position);

        new ListExpressionHolder(this, holder, position);

        try {
            holder.appPackage.post(() -> holder.appPackage.setText(model.get_package()));
        } catch (Exception ignored) {
        }
        try {
            holder.appName.post(() -> holder.appName.setText(model.get_name()));
        } catch (Exception ignored) {
        }
        try {
            holder.appIcon.post(() -> holder.appIcon.setImageDrawable(model.get_icon()));
        } catch (Exception ignored) {
        }

        holder.itemView.setOnClickListener(v -> new AppDetailDialog(context, model.get_package()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AdapterHolder extends RecyclerView.ViewHolder {

        protected MaterialTextView appPackage, appName;
        protected ImageView appIcon;

        public AdapterHolder(View item) {
            super(item);
            appPackage = item.findViewById(R.id.appPackage);
            appName = item.findViewById(R.id.appName);
            appIcon = item.findViewById(R.id.appIcon);
        }
    }
}
