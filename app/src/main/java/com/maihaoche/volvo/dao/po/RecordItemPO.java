package com.maihaoche.volvo.dao.po;

import com.maihaoche.volvo.dao.Convertors;
import com.maihaoche.volvo.dao.Enums;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

/**
 * 盘库记录相关的car的信息
 * 作者：yang
 * 时间：17/6/7
 * 邮箱：yangyang@maihaoche.com
 */
@Entity
public class RecordItemPO {
    /**
     * 盘库记录item的id
     */
    @Id(autoincrement = true)
    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long id;

    /**
     * 车辆入库时记录在数据库中的id
     */
    private String carId;

    /**
     * 盘库记录的id
     */
    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long recordId;

    /**
     * RFID,条形码
     */
    private String BC = "";

    /**
     * 车架号
     */
    private String VIN = "";

    /**
     * 车辆属性
     */
    private String attr = "";

    /**
     * 车辆标签
     */
    private String label = "";


    /**
     * 是否已经被盘到
     */
    @Convert(converter = Convertors.YesOrNoConverter.class, columnType = Integer.class)
    private Enums.YesOrNoEnum isInRecord;

    /**
     * 这辆车被盘到的时刻的时间
     */
    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long recordTime;


    /**
     * pda端数据生成时间
     */
    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long createTime;

    /**
     * pda端数据最新修改时间
     */
    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long modifyTime;

    @Keep
    public static RecordItemPO create(CarPO carPO, long recordTime, long recordId) {
        RecordItemPO recordItemPO = new RecordItemPO();
        recordItemPO.setCarId(carPO.getCarId());
        recordItemPO.setRecordId(recordId);
        recordItemPO.setAttr(carPO.getAttribute());
        recordItemPO.setBC(carPO.getBC());
        recordItemPO.setCarId(carPO.getCarId());
        recordItemPO.setRecordTime(recordTime);
        recordItemPO.setIsInRecord(recordTime > 0 ? Enums.YesOrNoEnum.YES : Enums.YesOrNoEnum.NO);
        recordItemPO.setLabel(carPO.getLable());
        recordItemPO.setVIN(carPO.getVIN());
        recordItemPO.setCreateTime(System.currentTimeMillis());
        recordItemPO.setModifyTime(System.currentTimeMillis());
        return recordItemPO;
    }


    @Generated(hash = 1972402062)
    public RecordItemPO(Long id, String carId, Long recordId, String BC, String VIN, String attr, String label,
                        Enums.YesOrNoEnum isInRecord, Long recordTime, Long createTime, Long modifyTime) {
        this.id = id;
        this.carId = carId;
        this.recordId = recordId;
        this.BC = BC;
        this.VIN = VIN;
        this.attr = attr;
        this.label = label;
        this.isInRecord = isInRecord;
        this.recordTime = recordTime;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
    }


    @Generated(hash = 1791103231)
    public RecordItemPO() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCarId() {
        return this.carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public Long getRecordId() {
        return this.recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getBC() {
        return this.BC;
    }

    public void setBC(String BC) {
        this.BC = BC;
    }

    public String getVIN() {
        return this.VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public String getAttr() {
        return this.attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Enums.YesOrNoEnum getIsInRecord() {
        return this.isInRecord;
    }

    public void setIsInRecord(Enums.YesOrNoEnum isInRecord) {
        this.isInRecord = isInRecord;
    }

    public Long getRecordTime() {
        return this.recordTime;
    }

    public void setRecordTime(Long recordTime) {
        this.recordTime = recordTime;
    }


    public Long getCreateTime() {
        return this.createTime;
    }


    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }


    public Long getModifyTime() {
        return this.modifyTime;
    }


    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }


}
