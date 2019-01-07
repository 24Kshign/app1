package com.maihaoche.volvo.ui.instorage.event;

import java.io.Serializable;

/**
 * Created by gujian
 * Time is 2017/8/2
 * Email is gujian@maihaoche.com
 */

public class BrandEvent implements Serializable {

    public static final int TYPE_BRAND = 1;
    public static final int TYPE_CAR_SERIES = 2;
    public static final int TYPE_CLIENT = 3;
    public static final int TYPE_MAN = 4;

    private String string;
    private Long code;
    private int type;
    private String phone;

    public BrandEvent(String string,Long code, int type) {
        this.string = string;
        this.code = code;
        this.type = type;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
