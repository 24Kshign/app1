package com.maihaoche.volvo.server.dto.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.commonbiz.module.dto.SessionRequest;
import com.maihaoche.volvo.server.dto.CarDTO;
import com.maihaoche.volvo.server.dto.RecordDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 上传数据的接口
 * 作者：yang
 * 时间：17/6/9
 * 邮箱：yangyang@maihaoche.com
 */
public class UploadRequestDTO extends SessionRequest {
    /**
     * 上传时间
     */
    @SerializedName("uploadTime")
    @Expose
    public long uploadTime = 0L;

    /**
     * pda的唯一标识
     */
    @SerializedName("pdaId")
    @Expose
    public String pdaId = "";

    /**
     * 车辆数据列表
     */
    @SerializedName("pdaCarFormList")
    @Expose
    public List<CarDTO> carList = new ArrayList<>();

    /**
     * 车辆盘点记录
     */
    @SerializedName("pdaCarStocktakeRecordFormList")
    @Expose
    public List<RecordDTO> mRecordDTOList = new ArrayList<>();


}
