package com.mikhno.appmanager.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListExpressiveDecorate extends RecyclerView.ItemDecoration {

    private final int padding;
    private final int marginX;
    private final int marginY;

    public ListExpressiveDecorate(Context context, int padding, int marginX, int marginY) {
        this.padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding, context.getResources().getDisplayMetrics());
        this.marginX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginX, context.getResources().getDisplayMetrics());
        this.marginY = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginY, context.getResources().getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();

        outRect.left = marginX;
        outRect.right = marginX;

        if (position == 0) {
            outRect.top = marginY;
        } else if (position == itemCount - 1) {
            outRect.bottom = marginY;
        }

        if (position != 0) {
            outRect.top = padding;
        }
    }
}
