package com.maihaoche.volvo.dao.po;

import com.maihaoche.volvo.dao.Convertors;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

/**
 * 仓库与用户的关联表。可以从用户获得仓库。无需从仓库获得用户。单向查询。
 * 作者：yang
 * 时间：17/6/7
 * 邮箱：yangyang@maihaoche.com
 */
@Entity(

        nameInDb = "GARAGE_2_USER",
        //   多个property共同形成一个索引。
        indexes = {
                @Index(value = "userName,wmsGarageId", unique = true)
        }
)
public class Garage2UserPO {


    @Id(autoincrement = true)
    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long id;

    private String userName;

    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long wmsGarageId = 0L;

    @Generated(hash = 260803273)
    public Garage2UserPO(Long id, String userName, Long wmsGarageId) {
        this.id = id;
        this.userName = userName;
        this.wmsGarageId = wmsGarageId;
    }

    @Generated(hash = 1692814178)
    public Garage2UserPO() {
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getWmsGarageId() {
        return this.wmsGarageId;
    }

    public void setWmsGarageId(Long wmsGarageId) {
        this.wmsGarageId = wmsGarageId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
