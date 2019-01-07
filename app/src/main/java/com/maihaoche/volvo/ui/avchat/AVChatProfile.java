package com.maihaoche.volvo.ui.avchat;

import android.os.Handler;

import com.maihaoche.volvo.ui.avchat.framework.Handlers;
import com.maihaoche.volvo.ui.avchat.log.LogUtil;
import com.maihaoche.volvo.ui.avchat.ui.AVChatActivity;
import com.netease.nimlib.sdk.avchat.model.AVChatData;

public class AVChatProfile {

    private final String TAG = "AVChatProfile";

    public static AVChatProfile getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        public final static AVChatProfile instance = new AVChatProfile();
    }

    private boolean isAVChatting = false; // 是否正在音视频通话

    public boolean isAVChatting() {
        return isAVChatting;
    }

    public void setAVChatting(boolean chating) {
        isAVChatting = chating;
    }

    public void launchActivity(final AVChatData data, final int source) {
        Runnable runnable = () -> {
            // 启动，如果 task正在启动，则稍等一下
            if (!AVChatCache.isMainTaskLaunching()) {
                launchActivityTimeout();
                AVChatActivity.launch(AVChatCache.getContext(), data, source);
            } else {
                LogUtil.i(TAG, "AVChatActivity登录检查");
                launchActivity(data, source);
            }
        };
        Handlers.sharedHandler(AVChatCache.getContext()).postDelayed(runnable, 200);
    }

    public void activityLaunched() {
        Handler handler = Handlers.sharedHandler(AVChatCache.getContext());
        handler.removeCallbacks(launchTimeout);
    }

    // 有些设备（比如OPPO、VIVO）默认不允许从后台broadcast receiver启动activity
    // 增加启动activity超时机制
    private void launchActivityTimeout() {
        Handler handler = Handlers.sharedHandler(AVChatCache.getContext());
        handler.removeCallbacks(launchTimeout);
        handler.postDelayed(launchTimeout, 3000);
    }

    private Runnable launchTimeout = () -> {
        // 如果未成功启动，就恢复av chatting -> false
        setAVChatting(false);
    };
}