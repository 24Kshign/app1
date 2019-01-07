package com.maihaoche.volvo.ui.login;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.maihaoche.base.log.LogUtil;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.module.ui.NormalDialog;
import com.maihaoche.commonbiz.service.utils.MobileUtil;
import com.maihaoche.commonbiz.service.utils.SharePreferenceHandler;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.dao.po.UserPO;
import com.maihaoche.volvo.ui.MainActivity1;
import com.maihaoche.volvo.ui.avchat.AVChatLibManager;
import com.maihaoche.volvo.util.CheckUpdateUtil;
import com.maihaoche.volvo.util.GpsUtil;
import com.maihaoche.volvo.util.permission.GoToPermissionSetting;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;

import static com.maihaoche.volvo.util.permission.GoToPermissionSetting.REQUEST_CODE_PERMISSION_SETTING;

/**
 * 登录的时候的启动页。
 * 作者：yang
 * 时间：17/6/12
 * 邮箱：yangyang@maihaoche.com
 */
public class LauncherActivity extends BaseActivity {

    private long mTimeOnCreate = 0;

    private static final int BASIC_PERMISSION_REQUEST_CODE = 0x101;

    private static final int REQUEST_GPS_CODE = 0x121;

    private Dialog mGpsDialog;
    private Dialog mLocationDialog;
    private UserPO mUserPO;

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        super.afterCreate(savedInstanceState);
        MobileUtil.setWindowFullScreen(this, true);
        mTimeOnCreate = System.currentTimeMillis();

        initDialog();
        checkPermission();
    }

    private void initDialog() {
        mLocationDialog = new NormalDialog(this)
                .setTilte("定位权限")
                .setContent("定位权限未授权，请到系统设置页面手动授权")
                .setSingleConfirmStr("去授权")
                .setCanceledOutside(false)
                .setIsCancelable(false)
                .setOnOKClickListener(() -> {
                    GoToPermissionSetting.GoToSetting(this);
                });
        mGpsDialog = new NormalDialog(this)
                .setTilte("Gps定位")
                .setContent("检测到您没有开启Gps定位，请去设置页面手动开启")
                .setSingleConfirmStr("开启")
                .setCanceledOutside(false)
                .setIsCancelable(false)
                .setOnOKClickListener(() -> {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_GPS_CODE);
                });
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, BASIC_PERMISSION_REQUEST_CODE);
        } else {
            checkUpdate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GPS_CODE) {
            showGpsDialog();
        } else if (requestCode == REQUEST_CODE_PERMISSION_SETTING) {
            checkPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BASIC_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//用户同意权限,执行我们的操作
                if (null != mLocationDialog && mLocationDialog.isShowing()) {
                    mLocationDialog.dismiss();
                }
                checkUpdate();
            } else {//用户拒绝之后，直接跳转到系统设置页面
                if (null != mLocationDialog && !mLocationDialog.isShowing()) {
                    mLocationDialog.show();
                }
            }
        }
    }

    @Override
    protected boolean isPermissionHandler() {
        return false;
    }

    private void checkUpdate() {
        CheckUpdateUtil.checkUpdate(this, getHandler(), true, (code, msg) -> {
            if (100 == code || msg.contains("已是最新版本")) {
                LoginBiz.getLoginHistory(LauncherActivity.this,
                        userPO -> {
                            mUserPO = userPO;
                            showGpsDialog();
                        });
            } else {
                AlertToast.show(msg);
            }
        });
    }

    private void showGpsDialog() {
        if (!GpsUtil.isGpsOpen(this)) {
            if (null != mGpsDialog && !mGpsDialog.isShowing()) {
                mGpsDialog.show();
            }
        } else {
            if (null != mGpsDialog && mGpsDialog.isShowing()) {
                mGpsDialog.dismiss();
            }
            if (null == mUserPO) {
                toLogin();
            } else {
                onLoginUserGet();
            }
        }
    }

    private void onLoginUserGet() {
        AppApplication.setUserPO(mUserPO);
        SharePreferenceHandler.commitStringPref(LoginBiz.SP_LOGIN_USER_NAME, mUserPO.getUserName());

        //自动登录
        if (SharePreferenceHandler.getPrefBooleanValue(LoginBiz.SP_AUTO_LOGIN, false)) {
            //在线。走接口。
            if (MobileUtil.isNetOn()) {
                LoginBiz.serverLogin(this,
                        mUserPO.getUserName(),
                        mUserPO.getPassWord(),
                        mUserPO.isMhcStaff(),
                        false,
                        userPO1 -> {
                            LoginBiz.getAllData(LauncherActivity.this, false, () -> {
                                AVChatLibManager.getInstance().login(AppApplication.getUserPO().getYunAccId(), AppApplication.getUserPO().getYunToken(), new RequestCallback<LoginInfo>() {
                                    @Override
                                    public void onSuccess(LoginInfo loginInfo) {
                                        LogUtil.v("云信登录成功: " + loginInfo.getAccount());
                                        toMain(mUserPO.isMhcStaff());
                                    }

                                    @Override
                                    public void onFailed(int code) {
                                        LogUtil.e("云信登录失败, code = " + code);
                                        toLogin();
                                    }

                                    @Override
                                    public void onException(Throwable exception) {
                                        LogUtil.e("云信登录失败, e = " + exception);
                                        toLogin();
                                    }
                                });
                            });
                        }, s -> {
                            //登录出错
                            LogUtil.v("登录出错:" + s);
                            toLogin();
                        });
            }
            //不在线.直接进入
            else {
                toMain(mUserPO.isMhcStaff());
            }
        }
        //不自动登录
        else {
            toLogin();
        }

    }

    private void toMain(boolean isMhcStaff) {
        delay(() -> {
            MobileUtil.setWindowFullScreen(this, false);
//            startActivity(MainActivity.createIntent(LauncherActivity.this));
            MainActivity1.start(this, isMhcStaff);
//            startActivity(new Intent(this, InputWebViewActivity.class));
            finish();
        });

    }

    private void toLogin() {
        delay(() -> {
            startActivity(LoginActivity.createIntent(LauncherActivity.this));
            finish();
        });
    }

    private void delay(Runnable runnable) {
        //至少延时2s进入主页面
        long timeToDelay = 2000 - (System.currentTimeMillis() - mTimeOnCreate);
        if (timeToDelay < 0) {
            timeToDelay = 0;
        }
        getHandler().postDelayed(runnable, timeToDelay);
    }

}
