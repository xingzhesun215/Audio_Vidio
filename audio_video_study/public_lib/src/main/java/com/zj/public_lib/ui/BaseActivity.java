package com.zj.public_lib.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.zj.public_lib.R;
import com.zj.public_lib.SystemBarTintManager;
import com.zj.public_lib.permission.PermissionUtils;
import com.zj.public_lib.utils.NullEvent;
import com.zj.public_lib.utils.PublicUtil;
import com.zj.public_lib.view.HeaderLayout;
import com.zj.public_lib.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public abstract class BaseActivity extends Activity {
    private Activity context;
    private Toast toast;
    private LoadingDialog loadingDialog;
    public View layoutView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        layoutView = LayoutInflater.from(this).inflate(getLayoutId(), null);
        setTranslucentStatus();
        setContentView(layoutView);
        context = this;
        loadingDialog = new LoadingDialog(this);
        initView();
        initData(savedInstanceState);
    }


    /**
     * 设置状态栏背景状态
     */
    @TargetApi(19)
    private void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.transparent);//状态栏无背景
    }

    public void onEventMainThread(NullEvent event) {

    }


    /**
     * 传布局id
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化view
     */
    protected abstract void initView();

    /**
     * 初始化数据
     *
     * @param bundle
     */
    protected abstract void initData(Bundle bundle);

    /**
     * 等待圈圈
     *
     * @param isShow
     */
    public void showProgressBar(boolean isShow) {
        if (isShow) {
            loadingDialog.show();
        } else {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
        }
    }

    public void hideProgressBar() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    public void loading_again(View view) {

    }

    public void backClick(View view) {
        PublicUtil.hideSoftInput(this);
        finish();
    }

    /**
     * 公用的Header布局
     */
    public HeaderLayout mHeaderLayout;

    /**
     * 初始化标题栏-带左右按钮
     */
    public void initTitleLeftAndRight(String titleName, String rightTitle, HeaderLayout.onRightClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleLeft(titleName, R.drawable.base_action_bar_back_bg_selector, new OnLeftButtonClickListener());
        mHeaderLayout.setTitleLeftAndRight(titleName, rightTitle, listener);
    }

    /**
     * 只有title initTopBarLayoutByTitle
     */
    public void initTopBarForOnlyTitle(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle(titleName);
    }

    /**
     * 只有左边按钮和Title
     */
    public void initTitleLeft(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_LIFT_IMAGEBUTTON);
        mHeaderLayout.setTitleLeft(titleName, R.drawable.base_action_bar_back_bg_selector, new OnLeftButtonClickListener());
    }

    public void setRightText(String rightText) {
        mHeaderLayout.setRight(rightText);
    }

    public TextView getRightView() {
        return mHeaderLayout.getRightImageButton();
    }

    // 左边按钮的点击事件 (返回按钮)
    public class OnLeftButtonClickListener implements HeaderLayout.onLeftClickListener {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick() {
            ActivityCompat.finishAfterTransition(context);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * 吐司
     *
     * @param msg
     */
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(msg)) {
                    return;
                }
                if (null == toast) {
                    toast = Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_LONG);
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
            toast = Toast.makeText(this, getResources().getString(msg),
                    Toast.LENGTH_LONG);
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


    @Override
    public void onResume() {
        super.onResume();
        PublicUtil.curActivity = this;
    }

    @Override
    public void onPause() {
        super.onPause();
        hideToast();
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
        }

        if (mListener != null) {
            mListener.clear();
        }
    }


    private ArrayList<ThemeStateListener> mListener = new ArrayList<>();

    public void changeTheme(int currentTheme) {
        for (final ThemeStateListener listener : mListener) {
            if (listener != null) {
                listener.changeTheme(currentTheme);
            }
        }
    }

    /**
     * @param outState 取消保存状态
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * @param savedInstanceState 取消保存状态
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


}
