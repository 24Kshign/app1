package com.maihaoche.commonbiz.service.http;

import com.maihaoche.commonbiz.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 类简介：
 * 作者：  yang
 * 时间：  17/6/6
 * 邮箱：  yangyang@maihaoche.com
 */

public class OkHttpHandler {
    private static final int TIMEOUT = 20_000;

    private OkHttpHandler() {
    }

    public static OkHttpClient getCommonClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //日志打印拦截工具
        OkHttpClient client = builder.addInterceptor(new HttpLoggingInterceptor().setLevel(
                BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE
        ))
                //超时设置
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
        //每个host最多同时发起的请求数。默认配置是5，太小了。扩大到20.
        client.dispatcher().setMaxRequestsPerHost(20);
        return client;
    }

}
