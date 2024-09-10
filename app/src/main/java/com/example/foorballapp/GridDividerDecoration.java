package com.example.foorballapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class GridDividerDecoration extends RecyclerView.ItemDecoration {

    private final Paint paint;
    private final int dividerSize;

    public GridDividerDecoration(Context context, int colorResId, int size) {
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, colorResId));
        paint.setStyle(Paint.Style.FILL);
        dividerSize = size;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int width = parent.getWidth();
        int height = parent.getHeight();

        // Draw vertical dividers
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getRight() + params.rightMargin;
            int top = child.getTop();
            int right = left + dividerSize;
            int bottom = child.getBottom() + params.bottomMargin;
            c.drawRect(left, top, right, bottom, paint);
        }

        // Draw horizontal dividers
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getLeft();
            int top = child.getBottom() + params.bottomMargin;
            int right = child.getRight() + params.rightMargin;
            int bottom = top + dividerSize;
            c.drawRect(left, top, right, bottom, paint);
        }
    }
}
