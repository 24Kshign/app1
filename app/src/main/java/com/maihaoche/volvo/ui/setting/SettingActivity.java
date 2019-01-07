package com.maihaoche.volvo.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.BuildConfig;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivitySettingBinding;
import com.maihaoche.volvo.ui.login.LoginBiz;
import com.maihaoche.volvo.util.CheckUpdateUtil;

public class SettingActivity extends HeaderProviderActivity<ActivitySettingBinding> {

    private static final String CHOSEN_WMS_GARAGE_ID = "chosen_wms_garage_id";
    private static final String APK_FILE_NAME = "volvo.apk";


    public static Intent createIntent(Context context, long chosenWmsGarageId) {
        Intent intent = new Intent(context, SettingActivity.class);
        intent.putExtra(CHOSEN_WMS_GARAGE_ID, chosenWmsGarageId);
        return intent;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            initHeader("设置", "环境切换", v -> {
                startActivity(new Intent(this, ChangeNetActivity.class));
            });
        } else {
            initHeader("设置");
        }
        ActivitySettingBinding binding = getContentBinding();
        binding.changePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });
        binding.logout.setOnClickListener(v -> {
            LoginBiz.logout(SettingActivity.this);
            AppApplication.setIsInit(true);
            finish();
        });
        binding.addLable.setOnClickListener(v -> {
            startActivity(LableActivity.createIntent(this, getIntent().getLongExtra(CHOSEN_WMS_GARAGE_ID, 0)));
        });

        binding.checkUpdate.setOnClickListener(v -> {
            CheckUpdateUtil.checkUpdate(this, getHandler(), false, null);
        });

        getContentBinding().setDefault.setOnClickListener(v -> {
            startActivity(new Intent(this, LocationActivity.class));
        });

        refreshVersion();
    }

    private void refreshVersion() {
        try {
            PackageInfo pInfo = AppApplication.getApplication().getPackageManager().getPackageInfo(AppApplication.getApplication().getPackageName(), 0);
            String version = pInfo.versionName;
            getContentBinding().versionTv.setText("当前版本：" + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


}
