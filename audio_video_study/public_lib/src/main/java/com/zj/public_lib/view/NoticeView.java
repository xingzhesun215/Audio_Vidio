package com.zj.public_lib.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;


import com.zj.public_lib.R;

import java.util.ArrayList;


/**
 * 滚动的公告栏
 */

public class NoticeView extends LinearLayout {
    private final Context mContext;
    private ViewFlipper scrollTitle_vf;
    private View scrollTitleView;
    private ArrayList<NoticeBean> notice_strs;
    private ClickListener clickListener;
    private int textColor;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    break;

                case -1:
                    break;
            }
        }
    };

    /**
     * @param context
     */
    public NoticeView(Context context) {
        this(context, null);
    }

    public NoticeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    /**
     */
    public void bindNotices(ArrayList<NoticeBean> notice_strs) {
        this.notice_strs = notice_strs;
        initNoticeLayout();
        scrollTitle_vf.removeAllViews();
        int i = 0;
        while (i < notice_strs.size()) {
            final NoticeBean bean = notice_strs.get(i);

            TextView textView = new TextView(mContext);
            textView.setLines(1);
            textView.setTextColor(Color.parseColor("#666666"));
            if (textColor != 0) {
                textView.setTextColor(textColor);
            }
            textView.setTextSize(12);
            textView.setText(bean.getMessage());
            LayoutParams lp = new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.click(bean.getTag());
                    }
                }
            });
            scrollTitle_vf.addView(textView, lp);
            i++;
        }
        Message msg = new Message();
        msg.what = 1;
        mHandler.sendMessageDelayed(msg, 2000);
    }

    /**
     */
    public void initNoticeLayout() {
        scrollTitleView = LayoutInflater.from(mContext).inflate(
                R.layout.noticeview_layout, null);
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(scrollTitleView, layoutParams);
        scrollTitle_vf = (ViewFlipper) scrollTitleView
                .findViewById(R.id.scrollTitle_vf);
        scrollTitle_vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.buttom_in));
        scrollTitle_vf.startFlipping();
        View v = scrollTitle_vf.getCurrentView();

    }

    /**
     * 设置点击事件
     *
     * @param clickListener
     */
    public void setOnNoticeClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;

    }

    /**
     * 设置字体颜色
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public interface ClickListener {
        void click(String tag);
    }
}
