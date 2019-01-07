package com.maihaoche.volvo.ui.car.option;

import android.app.Activity;
import android.text.TextUtils;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.dao.po.CarPO;
import com.maihaoche.volvo.ui.instorage.activity.AddStorageInfoActivity;

import java.util.List;

/**
 * Created by brantyu on 17/6/27.
 * 车辆相关跳转帮助类
 */

public class CarHelper {
    /**
     * 跳转到验车信息填写页面
     *
     * @param activity
     * @param carId
     */
    public static void goCheckCar(Activity activity, String carId) {
        if (activity == null || activity.isFinishing() || TextUtils.isEmpty(carId)){
            return;
        }

        AppApplication.getDaoApi()
                .getCar(carId)
                .setOnResultGet(car -> {
//                    activity.startActivity(AddStorageInfoActivity.createIntent(activity, car, AddStorageInfoActivity.FROM_CAR_DETAIL));
                })
                .setOnDataError(emsg -> {
                    AlertToast.show(emsg);
                })
                .call();
    }

    /**
     * 过滤出未验车的车辆
     *
     * @param list
     */
    public static void filterUncheckCar(List<CarPO> list) {
        if (list == null || list.isEmpty()){
            return;
        }
        for (int index = list.size() - 1; index >= 0; index--) {
            CarPO carPO = list.get(index);
            if (carPO.getCarExtraInfo() != null) {
                list.remove(index);
            }
        }
    }
}
