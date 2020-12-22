package com.ccb.arcselect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class HMatrixTranslateLayout extends LinearLayout {
    private int parentWidth = 0;
    private int topOffset = 0;
    public HMatrixTranslateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setParentWidth(int width) {
        parentWidth = width;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        if (topOffset == 0) {
            topOffset = getWidth() / 2;
        }
        int left = getLeft()+topOffset;

       float tran = calculateTranslate(left , parentWidth);

        Matrix m = canvas.getMatrix();
        m.setTranslate(0,tran);
        canvas.concat(m);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private float calculateTranslate(int left, int w) {
        float result = 0f;
        int hh = w/2;
        result = Math.abs(left - hh);
//        result = Math.abs(left - hh)*-1;
        return (float) (result/3.14);
    }
}
