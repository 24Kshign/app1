package com.maihaoche.volvo.server.dto.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 与服务端交互的用户数据
 * 作者：yang
 * 时间：17/6/8
 * 邮箱：yangyang@maihaoche.com
 */
public class LoginRequestDTO implements Serializable {

    @SerializedName("username")
    @Expose
    public String userName;

    @SerializedName("password")
    @Expose
    public String passWord = "";

    @SerializedName("isPda")
    @Expose
    public String isPda = "pda";

    @SerializedName("userType")
    @Expose
    public int userType;

}
