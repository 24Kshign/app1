package com.maihaoche.volvo.dao.po;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * 车辆补充数据
 * 作者：yang
 * 时间：17/6/7
 * 邮箱：yangyang@maihaoche.com
 */
@Entity
public class CarExtraInfoPO implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Id
    private String carId;

    /**
     * 车辆的额外信息为一个string，可以转化为Json，然后转化为内存对象，进而用于网络请求。
     */
    private String extraInfo = "";

    @Generated(hash = 961294520)
    public CarExtraInfoPO(String carId, String extraInfo) {
        this.carId = carId;
        this.extraInfo = extraInfo;
    }

    @Generated(hash = 1127240173)
    public CarExtraInfoPO() {
    }

    public String getCarId() {
        return this.carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getExtraInfo() {
        return this.extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }


}
