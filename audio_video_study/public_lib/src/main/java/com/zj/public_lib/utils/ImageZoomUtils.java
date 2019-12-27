package com.zj.public_lib.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Huang on 2017/2/15.
 */

public class ImageZoomUtils {

    public static List<String> getListPic(List<String> list, int height, int width) {
        if (list == null || list.size() == 0) {
            return null;
        }

        List<String> picList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            String picUrl = list.get(i);
            picUrl += "?x-oss-process=image/resize,m_fill,h_" + height + ",w_" + width;
            picList.add(picUrl);
        }
        return picList;
    }


    public static String getPic(String picUrl, int height, int width) {
        picUrl += "?x-oss-process=image/resize,m_fill,h_" + height + ",w_" + width;
        return picUrl;
    }

}
