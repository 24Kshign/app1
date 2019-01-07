package com.maihaoche.volvo.server.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.volvo.dao.Enums;
import com.maihaoche.volvo.dao.po.CarExtraInfoPO;
import com.maihaoche.volvo.dao.po.CarPO;

import java.io.Serializable;

/**
 * 作者：yang
 * 时间：17/6/8
 * 邮箱：yangyang@maihaoche.com
 */
public class CarDTO extends CarExtraInfoDTO implements Serializable {

    public static CarDTO createFromPO(CarPO carPO) {
        //补充资料
        CarExtraInfoPO carExtraInfoPO = carPO.getCarExtraInfo();
        CarDTO carDTO = new CarDTO();
        if (carExtraInfoPO != null) {
            carDTO = CarExtraInfoDTO.createFromPO(carExtraInfoPO);
        }
        carDTO.carId = carPO.getCarId();
        carDTO.wmsGarageId = carPO.getWmsGarageId();
        carDTO.BC = carPO.getBC();
        carDTO.VIN = carPO.getVIN();
        carDTO.userName = carPO.getUserName();
        carDTO.userID = carPO.getUserId();
        carDTO.attribute = carPO.getAttribute();
        carDTO.isIn = carPO.getIsIn().value();
        carDTO.remark = carPO.getRemarks();
        carDTO.inTime = carPO.getInTime();
        carDTO.modifyTime = carPO.getModifyTime();
        return carDTO;
    }

    public CarPO toCarPO() {
        CarPO carPO = new CarPO();
        carPO.setCarId(carId);
        carPO.setWmsGarageId(wmsGarageId);
        carPO.setBC(BC);
        carPO.setVIN(VIN);
        carPO.setAttribute(attribute);
        carPO.setIsIn(Enums.YesOrNoEnum.get(isIn));
        carPO.setRemarks(remark);
        carPO.setLable(label);
        carPO.setUserId(userID);
        carPO.setUserName(userName);
        carPO.setInTime(inTime);
        carPO.setModifyTime(modifyTime);
        carPO.setIsOnline(Enums.YesOrNoEnum.YES);
        carPO.setCarExtraInfo(toExtraInfoPO());
        return carPO;
    }

    /**
     * 仓库id
     */
    @SerializedName("warehouseId")
    @Expose
    public long wmsGarageId = 0;

    /**
     * rfid，二维码值
     */
    @SerializedName("carTagId")
    @Expose
    public String BC = "";
    /**
     * 车架号
     */
    @SerializedName("carUnique")
    @Expose
    public String VIN = "";

    /**
     * 入库人员UUID
     */
    @SerializedName("pdaUserUuid")
    @Expose
    public String userID = "";


    /**
     * 入库人员姓名
     */
    @SerializedName("pdaUserName")
    @Expose
    public String userName = "";


    /**
     * 车辆属性
     */
    @SerializedName("carAttribute")
    @Expose
    private String attribute = "";
    /**
     * 在库，出库状态
     */
    @SerializedName(value = "carStatus")
    @Expose
    public int isIn;

    /**
     * 入库时用户打的标签
     */
    @SerializedName("label")
    @Expose
    public String label = "";

    /**
     * 入库备注信息
     */
    @SerializedName("carRemark")
    @Expose
    public String remark = "";

    /**
     * 入库时间(pda端数据生成时间)
     */
    @SerializedName(value = "pdaGmtCreate",alternate = "gmtCreate")
    @Expose
    public long inTime;

    /**
     * 最近的修改时间(pda端数据最新修改时间)
     */
    @SerializedName(value = "pdaGmtModified",alternate = "gmtModified")
    @Expose
    public long modifyTime;


}
