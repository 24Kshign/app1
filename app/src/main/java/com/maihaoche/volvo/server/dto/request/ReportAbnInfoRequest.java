package com.maihaoche.volvo.server.dto.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.volvo.ui.instorage.daomain.CarCheckInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 类简介：上报异常的请求
 * 作者：  yang
 * 时间：  2017/8/15
 * 邮箱：  yangyang@maihaoche.com
 */

public class ReportAbnInfoRequest extends CarIdRequest implements Serializable{

    @SerializedName("massLossForms")
    @Expose
    public List<CarCheckInfo.CarMassLoss> carMassLossList;//异常照片
}
