package com.maihaoche.volvo.server.dto.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 类简介：单个车辆的id的请求
 * 作者：  yang
 * 时间：  2017/8/15
 * 邮箱：  yangyang@maihaoche.com
 */

public class CarIdRequest  implements Serializable {
    /**
     * 车辆的id
     */
    @SerializedName("carId")
    @Expose
    public long carId;


    /**
     * 车辆在库类型
     */
    @SerializedName("carStoreType")
    @Expose
    public int carStoreType;

    public CarIdRequest(long carId, int carStoreType) {
        this.carId = carId;
        this.carStoreType = carStoreType;
    }

    public CarIdRequest() {
    }
}
