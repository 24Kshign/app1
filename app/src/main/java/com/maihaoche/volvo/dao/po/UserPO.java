package com.maihaoche.volvo.dao.po;

import com.maihaoche.volvo.dao.DaoSession;
import com.maihaoche.volvo.dao.GaragePODao;
import com.maihaoche.volvo.dao.UserPODao;
import com.maihaoche.volvo.dao.UserRelatedInfoPODao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户表
 * 作者：yang
 * 时间：17/6/7
 * 邮箱：yangyang@maihaoche.com
 */
@Entity(indexes = {
        @Index(value = "userName DESC", unique = true)
})
public class UserPO {

    /**
     * 用户名作为主键
     */
    @Id
    private String userName;

    private String realName;

    private String passWord;

    private String jSessionId;

    private String mhcSessionId;


    private String userId;

    /**
     * 网易云信的accid
     */
    private String yunAccId;

    /**
     * 网易云信的token
     */
    private String yunToken;

    private boolean isMhcStaff;

    /**
     * 用户相关的信息
     * * joinProperty 指定了UserPO表中，哪个列名对应于UserRelatedInfoPO表中的id
     */
    @ToOne(joinProperty = "userName")
    private UserRelatedInfoPO userRelatedInfoPO = new UserRelatedInfoPO();

    /**
     * 关键搜索。M-M的关系,需要一个关联表{@link Garage2UserPO}.
     * sourceProperty 当前表中的属性
     * targetProperty 目标表中的属性
     * 要求，sourceProperty和targetProperty都要在关联表中出现。并且分别指向当前表(UserPO)和目标表(GaragePO)的id
     */
    @ToMany
    @JoinEntity(
            entity = Garage2UserPO.class,
            sourceProperty = "userName",
            targetProperty = "wmsGarageId"
    )
    @OrderBy("wmsGarageId ASC")
    private List<GaragePO> garagesOfThisUser;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1314985428)
    private transient UserPODao myDao;

    @Generated(hash = 265214954)
    public UserPO(String userName, String realName, String passWord, String jSessionId,
            String mhcSessionId, String userId, String yunAccId, String yunToken,
            boolean isMhcStaff) {
        this.userName = userName;
        this.realName = realName;
        this.passWord = passWord;
        this.jSessionId = jSessionId;
        this.mhcSessionId = mhcSessionId;
        this.userId = userId;
        this.yunAccId = yunAccId;
        this.yunToken = yunToken;
        this.isMhcStaff = isMhcStaff;
    }

    @Generated(hash = 1622285570)
    public UserPO() {
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return this.passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getJSessionId() {
        return this.jSessionId;
    }

    public void setJSessionId(String jSessionId) {
        this.jSessionId = jSessionId;
    }

    public String getMhcSessionId() {
        return this.mhcSessionId;
    }

    public void setMhcSessionId(String mhcSessionId) {
        this.mhcSessionId = mhcSessionId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isMhcStaff() {
        return isMhcStaff;
    }

    public void setMhcStaff(boolean mhcStaff) {
        isMhcStaff = mhcStaff;
    }

    @Generated(hash = 289518251)
    private transient String userRelatedInfoPO__resolvedKey;

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 56334971)
    @Keep
    public UserRelatedInfoPO getUserRelatedInfoPO() {
        String __key = this.userName;
        if (userRelatedInfoPO__resolvedKey == null
                || userRelatedInfoPO__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserRelatedInfoPODao targetDao = daoSession.getUserRelatedInfoPODao();
            UserRelatedInfoPO userRelatedInfoPONew = targetDao.load(__key);
            synchronized (this) {
                userRelatedInfoPO = userRelatedInfoPONew;
                userRelatedInfoPO__resolvedKey = __key;
            }
        }
        if (userRelatedInfoPO == null) {
            UserRelatedInfoPO userRelatedInfoPO = new UserRelatedInfoPO();
            userRelatedInfoPO.setUserName(getUserName());
        }
        return userRelatedInfoPO;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1638447610)
    public void setUserRelatedInfoPO(UserRelatedInfoPO userRelatedInfoPO) {
        synchronized (this) {
            this.userRelatedInfoPO = userRelatedInfoPO;
            userName = userRelatedInfoPO == null ? null : userRelatedInfoPO.getUserName();
            userRelatedInfoPO__resolvedKey = userName;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 540967458)
    @Keep
    public List<GaragePO> getGaragesOfThisUser() {
        if (garagesOfThisUser == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GaragePODao targetDao = daoSession.getGaragePODao();
            List<GaragePO> garagesOfThisUserNew = targetDao
                    ._queryUserPO_GaragesOfThisUser(userName);
            synchronized (this) {
                if (garagesOfThisUser == null) {
                    garagesOfThisUser = garagesOfThisUserNew;
                }
            }
        }
        return garagesOfThisUser == null ? new ArrayList<>() : garagesOfThisUser;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1397046454)
    public synchronized void resetGaragesOfThisUser() {
        garagesOfThisUser = null;
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
    @Generated(hash = 1421295912)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserPODao() : null;
    }

    public String getRealName() {
        return this.realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getYunAccId() {
        return this.yunAccId;
    }

    public void setYunAccId(String yunAccId) {
        this.yunAccId = yunAccId;
    }

    public String getYunToken() {
        return this.yunToken;
    }

    public void setYunToken(String yunToken) {
        this.yunToken = yunToken;
    }

    public boolean getIsMhcStaff() {
        return this.isMhcStaff;
    }

    public void setIsMhcStaff(boolean isMhcStaff) {
        this.isMhcStaff = isMhcStaff;
    }

}
