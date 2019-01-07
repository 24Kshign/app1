package com.maihaoche.volvo.dao.po;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.volvo.dao.Enums;

import java.io.Serializable;

/**
 * 类简介：网络返回的仓库的对象数据类型
 * 作者：  yang
 * 时间：  2017/8/21
 * 邮箱：  yangyang@maihaoche.com
 */

public class WarehouseVO implements Serializable{

    @SerializedName("warehouseId")
    @Expose
    public long warehouseId = 0L;//仓库id

    @SerializedName("warehouseName")
    @Expose
    public String warehouseName = "";//仓库名称

    @SerializedName("usePosition")
    @Expose
    public boolean hasStorage;//是否开启了库位

    @SerializedName("usePda")
    @Expose
    public Boolean usePda = true;//盘点类型，是否是使用pda盘点

    public GaragePO toPO(){
        GaragePO garagePO = new GaragePO();
        garagePO.setGarageName(warehouseName);
        garagePO.setWmsGarageId(warehouseId);
        garagePO.setUsePda(usePda? Enums.YesOrNoEnum.YES:Enums.YesOrNoEnum.NO);
        garagePO.setHasStroage(hasStorage == false?0:1);
        return garagePO;
    }
}
