package com.zj.public_lib.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 *  @项目名：  Customer2 
 *  @包名：    com.zhongjia.kwzo.util
 *  @创建者:   huang
 *  @创建时间:  2016/11/13 13:15
 *  @描述：    这个判断是否有网路工具类
 */
public class NetUtils {
    /**
     * 检测当的网络状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}
