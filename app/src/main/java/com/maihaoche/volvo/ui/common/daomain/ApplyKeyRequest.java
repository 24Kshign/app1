package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/1/17
 * Email is gujian@maihaoche.com
 */

public class ApplyKeyRequest implements Serializable {

    @SerializedName("carKeyIds")
    @Expose
    public List<Integer> carKeyIds;//钥匙id

    @SerializedName("riskReasonType")
    @Expose
    public Integer riskReasonType;//原因
}
