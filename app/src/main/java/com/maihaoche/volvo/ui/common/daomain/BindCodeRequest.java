package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gujian
 * Time is 2017/8/16
 * Email is gujian@maihaoche.com
 */

public class BindCodeRequest {

    @SerializedName("carId")
    @Expose
    public Long carId;//车辆Id

    @SerializedName("carStoreType")
    @Expose
    public Integer carStoreType;//在库类型

    @SerializedName("carTagId")
    @Expose
    public String carTagId;//条码标签

    @SerializedName("associationPicture")
    @Expose
    public String associationPicture;//条码标签
}
