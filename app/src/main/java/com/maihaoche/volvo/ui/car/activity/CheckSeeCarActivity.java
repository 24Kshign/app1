package com.maihaoche.volvo.ui.car.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityCheckSeeCar2Binding;
import com.maihaoche.volvo.databinding.ActivityCheckSeeCarBinding;
import com.maihaoche.volvo.ui.car.domain.CheckSeeCodeRequest;
import com.maihaoche.volvo.util.PhoneFormatCheckUtil;
import com.maihaoche.volvo.view.textView.VerifyCodeView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CheckSeeCarActivity extends HeaderProviderActivity<ActivityCheckSeeCar2Binding> {

    private ActivityCheckSeeCar2Binding binding;

    @Override
    public int getContentResId() {
        return R.layout.activity_check_see_car2;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        initHeader("看车验证");
        binding = getContentBinding();
        binding.phone.setFocusable(true);
        binding.phone.setFocusableInTouchMode(true);
        binding.phone.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.phone, InputMethodManager.SHOW_IMPLICIT);

        binding.sweep.setOnClickListener(v->{
            String phone = binding.phone.getText().toString().trim();
            if(StringUtil.isEmpty(phone)){
                AlertToast.show("请输入手机号");
                return;
            }

            if(!PhoneFormatCheckUtil.isPhoneLegal(phone)){
                AlertToast.show("手机号不合法");
                return;
            }

            checkSeeCode(phone);

        });


    }

    private void checkSeeCode(String code){
        CheckSeeCodeRequest request = new CheckSeeCodeRequest();
        request.verifyCode = code;
        AppApplication.getServerAPI().CheckSeeCode2(request)
                .setOnDataGet(response->{

                    if(response.result == null || response.result.size() == 0){
                        AlertToast.show(response.message);
                        showContent();

                        cancelLoading();
                    }else{
                        cancelLoading();
                        ArrayList<String> appIds = new ArrayList<>();
                        for(Long id :response.result){

                            appIds.add(id+"");
                        }
                        SeeCarDetailActivity2.toActivity(this, appIds);
                        finish();
                    }
                })
                .setOnDataError(emsg -> {
                    AlertToast.show(emsg);
                    cancelLoading();
                })
                .setDoOnSubscribe(disposable -> {
                    showLoading("看车码校验中");
                })
                .call(this);
    }
}
