package com.maihaoche.volvo.ui.instorage.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.volvo.ui.common.daomain.CheckCarResponse;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by gujian
 * Time is 2017/8/8
 * Email is gujian@maihaoche.com
 */

public class CarCheckInfo implements Serializable{

    @SerializedName("carMassLossList")
    @Expose
    public List<CarMassLoss> carMassLossList;//异常照片

    @SerializedName("certificateConsistentStatus")
    @Expose
    public Integer certificateConsistentStatus;//是否有一致性证书

    @SerializedName("checklistStatus")
    @Expose
    public Integer checklistStatus;//是否有商检单

    @SerializedName("customsClearanceStatus")
    @Expose
    public Integer customsClearanceStatus;//是否有关单/合格证

    @SerializedName("checkPhotos")
    @Expose
    public List<CheckCarResponse.PhotoConfig> checkPhotos;

    @SerializedName("imgBehind")
    @Expose
    public String imgBehind;//车辆后45度照片url

    @SerializedName("imgFront")
    @Expose
    public String imgFront;//车辆前45度照片url

    @SerializedName("imgInner")
    @Expose
    public String imgInner;//车辆内饰照片url

    @SerializedName("imgNameplate")
    @Expose
    public String imgNameplate;//入库车辆铭牌照片url

    @SerializedName("imgOdometer")
    @Expose
    public String imgOdometer;//入库车辆里程表照片url

    @SerializedName("keyNumber")
    @Expose
    public Integer keyNumber;//车辆钥匙数量

    @SerializedName("manualStatus")
    @Expose
    public Integer manualStatus;//是否有说明书

    @SerializedName("odometer")
    @Expose
    public String odometer;//车辆入库里程

    @SerializedName("productionDate")
    @Expose
    public String productionDate;//生产日期

    @SerializedName("remark")
    @Expose
    public String remark;//备注

    @SerializedName("upVideoStatus")
    @Expose
    public Integer upVideoStatus;//是否上传照片

    @SerializedName("aroundCarVideoUrl")
    @Expose
    public String aroundCarVideoUrl	;//照片路径

    public static class CarMassLoss{

        @SerializedName("massLossImg")
        @Expose
        public String massLossImg;//质损图片URL

        @SerializedName("remark")
        @Expose
        public String remark;//质损图片备注
    }

    /*---------------额外字段---------------*/

    // 验车时间 格式 YYYY年MM月dd日
    @SerializedName("checkDate")
    @Expose
    public String checkDate;


    //验车人姓名
    @SerializedName("operatorName")
    @Expose
    public String operatorName;



}
