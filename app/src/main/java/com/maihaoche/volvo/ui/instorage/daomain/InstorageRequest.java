package com.maihaoche.volvo.ui.instorage.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.commonbiz.module.dto.BaseRequest;

import java.io.Serializable;

/**
 * Created by gujian
 * Time is 2017/8/8
 * Email is gujian@maihaoche.com
 */

public class InstorageRequest extends BaseRequest{

    @SerializedName("carId")
    @Expose
    public Long carId;//

    @SerializedName("carTagId")
    @Expose
    public String tagId;

    @SerializedName("areaPositionId")
    @Expose
    public Long areaPositionId;

    @SerializedName("associationPicture")
    @Expose
    public String associationPicture;

    @SerializedName("keyId")
    @Expose
    public String keyId;//钥匙柜id

}
