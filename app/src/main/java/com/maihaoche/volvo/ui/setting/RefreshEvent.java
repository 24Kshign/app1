package com.maihaoche.volvo.ui.setting;

/**
 * Created by gujian
 * Time is 2017/6/15
 * Email is gujian@maihaoche.com
 */

public class RefreshEvent {

    //如果是删除操作，车辆详情里不应该刷新数据
    private boolean isDelete;

    public RefreshEvent(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public RefreshEvent() {
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}
