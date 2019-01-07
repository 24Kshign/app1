package com.maihaoche.volvo.server.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.volvo.ui.instorage.daomain.CarCheckInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 类简介：服务端返回的车辆详情的数据
 * 作者：  yang
 * 时间：  2017/8/16
 * 邮箱：  yangyang@maihaoche.com
 */
public class CarDetailVO implements Serializable {

    //车辆id
    @SerializedName("carId")
    @Expose
    public Long carId;
    // 车辆在库类型
    @SerializedName("carStoreType")
    @Expose
    public Integer carStoreType;
    //车架号
    @SerializedName("carUnique")
    @Expose
    public String carUnique;
    //车辆属性（除了正常的信息，再加上外观内饰）
    @SerializedName("carAttribute")
    @Expose
    public String carAttribute;
    //条形码
    @SerializedName("carTagId")
    @Expose
    public String carTagId;
    //条形码图片
    @SerializedName("associationPicture")
    @Expose
    public String associationPicture;
    //车辆库龄
    @SerializedName("inWarehouseDay")
    @Expose
    public String inWarehouseDay;
    //里程
    @SerializedName("odometer")
    @Expose
    public String odometer;
    //钥匙
    @SerializedName("keyNumber")
    @Expose
    public String keyNumber;

    //基本信息
    @SerializedName("carBaseInfoVO")
    @Expose
    public CarBaseInfoVO carBaseInfoVO;
    // 入库信息
    @SerializedName("carStoredInfoVO")
    @Expose
    public CarStoredInfoVO carStoredInfoVO;
    // 验车信息
    @SerializedName("carCheckedInfoVO")
    @Expose
    public CarCheckInfo carCheckedInfoVO;
    // 在库异常照片
    @SerializedName("carMassLossBriefVOList")
    @Expose
    public List<CarMassLossBriefVO> carMassLossBriefVOList;
    //盘点信息
    @SerializedName("stocktakeDetailVOList")
    @Expose
    public List<StocktakeDetailVO> stocktakeDetailVOList;
    //出库信息
    @SerializedName("deliveryVO")
    @Expose
    public CarDeliveryVO deliveryVO;
    //其他情况（这个是邮寄记录）
    @SerializedName("sendRecordList")
    @Expose
    public List<List<PvNameVO>> sendRecordList;

}
