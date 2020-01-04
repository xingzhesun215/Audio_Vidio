package com.sun.eg3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.zj.public_lib.ui.BaseActivity;

import java.io.ByteArrayOutputStream;
/*
在 Android 平台使用 Camera API 进行视频的采集，分别使用 SurfaceView、TextureView 来预览 Camera 数据，取到 NV21 的数据回调
* */

public class SurfaceViewActivity extends BaseActivity {

    private ImageView preview_image;
    private SurfaceView surfaceview;
    private Camera camera;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_surfaceview;
    }



    @Override
    protected void initView() {
        initTopBarForOnlyTitle("surfaceview");
        preview_image = this.findViewById(R.id.preview_image);
        surfaceview = this.findViewById(R.id.surfaceview);


    }

    int count = 0;

    @Override
    protected void initData(Bundle bundle) {
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        /**
         * 设置获取帧
         */
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);
        camera.setParameters(parameters);

        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                count++;
                if (count % 50 == 0) {
                    Log.e("ME", "得到帧大小    " + data.length);
                    Camera.Size size = camera.getParameters().getPreviewSize();
                    try {
                        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                        if (image != null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
                            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

                            preview_image.setImageBitmap(adjustPhotoRotation(bmp, 90));
                            stream.close();
                        }
                    } catch (Exception ex) {
                        Log.e("Sys", "Error:" + ex.getMessage());
                    }
                }
            }
        });

        surfaceview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();

                } catch (Exception e) {

                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                holder.removeCallback(this);
            }
        });
    }


    /**
     * bitmap选择角度
     *
     * @param bm
     * @param orientationDegree
     * @return
     */
    Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {

        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }
        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);


        return bm1;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera!=null){
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, SurfaceViewActivity.class));
    }
}
