package com.maihaoche.volvo.ui.car.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/14
 * Email is gujian@maihaoche.com
 */

public class OutStorageDetailRequest {

    @SerializedName("carIdList")
    @Expose
    public List<Long> wmsCarIdList;
}
