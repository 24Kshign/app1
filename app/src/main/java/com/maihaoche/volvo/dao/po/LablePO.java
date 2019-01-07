package com.maihaoche.volvo.dao.po;

import com.maihaoche.volvo.dao.Convertors;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by gujian
 * Time is 2017/6/15
 * Email is gujian@maihaoche.com
 */
@Entity(indexes = {
        @Index(value = "lableId DESC", unique = true)
})
public class LablePO implements Serializable{

    @Transient
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long lableId;

    private Long garageId;

    private String lableName;

    @Generated(hash = 2134319263)
    public LablePO(Long lableId, Long garageId, String lableName) {
        this.lableId = lableId;
        this.garageId = garageId;
        this.lableName = lableName;
    }

    @Generated(hash = 2069197507)
    public LablePO() {
    }

    public Long getLableId() {
        return lableId;
    }

    public void setLableId(Long lableId) {
        this.lableId = lableId;
    }

    public Long getGarageId() {
        return garageId;
    }

    public void setGarageId(Long garageId) {
        this.garageId = garageId;
    }

    public String getLableName() {
        return lableName;
    }

    public void setLableName(String lableName) {
        this.lableName = lableName;
    }
}
