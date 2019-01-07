package com.maihaoche.volvo.ui.instorage.event;

/**
 * Created by gujian
 * Time is 2017/6/12
 * Email is gujian@maihaoche.com
 */

public class SelectKeyEvent {

    public String key;
    public int number;

    public SelectKeyEvent(String key, int number) {
        this.key = key;
        this.number = number;
    }
}
