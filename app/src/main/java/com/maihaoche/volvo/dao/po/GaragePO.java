package com.maihaoche.volvo.dao.po;

import com.maihaoche.volvo.dao.Convertors;
import com.maihaoche.volvo.dao.Enums;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * 仓库表
 * 作者：yang
 * 时间：17/6/7
 * 邮箱：yangyang@maihaoche.com
 */
@Entity(indexes = {
        @Index(value = "wmsGarageId DESC", unique = true)
})
public class GaragePO implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Id
    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long wmsGarageId = 0L;//仓库id

    private String garageName = "";//仓库名称

    @Convert(converter = Convertors.YesOrNoConverter.class, columnType = Integer.class)
    private Enums.YesOrNoEnum usePda;//该仓库是否使用pda盘点。1:使用pda。0：不适用pda

    @Convert(converter = Convertors.AvoidNullIntegerConverter.class, columnType = Integer.class)
    private Integer hasStroage = 0;//该仓库是否配置库位

    @Generated(hash = 394997515)
    public GaragePO() {
    }



    @Generated(hash = 1104623473)
    public GaragePO(Long wmsGarageId, String garageName, Enums.YesOrNoEnum usePda, Integer hasStroage) {
        this.wmsGarageId = wmsGarageId;
        this.garageName = garageName;
        this.usePda = usePda;
        this.hasStroage = hasStroage;
    }



    public Long getWmsGarageId() {
        return this.wmsGarageId;
    }

    public void setWmsGarageId(Long wmsGarageId) {
        this.wmsGarageId = wmsGarageId;
    }

    public String getGarageName() {
        return this.garageName;
    }

    public void setGarageName(String garageName) {
        this.garageName = garageName;
    }

    public Integer getHasStroage() {
        return hasStroage;
    }

    public void setHasStroage(Integer hasStroage) {
        this.hasStroage = hasStroage;
    }

    public Enums.YesOrNoEnum getUsePda() {
        return this.usePda;
    }



    public void setUsePda(Enums.YesOrNoEnum usePda) {
        this.usePda = usePda;
    }



}
