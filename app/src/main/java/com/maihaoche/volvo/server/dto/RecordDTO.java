package com.maihaoche.volvo.server.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.dao.po.RecordItemPO;
import com.maihaoche.volvo.dao.po.RecordPO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 盘库记录数据。用于上传给服务器的，所以不需要缓存的库的id
 * 作者：yang
 * 时间：17/6/8
 * 邮箱：yangyang@maihaoche.com
 */
public class RecordDTO extends BaseDTO<RecordPO> implements Serializable {


    public RecordDTO(RecordPO recordPO) {
        localRecordId = recordPO.getRecordId();
        wmsGarageId = recordPO.getWmsGarageId();
        userId = AppApplication.getUserPO().getUserId();
        startTime = recordPO.getStartTime();
        endTime = recordPO.getEndTime();
        inNum = recordPO.getInNum();
        totalNum = recordPO.getTotalNum();
        result = recordPO.getResult().value();
        creatTime = recordPO.getCreateTime();
        modifyTime = recordPO.getModifyTime();
        List<RecordItemPO> recordItemPOS = recordPO.getRecordItems();
        if (recordItemPOS != null && recordItemPOS.size() > 0) {
            mRecordItemDTOS = new ArrayList<>();
            for (RecordItemPO po :
                    recordItemPOS) {
                mRecordItemDTOS.add(new RecordItemDTO(po));
            }
        }
    }

    @Override
    public RecordPO toPO() {
        return null;
    }


    public Long localRecordId = 0L;//本地存储的记录id。不用传给服务器
    /**
     * 操作员UUID
     */
    @SerializedName("pdaUserUuid")
    @Expose
    public String userId = "";

    /**
     * 仓库ID(和服务端数据库相同)
     */
    @SerializedName("warehouseId")
    @Expose
    public long wmsGarageId;

    /**
     * 开始时间
     */
    @SerializedName("startTime")
    @Expose
    public long startTime;

    /**
     * 结束时间
     */
    @SerializedName("endTime")
    @Expose
    public long endTime;


    /**
     * 在库数量
     */
    @SerializedName("inNum")
    @Expose
    public int inNum;

    /**
     * 车辆总数
     */
    @SerializedName("totalNum")
    @Expose
    public int totalNum;


    /**
     * 盘库结果，正常，异常
     */
    @SerializedName("result")
    @Expose
    public int result;


    /**
     * pda端数据生成时间
     */
    @SerializedName("pdaGmtCreate")
    @Expose
    public long creatTime;

    /**
     * pda端数据最新修改时间
     */
    @SerializedName("pdaGmtModified")
    @Expose
    public long modifyTime;


    /**
     * 盘点车辆详情集合
     */
    @SerializedName("pdaCarStocktakeDetailFormList")
    @Expose
    public List<RecordItemDTO> mRecordItemDTOS = new ArrayList<>();


}
