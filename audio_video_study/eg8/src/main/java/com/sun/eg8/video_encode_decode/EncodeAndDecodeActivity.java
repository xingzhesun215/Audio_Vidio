package com.sun.eg8.video_encode_decode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sun.eg8.R;
import com.sun.eg8.video_encode_decode.decode.DecodeH264ToSurfaceViewActivity;
import com.sun.eg8.video_encode_decode.decode.DecodeH264ToTextureViewActivity;
import com.sun.eg8.video_encode_decode.encode.camera.EncodeYUVToH264Activity;
import com.sun.eg8.video_encode_decode.encode.camera2.EncodeYUVToH264Activity2;


public class EncodeAndDecodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode_and_decode);
    }

    public void startEncodeCamera(View view) {
        Intent intent = new Intent(this, EncodeYUVToH264Activity.class);
        startActivity(intent);
    }

    public void startEncodeCamera2(View view) {
        Intent intent = new Intent(this, EncodeYUVToH264Activity2.class);
        startActivity(intent);
    }

    public void decodeVideo(View view) {
        Intent intent = new Intent(this, DecodeH264ToSurfaceViewActivity.class);
        startActivity(intent);
    }

    public void decodeVideoToTextureView(View view) {
        Intent intent = new Intent(this, DecodeH264ToTextureViewActivity.class);
        startActivity(intent);
    }


}
