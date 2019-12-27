package com.zj.public_lib.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.zj.public_lib.R;

import java.io.File;


/**
 * Created by Administrator on 2016-07-20.
 */
public class ImageLoader {

    private static int measuredHeight;
    private static int measuredWidth;

    /**
     * 图像显示
     *
     * @param context
     * @param picUrl
     * @param image_iv
     */
    public static void display(Context context, String picUrl, ImageView image_iv) {
        if (TextUtils.isEmpty(picUrl)) {
            image_iv.setImageResource(R.drawable.default_image);
            return;
        }
        Picasso.with(context).load(picUrl.replace("\\", "/"))
                .placeholder(R.drawable.pic_loading)
                .error(R.drawable.default_image).into(image_iv);
        Logutil.e("pic_url=" + picUrl);
    }

    public static void displayWithDefaultImage(Context context, String picUrl, ImageView image_iv, int defaultPic) {
        if (TextUtils.isEmpty(picUrl)) {
            image_iv.setImageResource(defaultPic);
            return;
        }
        Picasso.with(context).load(picUrl.replace("\\", "/"))
                .placeholder(R.drawable.pic_loading)
                .error(defaultPic).into(image_iv);
        Logutil.e("pic_url=" + picUrl);
    }

    /**
     * 图像显示
     *
     * @param context
     * @param picUrl
     * @param image_iv
     */
    public static void displayBanner(Context context, String picUrl, ImageView image_iv) {
        if (TextUtils.isEmpty(picUrl)) {
            image_iv.setImageResource(R.drawable.default_image);
            return;
        }
        Picasso.with(context).load(picUrl.replace("\\", "/"))
                .placeholder(R.drawable.empty_banner)
                .error(R.drawable.empty_banner).into(image_iv);
    }

    /**
     * 图像显示  屏幕的最高的宽度 做了oss 的处理
     *
     * @param context
     * @param picUrl
     * @param image_iv
     */
    public static void displayWindow(Context context, String picUrl, final ImageView image_iv) {
        if (TextUtils.isEmpty(picUrl)) {
            image_iv.setImageResource(R.drawable.default_image);
            return;
        }
        if (PublicUtil.curActivity != null) {
            DisplayMetrics metric = new DisplayMetrics();
            PublicUtil.curActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
            image_iv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    measuredHeight = image_iv.getMeasuredHeight();
                    image_iv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
            String pic = ImageZoomUtils.getPic(picUrl, (int) measuredHeight / 2, (int) metric.widthPixels / 2);
            display(context, pic, image_iv);
        } else {
            display(context, picUrl, image_iv);
        }
    }


    /**
     * 图像显示  正方形控件 做了oss 的处理
     *
     * @param context
     * @param picUrl
     * @param image_iv
     */
    public static void displaySquare(Context context, String picUrl, final ImageView image_iv) {
        if (TextUtils.isEmpty(picUrl)) {
            image_iv.setImageResource(R.drawable.default_image);
            return;
        }
        if (PublicUtil.curActivity != null) {
            image_iv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    measuredHeight = image_iv.getMeasuredHeight();
                    measuredWidth = image_iv.getMeasuredWidth();
                    image_iv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });

            String pic = ImageZoomUtils.getPic(picUrl, (int) measuredHeight / 2, (int) measuredWidth / 2);
            display(context, pic, image_iv);
        } else {
            display(context, picUrl, image_iv);
        }
    }


    /**
     * @param context
     * @param picUrl
     * @param image_iv
     * @param wid
     * @param hei
     */
    public static void display(Context context, String picUrl, ImageView image_iv, int wid, int hei) {
        if (TextUtils.isEmpty(picUrl)) {
            image_iv.setImageResource(R.drawable.default_image);
            return;
        }
        Picasso.with(context)
                .load(picUrl)
                .placeholder(R.drawable.pic_loading).error(R.drawable.default_image)
                .resize(wid, hei)
                .into(image_iv);
    }

    /**
     * 通过宽高处理图片url
     *
     * @param context
     * @param picUrl
     * @param image_iv
     * @param wid
     * @param hei
     */
    public static void displayByZoom(Context context, String picUrl, ImageView image_iv, int wid, int hei) {
        String pic = ImageZoomUtils.getPic(picUrl, hei, wid);
        Logutil.e("图片大小=" + pic);
        display(context, pic, image_iv);
    }

    /**
     * 展示大图，不加入缓存不加入内存
     *
     * @param context
     * @param imageUrl
     * @param image_iv
     */
    public static void diaplayBigImage(Context context, String imageUrl, ImageView image_iv) {
        if (TextUtils.isEmpty(imageUrl)) {
            image_iv.setImageResource(R.drawable.default_image);
            return;
        }
        Picasso.with(context)
                .load(imageUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(image_iv);
    }

    /**
     * 展示本地图片文件
     *
     * @param context
     * @param picPath
     * @param image_iv
     */
    public static void displayFilePath(Context context, String picPath, ImageView image_iv) {
        if (TextUtils.isEmpty(picPath)) {
            image_iv.setImageResource(R.drawable.default_image);
            return;
        }
        Picasso.with(context).load(new File(picPath))
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image).into(image_iv);
    }

}
