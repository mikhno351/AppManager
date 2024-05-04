package com.mixno35.app_manager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.mixno35.app_manager.R;
import com.mixno35.app_manager.model.MenuModel;

import java.util.ArrayList;

public class MenuHorizontalAdapter extends RecyclerView.Adapter<MenuHorizontalAdapter.AdapterHolder> {

    ArrayList<MenuModel> list;
    Context context;

    @SuppressLint("NotifyDataSetChanged")
    public void setList(@NonNull ArrayList<MenuModel> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    public MenuHorizontalAdapter(@NonNull ArrayList<MenuModel> list, @NonNull Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MenuHorizontalAdapter.AdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_menu_horizontal, parent, false);
        return new AdapterHolder(v);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull final MenuHorizontalAdapter.AdapterHolder holder, int position) {
        final MenuModel model = list.get(position);

        holder.titleTextView.post(() -> holder.titleTextView.setText(model.get_title()));
        holder.iconImageView.post(() -> holder.iconImageView.setImageResource(model.get_icon()));

        TooltipCompat.setTooltipText(holder.itemView, model.get_title());

        if (model.get_click() != null) {
            holder.itemView.setOnClickListener(model.get_click());
            holder.itemView.setClickable(true);
        } else holder.itemView.setClickable(false);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AdapterHolder extends RecyclerView.ViewHolder {

        protected MaterialTextView titleTextView;
        protected AppCompatImageView iconImageView;

        public AdapterHolder(View item) {
            super(item);
            titleTextView = item.findViewById(R.id.titleTextView);
            iconImageView = item.findViewById(R.id.iconImageView);
        }
    }
}
