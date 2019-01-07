package com.maihaoche.volvo.ui.car.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/14
 * Email is gujian@maihaoche.com
 */

public class DepartingCarDetail {

    @SerializedName("wmsCarId")
    @Expose
    public Long wmsCarId;

    @SerializedName("carStoreType")
    @Expose
    public Integer carStoreType;

    @SerializedName("carUnique")
    @Expose
    public String carUnique;

    @SerializedName("carAttribute")
    @Expose
    public String carAttribute;

    @SerializedName("paymentTime")
    @Expose
    public String paymentTime;

    @SerializedName("paymentWarehouseList")
    @Expose
    public List<DepartCarSettlement> paymentWarehouseList;

    @SerializedName("totalPaymentWarehouse")
    @Expose
    public String totalPaymentWarehouse;

    @SerializedName("isPayed")
    @Expose
    public Boolean isPayed;

    public boolean isPayed(){
        return isPayed == null?true:isPayed;
    }

    public static class DepartCarSettlement{

        @SerializedName("warehouseName")
        @Expose
        public String warehouseName;

        @SerializedName("storageDays")
        @Expose
        public Integer storageDays;

        @SerializedName("fees")
        @Expose
        public List<FeeItem> fees;

        @SerializedName("totalFee")
        @Expose
        public String totalFee;
    }

    public static class FeeItem{

        @SerializedName("feeTypeName")
        @Expose
        public String feeTypeName;

        //剩余应付
        @SerializedName("calcFeeValue")
        @Expose
        public String calcFeeValue;

        //调节费用
        @SerializedName("adjustFeeValue")
        @Expose
        public String adjustFeeValue;

        //调节原因
        @SerializedName("adjustCause")
        @Expose
        public String adjustCause;

        @SerializedName("feeValue")
        @Expose
        public String feeValue;
    }
}
