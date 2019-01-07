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

public class CancelApplyKeyRequest implements Serializable {

    @SerializedName("carKeyId")
    @Expose
    public Integer carKeyId;//钥匙id
}
