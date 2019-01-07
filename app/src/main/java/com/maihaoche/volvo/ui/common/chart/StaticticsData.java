package com.maihaoche.volvo.ui.common.chart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gujian
 * Time is 2017/8/18
 * Email is gujian@maihaoche.com
 */

public class StaticticsData {

    @SerializedName("outInToday")
    @Expose
    public InOutData outInToday;//今日出库，入库

    @SerializedName("outInThisWeek")
    @Expose
    public InOutData outInThisWeek;//本周出库，入库

    @SerializedName("outInThisMonth")
    @Expose
    public InOutData outInThisMonth;//本月出库，入库

    @SerializedName("outInLastMonth")
    @Expose
    public InOutData outInLastMonth;//上月出库，入库

    @SerializedName("capacity")
    @Expose
    public Storage capacity;//库存统计

    @SerializedName("carAge")
    @Expose
    public Older carAge;//车的库龄

    @SerializedName("exception")
    @Expose
    public Exception exception;//异常统计


    public static class InOutData{
        @SerializedName("inputCount")
        @Expose
        public Integer inputCount;//入库数量

        @SerializedName("outputCount")
        @Expose
        public Integer outputCount;//出库数量
    }

    public static class Storage{
        @SerializedName("capacity")
        @Expose
        public Integer capacity;//容量

        @SerializedName("usedCapacity")
        @Expose
        public Integer usedCapacity;//已用

        @SerializedName("unusedCapacity")
        @Expose
        public Integer unusedCapacity;//空余
    }

    public static class Older{
        @SerializedName("oneToSeven")
        @Expose
        public Integer oneToSeven;//1-7

        @SerializedName("eightToFifteen")
        @Expose
        public Integer eightToFifteen;//8-15

        @SerializedName("sixteenToThirty")
        @Expose
        public Integer sixteenToThirty;//16-30

        @SerializedName("overThirty")
        @Expose
        public Integer overThirty;//30以上
    }

    public static class Exception{
        @SerializedName("totalCount")
        @Expose
        public Integer totalCount;//总计

        @SerializedName("entryLossCount")
        @Expose
        public Integer entryLossCount;//入库异常

        @SerializedName("deliveryLossCount")
        @Expose
        public Integer deliveryLossCount;//在库异常

        @SerializedName("entryAndDeliveryCount")
        @Expose
        public Integer entryAndDeliveryCount;//入在库异常
    }
}
