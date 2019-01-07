package com.maihaoche.volvo.data;

import com.maihaoche.commonbiz.service.RxJavaCaller;
import com.maihaoche.volvo.server.ServerGetBuilder;
import com.maihaoche.volvo.server.ServerResultGetBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 类简介：发起数据请求的基类
 * （{@link Single}体系，如需要map，filter等操作，需要转换为{@link io.reactivex.Observable}
 * ,方法为{@link Single#toObservable()}）。
 * 作者：  yang
 * 时间：  17/6/22
 * 邮箱：  yangyang@maihaoche.com
 */

public class DataCallBuilder<T> extends RxJavaCaller {
    protected Single<T> mSingle;
    protected OnDataGet mOnDataGet;
    protected Consumer<? super Disposable> mDoOnSubscribe;
    protected OnDataError onDataError;
    protected SingleTransformer mSingleTransformer;

    public DataCallBuilder() {
    }

    public static DataCallBuilder create(Single single) {
        DataCallBuilder dataCallBuilder = new DataCallBuilder();
        dataCallBuilder.setSingle(single);
        return dataCallBuilder;
    }

    public DataCallBuilder<T> setOnDataGet(OnDataGet<T> onDaoGet) {
        this.mOnDataGet = onDaoGet;
        return this;
    }

    public void setSingle(Single<T> single) {
        mSingle = single;
    }

    /**
     * 所有的子类的生成方法。需要重写。
     *
     * @param type
     * @param single
     * @param <T>
     * @return
     */
    public static <T extends DataCallBuilder> T create(Type type, Single single) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (rawType == DataCallBuilder.class) {
                DataCallBuilder dataCallBuilder = new DataCallBuilder();
                dataCallBuilder.setSingle(single);
                return (T) dataCallBuilder;
            } else if (rawType == ServerGetBuilder.class) {
                return (T) new ServerGetBuilder(single);
            } else if (rawType == ServerResultGetBuilder.class) {
                return (T) new ServerResultGetBuilder(single);
            }
        }
        throw new IllegalArgumentException("DataCallBuilder 中的create方法，参数type类型出错:" + type.toString());
    }


    public DataCallBuilder<T> setTransformer(SingleTransformer<T, T> singleTransformer) {
        this.mSingleTransformer = singleTransformer;
        return this;
    }


    public DataCallBuilder<T> setDoOnSubscribe(Consumer<? super Disposable> doOnSubscribe) {
        mDoOnSubscribe = doOnSubscribe;
        return this;
    }

    public DataCallBuilder<T> setOnDataError(OnDataError onDataError) {
        this.onDataError = onDataError;
        return this;
    }

    public Single<T> getSingle(){
        return mSingle;
    }


    @Override
    public Disposable call() {
        if (mSingle == null) {
            throw new NullPointerException(DataCallBuilder.this.getClass().getSimpleName() + "中：mSingle 不能为空");
        }
        if (mSingleTransformer != null) {
            mSingle = mSingle.compose(mSingleTransformer);
        }
        if (mDoOnSubscribe != null) {
            mSingle = mSingle.doOnSubscribe(mDoOnSubscribe);
        }
        return mSingle.subscribe(t -> {
            if (mOnDataGet != null) {
                mOnDataGet.onDataGet(t);
            }
        }, throwable -> {
            if (onDataError != null) {
                onDataError.onDataError(throwable.getMessage());
            }
        });
    }



}
