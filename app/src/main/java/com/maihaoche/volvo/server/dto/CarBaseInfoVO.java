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

public class CarBaseInfoVO implements Serializable {

    //业务类型id
    @SerializedName("orderType")
    @Expose
    public Integer orderType;

    //业务类型描述
    @SerializedName("orderTypeDesc")
    @Expose
    public String orderTypeDesc;

    //订单编号
    @SerializedName("orderNo")
    @Expose
    public String orderNo;

    //业务员
    @SerializedName("salesman")
    @Expose
    public String salesman;

    //业务员联系方式
    @SerializedName("salesmanPhone")
    @Expose
    public String salesmanPhone;

    //客户公司
    @SerializedName("customer")
    @Expose
    public String customer;

    //客户联系人
    @SerializedName("customerContact")
    @Expose
    public String customerContact;

    //客户联系方式
    @SerializedName("customerPhone")
    @Expose
    public String customerPhone;

    //物流公司
    @SerializedName("logisticsCompany")
    @Expose
    public String logisticsCompany;

    //物流公司联系人
    @SerializedName("logisticsContact")
    @Expose
    public String logisticsContact;

    //物流公司联系方式
    @SerializedName("logisticsContactPhone")
    @Expose
    public String logisticsContactPhone;
}