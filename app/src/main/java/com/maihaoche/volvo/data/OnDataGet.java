package com.maihaoche.volvo.data;

/**
 * 类简介：数据库读取数据的回调
 * 作者：  yang
 * 时间：  17/6/22
 * 邮箱：  yangyang@maihaoche.com
 */

public interface OnDataGet<T> {
    void onDataGet(T t) throws Exception;
}
