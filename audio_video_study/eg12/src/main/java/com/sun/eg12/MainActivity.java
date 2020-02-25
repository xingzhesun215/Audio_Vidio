package com.sun.eg12;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import com.sun.eg12.preview.PreviewCameraActivity;
import com.zj.public_lib.permission.AfterPermissionGranted;
import com.zj.public_lib.permission.PermissionUtils;
import com.zj.public_lib.ui.BaseActivity;

import static com.zj.public_lib.utils.PublicUtil.CAMERA_PERIMISSION_CODE;

public class MainActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initTitleLeft("eg12");
    }

    @Override
    protected void initData(Bundle bundle) {

    }

    public void previewClick(View view) {
        String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (PermissionUtils.hasPermissions(this, perms)) {
            startPreview();
        } else {
            PermissionUtils.requestPermissions(this, CAMERA_PERIMISSION_CODE, perms);
        }

    }

    @AfterPermissionGranted(CAMERA_PERIMISSION_CODE)
    public void startPreview() {
        PreviewCameraActivity.startActivity(this);
    }
}
