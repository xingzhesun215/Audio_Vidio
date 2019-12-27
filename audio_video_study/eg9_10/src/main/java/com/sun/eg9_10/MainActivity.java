package com.sun.eg9_10;

import android.os.Bundle;

import com.zj.public_lib.ui.BaseActivity;

public class MainActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initTitleLeft("eg9_10");
    }

    @Override
    protected void initData(Bundle bundle) {

    }
}
