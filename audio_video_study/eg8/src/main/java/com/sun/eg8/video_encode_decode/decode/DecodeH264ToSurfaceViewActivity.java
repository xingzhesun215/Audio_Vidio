package com.sun.eg8.video_encode_decode.decode;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sun.eg8.R;


public class DecodeH264ToSurfaceViewActivity extends Activity {
    private static final String TAG = "DecodeH264ToSvActivity";
    private SurfaceView mSurfaceView;
    private Button mButton;
    private AVCDecoderToSurface mAvcDecoder;

    public static String filePath;

    //for receive video play auto finish
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(DecodeH264ToSurfaceViewActivity.this, "播放结束", Toast.LENGTH_SHORT).show();
            mButton.setText("开始解码视频");
            mAvcDecoder.stopDecodingThread();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode_h264_to_surface_view);
        if (TextUtils.isEmpty(filePath)) {
            Toast.makeText(this, "请生成文件", Toast.LENGTH_SHORT).show();
            return;
        }
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mSurfaceView.setKeepScreenOn(true);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (mAvcDecoder == null) {

                    mAvcDecoder = new AVCDecoderToSurface(mHandler,
                            DecodeH264ToSurfaceViewActivity.this, filePath,
                            holder.getSurface(), 1080, 1920, 30);
                    mAvcDecoder.initCodec();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.e(TAG, "surfaceChanged: ");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.e(TAG, "surfaceDestroyed: ");
            }
        });
        mButton = (Button) findViewById(R.id.button);
    }

    public void startDecode(View view) {
        if (!mAvcDecoder.mStartFlag) {
            mAvcDecoder.startDecodingThread();
            mButton.setText("停止解码视频");
        } else {
            mAvcDecoder.stopDecodingThread();
            mButton.setText("开始解码视频");
        }
    }
}
