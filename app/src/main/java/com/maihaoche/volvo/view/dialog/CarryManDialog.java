package com.maihaoche.volvo.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.DialogCarryBinding;
import com.maihaoche.volvo.databinding.DialogRukuBinding;
import com.maihaoche.volvo.ui.common.daomain.InstorageInfo;

/**
 * Created by gujian
 * Time is 2017/6/9
 * Email is gujian@maihaoche.com
 */

public class CarryManDialog extends Dialog {

    private DialogCarryBinding binding;
    private Context context;
    private String man;
    private String order;
    private String phone;

    public CarryManDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CarryManDialog(@NonNull Context context,String man,String order, String phone) {
        super(context);
        this.context = context;
        this.man = man;
        this.order = order;
        this.phone = phone;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configCenterDialog(280);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.dialog_carry,null,false);
        setContentView(binding.getRoot());

        initView();
    }

    private void initView() {
        binding.man.setText(man);
        binding.order.setText(order);
        binding.phone.setText(phone);
        binding.continueRuku.setOnClickListener(v->{
            dismiss();
        });
    }

    public void configCenterDialog(int widthDP) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window dialogWindow = getWindow();
        if (dialogWindow == null) {
            return;
        }
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        if (widthDP > 0) {
            lp.width = SizeUtil.dip2px(widthDP);
        } else {
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

}
