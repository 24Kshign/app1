package com.maihaoche.volvo.server.dto.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 作者：yang
 * 时间：17/7/3
 * 邮箱：yangyang@maihaoche.com
 */
public class CheckUpdateResponseDTO {
    @Expose
    @SerializedName("versionId")
    public int versionId;

    @Expose
    @SerializedName("downloadUrl")
    public String downloadUrl = "";

    @Expose
    @SerializedName("description")
    public String description = "";

    @Expose
    @SerializedName("md5")
    public String md5 = "";

    @Expose
    @SerializedName("isForce")
    public Boolean isForce;

    public boolean isForce(){
        return isForce == null?false:isForce;
    }
}
