package com.maihaoche.volvo.ui.instorage.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/16
 * Email is gujian@maihaoche.com
 */

public class BrandSeriesInfo implements Serializable {

    @SerializedName("brandId")
    @Expose
    public Long brandId;

    @SerializedName("brandName")
    @Expose
    public String brandName;

    @SerializedName("seriesId")
    @Expose
    public Long seriesId;

    @SerializedName("seriesName")
    @Expose
    public String seriesName;



}
