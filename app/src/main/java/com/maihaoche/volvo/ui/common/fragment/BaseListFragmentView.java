package com.maihaoche.volvo.ui.common.fragment;

import android.content.Context;

import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.module.ui.mvp.BaseView;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by gujian
 * Time is 2017/8/4
 * Email is gujian@maihaoche.com
 */

public interface BaseListFragmentView<P,V> extends BaseView<P>{

    void fillData(List<V> list);


    void getSearchValue(String string);


    void showEmpty();

    void showContent();

    void showProgress();

    void setHeader(int count);

    void showMore();

    void clear();

    void pend(Disposable disposable);
}
