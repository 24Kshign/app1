package com.maihaoche.volvo.view.dialog;

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
import com.maihaoche.volvo.databinding.DialogSelectResonForKeyBinding;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2017/12/27
 * Email is gujian@maihaoche.com
 */

public class SelectReasonForKeyDialog extends Dialog {

    private DialogSelectResonForKeyBinding binding;
    private Reason reason;
    private SureListener listener;

    public SelectReasonForKeyDialog(@NonNull Context context,SureListener listener) {
        super(context);
        this.listener = listener;
    }

    public void setListener(SureListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configCenterDialog(280);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.dialog_select_reson_for_key,null,false);
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.radioTemp.setOnClickListener(v->reason = new Reason(Reason.REASON_TEMP,"临时看车"));
        binding.radioOut.setOnClickListener(v->reason = new Reason(Reason.REASON_OUT,"出库"));
        binding.radioMove.setOnClickListener(v->reason = new Reason(Reason.REASON_MOVE,"移库"));
        binding.cancel.setOnClickListener(v->dismiss());
        binding.sure.setOnClickListener(v->{
            if(listener!=null){
                if(reason == null){
                    reason = new Reason(Reason.REASON_TEMP,"临时看车");
                }
                listener.click(reason);
                dismiss();
            }
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

    public static class Reason{
        public static final int REASON_TEMP = 10;
        public static final int REASON_OUT = 30;
        public static final int REASON_MOVE = 20;

        public int id;
        public String reason;

        public Reason(int id, String reason) {
            this.id = id;
            this.reason = reason;
        }
    }

    public interface SureListener{
        void click(Reason reason);
    }
}
