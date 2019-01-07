package com.maihaoche.volvo.server.dto.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 类简介：
 * 作者：  yang
 * 时间：  2017/8/15
 * 邮箱：  yangyang@maihaoche.com
 */

public class SendProcedureRequest extends CarIdRequest implements Serializable {

    //钥匙数量如果不寄钥匙，则未0
    @SerializedName("keyQuantity")
    @Expose
    public int keyQuantity = 0;

    //是否寄关单或合格证
    @SerializedName("certificateStatus")
    @Expose
    public int certificateStatus = 0;

    //是否寄一致性证书
    @SerializedName("certificateConsistentStatus")
    @Expose
    public int certificateConsistentStatus = 0;

    //是否寄商检单
    @SerializedName("checklistStatus")
    @Expose
    public int checklistStatus = 0;

    //是否寄说明书
    @SerializedName("manualStatus")
    @Expose
    public int manualStatus = 0;

    //快递单号
    @SerializedName("expressNo")
    @Expose
    public String expressNo;
}