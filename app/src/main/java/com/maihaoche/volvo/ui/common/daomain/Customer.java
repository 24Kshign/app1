package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gujian
 * Time is 2017/8/14
 * Email is gujian@maihaoche.com
 */

public class Customer implements Serializable {

    @SerializedName("id")
    @Expose
    public Long id;//客户id

    @SerializedName("name")
    @Expose
    public String name;//客户名称

    @SerializedName("phoneNumber")
    @Expose
    public String phoneName;//联系人手机
}
