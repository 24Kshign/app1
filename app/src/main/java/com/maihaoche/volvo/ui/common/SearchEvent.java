package com.maihaoche.volvo.ui.common;

/**
 * Created by gujian
 * Time is 2017/8/9
 * Email is gujian@maihaoche.com
 */

public class SearchEvent {

    public static final int TYPE_INSTORAGE = 1;
//    public static final int TYPE_INSTORAGED = 2;
    public static final int TYPE_CHECK_CAR = 3;
//    public static final int TYPE_CHECKED_CAR = 4;
    public static final int TYPE_OUT_STORAGE = 5;
//    public static final int TYPE_OUTED_STORAGE = 6;

    private String search;
    private int type;

    public SearchEvent(String search, int type) {
        this.search = search;
        this.type = type;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
