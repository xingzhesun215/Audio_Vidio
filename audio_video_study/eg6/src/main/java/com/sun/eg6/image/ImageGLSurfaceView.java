package com.sun.eg6.image;

import android.content.Context;
import android.opengl.GLSurfaceView;


import com.sun.eg6.base.BaseGLSurfaceView;

import java.io.IOException;

/**
 * 展示图片的 GLSurfaceView
 */
public class ImageGLSurfaceView extends BaseGLSurfaceView {

    public ImageGLSurfaceView(Context context) throws IOException {
        super(context);

//        setRenderer(new ImageRenderer(context));  // 展示图片渲染器

        setRenderer(new ImageTransformRenderer(context, ImageTransformRenderer.Filter.MAGN));  // 展示图片处理渲染器

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        requestRender();
    }
}
