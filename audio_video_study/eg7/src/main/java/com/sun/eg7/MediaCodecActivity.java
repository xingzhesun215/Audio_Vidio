package com.sun.eg7;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zj.public_lib.utils.Logutil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaCodecActivity extends Activity {

    private static final String TAG = "MediaCodec";

    TextView textView;
    Button recoderBtn;
    Button playBtn;

    private ExecutorService mExecutorService;
    private AudioTrack audioTrack;
    private AudioRecord audioRecord;
    private MediaExtractor mMediaExtractor;

    private int MAX_BUFFER_SIZE = 8192;

    private MediaCodec mAudioEncoder;
    private ByteBuffer[] encodeInputBuffers;
    private ByteBuffer[] encodeOutputBuffers;
    private MediaCodec.BufferInfo mAudioEncoderBufferInfo;

    private MediaCodec mAudioDecoder;

    String mFilePath;
    File mAudioFile;

    long start;
    long end;

    byte[] mBuffer;
    FileOutputStream mFileOutPutStream;
    private BufferedOutputStream mAudioBos;


    private volatile boolean mIsRecording;
    private volatile boolean mIsPlaying;

    private ArrayBlockingQueue<byte[]> queue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediacodec);
        init();
    }

    private void init() {
        textView = findViewById(R.id.textViewStream);
        mBuffer = new byte[2048];
        mExecutorService = Executors.newFixedThreadPool(2);
        queue = new ArrayBlockingQueue<byte[]>(10);
        recoderBtn = findViewById(R.id.buttonStream);
        playBtn = findViewById(R.id.button4);
    }

    public void log(String message) {
        Logutil.e(message);
        textView.append(message + "\n");
    }

    public void start(View view) {

        if (mIsRecording) {
            mIsRecording = false;
        } else {
            log("开始录音");
            recoderBtn.setText("结束录音");
            initAudioEncoder();
            initAudioRecord();
            mIsRecording = true;
            log("开线程录音");
            mExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    startRecorder();
                }
            });
            log("开线程编码");
            mExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    encodePcm();
                }
            });
        }
    }

    /**
     * 初始化编码器
     */
    private void initAudioEncoder() {
        log("初始化编码器");
        try {
            mAudioEncoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            MediaFormat format = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, 44100, 1);
            format.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, MAX_BUFFER_SIZE);
            mAudioEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (Exception e) {

        }
        mAudioEncoder.start();

        encodeInputBuffers = mAudioEncoder.getInputBuffers();
        encodeOutputBuffers = mAudioEncoder.getOutputBuffers();
        mAudioEncoderBufferInfo = new MediaCodec.BufferInfo();

    }

    /**
     * 初始化audioRecord
     */
    private void initAudioRecord() {
        log("初始化audioRecord");
        int audioSource = MediaRecorder.AudioSource.MIC;
        int sampleRate = 44100;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        int minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, Math.max(minBufferSize, 2048));
    }

    /**
     * 录音线程做的录音操作
     */
    private void startRecorder() {
        try {
            mFilePath = "/sdcard/eg7/" + System.currentTimeMillis() + ".aac";
            mAudioFile = new File(mFilePath);
            if (!mAudioFile.getParentFile().exists()) {
                mAudioFile.getParentFile().mkdirs();
            }
            mAudioFile.createNewFile();
            mFileOutPutStream = new FileOutputStream(mAudioFile);
            mAudioBos = new BufferedOutputStream(mFileOutPutStream, 200 * 1024);
            audioRecord.startRecording();
            start = System.currentTimeMillis();
            while (mIsRecording) {
                int read = audioRecord.read(mBuffer, 0, 1024);
                if (read > 0) {
                    byte[] audio = new byte[read];
                    System.arraycopy(mBuffer, 0, audio, 0, read);
                    putPcmData(audio);
                }
            }
        } catch (Exception e) {

        } finally {
            if (audioRecord != null) {
                audioRecord.release();
                audioRecord = null;
            }
        }
    }

    /**
     * 将PCM数据存入队列
     *
     * @param pcmChunk PCM数据块
     */
    public void putPcmData(byte[] pcmChunk) {
        Logutil.e("pcm数据放入队列");
        try {
            queue.put(pcmChunk);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在Container中队列取出PCM数据
     *
     * @return PCM数据块
     */
    public byte[] getPcmData() {
        Logutil.e("获取pcm数据从队列");
        try {
            if (queue.isEmpty()) {
                return null;
            }
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void encodePcm() {
        int inputIndex;
        ByteBuffer inputBuffer;
        int outputIndex;
        ByteBuffer outputBuffer;

        byte[] chunkAudio;
        int outBitSize;
        int outPacketSize;

        byte[] chunkPcm;

        while (mIsRecording || !queue.isEmpty()) {
            chunkPcm = getPcmData();
            if (chunkPcm == null) {
                continue;
            }
            inputIndex = mAudioEncoder.dequeueInputBuffer(-1);
            if (inputIndex > 0) {
                inputBuffer = encodeInputBuffers[inputIndex];
                inputBuffer.clear();
                inputBuffer.limit(chunkPcm.length);
                inputBuffer.put(chunkPcm);
                mAudioEncoder.queueInputBuffer(inputIndex, 0, chunkPcm.length, 0, 0);
            }
            outputIndex = mAudioEncoder.dequeueOutputBuffer(mAudioEncoderBufferInfo, 10000);
            while (outputIndex >= 0) {
                outBitSize = mAudioEncoderBufferInfo.size;
                outPacketSize = outBitSize + 7;//7为adts头部的大小
                outputBuffer = encodeOutputBuffers[outputIndex];//拿到输出的buffer
                outputBuffer.position(mAudioEncoderBufferInfo.offset);
                outputBuffer.limit(mAudioEncoderBufferInfo.offset + outBitSize);
                chunkAudio = new byte[outPacketSize];
                addADTStoPacket(44100, chunkAudio, outPacketSize);//添加ADTS
                outputBuffer.get(chunkAudio, 7, outBitSize);//将编码得到的aac数据放到byte[]中,偏移7
                outputBuffer.position(mAudioEncoderBufferInfo.offset);
                try {
                    mAudioBos.write(chunkAudio, 0, chunkAudio.length);
                } catch (Exception e) {

                }
                mAudioEncoder.releaseOutputBuffer(outputIndex, false);
                outputIndex = mAudioEncoder.dequeueOutputBuffer(mAudioEncoderBufferInfo, 10000);
            }
        }
        stopRecorder();
    }


    /**
     * 添加ADTS头
     *
     * @param packet
     * @param packetLen
     */
    public static void addADTStoPacket(int sampleRateType, byte[] packet, int packetLen) {
        int profile = 2; // AAC LC
        int chanCfg = 2; // CPE

        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (sampleRateType << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }

    private boolean stopRecorder() {
        try {
            if (mAudioBos != null) {
                mAudioBos.flush();
            }
        } catch (Exception e) {

        } finally {
            if (mAudioBos != null) {
                try {
                    mAudioBos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    mAudioBos = null;
                }
            }
        }
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (mAudioEncoder != null) {
            mAudioEncoder.stop();
            mAudioEncoder.release();
            mAudioEncoder = null;
        }
        end = System.currentTimeMillis();

        final int second = (int) ((end - start) / 1000);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recoderBtn.setText("开始录音");
                if (second > 3) {
                    log("录音成功,时间=" + second + "秒");
                } else {
                    log("录音失败,时间少于3秒");
                    mAudioFile.deleteOnExit();
                }
            }
        });
        return true;
    }

    public void streamPlay(View view) {
        if (mAudioFile == null) {
            log("文件为空,请先录音");
            return;
        }
        if (mIsPlaying) {
            mIsPlaying = false;
        } else {
            log("开始播放");
            playBtn.setText("结束播放");
            mIsPlaying = true;
            initAudioTrack();
            initAudioDecoder();
            mExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    log("开始解码并播放");
                    decodeAndPlay();
                }
            });
        }
    }

    private void decodeAndPlay() {
        boolean isFinish = false;
        MediaCodec.BufferInfo decodeBufferInfo = new MediaCodec.BufferInfo();

        while (!isFinish && mIsPlaying) {
            int inputIndex = mAudioDecoder.dequeueInputBuffer(10000);
            if (inputIndex < 0) {
                isFinish = true;
            }
            ByteBuffer inputBuffer = mAudioDecoder.getInputBuffer(inputIndex);
            inputBuffer.clear();
            int sampleSize = mMediaExtractor.readSampleData(inputBuffer, 0);
            if (sampleSize > 0) {
                mAudioDecoder.queueInputBuffer(inputIndex, 0, sampleSize, 0, 0);
                mMediaExtractor.advance();
            } else {
                isFinish = true;
            }
            int outputIndex = mAudioDecoder.dequeueOutputBuffer(decodeBufferInfo, 10000);
            ByteBuffer outputBuffer;
            byte[] chunkPCM;
            //每次解码完成的数据不一定能一次性吐出,所以用while,保证吐出所有数据
            while (outputIndex >= 0) {
                outputBuffer = mAudioDecoder.getOutputBuffer(outputIndex);
                chunkPCM = new byte[decodeBufferInfo.size];
                outputBuffer.get(chunkPCM);
                outputBuffer.clear();
                audioTrack.write(chunkPCM, 0, decodeBufferInfo.size);
                mAudioDecoder.releaseOutputBuffer(outputIndex, false);
                outputIndex = mAudioDecoder.dequeueOutputBuffer(decodeBufferInfo, 10000);

            }
        }
        stopPlay();
    }

    private void stopPlay() {
        mIsPlaying = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playBtn.setText("播放");
                log("播放结束");
            }
        });
        if (mAudioDecoder != null) {
            mAudioDecoder.stop();
            mAudioDecoder.release();
            mAudioDecoder = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecorder();
        stopPlay();
        mExecutorService.shutdownNow();
    }

    private void initAudioTrack() {
        int streamType = AudioManager.STREAM_MUSIC;
        int sampleRate = 44100;
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int mode = AudioTrack.MODE_STREAM;

        int minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        audioTrack = new AudioTrack(streamType, sampleRate, channelConfig, audioFormat, Math.max(minBufferSize, 2048), mode);
        audioTrack.play();
    }

    private void initAudioDecoder() {
        try {
            mMediaExtractor = new MediaExtractor();
            mMediaExtractor.setDataSource(mFilePath);

            MediaFormat format = mMediaExtractor.getTrackFormat(0);
            String mime = format.getString(MediaFormat.KEY_MIME);

            if (mime.startsWith("audio")) {

                mMediaExtractor.selectTrack(0);

                format.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm");
                format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
                format.setInteger(MediaFormat.KEY_SAMPLE_RATE, 0);
                format.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
                format.setInteger(MediaFormat.KEY_IS_ADTS, 1);
                format.setInteger(MediaFormat.KEY_AAC_PROFILE, 0);

                mAudioDecoder = MediaCodec.createDecoderByType(mime);
                mAudioDecoder.configure(format, null, null, 0);

            } else {
                return;
            }
        } catch (Exception e) {

        }

        if (mAudioDecoder == null) {
            log("mAudioDecoder=null");
            return;
        }
        mAudioDecoder.start();
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MediaCodecActivity.class));
    }
}
