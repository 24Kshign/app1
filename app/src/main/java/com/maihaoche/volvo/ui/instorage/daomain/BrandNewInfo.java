package com.maihaoche.volvo.ui.instorage.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/16
 * Email is gujian@maihaoche.com
 */

public class BrandNewInfo implements Serializable {

    @SerializedName("brandId")
    @Expose
    public Long brandId;

    @SerializedName("brandLetter")
    @Expose
    public String brandLetter;

    @SerializedName("brandName")
    @Expose
    public String brandName;

    @SerializedName("brandPic")
    @Expose
    public String brandPic;

    @SerializedName("smallPic")
    @Expose
    public String smallPic;

    @SerializedName("specList")
    @Expose
    public List<SpecInfo> specList = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BrandNewInfo) {
            if (((BrandNewInfo) o).brandId == null) {
                return brandId == null;
            }
            return brandId != null && ((BrandNewInfo) o).brandId.longValue() == brandId.longValue();
        }
        return false;
    }

    public class SpecInfo implements Serializable {
        @SerializedName("specId")
        @Expose
        public Long sepcId;

        @SerializedName("specName")
        @Expose
        public String spoecName;

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return o != null && o instanceof SpecInfo && ((SpecInfo) o).sepcId.equals(sepcId);
        }
    }

}
