package com.maihaoche.volvo.server.dto;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 类简介：出库信息
 * 作者：  yang
 * 时间：  2017/8/16
 * 邮箱：  yangyang@maihaoche.com
 */

public class CarDeliveryVO implements Serializable{
    //出库时间
    @SerializedName("deliveryTime")
    @Expose
    public String deliveryTime;

    //出库管理员
    @SerializedName("transactorName")
    @Expose
    public String transactorName;

    //存储服务费结算
    @SerializedName("servicePayment")
    @Expose
    public String servicePayment;

    //提车委托函url
    @SerializedName("imgEntrustmentLetter")
    @Expose
    public String imgEntrustmentLetter;



    //提车函url缩略图
    @SerializedName("imgEntrustmentLetterPre")
    @Expose
    public String imgEntrustmentLetterPre;

    //交车单
    @SerializedName("imgDeliveryOrder")
    @Expose
    public String imgDeliveryOrder;
}
