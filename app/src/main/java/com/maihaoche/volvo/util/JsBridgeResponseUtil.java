package com.maihaoche.volvo.util;

import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.data.JsBridgeResponse;

/**
 * Created by manji
 * Date：2018/8/22 下午4:25
 * Desc：
 */
public class JsBridgeResponseUtil {

    public static String getJsBridgeResponse(boolean success, String errorMsg, Object object) {
        JsBridgeResponse jsBridgeResponse;
        Gson gson = new Gson();
        try {
            jsBridgeResponse = new JsBridgeResponse(success, errorMsg, object);
            return gson.toJson(jsBridgeResponse);
        } catch (Exception e) {
            jsBridgeResponse = new JsBridgeResponse(false, "生成json错误", "");
            return gson.toJson(jsBridgeResponse);
        }
    }

    public static String getAppVersion() {
        String sAppVersion;
        try {
            PackageInfo pInfo = AppApplication.getApplication()
                    .getPackageManager()
                    .getPackageInfo(AppApplication.getApplication().getPackageName(), 0);
            sAppVersion = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            sAppVersion="";
        }
        return sAppVersion;
    }
}