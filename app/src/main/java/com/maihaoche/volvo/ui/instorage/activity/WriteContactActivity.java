package com.maihaoche.volvo.ui.instorage.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.commonbiz.service.utils.SoftKeyBoardUtil;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityWriteContactBinding;
import com.maihaoche.volvo.ui.instorage.event.BrandEvent;
import com.maihaoche.volvo.ui.instorage.event.ContactEvent;


public class WriteContactActivity extends HeaderProviderActivity<ActivityWriteContactBinding> {

    private static final String TYPE_INFO = "brand_info";

    private ActivityWriteContactBinding binding;
    private BrandEvent brandEvent;

    public static Intent create(Context context,BrandEvent brandEvent){
        Intent intent = new Intent(context,WriteContactActivity.class);
        intent.putExtra(TYPE_INFO,brandEvent);
        return intent;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_write_contact;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {

        initHeader("自定义联系人");
        binding = getContentBinding();
        brandEvent = (BrandEvent) getIntent().getSerializableExtra(TYPE_INFO);
        if(brandEvent!=null){
            binding.name.setText(brandEvent.getString());
            binding.phone.setText(brandEvent.getPhone());
        }
        SoftKeyBoardUtil.showKeyBoardDely(binding.name);
        binding.save.setOnClickListener(v->{
            save();
        });
    }

    private void save() {
        String name = binding.name.getText().toString().trim();
        String phone = binding.phone.getText().toString().trim();
        if(StringUtil.isEmpty(name)){
            AlertToast.show("请输入联系人姓名");
            return;
        }

        if(StringUtil.isEmpty(phone)){
            AlertToast.show("请输入联系人电话");
            return;
        }
        BrandEvent event = new BrandEvent(name,0L,BrandEvent.TYPE_MAN);
        event.setPhone(phone);
        RxBus.getDefault().post(event);
        finish();
    }
}
