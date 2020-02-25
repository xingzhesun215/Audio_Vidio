package com.sun.eg11.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.sun.eg11.R;
import com.sun.eg11.bean.SelectVideo;
import com.zj.public_lib.ui.BaseArrayListAdapter;

import java.util.ArrayList;

/**
 * 本地视频列表
 */
public class VideoAdapter extends BaseArrayListAdapter {
    Context context;

    ArrayList<SelectVideo> beans;

    public VideoAdapter(Context context, ArrayList<SelectVideo> beans) {
        super(context, beans);
        this.context = context;
        this.beans = beans;
    }


    @Override
    public int getContentView() {
        return R.layout.item_video_select;
    }

    @Override
    public void onInitView(View view, int position) {
        SelectVideo bean = beans.get(position);
        TextView tv_filepath = (TextView) get(view, R.id.tv_filepath);
        tv_filepath.setText(bean.getFilePath());
    }

}
