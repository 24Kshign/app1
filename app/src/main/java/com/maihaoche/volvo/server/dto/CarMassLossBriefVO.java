package com.maihaoche.volvo.server.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.volvo.ui.instorage.daomain.CarCheckInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 类简介：在库异常数据
 * 作者：  yang
 * 时间：  2017/8/16
 * 邮箱：  yangyang@maihaoche.com
 */

public class CarMassLossBriefVO implements Serializable{
    //上报时间
    @SerializedName("reportDate")
    @Expose
    public String reportDate;

    //操作管理员姓名
    @SerializedName("transactorName")
    @Expose
    public String transactorName;

    //异常照片列表
    @SerializedName("carMassLossList")
    @Expose
    public List<CarCheckInfo.CarMassLoss> carMassLossList;
}
