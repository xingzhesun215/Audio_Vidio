package com.sun.eg9_10.view;

import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.sun.eg9_10.R;
import com.zj.public_lib.ui.BaseActivity;

import java.io.File;

/**
 * Created By Chengjunsen on 2018/9/5
 */
public class ImageActivity extends BaseActivity {
    private String path = null;
    private ImageView mImageView;

    @Override
    protected int getLayoutId() { requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN, WindowManager.LayoutParams. FLAG_FULLSCREEN);
        return R.layout.activity_image;
    }

    @Override
    protected void initView() {
        mImageView = findViewById(R.id.image_view);
        path = getIntent().getStringExtra("path");
        resolvImage();
    }

    @Override
    protected void initData(Bundle bundle) {

    }

    private void resolvImage() {
        if (path.isEmpty()) {
            return;
        }
        mImageView.setImageURI(Uri.fromFile(new File(path)));
    }
}
