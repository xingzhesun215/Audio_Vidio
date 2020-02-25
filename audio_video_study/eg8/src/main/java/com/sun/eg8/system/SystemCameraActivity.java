package com.sun.eg8.system;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sun.eg8.R;


public class SystemCameraActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_photo);
    }

    public void takePhoto(View view) {
        Intent intent = new Intent(this, TakePhotoActivity.class);
        startActivity(intent);
    }

    public void takeVideo(View view) {
        Intent intent = new Intent(this, TakeVideoActivity.class);
        startActivity(intent);
    }
}
