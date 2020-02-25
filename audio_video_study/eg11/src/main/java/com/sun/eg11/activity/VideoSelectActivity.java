package com.sun.eg11.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.sun.eg11.R;
import com.sun.eg11.adapter.VideoAdapter;
import com.sun.eg11.bean.SelectVideo;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by cj on 2017/10/16.
 * desc: local video select activity
 */

public class VideoSelectActivity extends BaseActivity {
    public static final int TYPE_SHOW_DIALOG = 1;
    public static final int TYPE_BACK_PATH = 2;

    private ArrayList<SelectVideo> beans = new ArrayList<>();

    ImageView ivClose;
    ListView gridview;
    public static final String PROJECT_VIDEO = MediaStore.MediaColumns._ID;
    private VideoAdapter mVideoAdapter;
    private int pageType = TYPE_SHOW_DIALOG;

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, VideoSelectActivity.class);
        intent.putExtra("type", TYPE_SHOW_DIALOG);
        context.startActivity(intent);
    }

    public static void openActivityForResult(Activity context, int requestCodde) {
        Intent intent = new Intent(context, VideoSelectActivity.class);
        intent.putExtra("type", TYPE_BACK_PATH);
        context.startActivityForResult(intent, requestCodde);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_select);
        initView();
        initData();
    }

    private void initView() {
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gridview = (ListView) findViewById(R.id.gridview_media_video);
    }

    private void initData() {
        pageType = getIntent().getIntExtra("type", TYPE_SHOW_DIALOG);


        File allFiles = new File("sdcard/eg11/record/");
        if (allFiles != null && allFiles.isDirectory() && allFiles.listFiles().length > 0) {
            File[] files = allFiles.listFiles();
            for (int i = 0; i < files.length; i++) {
                SelectVideo bean = new SelectVideo(files[i].getAbsolutePath(), files[i].getName());
                beans.add(bean);
            }
        }
        mVideoAdapter = new VideoAdapter(this, beans);
        gridview.setAdapter(mVideoAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                intent.putExtra("path", beans.get(position).getFilePath());
                setResult(0, intent);
                finish();
                return;
            }
        });
    }

    @Override
    protected void onDestroy() {
        getLoaderManager().destroyLoader(0);
        Glide.get(this).clearMemory();
        super.onDestroy();
    }

}
