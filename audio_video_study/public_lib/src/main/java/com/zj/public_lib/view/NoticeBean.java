package com.zj.public_lib.view;

/**
 * Created by Sun
 * 根据tag来判断操作
 */
public class NoticeBean {
    private String message;
    private String tag;

    public NoticeBean(String message, String tag) {
        this.message = message;
        this.tag = tag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
