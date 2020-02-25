package com.sun.eg6;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;


import com.sun.eg6.image.ImageGLSurfaceView;

import java.io.IOException;

public class ImageActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(new ImageGLSurfaceView(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ImageActivity.class));
    }
}
