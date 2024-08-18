package com.mixno35.app_manager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.mixno35.app_manager.R;
import com.mixno35.app_manager.data.ShuffleBgAdapter;
import com.mixno35.app_manager.model.ActivityModel;

import java.util.ArrayList;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.AdapterHolder> {

    ArrayList<ActivityModel> list;
    Context context;

    @SuppressLint("NotifyDataSetChanged")
    public void setList(@NonNull ArrayList<ActivityModel> list) {
        this.list = list;
        this.list.sort(ActivityModel.TitleComparator);
        this.notifyDataSetChanged();
    }

    public ActivityAdapter(@NonNull ArrayList<ActivityModel> list, @NonNull Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ActivityAdapter.AdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_activity, parent, false);
        return new AdapterHolder(v);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull final ActivityAdapter.AdapterHolder holder, int position) {
        final ActivityModel model = list.get(position);

        String value = "-";
        String detail = "-";

        try {
            if (model.get_title() == null || model.get_title().trim().isEmpty()) {
                holder.titleTextView.post(() -> holder.titleTextView.setText("-"));
            } else {
                holder.titleTextView.post(() -> holder.titleTextView.setText(model.get_title()));
            }
        } catch (Exception ignore) {}
        try {
            if (model.get_value() == null) {
                value = "-";
            } else {
                value = (model.get_value().trim().isEmpty()) ? "-" : model.get_value();
            }
        } catch (Exception ignore) {}
        try {
            detail = String.format(context.getString(R.string.text_activity_enabled), model.isEnabled() ? context.getString(R.string.text_yes) : context.getString(R.string.text_no));
            detail += ", ";
            detail += String.format(context.getString(R.string.text_activity_exported), model.isExported() ? context.getString(R.string.text_yes) : context.getString(R.string.text_no));
        } catch (Exception ignore) {}

        String finalValue = value;
        String finalDetail = detail;

        holder.valueTextView.post(() -> holder.valueTextView.setText(finalValue));
        holder.detailTextView.post(() -> holder.detailTextView.setText(finalDetail));

        new ShuffleBgAdapter(context, holder.itemView, position);
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
