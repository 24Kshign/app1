package com.maihaoche.volvo.ui.avchat;

import android.content.Context;
import android.text.TextUtils;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.ui.avchat.log.LogUtil;
import com.maihaoche.volvo.ui.avchat.receiver.PhoneCallStateObserver;
import com.maihaoche.volvo.ui.avchat.ui.AVChatActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatControlCommand;
import com.netease.nimlib.sdk.util.NIMUtil;

/**
 * 云信视频管理类（视频通话入口）
 */
public class AVChatLibManager {
    public static final String TAG = AVChatLibManager.class.getSimpleName();

    private static AVChatLibManager sInstance = new AVChatLibManager();

    private AVChatLibManager() {
    }

    public static AVChatLibManager getInstance() {
        return sInstance;
    }

    public void initSDK(Context context) {
        AVChatCache.setContext(context);
        NIMClient.init(context, null, null);
        AVChatCache.setMainTaskLaunching(true);
    }

    public void login(String account, String token, RequestCallback callback) {
        LogUtil.i(TAG, "用户手动登录, 账号->" + account + " token->" + token);
        NIMClient.getService(AuthService.class)
                .login(new LoginInfo(account, token))
                .setCallback(callback);
    }

    /**
     * 云信登出接口
     */
    public void logout() {
        NIMClient.getService(AuthService.class).logout();
    }

    public void enableAVChat(Context context) {
        if (NIMUtil.isMainProcess(context)) {
            LogUtil.i(TAG, "注册用户来电监听");
            registerAVChatIncomingCallObserver(true);
        }
    }

    private void registerAVChatIncomingCallObserver(boolean register) {
        AVChatManager.getInstance().observeIncomingCall(data -> {
            LogUtil.i(TAG, "收到来电广播->" + data.getExtra());
            if (PhoneCallStateObserver.getInstance().getPhoneCallState() != PhoneCallStateObserver.PhoneCallStateEnum.IDLE
                    || AVChatProfile.getInstance().isAVChatting()
                    || AVChatManager.getInstance().getCurrentChatId() != 0) {
                LogUtil.e(TAG, "拒绝远程来电->" + data.toString());
                LogUtil.e(TAG, "判断依据->" + PhoneCallStateObserver.getInstance().getPhoneCallState() + " " +
                        AVChatManager.getInstance().getCurrentChatId());
                AVChatManager.getInstance().sendControlCommand(data.getChatId(), AVChatControlCommand.BUSY, null);
            }
            if (PhoneCallStateObserver.getInstance().getPhoneCallState() != PhoneCallStateObserver.PhoneCallStateEnum.IDLE) {
                AlertToast.show("该手机当前处于非空闲状态，请稍后再试...");
                return;
            }
            if (AVChatManager.getInstance().getCurrentChatId() != 0) {
                AlertToast.show("当前正在音视频通话，请稍后再试...");
                return;
            }
            // 有网络来电打开AVChatActivity
            AVChatProfile.getInstance().setAVChatting(true);
            AVChatProfile.getInstance().launchActivity(data, AVChatActivity.FROM_BROADCASTRECEIVER);
        }, register);
    }

    public void registerOnlineStatusObserver(Context context) {
        if (NIMUtil.isMainProcess(context)) {
            LogUtil.i(TAG, "注册用户状态监听");
            registerOnlineStatusObserver(true);
        }
    }

    private void registerOnlineStatusObserver(boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(statusCode -> {
            LogUtil.e(TAG, "用户状态变化->" + statusCode);
        }, register);
    }

    private LoginInfo getLoginInfo() {
        String account = AppApplication.getUserPO().getYunAccId();
        String token = AppApplication.getUserPO().getYunToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            LogUtil.i(TAG, "自动登录云信SDK, 账号->" + account + " token->" + token);
            AVChatCache.setAccount(account.toLowerCase());
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }
}
