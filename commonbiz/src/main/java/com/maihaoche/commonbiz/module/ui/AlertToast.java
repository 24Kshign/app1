package com.maihaoche.commonbiz.module.ui;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.maihaoche.base.application.BaseApplication;
import com.maihaoche.commonbiz.R;

/**
 * 作者：yang
 * 时间：17/6/9
 * 邮箱：yangyang@maihaoche.com
 */
public class AlertToast {
    private static Toast sToast = null;
    public static final int SHOW_TIME = 1_500;
    public static final int LENGTH_LONG = 3000;

    /**
     * 通用toast提示
     *
     * @param resId
     */
    public static void show(int resId) {
        showBlack(resId, SHOW_TIME);
    }

    /**
     * 通用toast提示
     *
     * @param text
     */
    public static void show(String text) {
        showBlack(text, SHOW_TIME);
    }

    /**
     * 线程中使用的toast提示
     *
     * @param text
     */
    public static void showAsync(String text) {
        showBlackFormThread(text, LENGTH_LONG);
    }


    private static void showBlack(String text, int duration) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (sToast == null) {
            View layout = LayoutInflater.from(BaseApplication.getApplication()).inflate(R.layout.toast_view, null);
            sToast = new Toast(BaseApplication.getApplication());
            sToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            sToast.setDuration(duration);
            sToast.setView(layout);
        }
        TextView tv = (TextView) sToast.getView().findViewById(R.id.toast_alert_txt);
        tv.setText(text);
        sToast.show();
    }

    private static void showBlack(int resId, int duration) {
        if (sToast == null) {
            View layout = LayoutInflater.from(BaseApplication.getApplication()).inflate(R.layout.toast_view, null);
            sToast = new Toast(BaseApplication.getApplication());
            sToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            sToast.setDuration(duration);
            sToast.setView(layout);
        }
        TextView tv = (TextView) sToast.getView().findViewById(R.id.toast_alert_txt);
        tv.setText(resId);
        sToast.show();
    }

    private static void showBlackFormThread(String text, int duration) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        Message msg = mHandler.obtainMessage();
        msg.obj = text;
        msg.arg1 = duration;
        mHandler.sendMessage(msg);
    }

    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            showBlack((String) msg.obj, msg.arg1);
        }
    };
}