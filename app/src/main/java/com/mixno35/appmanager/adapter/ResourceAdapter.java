package com.mixno35.appmanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.mixno35.appmanager.R;
import com.mixno35.appmanager.data.Data;
import com.mixno35.appmanager.model.ResourceApkModel;

import java.util.ArrayList;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.AdapterHolder> {

    ArrayList<ResourceApkModel> list;
    Context context;

    @SuppressLint("NotifyDataSetChanged")
    public void setList(@NonNull ArrayList<ResourceApkModel> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    public ResourceAdapter(@NonNull ArrayList<ResourceApkModel> list, @NonNull Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ResourceAdapter.AdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_resource_apk, parent, false);
        return new AdapterHolder(v);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull final ResourceAdapter.AdapterHolder holder, int position) {
        final ResourceApkModel model = list.get(position);

        holder.titleTextView.post(() -> holder.titleTextView.setText(model.get_name()));
        holder.valueTextView.post(() -> holder.valueTextView.setText(model.get_path()));
        holder.sizeTextView.post(() -> holder.sizeTextView.setText(Data.convertBytes(model.get_size())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AdapterHolder extends RecyclerView.ViewHolder {

        protected MaterialTextView titleTextView, valueTextView, sizeTextView;

        public AdapterHolder(View item) {
            super(item);
            titleTextView = item.findViewById(R.id.titleTextView);
            valueTextView = item.findViewById(R.id.valueTextView);
            sizeTextView = item.findViewById(R.id.sizeTextView);
        }
    }
}
