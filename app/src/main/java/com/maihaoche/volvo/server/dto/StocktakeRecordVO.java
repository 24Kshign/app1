package com.maihaoche.volvo.server.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 类简介：盘点记录的服务器返回数据
 * 作者：  yang
 * 时间：  2017/8/15
 * 邮箱：  yangyang@maihaoche.com
 */

public class StocktakeRecordVO {

    /**
     * 盘库记录id
     */
    @SerializedName("stocktakeRecordId")
    @Expose
    public long stocktakeRecordId;

    /**
     * 盘库记录的有效时间的文字说明
     */
    @SerializedName("stocktakePeriod")
    @Expose
    public String stocktakePeriod;

    /**
     * 已盘点数量
     */
    @SerializedName("stocktakenNum")
    @Expose
    public int stocktakenNum;

    /**
     * 需要盘点总数
     */
    @SerializedName("totalNum")
    @Expose
    public int totalNum;
}
