package com.maihaoche.volvo.ui.inwarehouse.cars;

/**
 * 类简介：车辆钥匙数量发生变化后的事件
 * 作者：  yang
 * 时间：  2017/8/22
 * 邮箱：  yangyang@maihaoche.com
 */

public class KeyNumChangeEvent {
    int keyNumLeft = 0;//剩余钥匙数量

    public KeyNumChangeEvent(int keyNumLeft) {
        this.keyNumLeft = keyNumLeft;
    }
}
