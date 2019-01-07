package com.maihaoche.volvo.server.dto.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.commonbiz.module.dto.PagerRequest;

/**
 * 类简介：盘库记录id的请求
 * 作者：  yang
 * 时间：  2017/8/14
 * 邮箱：  yangyang@maihaoche.com
 */

public class StocktakeRecordIdRequest extends PagerRequest{
    /**
     * 盘库记录的id
     */
    @SerializedName("stocktakeRecordId")
    @Expose
    public long stocktakeRecordId = 0L;
}
