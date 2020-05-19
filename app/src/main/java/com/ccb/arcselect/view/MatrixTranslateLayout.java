package com.ccb.arcselect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class MatrixTranslateLayout extends LinearLayout {
    private int parentHeight = 0;
    private int topOffset = 0;
    public MatrixTranslateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setParentHeight(int height) {
        parentHeight = height;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        if (topOffset == 0) {
            topOffset = getHeight() / 2;
        }
        int top = getTop()+topOffset;

       float tran = calculateTranslate(top , parentHeight);

        Matrix m = canvas.getMatrix();
        m.setTranslate(tran,0);
        canvas.concat(m);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private float calculateTranslate(int top, int h) {
        float result = 0f;
        int hh = h/2;
        result = Math.abs(top - hh)*-1;
        return (float) (result/2);
    }
}
