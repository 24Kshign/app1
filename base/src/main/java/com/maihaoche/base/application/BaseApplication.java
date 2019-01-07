package com.maihaoche.base.application;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

/**
 * 类简介：
 * 作者：  yang
 * 时间：  17/6/6
 * 邮箱：  yangyang@maihaoche.com
 */

public class BaseApplication extends MultiDexApplication {

    static Application sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        initInMain();
        new Thread() {
            @Override
            public void run() {
                super.run();
                initAsync();
            }
        }.start();
    }

    public static Application getApplication() {
        return sApplication;
    }

    /**
     * 有些sdk只能在主线程初始化
     */
    protected void initInMain() {
    }

    /**
     * 异步的初始化
     */
    protected void initAsync() {

    }
}
