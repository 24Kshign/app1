package com.maihaoche.volvo.util.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.maihaoche.volvo.util.permission.GoToPermissionSetting.REQUEST_CODE_PERMISSION_SETTING;


/**
 * Created by manji
 * Date：2018/12/28 10:10 AM
 * Desc：
 */

public class MIUITypeUtils {

    private static final String TAG = "MIUITypeUtils";

    /**
     * 获取小米 rom 版本号，获取失败返回 -1
     *
     * @return miui rom version code, if fail , return -1
     */
    public static int getMiuiVersion() {
        String version = getSystemProperty("ro.miui.ui.version.name");
        if (version != null) {
            try {
                return Integer.parseInt(version.substring(1));
            } catch (Exception e) {
                Log.e(TAG, "get miui version code error, version : " + version);
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return -1;
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    /**
     * 小米 ROM 权限设置界面
     */
    public static void applyMiuiPermission(Activity context) {
        try {
            int versionCode = getMiuiVersion();
            if (versionCode == 5) {
                goToMiuiPermissionActivity_V5(context);
            } else if (versionCode == 6) {
                goToMiuiPermissionActivity_V6(context);
            } else if (versionCode == 7) {
                goToMiuiPermissionActivity_V7(context);
            } else if (versionCode == 8) {
                goToMiuiPermissionActivity_V8(context);
            } else {
                goToMiuiPermissionActivity_V8(context);
                Log.e(TAG, "this is a special MIUI rom version, its version code " + versionCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isIntentAvailable(Intent intent, Context context) {
        return intent != null && context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    /**
     * 小米 V5 版本 ROM权限申请
     */
    public static void goToMiuiPermissionActivity_V5(Activity context) {
        Intent intent;
        String packageName = context.getPackageName();
        intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivityForResult(intent, REQUEST_CODE_PERMISSION_SETTING);
        } else {
            Log.e(TAG, "intent is not available!");
        }

    }

    /**
     * 小米 V6 版本 ROM权限申请
     */
    public static void goToMiuiPermissionActivity_V6(Activity context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
            context.startActivityForResult(intent, REQUEST_CODE_PERMISSION_SETTING);
        } else {
            Log.e(TAG, "Intent is not available!");
        }
    }

    /**
     * 小米 V7 版本 ROM权限申请
     */
    public static void goToMiuiPermissionActivity_V7(Activity context) {
        goToMiuiPermissionActivity_V6(context);
    }

    /**
     * 小米 V8 版本 ROM权限申请
     */
    public static void goToMiuiPermissionActivity_V8(Activity context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
            context.startActivityForResult(intent, REQUEST_CODE_PERMISSION_SETTING);
        } else {
            Log.e(TAG, "Intent is not available!");
        }
    }
}
