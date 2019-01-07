package com.maihaoche.commonbiz.service.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gujian
 * Time is 2017/6/9
 * Email is gujian@maihaoche.com
 */

public class DateUtil {

    public static final String YMD = "yyyy-MM-dd";
    public static final String YMDHM = YMD + " HH:mm";

    public static String getCurrenTime() {
        SimpleDateFormat formatter = new SimpleDateFormat(YMD);
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    /**
     * 输出时间格式：{@link #YMDHM}
     */
    public static String getDateYMDHM(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(YMDHM);
        Date data = new Date(time);
        return formatter.format(data);
    }

    /**
     * 获取今天零点的时间轴
     */
    public static long getTodayZero() {
        return System.currentTimeMillis() / (1000 * 3600 * 24) * (1000 * 3600 * 24);
    }

    public static String getFormatTime(long time){
        SimpleDateFormat formatter = new SimpleDateFormat(YMDHM);
        Date data = new Date(time);
        return formatter.format(data);
    }

}
