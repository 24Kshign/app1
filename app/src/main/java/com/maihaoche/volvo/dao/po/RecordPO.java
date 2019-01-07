package com.maihaoche.volvo.dao.po;

import com.maihaoche.volvo.dao.Convertors;
import com.maihaoche.volvo.dao.DaoSession;
import com.maihaoche.volvo.dao.Enums;
import com.maihaoche.volvo.dao.RecordItemPODao;
import com.maihaoche.volvo.dao.RecordPODao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 盘库记录表
 * 作者：yang
 * 时间：17/6/7
 * 邮箱：yangyang@maihaoche.com
 */
@Entity(indexes = {
        @Index(value = "recordId DESC", unique = true)
})
public class RecordPO implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;


    /**
     * 盘库记录的id
     */
    @Id(autoincrement = true)
    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long recordId;

    /**
     * 盘库记录对应的用户的用户名
     */
    private String userName;

    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long wmsRecId = 0L;

    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long wmsGarageId = 0L;


    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long createTime = 0L;

    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long modifyTime = 0L;

    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long startTime = 0L;

    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long endTime = 0L;

    @Convert(converter = Convertors.AvoidNullIntegerConverter.class, columnType = Integer.class)
    private Integer inNum = 0;

    @Convert(converter = Convertors.AvoidNullIntegerConverter.class, columnType = Integer.class)
    private Integer totalNum = 0;

    @Convert(converter = Convertors.RecStatusConvertor.class, columnType = Integer.class)
    private Enums.RecStatus recStatus;

    @Convert(converter = Convertors.YesOrNoConverter.class, columnType = Integer.class)
    private Enums.YesOrNoEnum result;


    /**
     * 具体的记录，按照carId降顺排列
     */
    @ToMany(referencedJoinProperty = "recordId")
    @OrderBy("recordTime DESC")
    private List<RecordItemPO> recordItems;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1481550707)
    private transient RecordPODao myDao;


    @Generated(hash = 1079330650)
    public RecordPO(Long recordId, String userName, Long wmsRecId, Long wmsGarageId, Long createTime,
                    Long modifyTime, Long startTime, Long endTime, Integer inNum, Integer totalNum,
                    Enums.RecStatus recStatus, Enums.YesOrNoEnum result) {
        this.recordId = recordId;
        this.userName = userName;
        this.wmsRecId = wmsRecId;
        this.wmsGarageId = wmsGarageId;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.inNum = inNum;
        this.totalNum = totalNum;
        this.recStatus = recStatus;
        this.result = result;
    }


    @Generated(hash = 1593376391)
    public RecordPO() {
    }


    public Long getRecordId() {
        return this.recordId;
    }


    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }


    public Long getWmsRecId() {
        return this.wmsRecId;
    }


    public void setWmsRecId(Long wmsRecId) {
        this.wmsRecId = wmsRecId;
    }


    public Long getWmsGarageId() {
        return this.wmsGarageId;
    }


    public void setWmsGarageId(Long wmsGarageId) {
        this.wmsGarageId = wmsGarageId;
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


    public Long getStartTime() {
        return this.startTime;
    }


    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }


    public Long getEndTime() {
        return this.endTime;
    }


    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }


    public Integer getInNum() {
        return this.inNum;
    }


    public void setInNum(Integer inNum) {
        this.inNum = inNum;
    }


    public Integer getTotalNum() {
        return this.totalNum;
    }


    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }


    public Enums.RecStatus getRecStatus() {
        return this.recStatus;
    }


    public void setRecStatus(Enums.RecStatus recStatus) {
        this.recStatus = recStatus;
    }


    public Enums.YesOrNoEnum getResult() {
        return this.result;
    }


    public void setResult(Enums.YesOrNoEnum result) {
        this.result = result;
    }


    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1921926534)
    @Keep
    public List<RecordItemPO> getRecordItems() {
        if (recordItems == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RecordItemPODao targetDao = daoSession.getRecordItemPODao();
            List<RecordItemPO> recordItemsNew = targetDao._queryRecordPO_RecordItems(recordId);
            synchronized (this) {
                if (recordItems == null) {
                    recordItems = recordItemsNew;
                }
            }
        }
        return recordItems == null ? new ArrayList<>() : recordItems;
    }


    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 293441392)
    public synchronized void resetRecordItems() {
        recordItems = null;
    }


    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }


    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }


    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }


    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 941553084)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRecordPODao() : null;
    }


    public String getUserName() {
        return this.userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

}
