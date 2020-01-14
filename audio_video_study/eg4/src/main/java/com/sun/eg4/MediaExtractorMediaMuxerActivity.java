package com.sun.eg4;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.zj.public_lib.utils.Logutil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaExtractorMediaMuxerActivity extends AppCompatActivity {

    /**
     * MediaExtractor主要用于提取音视频相关信息，分离音视频。
     * MediaMuxer主要复用和解复用音视频。
     * <p>
     * <p>
     * <p>
     * MediaExtractor使用一般步骤
     * 1.//设置数据源
     * setDataSource
     * 2.//分离轨道
     * getTrackCount，getTrackFormat
     * 3.//选择轨道
     * selectTrack，unselectTrack
     * 4.//读取数据
     * readSampleData
     * 5.//下一帧
     * advance
     * 6.//释放
     * release
     * <p>
     * <p>
     * <p>
     * MediaMuxer使用一般步骤
     * 1.//添加轨道
     * addTrack
     * 2.写数据
     * writeSampleData
     * 3.释放
     * release
     *
     * @return
     */
    private String MP3PATH = "/sdcard/eg4/abd.mp3";
    private String MP4PATH = "/sdcard/eg4/abd.mp4";
    private String COMPOSITINGPATH = "/sdcard/eg4/compositing.mp4";


    private TextView tv_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaextractor_mediamuxer);
        tv_text = (TextView) this.findViewById(R.id.tv_text);
        new Thread(new Runnable() {
            @Override
            public void run() {
                deMP3();
            }
        }).start();
    }

    public void log(final String message) {
        Logutil.e(message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_text.append(message + "\n");
            }
        });
    }

    //以下
    public void deMP3() {
        MediaExtractor extractor = new MediaExtractor();
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor afd = assetManager.openFd("test.mp4");
            log("asset test.mp4");
            extractor.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (Exception e) {

        }

        int audioIndex = -1;//音频轨道
        int videoIndex = -1;//视频轨道
        MediaFormat audioFormat = null;
        MediaFormat videoFormat = null;
        MediaFormat trackFormat;
        log("开始匹配音视频轨道数");
        int trackcount = extractor.getTrackCount();//轨道数
        log("轨道数=" + trackcount);
        for (int i = 0; i < trackcount; i++) {
            trackFormat = extractor.getTrackFormat(i);
            if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                audioIndex = i;
                audioFormat = trackFormat;
                log("音频通道=" + audioIndex + "  " + trackFormat.getString(MediaFormat.KEY_MIME));
            }
            if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("video/")) {
                videoIndex = i;
                videoFormat = trackFormat;
                log("视频通道=" + videoIndex + "   " + trackFormat.getString(MediaFormat.KEY_MIME));
            }
        }

        File file = new File("/sdcard/eg4/");
        if (!file.exists()) {
            file.mkdirs();
        } else {
            new File(MP3PATH);
            log("分离的音频文件存放目录=" + MP3PATH);
            new File(MP4PATH);
            log("分离的视频文件存放目录=" + MP4PATH);
        }


        doMp3(audioFormat, extractor, audioIndex);
        doMp4(videoFormat, extractor, videoIndex);
        extractor.release();
        composition();
    }


    /**
     * 提取mp3
     *
     * @param trackFormat
     * @param extractor
     * @param audioIndex
     */
    private void doMp3(MediaFormat trackFormat, MediaExtractor extractor, int audioIndex) {
        log("切换到音频通道");
        extractor.selectTrack(audioIndex);
        log("提取mp3");
        MediaMuxer mediaMuxer = null;
        try {
            mediaMuxer = new MediaMuxer(MP3PATH, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            Log.e("IOException", "" + e.toString());
            e.printStackTrace();
        }
        int writeAudioIndex = mediaMuxer.addTrack(trackFormat);
        mediaMuxer.start();

        log("添加轨道,得到轨道所在的index=" + writeAudioIndex + "   信道索引=" + audioIndex);

        ByteBuffer byteBuffer = ByteBuffer.allocate(trackFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE));
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        long stampTime = 0;
        log("声明缓冲区,用于读取一帧的数据存放");

        {
            log("获取相邻帧之间的间隔时间");
            log("读一帧");
            extractor.readSampleData(byteBuffer, 0);
            if (extractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                extractor.advance();
            }
            extractor.readSampleData(byteBuffer, 0);
            long secondTime = extractor.getSampleTime();
            log("跳下一帧");
            extractor.advance();
            extractor.readSampleData(byteBuffer, 0);
            long thirdTime = extractor.getSampleTime();
            stampTime = Math.abs(thirdTime - secondTime);
            log("获取相邻音频帧的间隔时间=" + stampTime);
        }
        extractor.unselectTrack(audioIndex);
        extractor.selectTrack(audioIndex);
        log("重新切换此信道,因为上面跳过了3帧");
        log("开始抽取音频文件");
        while (true) {
            int readSampleSize = extractor.readSampleData(byteBuffer, 0);
            if (readSampleSize < 0) {
                break;
            }
            extractor.advance();
            bufferInfo.size = readSampleSize;
            bufferInfo.flags = extractor.getSampleFlags();
            bufferInfo.offset = 0;
            bufferInfo.presentationTimeUs += stampTime;
            mediaMuxer.writeSampleData(writeAudioIndex, byteBuffer, bufferInfo);
        }
        mediaMuxer.stop();
        mediaMuxer.release();
        extractor.unselectTrack(audioIndex);

    }

    /**
     * 提取mp4
     *
     * @param trackFormat
     * @param extractor
     * @param videoIndex
     */
    private void doMp4(MediaFormat trackFormat, MediaExtractor extractor, int videoIndex) {
        extractor.selectTrack(videoIndex);
        log("切换成视频通道");
        MediaMuxer mediaMuxer = null;
        try {
            mediaMuxer = new MediaMuxer(MP4PATH, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (Exception e) {
        }
        int writeVideoIndex = mediaMuxer.addTrack(trackFormat);
        mediaMuxer.start();
        log("添加轨道,得到轨道所在的writeVideoIndex=" + writeVideoIndex + "   信道索引videoIndex=" + videoIndex);
        ByteBuffer byteBuffer = ByteBuffer.allocate(trackFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE));
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        long stamTime = 0;
        {
            extractor.readSampleData(byteBuffer, 0);
            if (extractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                extractor.advance();
            }
            extractor.readSampleData(byteBuffer, 0);
            long secondTime = extractor.getSampleTime();
            extractor.advance();
            extractor.readSampleData(byteBuffer, 0);
            long thirdTime = extractor.getSampleTime();
            stamTime = Math.abs(thirdTime - secondTime);
            log("视频相邻帧的间隔时间=" + stamTime);
        }
        extractor.unselectTrack(videoIndex);
        extractor.selectTrack(videoIndex);

        while (true) {
            int readSampleSize = extractor.readSampleData(byteBuffer, 0);
            if (readSampleSize < 0) {
                break;
            }
            extractor.advance();
            bufferInfo.size = readSampleSize;
            bufferInfo.flags = extractor.getSampleFlags();
            bufferInfo.offset = 0;
            bufferInfo.presentationTimeUs = +stamTime;
            mediaMuxer.writeSampleData(writeVideoIndex, byteBuffer, bufferInfo);
        }
        mediaMuxer.stop();
        mediaMuxer.release();
        extractor.unselectTrack(videoIndex);
    }

    /**
     * 合并视频
     */
    private void composition() {
        log("开始合成视频");
        MediaExtractor videoExtractor = new MediaExtractor();

        try {
            videoExtractor.setDataSource(MP4PATH);
            MediaFormat videoFormat = null;
            int videoTrackIndex = -1;
            int videoTrackCount = videoExtractor.getTrackCount();
            for (int i = 0; i < videoTrackCount; i++) {
                videoFormat = videoExtractor.getTrackFormat(i);
                String mimeType = videoFormat.getString(MediaFormat.KEY_MIME);
                if (mimeType.startsWith("video/")) {
                    videoTrackIndex = i;
                    break;
                }
            }
            MediaExtractor audioExtractor = new MediaExtractor();
            audioExtractor.setDataSource(MP3PATH);
            MediaFormat audioFormat = null;
            int audioTrackIndex = -1;
            int audioTrackCount = audioExtractor.getTrackCount();
            for (int i = 0; i < audioTrackCount; i++) {
                audioFormat = audioExtractor.getTrackFormat(i);
                String mimeType = audioFormat.getString(MediaFormat.KEY_MIME);
                if (mimeType.startsWith("audio/")) {
                    audioTrackIndex = i;
                    break;
                }
            }
            videoExtractor.selectTrack(videoTrackIndex);
            audioExtractor.selectTrack(audioTrackIndex);

            MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
            MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();

            MediaMuxer mediaMuxer = new MediaMuxer(COMPOSITINGPATH, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int writeVideoTrackIndex = mediaMuxer.addTrack(videoFormat);
            int writeAudioTrackIndex = mediaMuxer.addTrack(audioFormat);
            mediaMuxer.start();
            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            long sampTime = 0;
            {
                videoExtractor.readSampleData(byteBuffer, 0);
                if (videoExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    videoExtractor.advance();
                }
                videoExtractor.readSampleData(byteBuffer, 0);
                long secondTime = videoExtractor.getSampleTime();
                videoExtractor.advance();
                videoExtractor.readSampleData(byteBuffer, 0);
                long thirdTime = videoExtractor.getSampleTime();
                sampTime = Math.abs(thirdTime - secondTime);
                log("相邻视频帧的时间=" + sampTime);
            }
            videoExtractor.unselectTrack(videoTrackIndex);
            videoExtractor.selectTrack(videoTrackIndex);
            log("开始写入视频");
            while (true) {
                int readVideoSampleSize = videoExtractor.readSampleData(byteBuffer, 0);
                if (readVideoSampleSize < 0) {
                    break;
                }
                videoBufferInfo.size = readVideoSampleSize;
                videoBufferInfo.presentationTimeUs += sampTime;
                videoBufferInfo.offset = 0;
                videoBufferInfo.flags = videoExtractor.getSampleFlags();
                mediaMuxer.writeSampleData(writeVideoTrackIndex, byteBuffer, videoBufferInfo);
                videoExtractor.advance();
            }
            log("结束写入视频");
            log("开始写入音频");

            while (true) {
                int readAudioSampleSize = audioExtractor.readSampleData(byteBuffer, 0);
                if (readAudioSampleSize < 0) {
                    break;
                }
                audioBufferInfo.size = readAudioSampleSize;
                audioBufferInfo.presentationTimeUs += sampTime;
                audioBufferInfo.offset = 0;
                audioBufferInfo.flags = audioExtractor.getSampleFlags();
                mediaMuxer.writeSampleData(writeAudioTrackIndex, byteBuffer, audioBufferInfo);
                audioExtractor.advance();
            }
            mediaMuxer.stop();
            mediaMuxer.release();
            videoExtractor.release();
            audioExtractor.release();
            log("结束写入音频");
            log("合并完成");

        } catch (Exception e) {

        }
    }

}
