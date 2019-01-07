package com.maihaoche.volvo.server.dto.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by heliguang on 17-12-29.
 */

public class UploadDataRequest implements Serializable {
    @SerializedName("checkId")
    @Expose
    public long checkId = 0;

    @SerializedName("longitude")
    @Expose
    public String longitude = "";

    @SerializedName("latitude")
    @Expose
    public String latitude = "";

    @SerializedName("channelId")
    @Expose
    public String channelId = "";
}
