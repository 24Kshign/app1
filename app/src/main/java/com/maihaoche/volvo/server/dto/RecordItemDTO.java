package com.maihaoche.volvo.server.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.volvo.dao.po.RecordItemPO;

import java.io.Serializable;

/**
 * 盘库详情里的item数据。由于该数据只用于传输给服务端，而服务端是一张表，所以不需要盘库记录的id。
 * 作者：yang
 * 时间：17/6/8
 * 邮箱：yangyang@maihaoche.com
 */
public class RecordItemDTO extends BaseDTO<RecordItemPO> implements Serializable {


    public RecordItemDTO(RecordItemPO recordItemPO) {
        super(recordItemPO);
        carId = recordItemPO.getCarId();
        BC = recordItemPO.getBC();
        VIN = recordItemPO.getVIN();
        recordTime = recordItemPO.getRecordTime();
        isInRecord = recordItemPO.getIsInRecord().value();
        createTime = recordItemPO.getCreateTime();
        modifyTime = recordItemPO.getModifyTime();
    }

    @Override
    public RecordItemPO toPO() {
        return new RecordItemPO();
    }

    /**
     * 车辆入库时记录在数据库中的id
     */
    @SerializedName("pdaCarUuid")
    @Expose
    public String carId;


    /**
     * rfid
     */
    @SerializedName("carTagId")
    @Expose
    public String BC = "";
    /**
     * 车架号
     */
    @SerializedName("vin")
    @Expose
    public String VIN = "";


    /**
     * 盘点时间,这辆车被盘到的时刻的时间
     */
    @SerializedName("stocktakeTime")
    @Expose
    private Long recordTime = 0L;

    /**
     * 盘点状态 0-未盘点 1-已盘点
     */
    @SerializedName("stocktakeStatus")
    @Expose
    private int isInRecord;

    /**
     * pda端数据生成时间
     */
    @SerializedName("pdaGmtCreate")
    @Expose
    private Long createTime = 0L;

    /**
     * pda端数据最新修改时间
     */
    @SerializedName("pdaGmtModified")
    @Expose
    private Long modifyTime = 0L;
}
