package com.maihaoche.volvo.server.dto.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 类简介：上传盘点详情的请求
 * 作者：  yang
 * 时间：  2017/8/14
 * 邮箱：  yangyang@maihaoche.com
 */
public class UploadStocktakeDetailsRequest {

    /**
     * 盘点到的数据信息
     */
    @SerializedName("stocktakeDetailInfoList")
    @Expose
    public List<StocktakeDetailInfo> stocktakeDetailInfoList;

    /**
     * 单个盘点数据
     */
    public static class StocktakeDetailInfo {

        public StocktakeDetailInfo(Long stocktakeDetailId, Long pdaLocalTimeStamp) {
            this.stocktakeDetailId = stocktakeDetailId;
            this.pdaLocalTimeStamp = pdaLocalTimeStamp;
        }

        /**
         * 盘点记录的id
         */
        @SerializedName("stocktakeDetailId")
        @Expose
        public Long stocktakeDetailId;

        /**
         * pda设备端的盘点本地时间戳。
         */
        @SerializedName("pdaLocalTimeStamp")
        @Expose
        public Long pdaLocalTimeStamp;
    }
}
