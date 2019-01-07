package com.maihaoche.volvo.server.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 类简介：
 * 作者：  yang
 * 时间：  2017/8/16
 * 邮箱：  yangyang@maihaoche.com
 */

public class PvNameVO implements Serializable {

    //中文名
    @SerializedName("pname")
    @Expose
    public String pname;
    //中文名对应的属性
    @SerializedName("vname")
    @Expose
    public String vname;
}
