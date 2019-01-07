package com.maihaoche.base.jsbridge.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import com.maihaoche.base.jsbridge.BridgeWebView;
import com.tencent.smtt.sdk.WebSettings;


/**
 * Created by jack on 2017/9/27
 */

public class FRJSBridgeWebView extends BridgeWebView {

    public FRJSBridgeWebView(Context context) {
        this(context, null);
    }

    public FRJSBridgeWebView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FRJSBridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWebView(context);
    }

    private WebSettings mWebSetting;

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(Context context) {
//        setWebContentsDebuggingEnabled(true);
        mWebSetting = getSettings();
        mWebSetting.setJavaScriptEnabled(true);
        mWebSetting.setAllowFileAccess(true);
        mWebSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebSetting.setSupportZoom(false);
        mWebSetting.setBuiltInZoomControls(false);
        mWebSetting.setUseWideViewPort(true);
        mWebSetting.setSupportMultipleWindows(false);
        mWebSetting.setLoadWithOverviewMode(true);
        mWebSetting.setAppCacheEnabled(true);
        mWebSetting.setDatabaseEnabled(true);
        mWebSetting.setGeolocationEnabled(true);
        mWebSetting.setDomStorageEnabled(true);
        mWebSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        mWebSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebSetting.setAppCachePath(context.getDir("appCache", Context.MODE_PRIVATE).getPath());
        mWebSetting.setDatabasePath(context.getDir("databases", Context.MODE_PRIVATE).getPath());
        mWebSetting.setGeolocationDatabasePath(context.getDir("geolocation", Context.MODE_PRIVATE).getPath());
        mWebSetting.setPluginState(WebSettings.PluginState.ON);
        mWebSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebSetting.setTextSize(WebSettings.TextSize.NORMAL);
    }

    public void setUserAgent(String agent){
        mWebSetting.setUserAgentString(mWebSetting.getUserAgentString()+agent);
    }
}