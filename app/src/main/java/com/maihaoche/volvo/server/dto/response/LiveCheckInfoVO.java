package com.maihaoche.volvo.server.dto.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by heliguang on 17-12-29.
 */

public class LiveCheckInfoVO {

    @Expose
    @SerializedName("checkerName")
    public String checkerName = "";

    @Expose
    @SerializedName("carUnique")
    public String carUnique = "";

    @Expose
    @SerializedName("carInfo")
    public String carInfo = "";

    @Expose
    @SerializedName("postionInfo")
    public String postionInfo = "";

    @Expose
    @SerializedName("recordId")
    public Long recordId = 0L;

    @Expose
    @SerializedName("keyId")
    public String keyId = "";
}
