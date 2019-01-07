package com.maihaoche.volvo.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.maihaoche.base.log.LogUtil;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.service.utils.MobileUtil;
import com.maihaoche.commonbiz.service.utils.SharePreferenceHandler;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.BuildConfig;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.dao.po.UserPO;
import com.maihaoche.volvo.databinding.ActivityLoginBinding;
import com.maihaoche.volvo.ui.MainActivity1;
import com.maihaoche.volvo.ui.avchat.AVChatLibManager;
import com.maihaoche.volvo.ui.setting.ChangeNetActivity;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;


/**
 * 作者：yang
 * 时间：17/6/12
 * 邮箱：yangyang@maihaoche.com
 */
public class LoginActivity extends BaseActivity<ActivityLoginBinding> {


    private String mUserName = "";
    private String mPassWord = "";
    private boolean isMhcStaff = true;

    private ActivityLoginBinding binding;


    public static Intent createIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        super.afterCreate(savedInstanceState);
        init();
        load();
    }

    private void init() {
        MobileUtil.setWindowFullScreen(this, true);
        binding = getLayoutBinding();
        binding.username.addTextChangedListener(mMobileTextWathcer);
        binding.password.addTextChangedListener(mPasswordTextWatcher);
        binding.login.setOnClickListener(v -> login());
        if (BuildConfig.DEBUG) {
            binding.changeNet.setVisibility(View.VISIBLE);
            binding.changeNet.setOnClickListener(v -> {
                startActivity(new Intent(this, ChangeNetActivity.class));
            });
        } else {
            binding.changeNet.setVisibility(View.GONE);
        }
        binding.alTvMhcStaff.setOnClickListener(v -> {
            isMhcStaff = true;
            setMhcStaffStyle();
        });
        binding.alTvNotMhcStaff.setOnClickListener(v -> {
            isMhcStaff = false;
            setMhcStaffStyle();
        });
        refreshVersion();
    }

    private void setMhcStaffStyle() {
        binding.alTvMhcStaff.setEnabled(!isMhcStaff);
        binding.alTvNotMhcStaff.setEnabled(isMhcStaff);
        binding.alTvMhcStaff.setTextSize(isMhcStaff ? 18 : 14);
        binding.alTvNotMhcStaff.setTextSize(isMhcStaff ? 14 : 18);
    }

    private void load() {
        if (!SharePreferenceHandler.getPrefBooleanValue(LoginBiz.SP_IS_LOGIN, false)) {
            binding.autoLogin.setChecked(true);
        } else {
            binding.autoLogin.setChecked(SharePreferenceHandler.getPrefBooleanValue(LoginBiz.SP_AUTO_LOGIN, false));
        }
        LoginBiz.getLoginHistory(this,
                userPO -> {
                    if (userPO != null) {
                        AppApplication.setUserPO(userPO);
                        binding.username.setText(userPO.getUserName());
                        binding.password.setText(userPO.getPassWord());
                    }
                });
    }

    /**
     * 登录
     */
    private void login() {
        //有网络。进行网络请求登录
        if (MobileUtil.isNetOn()) {
            loginOnline();
        } else {
            loginOffline();
        }
    }

    private void loginOnline() {
        //先登录
        LoginBiz.serverLogin(LoginActivity.this
                , mUserName
                , mPassWord
                , isMhcStaff
                , true
                , (UserPO userPO) -> {
                    LoginBiz.getAllData(LoginActivity.this, true, () -> {
                        AVChatLibManager.getInstance().login(AppApplication.getUserPO().getYunAccId(), AppApplication.getUserPO().getYunToken(), new RequestCallback<LoginInfo>() {
                            @Override
                            public void onSuccess(LoginInfo loginInfo) {
                                LogUtil.v("云信登录成功: " + loginInfo.getAccount());
                                onDataSaved();
                            }

                            @Override
                            public void onFailed(int code) {
                                AlertToast.show("云信登录失败, code = " + code);
                            }

                            @Override
                            public void onException(Throwable exception) {
                                AlertToast.show("云信登录失败, exception = " + exception);
                            }
                        });
                    });
                }
                , s -> AlertToast.show(s));
    }

    /**
     * 保存完仓库数据和车辆数据后，走登录接口。
     */
    private void onDataSaved() {
        //保存完所有数据后，进入下一个页面
        SharePreferenceHandler.commitStringPref(LoginBiz.SP_LOGIN_USER_NAME, AppApplication.getUserName());
        toMain();
    }

    /**
     * 离线登录
     */
    private void loginOffline() {
        //在本地数据库中查到该数据
        AppApplication.getDaoApi().getUser(mUserName)
                .setDoOnSubscribe(disposable -> AlertToast.show("离线登录中..."))
                .setOnDataGet(userPO -> {
                    if (!userPO.getPassWord().equals(mPassWord)) {
                        AlertToast.show("离线登录。密码错误，若密码发生修改，请联网重新登录");
                        return;
                    }
                    AppApplication.setUserPO(userPO);
                    toMain();
                })
                .setOnDataError(emsg -> {
                    AlertToast.show("离线登录出错：没有历史登录的数据，请先联网登录");
                })
                .call(LoginActivity.this);
    }


    /**
     * 获得数据，进入主页
     */
    private void toMain() {
        MobileUtil.setWindowFullScreen(this, false);
        SharePreferenceHandler.commitBooleanPref(LoginBiz.SP_AUTO_LOGIN, binding.autoLogin.isChecked());
//        startActivity(MainActivity.createIntent(LoginActivity.this));
        MainActivity1.start(this, isMhcStaff);
//        startActivity(new Intent(this, InputWebViewActivity.class));
        finish();
    }

    private void refreshVersion() {
        try {
            PackageInfo pInfo = AppApplication.getApplication().getPackageManager().getPackageInfo(AppApplication.getApplication().getPackageName(), 0);
            String version = pInfo.versionName;
            binding.versionText.setText("库管助手" + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 允许登录
     */
    private void setLoginEnable(boolean enable) {
        binding.login.setEnabled(enable);
    }

    private TextWatcher mMobileTextWathcer = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mUserName = s.toString();
            setLoginEnable(!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mPassWord));
        }
    };

    private TextWatcher mPasswordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mPassWord = s.toString();
            setLoginEnable(!TextUtils.isEmpty(mPassWord) && !TextUtils.isEmpty(mUserName));
        }
    };

}
