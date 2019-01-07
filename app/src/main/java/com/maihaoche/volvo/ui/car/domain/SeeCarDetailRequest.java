package com.maihaoche.volvo.ui.car.domain;

import android.content.Intent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/4/9
 * Email is gujian@maihaoche.com
 */

public class SeeCarDetailRequest {

    @SerializedName("applId")
    @Expose
    public long appId;

    @SerializedName("appIds")
    @Expose
    public List<Long> appIds;
}
