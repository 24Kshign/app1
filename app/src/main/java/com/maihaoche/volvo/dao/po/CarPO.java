package com.maihaoche.volvo.dao.po;

import com.maihaoche.volvo.dao.CarExtraInfoPODao;
import com.maihaoche.volvo.dao.CarPODao;
import com.maihaoche.volvo.dao.Convertors;
import com.maihaoche.volvo.dao.DaoSession;
import com.maihaoche.volvo.dao.Enums;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * 车辆表
 * 作者：yang
 * 时间：17/6/7
 * 邮箱：yangyang@maihaoche.com
 */

@Entity(indexes = {
        @Index(value = "carId DESC", unique = true)
})
public class CarPO implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    /**
     * 车辆的id
     */
    @Id
    private String carId = "";


    /**
     * 车辆所在的仓库的id。由于仓库表是从服务端拉下来的，所以
     * 服务端的仓库数据是有可能发生变化的。既：可能一辆车对应的仓库id，在仓库表中没有该条数据。
     * 所以这里不能做成多对1的关联关系。
     */
    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long wmsGarageId = 0L;

    /**
     * 二维码，rfid码
     */
    private String BC = "";

    /**
     * 车架号
     */
    private String VIN = "";

    /**
     * 车辆属性
     */
    private String attribute = "";

    /**
     * 在库，出库状态
     */
    @Convert(converter = Convertors.YesOrNoConverter.class, columnType = Integer.class)
    private Enums.YesOrNoEnum isIn;

    /**
     * 备注
     */
    private String remarks = "";

    /**
     * 标签
     */
    private String lable = "";


    /**
     * 入库管理员用户id
     */
    private String userId = "";

    /**
     * 入库管理员姓名
     */
    private String userName = "";

    /**
     * 入库时间
     */
    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long inTime = 0L;

    /**
     * 修改时间
     */
    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long modifyTime = 0L;


    /**
     * 是否同步
     */
    @Convert(converter = Convertors.YesOrNoConverter.class, columnType = Integer.class)
    private Enums.YesOrNoEnum isOnline;


    /**
     * 一对一关系。一个car对应一个carExtraInfo。只需要从car查找extraInfo，不需要反查。
     * joinProperty 指定了car表中，哪个列名对应于CarExtraInfo表中的id
     */
    @ToOne(joinProperty = "carId")
    private CarExtraInfoPO carExtraInfo = null;


    @Keep
    public boolean canUpload() {
        return getCarExtraInfo() != null;
    }


    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 340944198)
    private transient CarPODao myDao;


    @Generated(hash = 1656310683)
    public CarPO(String carId, Long wmsGarageId, String BC, String VIN, String attribute,
                 Enums.YesOrNoEnum isIn, String remarks, String lable, String userId,
                 String userName, Long inTime, Long modifyTime, Enums.YesOrNoEnum isOnline) {
        this.carId = carId;
        this.wmsGarageId = wmsGarageId;
        this.BC = BC;
        this.VIN = VIN;
        this.attribute = attribute;
        this.isIn = isIn;
        this.remarks = remarks;
        this.lable = lable;
        this.userId = userId;
        this.userName = userName;
        this.inTime = inTime;
        this.modifyTime = modifyTime;
        this.isOnline = isOnline;
    }

    @Generated(hash = 1043136347)
    public CarPO() {
    }

    @Generated(hash = 354298609)
    private transient String carExtraInfo__resolvedKey;


    @Keep
    public boolean isIn() {
        return Enums.YesOrNoEnum.YES.equals(this.isIn);
    }

    public String getCarId() {
        return this.carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public Long getWmsGarageId() {
        return this.wmsGarageId;
    }

    public void setWmsGarageId(Long wmsGarageId) {
        this.wmsGarageId = wmsGarageId;
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

    public String getAttribute() {
        return this.attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Enums.YesOrNoEnum getIsIn() {
        return this.isIn;
    }

    public void setIsIn(Enums.YesOrNoEnum isIn) {
        this.isIn = isIn;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getLable() {
        return this.lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getInTime() {
        return this.inTime;
    }

    public void setInTime(Long inTime) {
        this.inTime = inTime;
    }

    public Long getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Enums.YesOrNoEnum getIsOnline() {
        return this.isOnline;
    }

    public void setIsOnline(Enums.YesOrNoEnum isOnline) {
        this.isOnline = isOnline;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 1683843116)
    public CarExtraInfoPO getCarExtraInfo() {
        String __key = this.carId;
        if (carExtraInfo__resolvedKey == null || carExtraInfo__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CarExtraInfoPODao targetDao = daoSession.getCarExtraInfoPODao();
            CarExtraInfoPO carExtraInfoNew = targetDao.load(__key);
            synchronized (this) {
                carExtraInfo = carExtraInfoNew;
                carExtraInfo__resolvedKey = __key;
            }
        }
        return carExtraInfo;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 731716236)
    public void setCarExtraInfo(CarExtraInfoPO carExtraInfo) {
        synchronized (this) {
            this.carExtraInfo = carExtraInfo;
            carId = carExtraInfo == null ? null : carExtraInfo.getCarId();
            carExtraInfo__resolvedKey = carId;
        }
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
    @Generated(hash = 380303740)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCarPODao() : null;
    }


}
