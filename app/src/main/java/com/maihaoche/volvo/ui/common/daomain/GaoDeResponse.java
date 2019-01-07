package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by manji
 * Date：2018/11/27 2:35 PM
 * Desc：
 */
public class GaoDeResponse implements Serializable {

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("info")
    @Expose
    public String info;

    @SerializedName("locations")
    @Expose
    public String locations;
}
