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
import com.mixno35.appmanager.model.ClassModel;

import java.util.ArrayList;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.AdapterHolder> {

    ArrayList<ClassModel> list;
    Context context;

    @SuppressLint("NotifyDataSetChanged")
    public void setList(@NonNull ArrayList<ClassModel> list) {
        this.list = list;
        this.list.sort(ClassModel.TitleComparator);
        this.notifyDataSetChanged();
    }

    public ClassAdapter(@NonNull ArrayList<ClassModel> list, @NonNull Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ClassAdapter.AdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_class, parent, false);
        return new AdapterHolder(v);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull final ClassAdapter.AdapterHolder holder, int position) {
        final ClassModel model = list.get(position);

        String value = "-";

        try {
            if (model.get_title() != null) holder.titleTextView.post(() -> holder.titleTextView.setText(model.get_title()));
            else holder.titleTextView.post(() -> holder.titleTextView.setText("-"));
        } catch (Exception e) {
            e.printStackTrace();
        } try {
            if (model.get_value() != null) value = (model.get_value().trim().isEmpty()) ? "-" : model.get_value();
            else value = "-";
        } catch (Exception e) {
            e.printStackTrace();
        }

        String finalValue = value;

        holder.valueTextView.post(() -> holder.valueTextView.setText(finalValue));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AdapterHolder extends RecyclerView.ViewHolder {

        protected MaterialTextView titleTextView, valueTextView;

        public AdapterHolder(View item) {
            super(item);
            titleTextView = item.findViewById(R.id.titleTextView);
            valueTextView = item.findViewById(R.id.valueTextView);
        }
    }
}
