package com.sun.eg6;

import android.os.Bundle;
import android.view.View;

import com.zj.public_lib.ui.BaseActivity;

public class MainActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initTitleLeft("eg6");
    }

    @Override
    protected void initData(Bundle bundle) {

    }

    public void imageClick(View view) {
        ImageActivity.startActivity(this);
    }
}
