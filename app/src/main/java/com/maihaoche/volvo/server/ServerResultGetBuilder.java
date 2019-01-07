package com.maihaoche.volvo.server;

import com.maihaoche.commonbiz.module.dto.BaseResponse;
import com.maihaoche.volvo.data.DataCallBuilder;
import com.maihaoche.volvo.data.OnDataGet;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * 请求返回类型为BaseResponse<T> ，将T直接传递给回调。而不是baseresponse。
 * 作者：yang
 * 时间：17/6/26
 * 邮箱：yangyang@maihaoche.com
 */
public class ServerResultGetBuilder<T, R> extends DataCallBuilder<BaseResponse<T>> {


    public ServerResultGetBuilder(Single<BaseResponse<T>> single) {
        super();
        mSingle = single;
    }

    public DataCallBuilder<BaseResponse<T>> setOnResultGet(OnDataGet<T> onDaoGet) {
        return super.setOnDataGet(tBaseResponse -> onDaoGet.onDataGet(tBaseResponse.result));
    }


    private Function<BaseResponse<T>, R> resultMap;



    @Override
    public Disposable call() {
        if (mSingle == null) {
            throw new NullPointerException(ServerResultGetBuilder.this.getClass().getSimpleName() + "中：mSingle 不能为空");
        }
        Observable<BaseResponse<T>> observable = mSingle.toObservable()
                .compose(ServerShell.applyServerTransform(onDataError));

        if (mSingleTransformer != null) {
            mSingle = mSingle.compose(mSingleTransformer);
        }
        if (mDoOnSubscribe != null) {
            observable = observable.doOnSubscribe(mDoOnSubscribe);
        }
        if (resultMap != null) {
            Observable<R>  observableR = observable.map(resultMap);
            return observableR.subscribe(t -> {
                if (mOnDataGet != null) {
                    mOnDataGet.onDataGet(t);
                }
            }, throwable -> {
                if (onDataError != null) {
                    onDataError.onDataError(throwable.toString());
                }
            });

        }
        return observable.subscribe(t -> {
            if (mOnDataGet != null) {
                mOnDataGet.onDataGet(t);
            }
        }, throwable -> {
            if (onDataError != null) {
                onDataError.onDataError(throwable.toString());
            }
        });
    }

}
