package com.maihaoche.volvo.ui.instorage.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/8
 * Email is gujian@maihaoche.com
 */

public class NoStandInstoRequest {

    @SerializedName("carTagId")
    @Expose
    public String carTagId;//RFID

    @SerializedName("associationPicture")
    @Expose
    public String associationPicture;//RFID照片

    @SerializedName("warehouseId")
    @Expose
    public Long warehouseId;//仓库ID

    @SerializedName("carUnique")
    @Expose
    public String carUnique;//车架号

    @SerializedName("areaPositionId")
    @Expose
    public Long areaPositionId;

    @SerializedName("brandId")
    @Expose
    public Long brandId;//品牌ID

    @SerializedName("brandName")
    @Expose
    public String brandName;//品牌名称

    @SerializedName("seriesId")
    @Expose
    public Long seriesId;//车系ID

    @SerializedName("seriesName")
    @Expose
    public String seriesName;//车系名称

    @SerializedName("customerId")
    @Expose
    public Long customerId;//客户ID

    @SerializedName("customer")
    @Expose
    public String customer;//客户名称

    @SerializedName("customerContactId")
    @Expose
    public Long customerContactId;//客户联系人ID

    @SerializedName("customerContact")
    @Expose
    public String customerContact;//客户联系人名称

    @SerializedName("customerContactPhone")
    @Expose
    public String customerContactPhone;//客户联系人手机

    @SerializedName("keyId")
    @Expose
    public String keyId;//钥匙柜id
}
