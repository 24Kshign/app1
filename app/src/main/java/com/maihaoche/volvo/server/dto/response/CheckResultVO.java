package com.maihaoche.volvo.server.dto.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by heliguang on 17-12-29.
 */

public class CheckResultVO {
    @Expose
    @SerializedName("resultCode")
    public int resultCode;

    @Expose
    @SerializedName("remark")
    public String remark = "";
}
