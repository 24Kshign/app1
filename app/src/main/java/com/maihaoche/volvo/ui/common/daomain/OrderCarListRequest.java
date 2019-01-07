package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/16
 * Email is gujian@maihaoche.com
 */

public class OrderCarListRequest {

    @SerializedName("bizOrderNo")
    @Expose
    public String bizOrderNo;//车辆Id

    @SerializedName("warehouseIdList")
    @Expose
    public List<Long> warehouseIdList;//ku


}
