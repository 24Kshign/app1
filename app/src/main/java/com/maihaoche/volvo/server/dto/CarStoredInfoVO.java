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

public class CarStoredInfoVO implements Serializable {

    // 入库类型名称
    @SerializedName("carEnterTypeDesc")
    @Expose
    public String carEnterTypeDesc;
    //入库日期 格式 YYYY年MM月dd日
    @SerializedName("entryDate")
    @Expose
    public String entryDate;
    //入库管理员
    @SerializedName("transactorName")
    @Expose
    public String transactorName;
}
