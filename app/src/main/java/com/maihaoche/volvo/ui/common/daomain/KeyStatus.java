package com.maihaoche.volvo.ui.common.daomain;

import com.maihaoche.commonbiz.service.utils.StringUtil;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/1/17
 * Email is gujian@maihaoche.com
 */

public enum KeyStatus {
    WAITE_BIND(0,"待绑定","绑定钥匙"),
    WAITE_IN(10,"待入柜",""),
    USING(20,"取用中",""),
    WAITE_APPLY(30,"","申请取用"),
    WAITE_CANCEL(40,"申请已通过","取消取用"),
    WAITE_OUT(50,"待出柜",""),
    NULL(9999,"","")
    ;


    private int code;
    private String desc;
    private String btnText;

    KeyStatus(int code, String desc, String btnText) {
        this.code = code;
        this.desc = desc;
        this.btnText = btnText;
    }

    public static KeyStatus getStatus(int code){
        switch (code){
            case 0:
                return WAITE_BIND;
            case 10:
                return WAITE_IN;
            case 20:
            case 60:
                return USING;
            case 30:
                return WAITE_APPLY;
            case 40:
                return WAITE_CANCEL;
            case 50:
                return WAITE_OUT;
            default:
                return WAITE_BIND;
        }
    }

    public boolean hasBtn(){
        if(StringUtil.isEmpty(btnText)){
            return false;
        }
        return true;
    }

    public boolean hasDesc(){
        if(StringUtil.isEmpty(desc)){
            return false;
        }
        return true;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }
}
