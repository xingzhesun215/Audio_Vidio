package com.sun.eg9_10.view;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;

import com.sun.eg9_10.R;
import com.sun.eg9_10.player.MediaPlayer;
import com.sun.eg9_10.util.StorageUtil;
import com.zj.public_lib.ui.BaseActivity;

/**
 * Created By Chengjunsen on 2018/9/6
 */
public class VideoActivity extends BaseActivity implements TextureView.SurfaceTextureListener {
    private TextureView mTextureView;
    private String path;
    private int videoWidth, videoHeight;
    private MediaPlayer mMediaPlayer;


    @Override
    protected int getLayoutId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_video;
    }

    @Override
    protected void initView() {
        path = getIntent().getStringExtra("path");
        videoWidth = getIntent().getIntExtra("width", 1920);
        videoHeight = getIntent().getIntExtra("height", 1080);

        if (path == null || path.isEmpty()) {
            path = StorageUtil.getVedioPath() + "video.mp4";
        }
        mTextureView = findViewById(R.id.video_view);
        mTextureView.setRotation(90);
        mTextureView.setScaleX((float) videoWidth / videoHeight);
        mTextureView.setScaleY((float) videoHeight / videoWidth);
        mTextureView.setSurfaceTextureListener(this);

        mMediaPlayer = new MediaPlayer(path);
        mMediaPlayer.setVideoSize(videoWidth, videoHeight);
    }

    @Override
    protected void initData(Bundle bundle) {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        mMediaPlayer.setSurface(new Surface(surfaceTexture));
        mMediaPlayer.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
