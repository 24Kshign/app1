package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/5/16
 * Email is gujian@maihaoche.com
 */
public class PhotoConfigForm implements Serializable{
    @SerializedName("wmsCarId")
    @Expose
    public Long wmsCarId;
}
