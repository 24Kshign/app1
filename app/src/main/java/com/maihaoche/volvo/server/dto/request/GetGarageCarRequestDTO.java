package com.maihaoche.volvo.server.dto.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 类简介：
 * 作者：  yang
 * 时间：  17/7/3
 * 邮箱：  yangyang@maihaoche.com
 */

public class GetGarageCarRequestDTO {
    /**
     * 最后更新时间
     */
    @SerializedName("lastModifiedTime")
    @Expose
    public Long lastModifiedTime;
}
