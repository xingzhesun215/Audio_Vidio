package com.zj.public_lib.lib.okhttp.utils;

/**
 * Created by zhy on 15/12/14.
 */
public class Exceptions
{
    public static void illegalArgument(String msg, Object... params)
    {
        throw new IllegalArgumentException(String.format(msg, params));
    }


}
