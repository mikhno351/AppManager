package com.mixno35.appmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.google.android.material.textview.MaterialTextView;
import com.mixno35.appmanager.R;

import java.util.List;

public class RecentAdapter extends ArrayAdapter<String> {

    public RecentAdapter(Context context, List<String> data) {
        super(context, R.layout.row_recent, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_recent, parent, false);
        }

        String item = getItem(position);

        MaterialTextView textView = convertView.findViewById(R.id.titleTextView);
        textView.setText(item);

        return convertView;
    }

    public boolean isItemContains(String text) {
        for (int i = 0; i < getCount(); i++) if (text.equals(getItem(i))) return true;
        return false;
    }
}
