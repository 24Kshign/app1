package com.maihaoche.volvo.util.permission;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.maihaoche.commonbiz.module.ui.AlertToast;

/**
 * Created by manji
 * Date：2018/12/28 10:00 AM
 * Desc：
 */

public class GoToPermissionSetting {

    public static final int REQUEST_CODE_PERMISSION_SETTING = 0x113;

    public static void GoToSetting(Activity activity) {
        try {
            Intent localIntent = new Intent();
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
            activity.startActivityForResult(localIntent, REQUEST_CODE_PERMISSION_SETTING);
        } catch (Exception e) {
            e.printStackTrace();
            AlertToast.show("暂不支持您的手机型号，请到设置中打开");
        }
    }
}