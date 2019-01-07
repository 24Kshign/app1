package com.maihaoche.volvo.ui.instorage.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/11
 * Email is gujian@maihaoche.com
 */

public class CheckCarRequest {

    @SerializedName("carCheckInfoForm")
    @Expose
    public CarCheckInfo carCheckInfoForm;

    @SerializedName("carId")
    @Expose
    public Long carId;

    @SerializedName("carStoreType")
    @Expose
    public Integer carStoreType;
}
