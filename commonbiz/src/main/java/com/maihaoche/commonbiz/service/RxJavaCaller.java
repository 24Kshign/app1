package com.maihaoche.commonbiz.service;


import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.module.ui.BaseFragmentActivity;

import io.reactivex.disposables.Disposable;

/**
 * 类简介：用于发起rxjava的disposable的请求的接口
 * 作者：  yang
 * 时间：  17/6/22
 * 邮箱：  yangyang@maihaoche.com
 */

public abstract class RxJavaCaller {

    abstract public Disposable call();

    final public void call(BaseActivity activity) {
        if(activity!=null){
            activity.pend(call());
        }
    }

    final public void call(BaseFragmentActivity activity) {
        if(activity!=null){
            activity.pend(call());
        }
    }

}
