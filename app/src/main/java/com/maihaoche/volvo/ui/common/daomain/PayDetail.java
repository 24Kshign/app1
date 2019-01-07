package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/1/11
 * Email is gujian@maihaoche.com
 */

public class PayDetail implements Serializable{

    @SerializedName("settlementNo")
    @Expose
    public String settlementNo;

    @SerializedName("payNo")
    @Expose
    public String payNo;

    @SerializedName("finalFee")
    @Expose
    public String finalFee;

    @SerializedName("codeUrl")
    @Expose
    public String qrcode;


    @SerializedName("immediate")
    @Expose
    public Boolean immediate;

    public boolean isImmediate(){
        return immediate == null ?false:immediate;
    }
}
