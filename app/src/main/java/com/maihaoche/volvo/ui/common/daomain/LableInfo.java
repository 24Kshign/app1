package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gujian
 * Time is 2017/8/14
 * Email is gujian@maihaoche.com
 */

public class LableInfo implements Serializable {

    @SerializedName("warehouseId")
    @Expose
    public Long warehouseId;//仓库id

    @SerializedName("warehouseName")
    @Expose
    public String warehouseName;//仓库名称
}
