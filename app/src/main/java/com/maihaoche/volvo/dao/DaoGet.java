package com.maihaoche.volvo.dao;

/**
 * 类简介：
 * 作者：  yang
 * 时间：  17/6/22
 * 邮箱：  yangyang@maihaoche.com
 */

import com.maihaoche.commonbiz.service.RxJavaCaller;
import com.maihaoche.volvo.data.DataCallBuilder;
import com.maihaoche.volvo.data.OnDataError;
import com.maihaoche.volvo.data.OnDataGet;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 数据库的一次异步操作。一个操作对应一个builder。
 */
public abstract class DaoGet<T> extends RxJavaCaller implements SingleOnSubscribe<T> {

    private DataCallBuilder<T> mDataCallBuilder;
    public static final String NO_DATA = "数据为空";


    public DaoGet() {
        mDataCallBuilder = DataCallBuilder.create(Single.create(this).compose(DaoShell.getDataTransformer()));
    }

    public DataCallBuilder<T> setOnResultGet(OnDataGet<T> onDaoGet) {
        mDataCallBuilder.setOnDataGet(onDaoGet);
        return mDataCallBuilder;
    }


    public DataCallBuilder<T> setTransformer(SingleTransformer<T, T> singleTransformer) {
        mDataCallBuilder.setTransformer(singleTransformer);
        return mDataCallBuilder;
    }


    public DataCallBuilder<T> setDoOnSubscribe(Consumer<? super Disposable> doOnSubscribe) {
        mDataCallBuilder.setDoOnSubscribe(doOnSubscribe);
        return mDataCallBuilder;
    }

    public DataCallBuilder<T> setOnError(OnDataError onError) {
        mDataCallBuilder.setOnDataError(onError);
        return mDataCallBuilder;
    }

    @Override
    final public void subscribe(@NonNull SingleEmitter<T> e) throws Exception {
        T t = getResult();
        if (t == null) {
            e.onError(new NullPointerException(NO_DATA));
        } else {
            e.onSuccess(t);
        }
    }

    @Override
    public Disposable call() {
        return mDataCallBuilder.call();
    }

    abstract T getResult() throws Exception;

}
