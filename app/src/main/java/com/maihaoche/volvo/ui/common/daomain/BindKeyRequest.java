package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/1/17
 * Email is gujian@maihaoche.com
 */

public class BindKeyRequest implements Serializable {

    @SerializedName("carId")
    @Expose
    public Long carId;//车辆id

    @SerializedName("carStoreType")
    @Expose
    public Integer carStoreType;//在库类型

    @SerializedName("keyId")
    @Expose
    public String keyId;//钥匙id

    @SerializedName("warehouseId")
    @Expose
    public Long warehouseId;//仓库id

    @SerializedName("oldKeyId")
    @Expose
    public String oldKeyId;//旧的钥匙id

    @SerializedName("changeReason")
    @Expose
    public String changeReason;//换绑原因
}
