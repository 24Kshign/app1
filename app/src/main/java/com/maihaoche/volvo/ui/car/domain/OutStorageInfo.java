package com.maihaoche.volvo.ui.car.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gujian
 * Time is 2017/8/10
 * Email is gujian@maihaoche.com
 */

public class OutStorageInfo implements Serializable{

    @SerializedName("wmsCarId")
    @Expose
    public Long wmsCarId;//wms_car的id

    @SerializedName("carId")
    @Expose
    public Long carId;//wms_car的id

    @SerializedName("carStoreType")
    @Expose
    public Integer carStoreType;//wms_car的id

    @SerializedName("carUnique")
    @Expose
    public String carUnique;//车架号

    @SerializedName("carAttribute")
    @Expose
    public String carAttribute;//车辆属性

    @SerializedName("wmsOrderId")
    @Expose
    public Long wmsOrderId;//关联wms订单ID

    @SerializedName("orderNo")
    @Expose
    public String orderNo;//关联业务订单编号

    @SerializedName("inWarehouseDay")
    @Expose
    public String inWarehouseDay;//车辆库龄

    @SerializedName("odometer")
    @Expose
    public String odometer;//里程数

    @SerializedName("keyNumber")
    @Expose
    public String keyNumber;//钥匙数

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

    @SerializedName("carKeyId")
    @Expose
    public Integer carKeyId;

    @SerializedName("keyStatus")
    @Expose
    public Integer keyStatus;

    @SerializedName("isShowCancelKey")
    @Expose
    public Boolean isShowCancelKey;

    public boolean isShowCancelKey(){
        return isShowCancelKey == null?false:isShowCancelKey;
    }

    public boolean isSelect;//是否被选中，批量出库使用

    @SerializedName("imgPickLetter")
    @Expose
    public String imgPickLetter;

    //出库单号
    @SerializedName("outWarehouseNo")
    @Expose
    public String outWarehouseNo;

    //物流提车人
    @SerializedName("pickPersonJpgUrl")
    @Expose
    public String pickPersonJpgUrl;

    //纸函指定提车人
    @SerializedName("paperPickPersonJpgUrl")
    @Expose
    public String paperPickPersonJpgUrl;




}
