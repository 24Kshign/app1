package com.maihaoche.volvo.ui.inwarehouse;

import android.text.TextUtils;
import android.view.View;

import com.maihaoche.volvo.databinding.IncludeCarInfoBinding;
import com.maihaoche.volvo.server.dto.CarBaseInfoOnMailVO;

/**
 * 类简介：在库的工具类
 * 作者：  yang
 * 时间：  2017/8/15
 * 邮箱：  yangyang@maihaoche.com
 */
public class InWarehouseBiz {


    public static void fillBaseCarInfo(IncludeCarInfoBinding carInfoBinding,CarBaseInfoOnMailVO vo){
        if(carInfoBinding==null || vo==null){
            return;
        }
        //车辆信息
        carInfoBinding.carUnique.setText(vo.carUnique);
        carInfoBinding.carAttribute.setText(vo.carAttribute);
        if (TextUtils.isEmpty(vo.carTagId)) {
            carInfoBinding.vinArea.setVisibility(View.GONE);
        } else {
            carInfoBinding.vinCode.setText(vo.carTagId);
            carInfoBinding.vinArea.setVisibility(View.VISIBLE);
        }
        if(TextUtils.isEmpty(vo.remark)){
            carInfoBinding.remarkArea.setVisibility(View.GONE);
        }else {
            carInfoBinding.remark.setText(vo.remark);
            carInfoBinding.remarkArea.setVisibility(View.VISIBLE);
        }
        carInfoBinding.time.setText(vo.entryDate);
        //入库时间不需要显示
        carInfoBinding.timeArea.setVisibility(View.GONE);

    }

}
