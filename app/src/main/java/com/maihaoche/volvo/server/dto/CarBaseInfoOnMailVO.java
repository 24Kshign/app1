package com.maihaoche.volvo.server.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 类简介：车辆基础信息(邮寄手续和上报异常页面的展示数据)
 * 作者：  yang
 * 时间：  2017/8/15
 * 邮箱：  yangyang@maihaoche.com
 */

public class CarBaseInfoOnMailVO implements Serializable {


    @SerializedName("carId")
    @Expose
    public long carId;

    /**
     * 车架号
     */
    @SerializedName("carUnique")
    @Expose
    public String carUnique;

    /**
     * 车辆属性
     */
    @SerializedName("carAttribute")
    @Expose
    public String carAttribute;

    /**
     * 条形码
     */
    @SerializedName("carTagId")
    @Expose
    public String carTagId;


    /**
     * 备注
     */
    @SerializedName("remark")
    @Expose
    public String remark;

    /**
     * 入库时间
     */
    @SerializedName("entryDate")
    @Expose
    public String entryDate;


    /**
     * 可以邮寄的验车信心
     */
    @SerializedName("checkInfo4Mail")
    @Expose
    public CheckInfo4Mail checkInfo4Mail;

    /**
     * 邮寄的验车信息
     */
    public static class CheckInfo4Mail implements Serializable {
        /**
         * 剩余钥匙数量
         */
        @SerializedName("keyNumber")
        @Expose
        public int keyNumber;

        /**
         * 是否有关单或合格证。1:有。0:没有
         */
        @SerializedName("certificateStatus")
        @Expose
        public int certificateStatus;

        /**
         * 是否有一致性证书。1:有。0:没有
         */
        @SerializedName("certificateConsistentStatus")
        @Expose
        public int certificateConsistentStatus;

        /**
         * 是否有商检单。1:有。0:没有
         */
        @SerializedName("checklistStatus")
        @Expose
        public int checklistStatus;

        /**
         * 是否有说明书。1:有。0:没有
         */
        @SerializedName("manualStatus")
        @Expose
        public int manualStatus;
    }


}
