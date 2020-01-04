package com.sun.eg3;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import com.zj.public_lib.permission.AfterPermissionGranted;
import com.zj.public_lib.permission.PermissionUtils;
import com.zj.public_lib.ui.BaseActivity;
/*
在 Android 平台使用 Camera API 进行视频的采集，分别使用 SurfaceView、TextureView 来预览 Camera 数据，取到 NV21 的数据回调
* */

public class MainActivity extends BaseActivity {

    public final static int REQUEST_CODE_STORAGE = 200;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initTopBarForOnlyTitle("eg3");
    }

    boolean isCamere;

    @Override
    protected void initData(Bundle bundle) {
        String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (PermissionUtils.hasPermissions(this, perms)) {
            isCamere = true;
        } else {
            isCamere = false;
            PermissionUtils.requestPermissions(this, REQUEST_CODE_STORAGE, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_STORAGE)
    public void setCamere() {
        isCamere = true;
    }


    public void surfaceViewClick(View view) {
        if (isCamere) {
            SurfaceViewActivity.startActivity(this);
        }

    }

    public void TextureViewClick(View view) {
        if (isCamere) {
            TextTureViewActivity.startActivity(this);
        }

    }


}
