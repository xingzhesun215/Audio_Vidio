package com.sun.eg8;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sun.eg8.camera.CameraActivity;
import com.sun.eg8.camera2.Camera2Activity;
import com.sun.eg8.system.SystemCameraActivity;
import com.sun.eg8.video_encode_decode.EncodeAndDecodeActivity;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 调用系统拍照录像
     *
     * @param view
     */
    public void onShowSystemApp(View view) {
        Intent intent = new Intent(this, SystemCameraActivity.class);
        startActivity(intent);
    }


    public void onShowCamera(View view) {
        if (!checkCameraHardware()) {
            Toast.makeText(this, "没有摄像头", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    private boolean checkCameraHardware() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public void onShowCamera2(View view) {
        if (!checkCameraHardware()) {
            Toast.makeText(this, "没有摄像头", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, Camera2Activity.class);
        startActivity(intent);
    }


    public void encodeAndDecodeVideo(View view) {
        Intent intent = new Intent(this, EncodeAndDecodeActivity.class);
        startActivity(intent);
    }



}
