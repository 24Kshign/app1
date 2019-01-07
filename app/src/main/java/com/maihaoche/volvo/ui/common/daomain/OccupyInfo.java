package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gujian
 * Time is 2017/9/22
 * Email is gujian@maihaoche.com
 */

public class OccupyInfo implements Serializable {

    @SerializedName("areaPositionId")
    @Expose
    public Long areaPositionId;//库位id

    @SerializedName("occupied")
    @Expose
    public boolean occupied;//是否占用

}
