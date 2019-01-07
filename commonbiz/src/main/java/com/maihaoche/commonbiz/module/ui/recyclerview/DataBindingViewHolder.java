package com.maihaoche.commonbiz.module.ui.recyclerview;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 使用dataBinding封装的viewholder。
 * 作者：yang
 * 时间：17/6/9
 * 邮箱：yangyang@maihaoche.com
 */
public class DataBindingViewHolder extends RecyclerView.ViewHolder {
    ViewDataBinding mViewDataBinding;

    public DataBindingViewHolder(View view) {
        super(view);
        mViewDataBinding = DataBindingUtil.bind(view);
    }

    public <BINDG extends ViewDataBinding> BINDG getBinding() {
        try {
            return (BINDG) mViewDataBinding;
        } catch (ClassCastException e) {
            throw new ClassCastException("getBinding中类型返回出错");
        }
    }
}
