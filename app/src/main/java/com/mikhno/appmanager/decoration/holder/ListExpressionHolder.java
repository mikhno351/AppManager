package com.mikhno.appmanager.decoration.holder;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.mikhno.appmanager.R;

public class ListExpressionHolder {

    public ListExpressionHolder(RecyclerView.Adapter<?> adapter, RecyclerView.ViewHolder holder, int position) {
        int count = adapter.getItemCount();
        Context context = holder.itemView.getContext();

        int overlayRes;
        if (count == 1) {
            overlayRes = R.style.ShapeAppearanceOverlay_App_ListItem_Single;
        } else if (position == 0) {
            overlayRes = R.style.ShapeAppearanceOverlay_App_ListItem_Top;
        } else if (position == count - 1) {
            overlayRes = R.style.ShapeAppearanceOverlay_App_ListItem_Bottom;
        } else {
            overlayRes = R.style.ShapeAppearanceOverlay_App_ListItem_Middle;
        }

        ((MaterialCardView) holder.itemView).setShapeAppearanceModel(ShapeAppearanceModel.builder(context, 0, overlayRes).build());
    }
}
