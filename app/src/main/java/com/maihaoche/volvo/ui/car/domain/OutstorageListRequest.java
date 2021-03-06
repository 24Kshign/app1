package com.maihaoche.volvo.ui.car.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.commonbiz.module.dto.PagerRequest;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/8
 * Email is gujian@maihaoche.com
 */

public class OutstorageListRequest extends PagerRequest {

    @SerializedName("searchParam")
    @Expose
    public String searchParam;

    @SerializedName("warehouseIdList")
    @Expose
    public List<Long> warehouseIdList;

}
