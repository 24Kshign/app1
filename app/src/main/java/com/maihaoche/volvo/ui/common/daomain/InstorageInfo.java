package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gujian
 * Time is 2017/8/8
 * Email is gujian@maihaoche.com
 */

public class InstorageInfo implements Serializable{

    @SerializedName("wmsCarId")
    @Expose
    public Long wmsCarId;//入库

    @SerializedName("carId")
    @Expose
    public Long carId;//

    @SerializedName("carTagId")
    @Expose
    public String carTagId;//条码

    @SerializedName("carStoreType")
    @Expose
    public Integer carStoreType;//1-标准，2是非标

    @SerializedName("carEnterTypeName")
    @Expose
    public String carEnterTypeName;//1-标准，2是非标

    @SerializedName("carUnique")
    @Expose
    public String carUnique;

    @SerializedName("carAttribute")
    @Expose
    public String carAttribute;

    @SerializedName("bizOrderNo")
    @Expose
    public String bizOrderNo;

    @SerializedName("orderNo")
    @Expose
    public String orderNo;

    @SerializedName("orderNoClickable")
    @Expose
    public Boolean orderNoClickable;

    @SerializedName("customer")
    @Expose
    public String customer;

    @SerializedName("salesman")
    @Expose
    public String salesman;

    @SerializedName("warehouseName")
    @Expose
    public String warehouseName;

    @SerializedName("showRefuseButton")
    @Expose
    public Boolean showRefuseButton;

    @SerializedName("areaPositionName")
    @Expose
    public String areaPositionName;

    //调拨单号
    @SerializedName("allotOrderNo")
    @Expose
    public String allotOrderNo;

    //提车人
    @SerializedName("carrierPickContactName")
    @Expose
    public String carrierPickContactName;

    //提车人联系方式
    @SerializedName("carrierPickContactPhone")
    @Expose
    public String carrierPickContactPhone;

    //提车人身份证
    @SerializedName("carrierPickContactIdentity")
    @Expose
    public String carrierPickContactIdentity;

    //预计到库时间
    @SerializedName("expectedWarehousingTime")
    @Expose
    public String expectedWarehousingTime;

    //是否需要绑定钥匙
    @SerializedName("bindingKeyBoxFlag")
    @Expose
    public Boolean bindingKeyBoxFlag;

    //是否需要绑定钥匙
    @SerializedName("priorCheck")
    @Expose
    public Boolean priorCheck;

    public boolean bindingKeyBoxFlag(){
        return bindingKeyBoxFlag == null ?false :bindingKeyBoxFlag;
    }

    public boolean priorCheck(){
        return priorCheck == null ?false : priorCheck;
    }
}
