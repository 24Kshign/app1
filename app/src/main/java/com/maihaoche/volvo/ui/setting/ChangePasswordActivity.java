package com.maihaoche.volvo.ui.setting;

import android.os.Bundle;

import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityChangePasswordBinding;
import com.maihaoche.volvo.util.TextWatcherUtil;


public class ChangePasswordActivity extends HeaderProviderActivity<ActivityChangePasswordBinding> {

    @Override
    public int getContentResId() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initHeader("修改密码");
        initView();
    }

    private void initView() {
        getContentBinding().newPassword.addTextChangedListener(new TextWatcherUtil(0,null,getContentBinding().clearNew));
        getContentBinding().ordPassword.addTextChangedListener(new TextWatcherUtil(0,null,getContentBinding().clearOld));
        getContentBinding().renewPassword.addTextChangedListener(new TextWatcherUtil(0,null,getContentBinding().clearRenew));
        getContentBinding().clearNew.setOnClickListener(v->{
            getContentBinding().newPassword.setText("");
        });
        getContentBinding().clearOld.setOnClickListener(v->{
            getContentBinding().ordPassword.setText("");
        });
        getContentBinding().clearRenew.setOnClickListener(v->{
            getContentBinding().renewPassword.setText("");
        });
        getContentBinding().sureChange.setOnClickListener(v->{
            sureChange();
        });
    }

    private void sureChange() {

    }
}
