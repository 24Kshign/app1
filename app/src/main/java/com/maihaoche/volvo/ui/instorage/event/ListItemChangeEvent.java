package com.maihaoche.volvo.ui.instorage.event;

import com.maihaoche.volvo.ui.car.domain.OutStorageInfo;
import com.maihaoche.volvo.ui.common.daomain.InstorageInfo;

import java.util.ArrayList;

/**
 * Created by gujian
 * Time is 2017/8/11
 * Email is gujian@maihaoche.com
 */

public class ListItemChangeEvent {

    public static final String TYPE_INSTORAGE = "instorage";
    public static final String TYPE_CHECK_CAR = "check_car";
    public static final String TYPE_OUTSTORAGE = "outstorage";
    public static final String TYPE_BATCHOUTSTORAGE = "batchoutstorage";
    public static final String TYPE_UNSTANDINSTORAGE = "unstandinstorage";
    public static final String TYPE_INIT = "init";


    private String type;
    private InstorageInfo instorageInfo;
    private OutStorageInfo outStorageInfo;
    private ArrayList<OutStorageInfo> infos;

    public ListItemChangeEvent(String type) {
        this.type = type;
    }

    public ListItemChangeEvent(String type, InstorageInfo instorageInfo) {
        this.type = type;
        this.instorageInfo = instorageInfo;
    }

    public ListItemChangeEvent(String type, OutStorageInfo outStorageInfo) {
        this.type = type;
        this.outStorageInfo = outStorageInfo;
    }

    public ListItemChangeEvent(String type, ArrayList<OutStorageInfo> infos) {
        this.type = type;
        this.infos = infos;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public InstorageInfo getInstorageInfo() {
        return instorageInfo;
    }

    public void setInstorageInfo(InstorageInfo instorageInfo) {
        this.instorageInfo = instorageInfo;
    }

    public ArrayList<OutStorageInfo> getInfos() {
        return infos;
    }

    public void setInfos(ArrayList<OutStorageInfo> infos) {
        this.infos = infos;
    }

    public OutStorageInfo getOutStorageInfo() {
        return outStorageInfo;
    }

    public void setOutStorageInfo(OutStorageInfo outStorageInfo) {
        this.outStorageInfo = outStorageInfo;
    }
}
