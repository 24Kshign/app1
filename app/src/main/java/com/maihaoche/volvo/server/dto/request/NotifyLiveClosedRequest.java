package com.maihaoche.volvo.server.dto.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotifyLiveClosedRequest {
    @SerializedName("checkId")
    @Expose
    public long checkId = 0;

    @SerializedName("recordId")
    @Expose
    public long recordId = 0;

    @Expose
    public String channelId = "";
}
