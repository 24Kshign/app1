package com.maihaoche.volvo.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by wangshengru on 16/1/15.
 * 设备相关工具类
 */
public class DeviceUtil {
    public static final String DEFAULT_IMEI = "000000000000000";


    /**
     * 获取设备的IMEI，即设备标识
     */
    public static String getIMEI(final Context context) {
        String imei = null;
        try {
            imei = null;
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (null != tm) {
                imei = tm.getDeviceId();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(imei)) {
            imei = DEFAULT_IMEI;
        }
        return imei;
    }

    /**
     * 网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        Context ct = context.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) ct
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == cm) {
            return false;
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (null != info) {
                int i = 0;
                while (i < info.length) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                    i++;
                }
            }
        }
        return false;
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();

            if (type.equalsIgnoreCase("WIFI")) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getDeviceWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }


    /**
     * 获取状态栏的高度
     */
    public static int getStatusHeight(Context context) {
        int result = 10;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelOffset(resourceId);
        }
        return result;
    }

    /**
     * 获取屏幕高度
     */
    public static int getDeviceHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取设备系统版本
     *
     * @return 设备系统版本
     */
    public static String getRelease() {
        return Build.VERSION.RELEASE;
    }


    /**
     * 获取设备系统版本号
     *
     * @return 设备系统版本号
     */
    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取设备厂商
     * <p>如Xiaomi</p>
     *
     * @return 设备厂商
     */

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取设备型号
     * <p>如MI2SC</p>
     *
     * @return 设备型号
     */
    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }

    /**
     * 动态设置窗口全屏
     */
    public static void setWindowFullScreen(Activity activity, boolean fullScreen) {
        if (activity == null || activity.getWindow() == null) {
            return;
        }
        View decorView = activity.getWindow().getDecorView();
        if (fullScreen && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        } else {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
