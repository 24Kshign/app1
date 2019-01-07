package com.maihaoche.volvo.server.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 类简介：盘库列表中的盘库数据
 * 作者：  yang
 * 时间：  2017/8/16
 * 邮箱：  yangyang@maihaoche.com
 */

public class StocktakeDetailVO implements Serializable{

    @SerializedName("stocktakeDetailId")
    @Expose
    public long stocktakeDetailId;//盘点id

    @SerializedName("stockTakeTime")
    @Expose
    public String stockTakeTime;//盘点时间 样式 2017年11月18日 14:30

    @SerializedName("operatorName")
    @Expose
    public String operatorName;//盘点人的姓名

    @SerializedName("stocktakeType")
    @Expose
    public String stocktakeType;//盘点类型 "钉钉盘点"。"pda盘点"

    @SerializedName("imgCar")
    @Expose
    public String imgCar;//车辆照片

    @SerializedName("imgNameplate")
    @Expose
    public String imgNameplate;//盘点上传的铭牌照片
}
