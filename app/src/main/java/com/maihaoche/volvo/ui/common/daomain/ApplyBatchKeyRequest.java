package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.commonbiz.module.dto.PagerRequest;

import java.io.Serializable;
import java.util.List;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/1/17
 * Email is gujian@maihaoche.com
 */

public class ApplyBatchKeyRequest extends PagerRequest {

    @SerializedName("warehouseId")
    @Expose
    public List<Long> warehouseIdList;//钥匙id

    @SerializedName("riskReasonType")
    @Expose
    public Integer riskReasonType;//原因

    @SerializedName("searchParam")
    @Expose
    public String searchParam;//原因
}
