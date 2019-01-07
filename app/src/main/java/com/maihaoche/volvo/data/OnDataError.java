package com.maihaoche.volvo.data;

/**
 * 类简介：数据库操作失败的接口
 * 作者：  yang
 * 时间：  17/6/22
 * 邮箱：  yangyang@maihaoche.com
 */

public interface OnDataError {

    void onDataError(String emsg) throws Exception;
}
