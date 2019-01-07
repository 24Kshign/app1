package com.maihaoche.commonbiz.service.utils;

import android.os.Build;

/**
 * Created by brantyu on 17/7/24.
 */

/**
 * 设备工具类
 */
public class DeviceUtil {

    public static final String MANUFACTURER_UBX = "UBX";
    public static final String MANUFACTURER_SENTER = "SENTER";
    public static final String MODEL_PDA = "PDA";

    /**
     * 判断是否是优博讯设备
     *
     * @return
     */
    public static boolean isUBX() {
        return MANUFACTURER_UBX.equals(Build.MANUFACTURER);
    }


    /**
     * 判断是否是信通设备
     *
     * @return
     */
    public static boolean isSENTER() {
        return MANUFACTURER_SENTER.equals(Build.MANUFACTURER) || MODEL_PDA.equals(Build.MODEL);
    }
}