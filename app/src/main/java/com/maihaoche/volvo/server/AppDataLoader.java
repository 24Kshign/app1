package com.maihaoche.volvo.server;

import com.maihaoche.base.http.RetrofitProvider;
import com.maihaoche.commonbiz.service.http.GsonConverterFactory;
import com.maihaoche.commonbiz.service.http.OkHttpHandler;
import com.maihaoche.volvo.BuildConfig;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;

/**
 * 类简介：
 * 作者：  yang
 * 时间：  17/6/6
 * 邮箱：  yangyang@maihaoche.com
 */

public class AppDataLoader {

    private static final String TAG = AppDataLoader.class.getSimpleName();

    private static RetrofitProvider sRetrofitProvider = null;

    public static final RetrofitProvider.RetrofitCompnent DEFAULT = new RetrofitProvider.RetrofitCompnent() {
        @Override
        public List<CallAdapter.Factory> getCallAdapterFactories() {
            ArrayList<CallAdapter.Factory> factories = new ArrayList<>();
            factories.add(ServerGetCallAdapterFactory.create());
            return factories;
        }

        @Override
        public String getBaseURL() {
            return BuildConfig.HOST;
        }

        @Override
        public Converter.Factory getFactory() {
            return GsonConverterFactory.create();
        }

        @Override
        public OkHttpClient getClient() {
            return getAppHttpClient();
        }
    };

    public static final RetrofitProvider.RetrofitCompnent GAODE = new RetrofitProvider.RetrofitCompnent() {
        @Override
        public List<CallAdapter.Factory> getCallAdapterFactories() {
            ArrayList<CallAdapter.Factory> factories = new ArrayList<>();
            return factories;
        }

        @Override
        public String getBaseURL() {
            return "https://restapi.amap.com/v3/";
        }

        @Override
        public Converter.Factory getFactory() {
            return GsonConverterFactory.create();
        }

        @Override
        public OkHttpClient getClient() {
            return getAppHttpClient();
        }
    };

    private AppDataLoader() {
    }


    private static OkHttpClient mOkHttpClient = null;

    /**
     * 单例
     */
    public static OkHttpClient getAppHttpClient() {
        if (mOkHttpClient != null) {
            return mOkHttpClient;
        } else {
            synchronized (AppDataLoader.class) {
                if (mOkHttpClient == null) {
                    mOkHttpClient = OkHttpHandler
                            .getCommonClient()
                            .newBuilder()
                            .cookieJar(new AppCookieHandler())
                            .build();
                }
            }
        }
        return mOkHttpClient;
    }


    /**
     * 构建某个接口的实现类。
     *
     * @param restAPI
     * @param <T>
     * @return
     */
    public static <T> T load(Class<T> restAPI) {
        if (sRetrofitProvider == null) {
            synchronized (AppDataLoader.class) {
                if (sRetrofitProvider == null) {
                    sRetrofitProvider = new RetrofitProvider(DEFAULT);
                }
            }
        }
        return sRetrofitProvider.provide(restAPI);
    }

    /**
     * 构建某个接口的实现类。
     *
     * @param restAPI
     * @param <T>
     * @return
     */
    public static <T> T load(Class<T> restAPI, RetrofitProvider.RetrofitCompnent retrofitCompnent) {
        if (sRetrofitProvider == null) {
            synchronized (AppDataLoader.class) {
                if (sRetrofitProvider == null) {
                    sRetrofitProvider = new RetrofitProvider(retrofitCompnent);
                } else {
                    sRetrofitProvider.resetCompnent(retrofitCompnent);
                }
            }
        } else {
            sRetrofitProvider.resetCompnent(retrofitCompnent);
        }
        return sRetrofitProvider.provide(restAPI);
    }
}
