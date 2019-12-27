package com.zj.public_lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * Created by Administrator on 2016/3/31.
 */
public class ContentViewFlipper extends ViewFlipper {
    public ContentViewFlipper(Context context) {
        super(context);
    }

    public ContentViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch (Exception e) {
        }
    }
}