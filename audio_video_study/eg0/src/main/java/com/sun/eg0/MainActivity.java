package com.sun.eg0;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zj.public_lib.ui.BaseActivity;

public class MainActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initTitleLeft("eg0");
    }

    @Override
    protected void initData(Bundle bundle) {

    }
}
