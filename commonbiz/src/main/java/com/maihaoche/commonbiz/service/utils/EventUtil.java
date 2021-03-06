package com.maihaoche.commonbiz.service.utils;

import android.view.MotionEvent;

public class EventUtil {

    /** 最大同时点击数 */
    private static final int MAX_POINT_NUMBER = 2;

    /** 两次点击最小时间间隔 */
    private static final int MIN_TIME_INTERVAL = 300;

    /** 记录上一次Touch Down的时间 */
    private static long mLastTouchDownTime;

    public static boolean isAllowDispatchEvent(MotionEvent event) {
        // forbid touch event according point index
        if (event.getActionIndex() >= MAX_POINT_NUMBER) {
            return false; // 屏蔽多点事件
        }
        // time interval
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (Math.abs(event.getDownTime() - mLastTouchDownTime) < MIN_TIME_INTERVAL) {
                return false; // 两次点击时间间隔过短，屏蔽该事件
            } else {
                mLastTouchDownTime = event.getDownTime(); // 记录第一次Touch Down时间点
            }
        }
        return true;
    }
}