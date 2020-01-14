package com.sun.eg7;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import com.zj.public_lib.permission.PermissionUtils;
import com.zj.public_lib.ui.BaseActivity;

public class MainActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initTitleLeft("eg7");
    }

    @Override
    protected void initData(Bundle bundle) {
        String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

        PermissionUtils.requestPermissions(this, 10, perms);
    }

    public void mediaCodecClick(View view) {
        MediaCodecActivity.startActivity(this);
    }
}
