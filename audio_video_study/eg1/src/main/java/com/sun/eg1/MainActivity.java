package com.sun.eg1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zj.public_lib.ui.BaseActivity;
import com.zj.public_lib.utils.Logutil;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    TextView bt_show1, bt_show2, bt_show3;
    ImageView imageview;
    SurfaceView surfaceview;
    CustomView customview;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initTopBarForOnlyTitle("eg1");
        bt_show1 = this.findViewById(R.id.bt_show1);
        bt_show2 = this.findViewById(R.id.bt_show2);
        bt_show3 = this.findViewById(R.id.bt_show3);

        imageview = this.findViewById(R.id.imageview);
        surfaceview = this.findViewById(R.id.surfaceview);
        customview = this.findViewById(R.id.customview);

        bt_show1.setOnClickListener(this);
        bt_show2.setOnClickListener(this);
        bt_show3.setOnClickListener(this);
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bd);
        imageview.setImageBitmap(bitmap);

        surfaceview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(holder==null){
                    return;
                }
                Paint paint=new Paint();
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);

                Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.bd);
                Canvas canvas=holder.lockCanvas();
                canvas.drawBitmap(bitmap,0,0,paint);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    @Override
    protected void initData(Bundle bundle) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_show1:
                surfaceview.setVisibility(View.GONE);
                customview.setVisibility(View.GONE);
                imageview.setVisibility(View.VISIBLE);
                showToast("当前为imageview");
                break;
            case R.id.bt_show2:
                surfaceview.setVisibility(View.VISIBLE);
                customview.setVisibility(View.GONE);
                imageview.setVisibility(View.GONE);
                showToast("当前为surfaceview");
                break;
            case R.id.bt_show3:
                surfaceview.setVisibility(View.GONE);
                customview.setVisibility(View.VISIBLE);
                imageview.setVisibility(View.GONE);
                showToast("当前为自定义view");
                break;
        }
    }
}
