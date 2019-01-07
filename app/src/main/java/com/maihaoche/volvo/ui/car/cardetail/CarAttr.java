package com.maihaoche.volvo.ui.car.cardetail;

/**
 * 类简介：车辆属性的数据模型
 * 作者：  yang
 * 时间：  2017/8/16
 * 邮箱：  yangyang@maihaoche.com
 */
public class CarAttr {

    public String mAttrName="";
    public String mAttrValue = "";
    public OnCarAttrClick mOnCarAttrClick;

    public CarAttr(String attrName, String attrValue) {
        this(attrName,attrValue,null);
    }

    public CarAttr(String attrName, String attrValue, OnCarAttrClick onCarAttrClick) {
        mAttrName = attrName;
        mAttrValue = attrValue;
        mOnCarAttrClick = onCarAttrClick;
    }

    public interface OnCarAttrClick{
        void onAttrClick(CarAttr carAttr);
    }
}
