package com.maihaoche.volvo.server.dto;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.volvo.dao.po.CarExtraInfoPO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 入库补填资料
 * 作者：yang
 * 时间：17/6/8
 * 邮箱：yangyang@maihaoche.com
 */
public class CarExtraInfoDTO implements Serializable {

    public CarExtraInfoPO toExtraInfoPO() {
        CarExtraInfoPO po = new CarExtraInfoPO();
        po.setCarId(carId);
        po.setExtraInfo(new Gson().toJson(this));
        return po;
    }

    /**
     * 从存储对象转换成请求对象。
     *
     * @param carExtraInfoPO
     */
    public static CarDTO createFromPO(CarExtraInfoPO carExtraInfoPO) {
        if (carExtraInfoPO != null && !TextUtils.isEmpty(carExtraInfoPO.getExtraInfo())) {
            CarDTO dto = new Gson().fromJson(carExtraInfoPO.getExtraInfo(), CarDTO.class);
            dto.carId = carExtraInfoPO.getCarId();
            return dto;
        }
        return null;
    }

    /**
     * 车辆在本地保存的uuid
     */
    @SerializedName("pdaCarUuid")
    @Expose
    public String carId = "";

    /**
     * 里程（精确到小数点后1位）
     */
    @SerializedName("odometer")
    @Expose
    public String distance = "";

    /**
     * 钥匙数量
     */
    @SerializedName("keyNumber")
    @Expose
    public int keyNum;

    /**
     * 是否有合格证或关单（0-否 1-是）
     */
    @SerializedName("customsClearanceStatus")
    @Expose
    public int customsClearanceStatus;

    /**
     * 是否有一致性证书（0-否 1-是）
     */
    @SerializedName("certificateConsistentStatus")
    @Expose
    public int certificateConsistentStatus;

    /**
     * 是否有商检书（0-否 1-是）
     */
    @SerializedName("checklistStatus")
    @Expose
    public int checklistStatus;

    /**
     * 是否有说明书（0-否 1-是）
     */
    @SerializedName("manualStatus")
    @Expose
    public int manualStatus;

    /**
     * 常规照片——前45度角
     */
    @SerializedName("imgFront")
    @Expose
    public String photoFront45Degree = "";

    /**
     * 常规照片——后45度角
     */
    @SerializedName("imgBehind")
    @Expose
    public String photoBack45Degree = "";

    /**
     * 常规照片——内饰
     */
    @SerializedName("imgInner")
    @Expose
    public String photoInner = "";

    /**
     * 常规照片——里程表
     */
    @SerializedName("imgEntryOdometer")
    @Expose
    public String photoDistance = "";

    /**
     * 常规照片——铭牌
     */
    @SerializedName("imgEntryNameplate")
    @Expose
    public String photoCard = "";


    /**
     * 车况说明,车辆状态
     */
    @SerializedName("remark")
    @Expose
    public String carDesc = "";

    /**
     * 异常情况
     */
    @SerializedName(value = "pdaCarMassLossFormList", alternate = "pdaCarMassLossDTOList")
    @Expose
    public List<CarPhotoAbnormal> carAbnormals = new ArrayList<>();

    /**
     * 异常照片
     */
    public class CarPhotoAbnormal implements Serializable {
        /**
         * 图片路径
         */
        @SerializedName("massLossImg")
        @Expose
        public String picPath = "";

        /**
         * 异常说明
         */
        @SerializedName("remark")
        @Expose
        public String remark = "";

        /**
         * pda端数据生成时间
         */
        @SerializedName("pdaGmtCreate")
        @Expose
        public long pdaGmtCreate;

        /**
         * pda端数据最新修改时间
         */
        @SerializedName("pdaGmtModified")
        @Expose
        public long pdaGmtModified;

    }

}
