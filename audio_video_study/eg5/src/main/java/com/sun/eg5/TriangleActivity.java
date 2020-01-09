package com.sun.eg5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sun.eg5.triangle.TriangleGLSurfaceView;


public class TriangleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(new TriangleGLSurfaceView(this)); // 绘制三角形
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TriangleActivity.class));
    }
}