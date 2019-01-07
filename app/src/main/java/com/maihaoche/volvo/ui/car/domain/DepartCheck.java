package com.maihaoche.volvo.ui.car.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/1/23
 * Email is gujian@maihaoche.com
 */

public class DepartCheck {

    @SerializedName("canDepart")
    @Expose
    public Boolean canDepart;//是否可出库

    public boolean canDepart(){
        return canDepart == null ? false:canDepart;
    }

    @SerializedName("message")
    @Expose
    public String message;//信息描述


}
