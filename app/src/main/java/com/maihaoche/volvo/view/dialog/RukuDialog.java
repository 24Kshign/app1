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
import com.maihaoche.volvo.databinding.DialogRukuBinding;

/**
 * Created by gujian
 * Time is 2017/6/9
 * Email is gujian@maihaoche.com
 */

public class RukuDialog extends Dialog {

    private DialogRukuBinding binding;
    private Context context;
    private String content;
    private ContinueListener listener;

    public RukuDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public RukuDialog(@NonNull Context context,String content,ContinueListener listener) {
        super(context);
        this.context = context;
        this.content = content;
        this.listener = listener;
    }

    public void setContent(String content) {
        binding.content.setText(content);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configCenterDialog(280);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.dialog_ruku,null,false);
        setContentView(binding.getRoot());

        initView();
    }

    private void initView() {
        binding.content.setText(content);
        binding.continueRuku.setOnClickListener(v->{
            if(listener!=null) {
                listener.conti();
            }
            dismiss();
        });
        binding.back.setOnClickListener(v->{
            ((Activity)context).finish();
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

    public interface ContinueListener{
        void conti();
    }
}
