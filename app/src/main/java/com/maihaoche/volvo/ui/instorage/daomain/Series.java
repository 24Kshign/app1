package com.maihaoche.volvo.ui.instorage.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gujian
 * Time is 2017/8/16
 * Email is gujian@maihaoche.com
 */

public class Series implements Serializable{

    @SerializedName("seriesId")
    @Expose
    public Long seriesId;

    @SerializedName("seriesName")
    @Expose
    public String seriesName;
}
