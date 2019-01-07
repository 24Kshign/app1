package com.maihaoche.volvo.ui.common.daomain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangshengru on 16/3/28.
 * 选择图片回调事件
 */
public class ChoosePicEvent {
    public static final int TYPE_SINGLE = 1;//单张
    public static final int TYPE_MULTI = 2;//多张

    public String path = "";
    public List<String> pathList = new ArrayList<>();
    public int type;

    public String tag;

}
