package com.maihaoche.volvo.ui.avchat;

import android.app.Notification;
import android.content.Context;
import android.util.SparseArray;

import com.netease.nimlib.sdk.StatusBarNotificationConfig;

public class AVChatCache {

    private static Context context;

    private static String account;

    private static boolean requestPermission;

    private static StatusBarNotificationConfig notificationConfig;

    private static SparseArray<Notification> notifications = new SparseArray<>();

    private static double latitude;

    private static double longitude;

    public static SparseArray<Notification> getNotifications() {
        return notifications;
    }

    public static void clear() {
        account = null;
    }

    public static String getAccount() {
        return account;
    }

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        AVChatCache.latitude = latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static void setLongitude(double longitude) {
        AVChatCache.longitude = longitude;
    }

    private static boolean mainTaskLaunching;

    public static void setAccount(String account) {
        AVChatCache.account = account;
    }

    public static void setNotificationConfig(StatusBarNotificationConfig notificationConfig) {
        AVChatCache.notificationConfig = notificationConfig;
    }

    public static StatusBarNotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        AVChatCache.context = context.getApplicationContext();
    }

    public static void setMainTaskLaunching(boolean mainTaskLaunching) {
        AVChatCache.mainTaskLaunching = mainTaskLaunching;
    }

    public static boolean isMainTaskLaunching() {
        return mainTaskLaunching;
    }

    public static boolean isRequestPermission() {
        return requestPermission;
    }

    public static void setRequestPermission(boolean requestPermission) {
        AVChatCache.requestPermission = requestPermission;
    }
}
