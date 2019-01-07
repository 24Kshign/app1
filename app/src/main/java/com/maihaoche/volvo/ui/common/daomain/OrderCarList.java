package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/14
 * Email is gujian@maihaoche.com
 */

public class OrderCarList implements Serializable {

    @SerializedName("totalNum")
    @Expose
    public Integer totalNum;//车辆总数

    @SerializedName("storedNum")
    @Expose
    public Integer storedNum;//已入库数量

    @SerializedName("unstoredNum")
    @Expose
    public Integer unstoredNum;//未入库数量

    @SerializedName("orderTypeName")
    @Expose
    public String orderTypeName;//订单类型名称

    @SerializedName("orderNo")
    @Expose
    public String orderNo;//订单编号

    @SerializedName("salesman")
    @Expose
    public String salesman;//客户名称

    @SerializedName("salesmanPhone")
    @Expose
    public String salesmanPhone;//客户名称

    @SerializedName("customer")
    @Expose
    public String customer;//客户联系人名称

    @SerializedName("customerContact")
    @Expose
    public String customerContact;//客户联系人联系方式

    @SerializedName("customerContactPhone")
    @Expose
    public String customerContactPhone;//物流公司

    @SerializedName("logisticsCompany")
    @Expose
    public String logisticsCompany;//物流公司联系人

    @SerializedName("logisticsContact")
    @Expose
    public String logisticsContact;//物流公司联系人

    @SerializedName("logisticsContactPhone")
    @Expose
    public String logisticsContactPhone;//物流公司联系方式

    @SerializedName("unstoredCarVOList")
    @Expose
    public List<SimpleCar> unstoredCarVOList;//未入库车辆

    @SerializedName("storedCarVOList")
    @Expose
    public List<SimpleCar> storedCarVOList;//已入库车辆

    public static class SimpleCar{

        @SerializedName("carId")
        @Expose
        public Long carId;//

        @SerializedName("carStoreType")
        @Expose
        public Integer carStoreType;//

        @SerializedName("carUnique")
        @Expose
        public String carUnique;//车架号

        @SerializedName("carAttribute")
        @Expose
        public String carAttribute;//车辆属性

        @SerializedName("isStored")
        @Expose
        public Boolean isStored;//是否已入库

        @SerializedName("showRefuseButton")
        @Expose
        public Boolean showRefuseButton;

        @SerializedName("bindingKeyBoxFlag")
        @Expose
        public Boolean bindingKeyBoxFlag;
    }
}
