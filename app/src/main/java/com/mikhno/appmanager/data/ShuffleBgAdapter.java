package com.mikhno.appmanager.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.mikhno.appmanager.R;

public class ShuffleBgAdapter {

    @SuppressLint({"InlinedApi", "ResourceType"})
    public ShuffleBgAdapter(Context context, View view, int position) {
        if (position % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.itemRawAlternative));
        }
    }
}
