package com.maihaoche.commonbiz.module.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/1
 * Email is gujian@maihaoche.com
 */

public class PagerResponse<T>{

    public static final int DEFAULT_PAGE_SIZE = 20;

    @SerializedName("pageNo")
    @Expose
    public Integer pageNo;

    @SerializedName("pageSize")
    @Expose
    public Integer pageSize = DEFAULT_PAGE_SIZE;

    @SerializedName("totalCount")
    @Expose
    public Integer totalCount;

    @SerializedName("result")
    @Expose
    public List<T> result;
}
