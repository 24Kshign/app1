package com.maihaoche.volvo.server;

import android.text.TextUtils;

import com.maihaoche.base.log.LogUtil;
import com.maihaoche.volvo.AppApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * 作者：yang
 * 时间：17/6/30
 * 邮箱：yangyang@maihaoche.com
 */
public class AppCookieHandler implements CookieJar {

    private static final String TAG = AppCookieHandler.class.getSimpleName();
    private static final String COOKIE_SEESION_ID = "JSESSIONID";
    private static final String COOKIE_MHC_SEEION_ID = "MHCSESSIONID";
    private static final String LOGIN_API = "pda/login.json";

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        String cookieStr = "";
        if (cookies != null && cookies.size() > 0) {
            for (Cookie cookie :
                    cookies) {
                if (TextUtils.isEmpty(cookie.value())) {
                    continue;
                }
                cookieStr+=cookie.name()+":"+cookie.value()+" ";
                if (cookie.name().equals(COOKIE_SEESION_ID)) {
                    if (!cookie.value().equals(AppApplication.getUserPO().getJSessionId())) {
                        AppApplication.getUserPO().setJSessionId(cookie.value());
                    }
                }
                if (cookie.name().equals(COOKIE_MHC_SEEION_ID)) {
                    if (!cookie.value().equals(AppApplication.getUserPO().getMhcSessionId())) {
                        AppApplication.getUserPO().setMhcSessionId(cookie.value());
                    }
                }
            }
        }
        LogUtil.v(TAG,"从网络返回url:"+url+",中获取cookies值为："+cookieStr);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        String cookieStr = "";
        if (url.url().toString().contains(LOGIN_API)) {
            return Collections.emptyList();
        }
        String sesstionId = AppApplication.getUserPO().getJSessionId();
        String mhcSessionId = AppApplication.getUserPO().getMhcSessionId();
        if (!TextUtils.isEmpty(sesstionId) || !TextUtils.isEmpty(mhcSessionId)) {
            Cookie cookieJSesstion = new Cookie.Builder()
                    .domain("mclaren-test-sell.haimaiche.net")
                    .name(COOKIE_SEESION_ID)
                    .value(sesstionId)
                    .build();
            Cookie cookieMhcSession = new Cookie.Builder()
                    .domain("mclaren-test-sell.haimaiche.net")
                    .name(COOKIE_MHC_SEEION_ID)
                    .value(mhcSessionId)
                    .build();
            ArrayList list = new ArrayList<Cookie>();
            list.add(cookieJSesstion);
            list.add(cookieMhcSession);
            cookieStr+=COOKIE_SEESION_ID+":"+sesstionId+" ";
            cookieStr+=COOKIE_MHC_SEEION_ID+":"+mhcSessionId+" ";
            LogUtil.v(TAG,"发起网络请求url:"+url+"，设置cookies值为："+cookieStr);
            return list;

        }
        return Collections.emptyList();
    }
}
