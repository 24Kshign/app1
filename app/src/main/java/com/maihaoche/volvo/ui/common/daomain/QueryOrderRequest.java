package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/1/11
 * Email is gujian@maihaoche.com
 */

public class QueryOrderRequest {

    @SerializedName("settlementNo")
    @Expose
    public String settlementNo;
}
