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
import com.maihaoche.volvo.databinding.DialogDeleteBinding;
import com.maihaoche.volvo.ui.vo.Car;

/**
 * Created by gujian
 * Time is 2017/6/9
 * Email is gujian@maihaoche.com
 */

public class CommonDialog extends Dialog {

    private DialogDeleteBinding binding;
    private Context context;
    private OutClickListener listener;
    private OutCancelListener cancelListener;
    private String title;
    private String content;

    public CommonDialog(Context context) {
        this(context,"","",null);
    }

    public CommonDialog(@NonNull Context context,String title,String content, OutClickListener listener) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
        this.listener = listener;
    }

    public CommonDialog(@NonNull Context context,String title,String content, OutClickListener listener,OutCancelListener cancelListener) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
        this.listener = listener;
        this.cancelListener = cancelListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configCenterDialog(280);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.dialog_delete,null,false);
        setContentView(binding.getRoot());
        initView();
        setCanceledOnTouchOutside(false);
    }

    private void initView() {
        binding.continueRuku.setOnClickListener(v->{
            dismiss();
            if(listener!=null) {
                listener.click();
            }

        });

        binding.back.setOnClickListener(v->{
            if(cancelListener!=null){
                cancelListener.cancel();
            }else{
                dismiss();
            }
            dismiss();
        });
        binding.title.setText(title);
        binding.content.setText(content);
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

    public interface OutClickListener{
        void click();
    }

    public interface OutCancelListener{
        void cancel();
    }
}
