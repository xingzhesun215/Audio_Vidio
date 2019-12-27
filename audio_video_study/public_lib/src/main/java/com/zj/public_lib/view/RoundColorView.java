package com.zj.public_lib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RoundColorView extends View {


    private final Paint mPaint;
    private final Context context;
    private int color_0;

    public RoundColorView(Context context) {
        this(context, null);
    }

    public RoundColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.mPaint = new Paint();
        this.color_0 = Color.RED;
        this.mPaint.setAntiAlias(true); //消除锯齿
        this.mPaint.setStyle(Paint.Style.STROKE); //绘制空心圆
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        int center = getWidth() / 2;

//        //绘制内圆
//        this.mPaint.setColor(Color.WHITE);
//        this.mPaint.setStrokeWidth(2);
//        canvas.drawCircle(center, center, 52, this.mPaint);

        //绘制圆环
        this.mPaint.setColor(color_0);
        this.mPaint.setStrokeWidth(1);
        canvas.drawCircle(center, center, 52, this.mPaint);

        super.onDraw(canvas);
    }


    public void setColor(String color) {
        color_0 = Color.parseColor(color);
        mPaint.setColor(color_0);
        invalidate();
    }

}


