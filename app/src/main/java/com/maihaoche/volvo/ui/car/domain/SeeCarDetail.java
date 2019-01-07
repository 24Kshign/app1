package com.maihaoche.volvo.ui.car.domain;

import android.content.Intent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/4/9
 * Email is gujian@maihaoche.com
 */

public class SeeCarDetail implements Serializable{

    @SerializedName("warehouseName")
    @Expose
    public String warehouseName;

    @SerializedName("appointmentDate")
    @Expose
    public String appointmentDate;

    @SerializedName("receiverPhone")
    @Expose
    public String receiverPhone;

    @SerializedName("carSimpleVOList")
    @Expose
    public List<CarSimpleVOList> carSimpleVOList;


    public static class CarSimpleVOList{

        @SerializedName("simpleName")
        @Expose
        public String simpleName;

        @SerializedName("subtotalCars")
        @Expose
        public Integer subtotalCars;

        @SerializedName("outerInner")
        @Expose
        public String outerInner;

        @SerializedName("carInfoVOList")
        @Expose
        public List<CarInfoVOList> carInfoVOList;
    }

    public static class CarInfoVOList{

        @SerializedName("carUnique")
        @Expose
        public String carUnique;

        @SerializedName("position")
        @Expose
        public String position;
    }
}
