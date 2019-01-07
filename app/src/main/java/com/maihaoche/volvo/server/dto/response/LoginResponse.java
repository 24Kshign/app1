package com.maihaoche.volvo.server.dto.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * 类简介：
 * 作者：  yang
 * 时间：  17/6/16
 * 邮箱：  yangyang@maihaoche.com
 */

public class LoginResponse {

    /**
     * 用户的id
     */
    @SerializedName("pdaUserUuid")
    @Expose
    public String userId;

    /**
     * 用户的用户名
     */
    @SerializedName("pdaUserName")
    @Expose
    public String userName;

    /**
     * 用户的真实用户名
     */
    @SerializedName("pdaUserRealName")
    @Expose
    public String pdaUserRealName;

    /**
     * 仓库集合
     */
    @SerializedName("warehouseIdList")
    @Expose
    public ArrayList<Long> userGarageIds;

    /**
     * 云信登录ID
     */
    @SerializedName("accId")
    @Expose
    public String accId;

    /**
     * 云信登录tonken
     */
    @SerializedName("token")
    @Expose
    public String token;
}
