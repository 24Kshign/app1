package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gujian
 * Time is 2017/9/22
 * Email is gujian@maihaoche.com
 */

public class SearchRequest implements Serializable {

    @SerializedName("warehouseId")
    @Expose
    public Long warehouseId;//仓库id

    @SerializedName("searchParam")
    @Expose
    public String searchParam;//搜索条件

}
