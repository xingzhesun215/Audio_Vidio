package com.zj.public_lib.lib.okhttp.builder;

import com.zj.public_lib.lib.okhttp.OkHttpUtils;
import com.zj.public_lib.lib.okhttp.request.OtherRequest;
import com.zj.public_lib.lib.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
