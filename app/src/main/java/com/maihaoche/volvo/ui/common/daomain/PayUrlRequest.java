package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/3/15
 * Email is gujian@maihaoche.com
 */

public class PayUrlRequest {

    public static final int ZHIFUBAO = 23;
    public static final int WEIXIN = 24;

    @SerializedName("settlementNo")
    @Expose
    public String settlementNo;

    //23-支付宝扫码支付24-微信扫码支付
    @SerializedName("payType")
    @Expose
    public Integer payType;

    @SerializedName("immediate")
    @Expose
    public Boolean immediate;
}
