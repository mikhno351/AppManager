package com.mixno35.app_manager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.mixno35.app_manager.R;
import com.mixno35.app_manager.dialog.AppDetailDialog;
import com.mixno35.app_manager.model.AppModel;

import java.util.ArrayList;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AdapterHolder> implements Filterable {

    ArrayList<AppModel> list;
    ArrayList<AppModel> appModelsFull;
    Context context;
    SharedPreferences prefs;

    @SuppressLint("NotifyDataSetChanged")
    public void setList(@NonNull ArrayList<AppModel> list) {
        this.list = list;
        this.appModelsFull = new ArrayList<>(list);
        this.list.sort(AppModel.NameComparator);
        this.notifyDataSetChanged();
    }

    public AppAdapter(@NonNull ArrayList<AppModel> list, @NonNull Context context, @NonNull SharedPreferences prefs) {
        this.list = list;
        this.context = context;
        this.prefs = prefs;
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

        holder.appPackage.post(() -> holder.appPackage.setText(model.get_package()));
        holder.appName.post(() -> holder.appName.setText(model.get_name()));
        holder.appIcon.post(() -> holder.appIcon.setImageDrawable(model.get_icon()));

        holder.itemView.setOnClickListener(v -> new AppDetailDialog(context, model.get_package()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return appModelFilter;
    }

    Filter appModelFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<AppModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(appModelsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (AppModel appModel : appModelsFull) {
                    if (appModel.get_name().toLowerCase().contains(filterPattern)) {
                        filteredList.add(appModel);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((ArrayList) results.values);
            list.sort(AppModel.NameComparator);
            notifyDataSetChanged();
        }
    };

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
