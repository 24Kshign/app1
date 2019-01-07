package com.maihaoche.volvo.server;

import com.maihaoche.volvo.data.DataCallBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 网络请求的call Adapter factory。用于将网络请求返回的对象设置为我们封装的对象{@link DataCallBuilder}
 * 作者：yang
 * 时间：17/6/26
 * 邮箱：yangyang@maihaoche.com
 */
public final class ServerGetCallAdapterFactory extends CallAdapter.Factory {

    private RxJava2CallAdapterFactory delegateFactory = RxJava2CallAdapterFactory.createAsync();


    private ServerGetCallAdapterFactory() {
    }

    public static ServerGetCallAdapterFactory create() {
        return new ServerGetCallAdapterFactory();
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("returnType：" + returnType.getClass().getSimpleName() + "，必须有泛型参数");
        }
        Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);
        CallAdapter callAdapter = delegateFactory.get(new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return ((ParameterizedType) returnType).getActualTypeArguments();
            }

            @Override
            public Type getRawType() {
                return Single.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        }, annotations, retrofit);
        return new ServerGetCallAdpater<>(returnType, responseType, callAdapter);
    }


    final class ServerGetCallAdpater<R> implements CallAdapter<R, Object> {
        Type returnType;
        Type responseType;
        CallAdapter delegateCallAdpater;

        public ServerGetCallAdpater(Type returnType, Type responseType, CallAdapter delegateCallAdpater) {
            this.responseType = responseType;
            this.returnType = returnType;
            this.delegateCallAdpater = delegateCallAdpater;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public Object adapt(Call<R> call) {
            Object observable = delegateCallAdpater.adapt(call);
            if (observable instanceof Single) {
                try {
                    return ServerGetBuilder.create(returnType, (Single) observable);
                } catch (Exception e) {
                    throw new IllegalArgumentException("ServerGetCallAdpater 返回的类型，必须为DataCallBuilder或其子类");
                }
            } else {
                throw new IllegalArgumentException("ServerGetCallAdpater 只能用于 ServerGetCallAdapterFactory 中");
            }
        }
    }


}
