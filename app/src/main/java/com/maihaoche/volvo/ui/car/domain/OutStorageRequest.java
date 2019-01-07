package com.maihaoche.volvo.ui.car.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/14
 * Email is gujian@maihaoche.com
 */

public class OutStorageRequest {

    @SerializedName("carIdList")
    @Expose
    public List<Long> wmsCarIdList;//wms车辆ID

    @SerializedName("imgDelivery")
    @Expose
    public String imgDelivery;//交车单

    @SerializedName("imgPickLetter")
    @Expose
    public String imgPickLetter;//提车委托函
}
