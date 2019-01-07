package com.maihaoche.volvo.ui.car.cardetail;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ItemCarAttrBinding;

/**
 * 类简介：车辆属性的vh
 * 作者：  yang
 * 时间：  2017/8/16
 * 邮箱：  yangyang@maihaoche.com
 */

public final class CarAttrVH extends RecyclerView.ViewHolder {
    private ItemCarAttrBinding mBinding;

    private int mAttrNameWidthDP = 80;//名字的宽度,单位dp

    public CarAttrVH(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car_attr, parent, false));
        mBinding = DataBindingUtil.bind(itemView);
        setAttrNameWidthDP(mAttrNameWidthDP);
    }

    /**
     * 设置宽度
     */
    public CarAttrVH setAttrNameWidthDP(int attrNameWidthDP) {
        mAttrNameWidthDP = attrNameWidthDP;
        if (mBinding != null) {
            if (mAttrNameWidthDP > 0) {
                mBinding.attrName.setWidth(SizeUtil.dip2px(mAttrNameWidthDP));
            }
        }
        return this;
    }

    public ItemCarAttrBinding getBinding() {
        return mBinding;
    }

    public interface CarAttrVHFactory {
        CarAttrVH get(ViewGroup parent);
    }
}
