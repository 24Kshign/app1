package com.maihaoche.volvo.server.dto.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.commonbiz.module.dto.PagerResponse;
import com.maihaoche.volvo.server.dto.StocktakeDetailCarVO;

/**
 * 类简介：待盘点车辆列表的接口返回数据
 * 作者：  yang
 * 时间：  2017/8/19
 * 邮箱：  yangyang@maihaoche.com
 */

public class ToStocktakeDataResponse {
    /**
     * 用户的id
     */
    @SerializedName("hasAvailable")
    @Expose
    public boolean hasAvailable; //当前时间,是否有可以盘点的数据。


    /**
     * 待盘点数据
     */
    @SerializedName("toStocktakeCarVOs")
    @Expose
    public PagerResponse<StocktakeDetailCarVO> toStocktakeCarVOs;

}
