package com.maihaoche.commonbiz.service.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.WindowManager;

import com.maihaoche.base.application.BaseApplication;

/**
 * 作者：yang
 * 时间：17/6/12
 * 邮箱：yangyang@maihaoche.com
 */
public class MobileUtil extends ApplicationProvider {
    /**
     * 动态设置窗口全屏
     *
     * @param activity
     * @param fullScreen
     */
    public static void setWindowFullScreen(Activity activity, boolean fullScreen) {
        if (activity == null || activity.getWindow() == null) {
            return;
        }
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        if (fullScreen) {
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            params.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        } else {
            params.flags |= WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
            params.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        activity.getWindow().setAttributes(params);
    }

    /**
     * 手机是否连接网络
     *
     * @return
     */
    public static boolean isNetOn() {
        try {
            ConnectivityManager cm = (ConnectivityManager) BaseApplication.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }


}
