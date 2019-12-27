package com.zj.public_lib.ui;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zj.public_lib.R;
import com.zj.public_lib.utils.NullEvent;
import com.zj.public_lib.view.HeaderLayout;
import com.zj.public_lib.view.LoadingDialog;

import de.greenrobot.event.EventBus;

public abstract class BaseFragment extends android.support.v4.app.Fragment {

    private int page = 1;
    private Toast toast;
    private LoadingDialog loadingDialog;
    public FragmentActivity myActivity;
    private View layoutView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(getLayoutId(), container, false);
        myActivity = getActivity();
        loadingDialog = new LoadingDialog(myActivity);
        loadingDialog.setCancelable(true);
        return layoutView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(layoutView);
        initData(savedInstanceState);
    }

    protected abstract int getLayoutId();

    protected abstract void initView(View view);

    protected abstract void initData(Bundle savedInstanceState);

    protected void showProgressBar(boolean show) {
        showProgressBar(show, "");
    }

    protected void setProgressBarProgress() {
        if (loadingDialog == null) {
            return;
        }
    }

    protected void showProgressBar(boolean show, String message) {
        if (loadingDialog == null) {
            return;
        }
        if (show) {
            loadingDialog.show();
        } else {
            loadingDialog.hide();
        }
    }

    protected void showProgressBar(int messageId) {
        String message = getString(messageId);
        showProgressBar(true, message);
    }

    public void onEventMainThread(NullEvent event) {
    }

    public void showToast(final String msg) {
        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == toast) {
                    toast = Toast.makeText(myActivity, msg, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                } else {
                    toast.setText(msg);
                }

                toast.show();
            }
        });

    }

    public void showToast(int msg) {
        if (null == toast) {
            toast = Toast.makeText(myActivity,
                    getResources().getString(msg), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(getResources().getString(msg));
        }
        toast.show();
    }



    /**
     * Hide the toast, if any.
     */
    private void hideToast() {
        if (null != toast) {
            toast.cancel();
        }
    }

    public void onPause() {
        super.onPause();
        hideToast();
    }


    /**
     * 公用的Header布局
     */
    public HeaderLayout mHeaderLayout;

    /**
     * 初始化标题栏-带左右按钮
     */
    public void initTitleLeftAndRight(String titleName, String rightTitle, HeaderLayout.onRightClickListener listener) {
        mHeaderLayout = (HeaderLayout) layoutView.findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleLeft(titleName, R.drawable.base_action_bar_back_bg_selector, new OnLeftButtonClickListener());
        mHeaderLayout.setTitleLeftAndRight(titleName, rightTitle, listener);
    }

    /**
     * 只有左边按钮和Title
     */
    public void initTitleLeft(String titleName) {
        mHeaderLayout = (HeaderLayout) layoutView.findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_LIFT_IMAGEBUTTON);
        mHeaderLayout.setTitleLeft(titleName, R.drawable.base_action_bar_back_bg_selector, new OnLeftButtonClickListener());
    }

    // 左边按钮的点击事件 (返回按钮)
    public class OnLeftButtonClickListener implements HeaderLayout.onLeftClickListener {
        public void onClick() {
            ActivityCompat.finishAfterTransition(myActivity);
        }
    }
    /**
     * 只有title initTopBarLayoutByTitle
     */
    public void initTopBarForOnlyTitle(String titleName) {
        mHeaderLayout = (HeaderLayout) layoutView.findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle(titleName);
    }


    public void onDestroy() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
        super.onDestroy();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
        }
    }
}
