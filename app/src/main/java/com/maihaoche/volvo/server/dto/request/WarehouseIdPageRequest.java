package com.maihaoche.volvo.server.dto.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.commonbiz.module.dto.PagerRequest;

/**
 * 类简介：仓库id的页面请求
 * 作者：  yang
 * 时间：  2017/8/10
 * 邮箱：  yangyang@maihaoche.com
 */

public class WarehouseIdPageRequest extends PagerRequest {

    /**
     * 搜索字段
     */
    @SerializedName("searchParam")
    @Expose
    public String searchParam;

    /**
     * 仓库id
     */
    @SerializedName("warehouseId")
    @Expose
    public long warehouseId;


}
