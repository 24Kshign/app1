package com.maihaoche.commonbiz.module.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gujian
 * Time is 2017/8/1
 * Email is gujian@maihaoche.com
 */

public class PagerRequest {

    public static final int DEFAULT_PAGE_SIZE = 20;

    @SerializedName("pageNo")
    @Expose
    public int pageNo = 1;//默认第一页

    @SerializedName("pageSize")
    @Expose
    public int pageSize = DEFAULT_PAGE_SIZE;
}
