package com.maihaoche.volvo.util;

import android.content.Context;
import android.os.Build;

import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.BuildConfig;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;

/**
 * Created by manji
 * Date：2018/10/26 11:13 AM
 * Desc：
 */
public class CookieUtil {

    public static void setCookie(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        String sessionId = AppApplication.getUserPO().getMhcSessionId();
        cookieManager.setCookie(BuildConfig.WEB_HOST, "MHCSESSIONID=" + sessionId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager.getInstance().sync();
        }
    }
}
