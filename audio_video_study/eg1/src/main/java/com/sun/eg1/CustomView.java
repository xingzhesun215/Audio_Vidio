package com.sun.eg1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.zj.public_lib.utils.Logutil;

public class CustomView extends View {

    Paint paint = new Paint();
    Bitmap bitmap;

    public CustomView(Context context) {
        super(context);
        init();
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bd);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        } else {
            Logutil.e("custom view ondraw bitmap null");
        }
    }
}
