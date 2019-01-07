package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by manji
 * Date：2018/11/27 2:31 PM
 * Desc：
 */
public class GaoDeRequest {

    @SerializedName("key")
    @Expose
    public String key;

    @SerializedName("locations")
    @Expose
    public String locations;

    @SerializedName("coordsys")
    @Expose
    public String coordsys;

    @SerializedName("output")
    @Expose
    public String output;
}
