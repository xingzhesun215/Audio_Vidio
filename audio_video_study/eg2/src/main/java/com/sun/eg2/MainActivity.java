package com.sun.eg2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zj.public_lib.permission.AfterPermissionGranted;
import com.zj.public_lib.permission.PermissionUtils;
import com.zj.public_lib.ui.BaseActivity;
import com.zj.public_lib.utils.Logutil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.sun.eg2.GlobalConfig.AUDIO_FORMAT;
import static com.sun.eg2.GlobalConfig.CHANNEL_CONFIG;
import static com.sun.eg2.GlobalConfig.SAMPLE_RATE_INHZ;


public class MainActivity extends BaseActivity implements View.OnClickListener, PermissionUtils.PermissionCallbacks {
    private static final String TAG = "jqd";

    private Button mBtnControl;
    private Button mBtnPlay;

    /**
     * 需要申请的运行时权限
     */
    private String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private boolean isRecording;
    private AudioRecord audioRecord;
    private Button mBtnConvert;
    private AudioTrack audioTrack;
    private byte[] audioData;
    private FileInputStream fileInputStream;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initTopBarForOnlyTitle("eg2");

        mBtnControl = (Button) findViewById(R.id.btn_control);
        mBtnControl.setOnClickListener(this);
        mBtnConvert = (Button) findViewById(R.id.btn_convert);
        mBtnConvert.setOnClickListener(this);
        mBtnPlay = (Button) findViewById(R.id.btn_play);
        mBtnPlay.setOnClickListener(this);

        requestCamerePermission();
    }


    @Override
    protected void initData(Bundle bundle) {

    }





    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_control:
                Button button = (Button) view;
                if (button.getText().toString().equals(getString(R.string.start_record))) {
                    button.setText(getString(R.string.stop_record));
                    startRecord();
                } else {
                    button.setText(getString(R.string.start_record));
                    stopRecord();
                }

                break;
            case R.id.btn_convert:
                PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
                File pcmFile = new File("/sdcard/eg2/test.pcm");

                File wavFile = new File("/sdcard/eg2/test.wav");
                if (!wavFile.mkdirs()) {
                    Log.e(TAG, "wavFile Directory not created");
                }
                if (wavFile.exists()) {
                    wavFile.delete();
                }
                pcmToWavUtil.pcmToWav(pcmFile.getAbsolutePath(), wavFile.getAbsolutePath());

                break;
            case R.id.btn_play:
                Button btn = (Button) view;
                String string = btn.getText().toString();
                if (string.equals(getString(R.string.start_play))) {
                    btn.setText(getString(R.string.stop_play));
                    playInModeStream();
//                    playInModeStatic();
                } else {
                    btn.setText(getString(R.string.start_play));
                    stopPlay();
                }
                break;

            default:
                break;
        }
    }


    public void startRecord() {
        //采集数据需要的缓冲区大小                              采样率             声道数         编码制式,即音频数据的采样精度
        final int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
        //                                        音频来源,默认麦克风    采样率             声道数            采样精度      缓冲区大小
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);
        final byte data[] = new byte[minBufferSize];
        final File file = new File("/sdcard/eg2/test.pcm");
        Logutil.e("ME", "路径=" + file.getAbsolutePath());
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        if (file.exists()) {
            file.delete();
        }
        audioRecord.startRecording();
        isRecording = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(file);
                } catch (Exception e) {

                }
                if (os != null) {
                    while (isRecording) {
                        int read = audioRecord.read(data, 0, minBufferSize);
                        //如果读取音频数据没有出现错误,将数据写入文件
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            try {
                                os.write(data);
                            } catch (Exception e) {

                            }
                        }
                    }
                    try {
                        os.close();
                    } catch (Exception e) {

                    }
                }

            }
        }).start();

    }

    public void stopRecord() {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

    public void playInModeStream() {
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        final int minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_INHZ, channelConfig, AUDIO_FORMAT);
        audioTrack = new AudioTrack(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build(),
                new AudioFormat.Builder().setSampleRate(SAMPLE_RATE_INHZ)
                        .setEncoding(AUDIO_FORMAT)
                        .setChannelMask(channelConfig)
                        .build(),
                minBufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE);
        final File file = new File("/sdcard/eg2/test.pcm");
        try {
            fileInputStream = new FileInputStream(file);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] tempBuffer = new byte[minBufferSize];
                        while (fileInputStream.available() > 0) {
                            int readCount = fileInputStream.read(tempBuffer);
                            if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                                continue;
                            }
                            if (readCount != 0 && readCount != -1) {
                                audioTrack.write(tempBuffer, 0, readCount);
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }).start();
        } catch (Exception e) {

        }
    }

    public void playInModeStatic() {
        //static模式,需要将音频数据一次性write到audiotrack的内部缓冲区
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    InputStream in = getResources().openRawResource(R.raw.ding);
                    try {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        for (int b; (b = in.read()) != -1; ) {
                            out.write(b);
                        }
                        Log.d(TAG, "Got the data");
                        audioData = out.toByteArray();
                    } finally {
                        in.close();
                    }
                } catch (IOException e) {
                    Log.wtf(TAG, "Failed to read", e);
                }
                return null;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            protected void onPostExecute(Void v) {
                Log.i(TAG, "Creating track...audioData.length = " + audioData.length);

                // R.raw.ding铃声文件的相关属性为 22050Hz, 8-bit, Mono
                audioTrack = new AudioTrack(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build(),
                        new AudioFormat.Builder().setSampleRate(22050)
                                .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build(),
                        audioData.length,
                        AudioTrack.MODE_STATIC,
                        AudioManager.AUDIO_SESSION_ID_GENERATE);
                Log.d(TAG, "Writing audio data...");
                audioTrack.write(audioData, 0, audioData.length);
                Log.d(TAG, "Starting playback");
                audioTrack.play();
                Log.d(TAG, "Playing");
            }

        }.execute();


    }

    public void stopPlay() {
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
        }
    }
    //录音
    public static final int AUDIO_CODE = 10;

    public void requestCamerePermission() {
        if (PermissionUtils.hasPermissions(this, permissions)) {
            doNothing();
        } else {
            PermissionUtils.requestPermissions(this, AUDIO_CODE, permissions);
        }
    }

    @AfterPermissionGranted(AUDIO_CODE)
    private void doNothing() {
        Logutil.e("啥事不干");
    }

    //录音

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        if (PermissionUtils.somePermissionsPermanentlyDenied(this, perms)) {
            showToast(PermissionUtils.notifyMessage(perms));
        }

    }
    //6.0权限的玩意，别动，动了会出差
}
