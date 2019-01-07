package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gujian
 * Time is 2017/9/22
 * Email is gujian@maihaoche.com
 */

public class GraphicQuery implements Serializable {

    @SerializedName("doorX")
    @Expose
    public Integer doorX;//门的横坐标

    @SerializedName("doorY")
    @Expose
    public Integer doorY;//门的纵坐标

    @SerializedName("areaPositions")
    @Expose
    public AreaPositionDetail[][] areaPositions;//车辆位置信息

    public static class AreaPositionDetail implements Serializable{

        @SerializedName("areaPositionId")
        @Expose
        public Long areaPositionId;//库位ID

        @SerializedName("warehouseId")
        @Expose
        public Long warehouseId;//对应仓库ID

        @SerializedName("areaId")
        @Expose
        public Integer areaId;//库区ID

        @SerializedName("areaName")
        @Expose
        public String areaName;//库区名称

        @SerializedName("rowId")
        @Expose
        public Integer rowId;//排ID

        @SerializedName("rowName")
        @Expose
        public String rowName;//排名称

        @SerializedName("locationId")
        @Expose
        public Integer locationId;//列ID

        @SerializedName("locationName")
        @Expose
        public String locationName;//列名称

        @SerializedName("occupied")
        @Expose
        public boolean occupied;//是否占用

        @SerializedName("hasCar")
        @Expose
        public boolean hasCar;//是否绑定车

        @SerializedName("carUnique")
        @Expose
        public String frameCode;//车架号

        @SerializedName("carInfo")
        @Expose
        public String carAttribute;//车辆属性

        @SerializedName("carId")
        @Expose
        public Long carId;//车辆id

        @SerializedName("carStoreType")
        @Expose
        public Integer carStoreType;//入库类型 1是标准入库
    }

}
