package com.maihaoche.volvo.ui.car.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/14
 * Email is gujian@maihaoche.com
 */

public class DepartingCarList {

    @SerializedName("imgPickLetter")
    @Expose
    public String imgPickLetter;

    @SerializedName("uploadPickLetter")
    @Expose
    public Boolean uploadPickLetter;

    @SerializedName("departingCarDetailVOList")
    @Expose
    public List<DepartingCarDetail> departingCarDetailVOList;

    @SerializedName("isRegular")
    @Expose
    public Boolean isRegular;

    public boolean isRegular(){
        return isRegular == null?false:isRegular;
    }

    @SerializedName("isPayed")
    @Expose
    public Boolean isPayed;

    @SerializedName("totalPaymentWarehouse")
    @Expose
    public String totalPaymentWarehouse;

    public boolean isPayed(){
        return isPayed == null ? true:isPayed;
    }

    public static DepartingCarList getTestData(){
        DepartingCarList list = new DepartingCarList();
        list.imgPickLetter = "";
        list.uploadPickLetter = false;
        list.isPayed = false;
        list.totalPaymentWarehouse = "仓促费合计：1000000";
        list.departingCarDetailVOList = new ArrayList<>();
        for(int i=0;i<1;i++){
            DepartingCarDetail departingCarDetail = new DepartingCarDetail();
            departingCarDetail.carStoreType = 1;
            departingCarDetail.isPayed = false;
            departingCarDetail.carUnique = "sdfsdfsd";
            departingCarDetail.carAttribute = "jdflsdjflksd";
            departingCarDetail.totalPaymentWarehouse = "共2个仓库 小计：¥ 11000000.00";
            departingCarDetail.paymentWarehouseList = new ArrayList<>();
            for(int ii=0;ii<2;ii++){
                DepartingCarDetail.DepartCarSettlement settlement = new DepartingCarDetail.DepartCarSettlement();
                settlement.totalFee = "100";
                settlement.warehouseName = "北京自建库";
                settlement.fees = new ArrayList<>();
                for(int iii = 0;iii<2;iii++){
                    DepartingCarDetail.FeeItem item = new DepartingCarDetail.FeeItem();
                    item.feeTypeName = "hehe";
                    item.feeValue = "100";
                    settlement.fees.add(item);
                }
                departingCarDetail.paymentWarehouseList.add(settlement);

            }
            list.departingCarDetailVOList.add(departingCarDetail);
        }
        return list;
    }
}
