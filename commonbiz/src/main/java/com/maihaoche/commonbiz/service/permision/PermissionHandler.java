package com.maihaoche.commonbiz.service.permision;

import android.Manifest;
import android.app.Activity;

/**
 * 权限工具类对外的API
 * 支持多个请求同时申请，支持多个请求顺序申请(放入队列，一个一个处理)
 * 作者：yang
 * 时间：16/9/18
 * 邮箱：yangyang@maihaoche.com
 * <p>
 * 安卓的权限分为两类，一类是安全权限，即应用注册在manifest里面，即可以获取得到的。
 * 还有一类是敏感权限。该类权限是受到系统保护的，应用想获取需要动态的申请，并被用户操作授权后才能得到。
 * 如果不申请敏感权限，就直接调用这些权限相关的API，就会报SecurityExecption。
 * 目前我们应用用到的敏感权限包括：
 * {@link Manifest.permission#READ_PHONE_STATE  比如获取手机设备id
 *
 * @link Manifest.permission#READ_EXTERNAL_STORAGE  比如获取手机相册
 * @link Manifest.permission#CAMERA  比如获取手机相机
 * <p>
 * 6.0划分权限：
 * 安全的权限：
 * ACCESS_LOCATION_EXTRA_COMMANDS
 * ACCESS_NETWORK_STATE
 * ACCESS_NOTIFICATION_POLICY
 * ACCESS_WIFI_STATE
 * BLUETOOTH
 * BLUETOOTH_ADMIN
 * BROADCAST_STICKY
 * CHANGE_NETWORK_STATE
 * CHANGE_WIFI_MULTICAST_STATE
 * CHANGE_WIFI_STATE
 * DISABLE_KEYGUARD
 * EXPAND_STATUS_BAR
 * GET_PACKAGE_SIZE
 * INSTALL_SHORTCUT
 * INTERNET
 * KILL_BACKGROUND_PROCESSES
 * MODIFY_AUDIO_SETTINGS
 * NFC
 * READ_SYNC_SETTINGS
 * READ_SYNC_STATS
 * RECEIVE_BOOT_COMPLETED
 * REORDER_TASKS
 * REQUEST_INSTALL_PACKAGES
 * SET_ALARM
 * SET_TIME_ZONE
 * SET_WALLPAPER
 * SET_WALLPAPER_HINTS
 * TRANSMIT_IR
 * UNINSTALL_SHORTCUT
 * USE_FINGERPRINT
 * VIBRATE
 * WAKE_LOCK
 * WRITE_SYNC_SETTINGS
 * <p>
 * 不安全的权限：
 * group:android.permission-group.CONTACTS
 * permission:android.permission.WRITE_CONTACTS
 * permission:android.permission.GET_ACCOUNTS
 * permission:android.permission.READ_CONTACTS
 * <p>
 * group:android.permission-group.PHONE
 * permission:android.permission.READ_CALL_LOG
 * permission:android.permission.READ_PHONE_STATE
 * permission:android.permission.CALL_PHONE
 * permission:android.permission.WRITE_CALL_LOG
 * permission:android.permission.USE_SIP
 * permission:android.permission.PROCESS_OUTGOING_CALLS
 * permission:com.android.voicemail.permission.ADD_VOICEMAIL
 * <p>
 * group:android.permission-group.CALENDAR
 * permission:android.permission.READ_CALENDAR
 * permission:android.permission.WRITE_CALENDAR
 * <p>
 * group:android.permission-group.CAMERA
 * permission:android.permission.CAMERA
 * <p>
 * group:android.permission-group.SENSORS
 * permission:android.permission.BODY_SENSORS
 * <p>
 * group:android.permission-group.LOCATION
 * permission:android.permission.ACCESS_FINE_LOCATION
 * permission:android.permission.ACCESS_COARSE_LOCATION
 * <p>
 * group:android.permission-group.STORAGE
 * permission:android.permission.READ_EXTERNAL_STORAGE
 * permission:android.permission.WRITE_EXTERNAL_STORAGE
 * <p>
 * group:android.permission-group.MICROPHONE
 * permission:android.permission.RECORD_AUDIO
 * <p>
 * group:android.permission-group.SMS
 * permission:android.permission.READ_SMS
 * permission:android.permission.RECEIVE_WAP_PUSH
 * permission:android.permission.RECEIVE_MMS
 * permission:android.permission.RECEIVE_SMS
 * permission:android.permission.SEND_SMS
 * permission:android.permission.READ_CELL_BROADCASTS
 * <p>
 * <p>
 * }
 */

public class PermissionHandler {
    /**
     * 封装好的检查权限的函数检查权限
     *
     * @param activity         当前的activity
     * @param hintContent      申请该权限时的提示文案
     * @param permission       具体的权限名，数组形式，支持同时申请多个权限
     * @param requestInterface 监听申请权限结果的回调
     */
    public static void checkPermission(
            final Activity activity
            , String hintContent
            , final String[] permission
            , final PermissionRequestInterface requestInterface
    ) {
        PermissionUtils.checkPermission(activity, hintContent, permission, requestInterface);
    }

    /**
     * 权限申请结果回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * 读取手机外部存储器的权限
     *
     * @param activity
     * @param permissionRequestInterface
     */
    public static void checkReadExternalStorage(Activity activity, PermissionRequestInterface permissionRequestInterface) {
        checkPermission(activity
                , "需要读取手机存储,是否授权?"
                , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                , permissionRequestInterface);
    }

    /**
     * 申请手机拍照的权限
     *
     * @param activity
     * @param permissionRequestInterface
     */
    public static void checkCamera(Activity activity, PermissionRequestInterface permissionRequestInterface) {
        checkPermission(activity
                , "需要访问手机相机,是否授权?"
                , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}
                , permissionRequestInterface);
    }

    /**
     * 申请手机拍照的权限
     *
     * @param activity
     * @param permissionRequestInterface
     */
    public static void checkWriteSetting(Activity activity, PermissionRequestInterface permissionRequestInterface) {
        checkPermission(activity
                , "需要修改手机设置,是否授权?"
                , new String[]{Manifest.permission.WRITE_SETTINGS}
                , permissionRequestInterface);
    }


    /**
     * 申请获取手机设备信息的权限,在获取设备id，在调用系统服务TELEPHONY_SERVICE的getDeviceId接口时，需要该权限。
     *
     * @param activity
     * @param permissionRequestInterface
     */
    public static void checkReadPhoneState(Activity activity, PermissionRequestInterface permissionRequestInterface) {
        checkPermission(activity
                , "需要获取手机设备信息,是否授权?"
                , new String[]{Manifest.permission.READ_PHONE_STATE}
                , permissionRequestInterface);
    }


    /**
     * 权限申请回调的接口
     */
    public interface PermissionRequestInterface {
        /**
         * 当权限请求完毕后的回调
         *
         * @param granted 是否授权。true，已授权。false，未授权
         */
        void onPermissionGranted(boolean granted);
    }

}
