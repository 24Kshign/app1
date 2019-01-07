package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/14
 * Email is gujian@maihaoche.com
 */

public class CarAreaInfo implements Serializable {

    @SerializedName("areaId")
    @Expose
    public Integer areaId;//库区id

    @SerializedName("areaName")
    @Expose
    public String areaName;//库区名称

    @SerializedName("usbaleRowVOs")
    @Expose
    public List<CarRowInfo> usbaleRowVOs;//排


    public static class CarRowInfo{

        @SerializedName("rowId")
        @Expose
        public Integer rowId;//库区id

        @SerializedName("rowName")
        @Expose
        public String rowName;//库区名称

        @SerializedName("usbaleLocationVOs")
        @Expose
        public List<CarLocationInfo> usbaleLocationVOs;//列
    }

    public static class CarLocationInfo{

        @SerializedName("locationId")
        @Expose
        public Integer locationId;//库区id

        @SerializedName("locationName")
        @Expose
        public String locationName;//库区名称

        @SerializedName("areaPositionId")
        @Expose
        public Long areaPositionId;
    }


}
