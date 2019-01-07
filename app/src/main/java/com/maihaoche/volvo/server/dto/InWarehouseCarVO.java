package com.maihaoche.volvo.server.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 类简介：在库车辆的vo
 * 作者：  yang
 * 时间：  2017/8/10
 * 邮箱：  yangyang@maihaoche.com
 */
public class InWarehouseCarVO implements Serializable{

    public static final int CAR_STORE_TYPE_S = 1;//标准在库
    public static final int CAR_STORE_TYPE_NS = 2;//非标在库


    @SerializedName("carId")
    @Expose
    public long carId;

    /**
     * 车架号
     */
    @SerializedName("carUnique")
    @Expose
    public String carUnique;

    /**
     * 车辆的条形码
     */
    @SerializedName("carTagId")
    @Expose
    public String carTagId;


    /**
     * 车辆属性
     */
    @SerializedName("carAttribute")
    @Expose
    public String carAttribute;

    /**
     * 车辆库龄
     */
    @SerializedName("inWarehouseDay")
    @Expose
    public int inWarehouseDay;

    /**
     * 里程数
     */
    @SerializedName("odometer")
    @Expose
    public float odometer;

    /**
     * 钥匙数量
     */
    @SerializedName("keyNumber")
    @Expose
    public int keyNumber;

    /**
     * 车辆入库类型。车辆入库类型：1为标准入库，2为非标入库
     */
    @SerializedName("carStoreType")
    @Expose
    public int carStoreType;

    /**
     * 库位的名字
     */
    @SerializedName("areaPositionName")
    @Expose
    public String areaPositionName;

    /**
     * 库位的id
     */
    @SerializedName("areaPositionId")
    @Expose
    public long areaPositionId;

    /**
     * 钥匙主键
     */
    @SerializedName("carKeyId")
    @Expose
    public int carKeyId;

    /**
     * 钥匙id
     */
    @SerializedName("keyId")
    @Expose
    public String keyId;

    /**
     * 钥匙状态
     */
    @SerializedName("keyStatus")
    @Expose
    public Integer keyStatus;

    /**
     * 是否展示取消钥匙申请
     */
    @SerializedName("isShowCancelKey")
    @Expose
    public Boolean isShowCancelKey;

    public boolean isShowCancelKey(){
        return isShowCancelKey == null?false:isShowCancelKey;
    }

}
