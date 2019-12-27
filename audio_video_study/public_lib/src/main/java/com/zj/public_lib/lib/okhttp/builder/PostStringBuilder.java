package com.zj.public_lib.lib.okhttp.builder;


import com.zj.public_lib.lib.okhttp.request.PostStringRequest;
import com.zj.public_lib.lib.okhttp.request.RequestCall;

import java.util.Map;

import okhttp3.MediaType;

/**
 * Created by zhy on 15/12/14.
 */
public class PostStringBuilder extends OkHttpRequestBuilder<PostStringBuilder> {
    private String content;
    private MediaType mediaType;


    public PostStringBuilder content(String content) {
        this.content = content;
        return this;
    }

    public PostStringBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public PostStringBuilder postContent(Map<String, String> parmss) {
        this.params = parmss;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostStringRequest(url, tag, params, headers, content, mediaType, id).build();
    }


}
