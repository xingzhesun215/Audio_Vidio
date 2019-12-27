package com.zj.public_lib.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zj.public_lib.R;
import com.zj.public_lib.utils.PublicUtil;
import com.zj.public_lib.utils.theme.ThemeUtils;

/**
 * 自定义头部布局
 *
 * @author smile
 * @ClassName: HeaderLayout
 * @Description:
 */
public class HeaderLayout extends LinearLayout {
    private LayoutInflater mInflater;
    private View mHeader;
    private LinearLayout mLayoutLeftContainer;
    private LinearLayout mLayoutRightContainer;
    private TextView mHtvSubTitle;
    private TextView header_tv_right;
    private onRightClickListener mRightClickListener;

    private LinearLayout mLayoutLeftImageButtonLayout;
    private onLeftClickListener mLeftClickListener;
    public static RelativeLayout rl_actionbar;
    private ImageView mLeftImageButton;

    public enum HeaderStyle {
        DEFAULT_TITLE, TITLE_LIFT_IMAGEBUTTON, TITLE_DOUBLE_IMAGEBUTTON;
    }

    public HeaderLayout(Context context) {
        super(context);
        init(context);
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        mInflater = LayoutInflater.from(context);
        mHeader = mInflater.inflate(R.layout.common_header, null);
        rl_actionbar = (RelativeLayout) mHeader.findViewById(R.id.rl_actionbar);
        if (PublicUtil.curActivity != null) {
            ThemeUtils.setChageTheme(PublicUtil.curActivity, rl_actionbar);
        }
        addView(mHeader);
        initViews();
    }

    public void initViews() {
        mLayoutLeftContainer = (LinearLayout) findViewByHeaderId(R.id.header_layout_leftview_container);
        mLayoutRightContainer = (LinearLayout) findViewByHeaderId(R.id.header_layout_rightview_container);
        mHtvSubTitle = (TextView) findViewByHeaderId(R.id.header_htv_subtitle);
        header_tv_right = (TextView) findViewByHeaderId(R.id.header_tv_right);

    }

    public void setRight(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        header_tv_right.setText(text);
    }


    public View findViewByHeaderId(int id) {
        return mHeader.findViewById(id);
    }

    public void init(HeaderStyle hStyle) {
        switch (hStyle) {
            case DEFAULT_TITLE:
                defaultTitle();
                break;
            case TITLE_LIFT_IMAGEBUTTON:
                defaultTitle();
                titleLeftImageButton();
                break;

            case TITLE_DOUBLE_IMAGEBUTTON:
                defaultTitle();
                titleLeftImageButton();
                titleRightImageButton();
                break;
        }
    }

    // 默认文字标题
    private void defaultTitle() {
        mLayoutLeftContainer.removeAllViews();
    }


    private void titleLeftImageButton() {
        View mleftImageButtonView = mInflater.inflate(R.layout.common_header_button, null);
        mLayoutLeftContainer.addView(mleftImageButtonView);
        mLayoutLeftImageButtonLayout = (LinearLayout) mleftImageButtonView.findViewById(R.id.header_layout_imagebuttonlayout);
        RelativeLayout rl = (RelativeLayout) mleftImageButtonView.findViewById(R.id.rl);
        mLeftImageButton = (ImageView) mleftImageButtonView.findViewById(R.id.header_ib_imagebutton);
        rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mLeftClickListener != null) {
                    mLeftClickListener.onClick();
                }
            }
        });
    }

    // 右侧自定义按钮
    private void titleRightImageButton() {
        header_tv_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mRightClickListener != null) {
                    mRightClickListener.onClick();
                }
            }
        });
    }

    /**
     * 获取右边按钮
     */
    public TextView getRightImageButton() {
        if (header_tv_right != null) {
            return header_tv_right;
        }
        return null;
    }

    public void setDefaultTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            mHtvSubTitle.setText(title);
        } else {
            mHtvSubTitle.setVisibility(View.GONE);
        }
    }


    public void setTitleLeftAndRight(CharSequence title, CharSequence rightTiele,
                                     onRightClickListener onRightClickListener) {
        setDefaultTitle(title);
        mLayoutRightContainer.setVisibility(View.VISIBLE);
        if (header_tv_right != null && !TextUtils.isEmpty(rightTiele)) {
            header_tv_right.setText(rightTiele);
            setOnRightClickListener(onRightClickListener);
        }
    }


    public void setTitleLeft(CharSequence title, int leftID, onLeftClickListener listener) {
        mLayoutRightContainer.setVisibility(View.GONE);
        setDefaultTitle(title);
        if (listener != null) {
            mLeftImageButton.setImageResource(leftID);
            setOnLeftClickListener(listener);
        }
    }

    public void setOnRightClickListener(
            onRightClickListener listener) {
        mRightClickListener = listener;
    }

    public interface onRightClickListener {
        void onClick();
    }

    public void setOnLeftClickListener(
            onLeftClickListener listener) {
        mLeftClickListener = listener;
    }

    public interface onLeftClickListener {
        void onClick();
    }

}