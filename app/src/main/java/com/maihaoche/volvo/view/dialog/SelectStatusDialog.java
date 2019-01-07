package com.maihaoche.volvo.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.SelectStatusDialogBinding;
import com.maihaoche.volvo.ui.car.event.SelectStatusEnvent;

/**
 * Created by gujian
 * Time is 2017/6/12
 * Email is gujian@maihaoche.com
 */

public class SelectStatusDialog extends Dialog {
    private Context mContext;
    private SelectStatusDialogBinding binding;
    public SelectStatusDialog(@NonNull Context context) {
        this(context,0);
    }


    public SelectStatusDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configDialog();
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.select_status_dialog,null,false);
        setContentView(binding.getRoot());
        binding.cancel.setOnClickListener(v->{dismiss();});
        binding.inStorage.setOnClickListener(v->{
            RxBus.getDefault().post(new SelectStatusEnvent("在库"));
            dismiss();
        });
        binding.outStorage.setOnClickListener(v->{
            RxBus.getDefault().post(new SelectStatusEnvent("出库"));
            dismiss();
        });


    }

    private void configDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window dialogWindow = getWindow();
        if(dialogWindow!=null){
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setWindowAnimations(R.style.anim_slide_share_from_bottom);
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            setCancelable(true);
        }
    }
}
