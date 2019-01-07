package com.maihaoche.volvo.dao.po;

import com.maihaoche.volvo.dao.Convertors;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 与用户相关的单个信息.
 * 作者：yang
 * 时间：17/6/10
 * 邮箱：yangyang@maihaoche.com
 */
@Entity(indexes = {
        @Index(value = "userName DESC", unique = true)
})
public class UserRelatedInfoPO {
    @Id
    private String userName;

    /**
     * 上次切换的仓库的wmsId。
     */
    @Convert(converter = Convertors.AvoidNullLongConverter.class, columnType = Long.class)
    private Long lastWmsGarageId = 0L;

@Generated(hash = 834867594)
public UserRelatedInfoPO(String userName, Long lastWmsGarageId) {
    this.userName = userName;
    this.lastWmsGarageId = lastWmsGarageId;
}

@Generated(hash = 452298408)
public UserRelatedInfoPO() {
}

public String getUserName() {
    return this.userName;
}

public void setUserName(String userName) {
    this.userName = userName;
}

public Long getLastWmsGarageId() {
    return this.lastWmsGarageId;
}

public void setLastWmsGarageId(Long lastWmsGarageId) {
    this.lastWmsGarageId = lastWmsGarageId;
}

}
