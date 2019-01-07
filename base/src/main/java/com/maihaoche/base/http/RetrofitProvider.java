package com.maihaoche.base.http;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 类简介：retrofit的构造工具类，需要提供retrofit的组件
 * 作者：  yang
 * 时间：  17/6/6
 * 邮箱：  yangyang@maihaoche.com
 */
public class RetrofitProvider {
    private static Retrofit sRetrofit;
    private RetrofitCompnent mRetrofitCompnent;
    private HashMap<String, Object> dataServiceMap;

    public RetrofitProvider(@NonNull RetrofitCompnent retrofitCompnent) {
        if (retrofitCompnent == null) {
            throw new NullPointerException("RetrofitProvider构造函数中retrofitCompnent不能为空");
        }
        mRetrofitCompnent = retrofitCompnent;
    }


    /**
     * 重新配置组件
     *
     * @param retrofitCompnent
     * @return
     */
    public RetrofitProvider resetCompnent(@NonNull RetrofitCompnent retrofitCompnent) {
        if (retrofitCompnent == null) {
            throw new NullPointerException("RetrofitProvider构造函数中retrofitCompnent不能为空");
        }
        mRetrofitCompnent = retrofitCompnent;
        //清缓存
        sRetrofit = null;
        dataServiceMap.clear();
        return this;
    }

    /**
     * 构造请求的具体实现类，如果有缓存，则使用缓存中的数据
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T provide(Class<T> tClass) {
        if (dataServiceMap == null) {
            dataServiceMap = new HashMap<>();
        }
        if (!dataServiceMap.containsKey(tClass.getName())
                || dataServiceMap.get(tClass.getName()) == null) {
            dataServiceMap.put(tClass.getName(), getRetrofit().create(tClass));
        }
        return (T) dataServiceMap.get(tClass.getName());
    }


    /**
     * 构造retrofit
     */
    private Retrofit getRetrofit() {
        if (sRetrofit == null) {
            synchronized (RetrofitProvider.class) {
                if (sRetrofit == null) {
                    Retrofit.Builder builder = new Retrofit.Builder()
                            .baseUrl(mRetrofitCompnent.getBaseURL())
                            //gson的convertfactory
                            .addConverterFactory(mRetrofitCompnent.getFactory())
                            //okhttp的client
                            .client(mRetrofitCompnent.getClient());
                    //如果自定义了callAdpater，则使用自定义的。否则使用默认的
                    List<CallAdapter.Factory> factories = mRetrofitCompnent.getCallAdapterFactories();
                    if (factories != null && factories.size() > 0) {
                        for (CallAdapter.Factory factory :
                                factories) {
                            builder = builder.addCallAdapterFactory(factory);
                        }
                    } else {
                        //rxjava 的adapter
                        builder = builder.addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync());
                    }
                    sRetrofit = builder.build();
                }
            }
        }
        return sRetrofit;
    }

    /**
     * retrofit的组件，需要根据业务去写实现类
     */
    public interface RetrofitCompnent {

        List<CallAdapter.Factory> getCallAdapterFactories();

        String getBaseURL();

        Converter.Factory getFactory();

        OkHttpClient getClient();
    }


}
