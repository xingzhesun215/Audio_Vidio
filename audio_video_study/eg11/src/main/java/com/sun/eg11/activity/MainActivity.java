package com.sun.eg11.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sun.eg11.R;
import com.zj.public_lib.permission.PermissionUtils;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] perms = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!PermissionUtils.hasPermissions(this, perms)) {
            PermissionUtils.requestPermissions(this,  CAMERE_CODE, perms);
        }
        Button recordBtn = (Button) findViewById(R.id.record_activity);
        Button videoBtn = (Button) findViewById(R.id.video_connect);

        recordBtn.setOnClickListener(this);
        videoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_activity:
                requestCamerePermission(1);
                break;
            case R.id.video_connect:
                requestCamerePermission(4);
                break;
        }
    }


    //摄像头
    public static final int CAMERE_CODE = 10;

    public void requestCamerePermission(int type) {
        if (type == 1) {
            startActivity(new Intent(MainActivity.this, RecordedActivity.class));
        } else if (type == 2) {
        } else if (type == 4) {
            startActivity(new Intent(MainActivity.this, VideoConnectActivity.class));
        }
    }

}
