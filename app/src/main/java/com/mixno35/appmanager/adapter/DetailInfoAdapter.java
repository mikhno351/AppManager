package com.mixno35.appmanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.mixno35.appmanager.R;
import com.mixno35.appmanager.model.DetailInfoModel;

import java.util.ArrayList;

public class DetailInfoAdapter extends RecyclerView.Adapter<DetailInfoAdapter.AdapterHolder> {

    ArrayList<DetailInfoModel> list;
    Context context;

    @SuppressLint("NotifyDataSetChanged")
    public void setList(@NonNull ArrayList<DetailInfoModel> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    public DetailInfoAdapter(@NonNull ArrayList<DetailInfoModel> list, @NonNull Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public DetailInfoAdapter.AdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_detail_info, parent, false);
        return new AdapterHolder(v);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull final DetailInfoAdapter.AdapterHolder holder, int position) {
        final DetailInfoModel model = list.get(position);

        try {
            if (model.get_title() != null) holder.titleTextView.post(() -> holder.titleTextView.setText(model.get_title()));
            else holder.titleTextView.post(() -> holder.titleTextView.setText("-"));
        } catch (Exception e) {
            e.printStackTrace();
        } try {
            if (model.get_value() != null) holder.valueTextView.post(() -> holder.valueTextView.setText((model.get_value().trim().isEmpty()) ? "-" : model.get_value()));
            else holder.valueTextView.post(() -> holder.valueTextView.setText("-"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (model.get_click() != null) {
            holder.imageButton.post(() -> {
                holder.imageButton.setVisibility(View.VISIBLE);
                holder.imageButton.setImageResource(model.get_icon());
            });

            if (model.get_action_title() != null && !model.get_action_title().trim().isEmpty())
                TooltipCompat.setTooltipText(holder.imageButton, model.get_action_title());

            holder.imageButton.setOnClickListener(model.get_click());
        } else holder.imageButton.post(() -> holder.imageButton.setVisibility(View.GONE));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AdapterHolder extends RecyclerView.ViewHolder {

        protected MaterialTextView titleTextView, valueTextView;
        protected AppCompatImageButton imageButton;

        public AdapterHolder(View item) {
            super(item);
            titleTextView = item.findViewById(R.id.titleTextView);
            valueTextView = item.findViewById(R.id.valueTextView);
            imageButton = item.findViewById(R.id.imageButton);
        }
    }
}
