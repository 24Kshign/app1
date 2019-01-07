package com.maihaoche.volvo.server.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 类简介：盘库详情的vo
 * 作者：  yang
 * 时间：  2017/8/11
 * 邮箱：  yangyang@maihaoche.com
 */

public class StocktakeDetailCarVO {

    public static final int STOCK_TAKE_STATUS_TO = 0;//盘点状态：0为未盘点，1为已盘点
    public static final int STOCK_TAKE_STATUS_DONE = 1;//盘点状态：0为未盘点，1为已盘点

    /**
     * 盘库详情id
     */
    @SerializedName("stocktakeDetailId")
    @Expose
    public long stocktakeDetailId;

    /**
     * 盘库记录id
     */
    @SerializedName("stocktakeRecordId")
    @Expose
    public long stocktakeRecordId;
    /**
     * 盘点状态：0为未盘点，1为已盘点
     */
    @SerializedName("stocktakeDetailStatus")
    @Expose
    public int stocktakeDetailStatus;


    /**
     * 修改时间
     */
    @SerializedName("gmtModifiedTimeStamp")
    @Expose
    public long gmtModifiedTimeStamp;

    /**
     * 对应的车辆数据
     */
    @SerializedName("inWarehouseCarVO")
    @Expose
    public InWarehouseCarVO inWarehouseCarVO;


}
