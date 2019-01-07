package com.maihaoche.commonbiz.module.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by gujian
 * Time is 2017/7/27
 * Email is gujian@maihaoche.com
 */

public abstract class BaseFragment<T extends ViewDataBinding> extends HandlerProviderFragment {

    private ViewDataBinding binding;

    protected abstract int getLayoutResId();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(getLayoutResId()!=0){
            binding = DataBindingUtil.inflate(inflater,getLayoutResId(),container,false);
        }
        onAfterCreateView(inflater,container,savedInstanceState);
        return binding.getRoot();
    }

    protected void onAfterCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){

    }

    protected T getLayoutBinding(){
        return (T)binding;
    }

    protected void reload(){}
}
