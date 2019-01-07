package com.maihaoche.volvo.server;

import com.maihaoche.base.log.LogUtil;
import com.maihaoche.volvo.BuildConfig;
import com.maihaoche.volvo.data.DataCallBuilder;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 发起网络请求的builder
 * 作者：yang
 * 时间：17/6/26
 * 邮箱：yangyang@maihaoche.com
 */
public class ServerGetBuilder<T> extends DataCallBuilder<T> {

    //时间错误导致的网络错误
    private static final String ERROR1 = "Could not validate certificate";
    private static final String ERROR2 = "Chain validation failed";
    //请求超时
    private static final String ERROR3 = "time out";
    //无网络
    private static final String ERROR4 = "address associated";


    /**
     * 调试web.true:网络请求的错误会抛出，并定位到该异常代码处。
     * false：网络请求的错误不会抛出，会进入onerror回调处，只给出提示信息。
     */
    protected static boolean sDebugWeb = BuildConfig.DEBUG ? false : false;


    public ServerGetBuilder(Single<T> single) {
        super();
        mSingle = single;
    }


    @Override
    public Disposable call() {
        if (mSingle == null) {
            throw new NullPointerException(ServerGetBuilder.this.getClass().getSimpleName() + "中：mSingle 不能为空");
        }
        Observable observable = mSingle.toObservable()
                .compose(ServerShell.applyServerTransform(onDataError));

        if (mDoOnSubscribe != null) {
            observable = observable.doOnSubscribe(mDoOnSubscribe);
        }
        if (sDebugWeb) {
            return observable.doOnError(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    if (onDataError != null) {
                        onDataError.onDataError(throwable.getMessage());
                    }
                }
            }).subscribe(t -> {
                if (mOnDataGet != null) {
                    mOnDataGet.onDataGet(t);
                }
            });
        } else {
            return observable.subscribe(t -> {
                if (mOnDataGet != null) {
                    mOnDataGet.onDataGet(t);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    LogUtil.e("网络请求出错:"+throwable.getMessage());
                    String msg = throwable.getMessage();
                    if(msg!=null &&(msg.contains(ERROR1) || msg.contains(ERROR2))){
                        msg = "系统时间错误，请修改为正确的时间";
                    }else if(msg!=null && msg.contains(ERROR3)){
                        msg = "请求超时，请检查网络连接或者稍后在试";
                    }else if(msg!=null && msg.contains(ERROR4)){
                        msg = "无网络连接，请检查您的网络是否正常";
                    }
                    if (onDataError != null) {
                        onDataError.onDataError(msg);
                    }
                }
            });
        }
    }
}
