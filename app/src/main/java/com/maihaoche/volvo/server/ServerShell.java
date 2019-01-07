package com.maihaoche.volvo.server;

import android.content.Intent;

import com.maihaoche.commonbiz.module.dto.BaseResponse;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.data.OnDataError;
import com.maihaoche.volvo.ui.login.LoginActivity;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 网络请求的封装类
 * 作者：yang
 * 时间：17/6/14
 * 邮箱：yangyang@maihaoche.com
 */
public class ServerShell {


    public static final String ERROR_CODE_SESSION_INVALID = "3001";//登录过期的错误码

    /**
     * 服务端的异步处理流
     *
     * @param onDataError
     * @return
     */
    public static <T> ObservableTransformer<T, T> applyServerTransform(OnDataError onDataError) {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(nullFilter(onDataError))
                .filter(businessFilter(onDataError));
    }


    /**
     * 请求获得的数据为空的处理
     */
    public static <T> Predicate<T> nullFilter(OnDataError onDataError) {
        return baseResponse -> {
            if (baseResponse == null) {
                if (onDataError != null) {
                    onDataError.onDataError("网络异常");
                }
                return false;
            }
            return true;
        };
    }

    /**
     * 通用的业务错误码处理
     */
    public static <T> Predicate<T> businessFilter(OnDataError onDataError) {
        return (T response) -> {
            if (response instanceof BaseResponse) {
                BaseResponse baseResponse = (BaseResponse) response;
                if (!baseResponse.success) {
                    if (baseResponse.code.equals(ERROR_CODE_SESSION_INVALID)) {
                        Intent intent = LoginActivity.createIntent(AppApplication.getApplication());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        AppApplication.getApplication().startActivity(intent);
                    } else if (onDataError != null) {
                        onDataError.onDataError(baseResponse.message);
                    }
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * BaseResponse 的result 的map
     */
    public static <T> Function<BaseResponse<T>, T> resultMap() {
        return tBaseResponse -> {
            if (tBaseResponse != null) {
                return tBaseResponse.result;
            }
            return null;
        };
    }

}
