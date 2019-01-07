package com.maihaoche.volvo.server.dto.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 类简介：移库的请求
 * 作者：  yang
 * 时间：  2017/8/22
 * 邮箱：  yangyang@maihaoche.com
 */

public class AreaPositionMoveRequest extends CarIdRequest{
    /**
     * 库位ID
     */
    @SerializedName("areaPositionId")
    @Expose
    public long areaPositionId;
}
