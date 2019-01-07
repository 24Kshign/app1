package com.maihaoche.volvo.server.dto.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by heliguang on 17-12-29.
 */

public class NotifyLiveConnectedRequest implements Serializable {
    @SerializedName("checkId")
    @Expose
    public long checkId = 0;

    @SerializedName("checkerId")
    @Expose
    public String checkerId = "";

    @SerializedName("channelId")
    @Expose
    public String channelId = "";

    @SerializedName("recordId")
    @Expose
    public Long recordId = 0L;
}
