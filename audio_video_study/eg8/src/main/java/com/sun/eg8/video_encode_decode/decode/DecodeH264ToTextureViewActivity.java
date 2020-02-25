package com.sun.eg8.video_encode_decode.decode;

import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sun.eg8.R;


public class DecodeH264ToTextureViewActivity extends AppCompatActivity {
    private static final String TAG = "DecodeH264ToSurfac";
    private TextureView mTextureView;
    private Button mButton;
    private AVCDecoderToSurface mAvcDecoder;
    //视频文件
    public static String filePath;

    //for receive video play auto finish
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(DecodeH264ToTextureViewActivity.this, "播放结束", Toast.LENGTH_SHORT).show();
            mButton.setText("开始解码视频");
            mAvcDecoder.stopDecodingThread();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
        setContentView(R.layout.activity_decode_h264_to_texture_view);
        mTextureView = (TextureView) findViewById(R.id.textureview);
        mTextureView.setKeepScreenOn(true);
        mButton = (Button) findViewById(R.id.button);
        initTextureView();
    }

    private void initTextureView() {
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90, mTextureView.getWidth() / 2, mTextureView.getHeight() / 2);
                mTextureView.setTransform(matrix);
                if (mAvcDecoder == null) {

                    mAvcDecoder = new AVCDecoderToSurface(mHandler,
                            DecodeH264ToTextureViewActivity.this, filePath,
                            new Surface(surface), 1920, 1080, 30);
                    mAvcDecoder.initCodec();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (mAvcDecoder != null) {
                    mAvcDecoder.stopDecodingThread();
                }
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
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
