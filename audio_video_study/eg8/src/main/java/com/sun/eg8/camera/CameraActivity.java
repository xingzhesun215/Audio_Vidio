package com.sun.eg8.camera;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sun.eg8.R;
import com.zj.public_lib.permission.AfterPermissionGranted;
import com.zj.public_lib.permission.PermissionUtils;

import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraActivity extends Activity implements PermissionUtils.PermissionCallbacks {
    private CameraPreview mPreview;
    private WaterMarkPreview mWaterMarkPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        requestPermission();
    }

    private void initCameraView() {
        mPreview = (CameraPreview) findViewById(R.id.camera_preview);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void switchCamera(View view) {
        mPreview.switchCamera();
    }

    public void takePicture(View view) {
        mPreview.takePicture();
    }

    public void toggleVideo(View view) {
        int result = mPreview.toggleVideo();
        Button button = (Button) view;
        if (result == 1) {
            button.setText("开始录制视频");
        } else if (result == 2) {
            button.setText("结束录制视频");
        } else {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void toggleWaterMark(View view) {
        if (mWaterMarkPreview == null) {
            mWaterMarkPreview = (WaterMarkPreview) findViewById(R.id.camera_watermark_preview);
            mPreview.setWaterMarkPreview(mWaterMarkPreview);
        }
        if (mPreview.toggleWaterMark()) {
            mWaterMarkPreview.setVisibility(View.VISIBLE);
        } else {
            mWaterMarkPreview.setVisibility(View.GONE);
        }
    }

    public static final int PERMISSION_CODE = 10;
    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    public void requestPermission() {
        if (PermissionUtils.hasPermissions(this, permissions)) {
            doNothing();
        } else {
            PermissionUtils.requestPermissions(this, PERMISSION_CODE, permissions);
        }
    }

    @AfterPermissionGranted(PERMISSION_CODE)
    private void doNothing() {
        initCameraView();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        if (PermissionUtils.somePermissionsPermanentlyDenied(this, perms)) {
            Toast.makeText(this, PermissionUtils.notifyMessage(perms), Toast.LENGTH_SHORT).show();
        }

    }
    //6.0权限的玩意，别动，动了会出差
}
