package com.zj.public_lib.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.zj.public_lib.R;


/**
 * 转圈圈，等待
 */
public class LoadingDialog extends Dialog {
    private ImageView dialog_loading_iv;
    private Animation anim;
    Context context;

    public LoadingDialog(Context context) {
        super(context, R.style.loadingDialogStyle);
        this.context = context;
    }

    private LoadingDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        setCanceledOnTouchOutside(false);
        dialog_loading_iv = (ImageView) this
                .findViewById(R.id.dialog_loading_iv);
        anim = AnimationUtils.loadAnimation(context, R.anim.loading);
        dialog_loading_iv.startAnimation(anim);
    }

    @Override
    public void show() {
        super.show();
        dialog_loading_iv.startAnimation(anim);
    }

}
