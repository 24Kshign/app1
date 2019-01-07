package com.maihaoche.commonbiz.module.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 作者：yang
 * 时间：17/6/8
 * 邮箱：yangyang@maihaoche.com
 */
public class BaseResponse<T> implements Serializable {
    /**
     * 错误码
     */
    @SerializedName("code")
    @Expose
    public String code;

    /**
     * 错误信息
     */
    @SerializedName("message")
    @Expose
    public String message;

    /**
     * 业务结果
     */
    @SerializedName("success")
    @Expose
    public boolean success;


    @SerializedName("data")
    @Expose
    public T result;
}
