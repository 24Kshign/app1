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
import com.maihaoche.volvo.databinding.SelectKeyDialogBinding;
import com.maihaoche.volvo.ui.instorage.event.SelectKeyEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gujian
 * Time is 2017/6/12
 * Email is gujian@maihaoche.com
 */

public class SelectKeyDialog extends Dialog{

    private Context mContext;
    private SelectKeyDialogBinding binding;
    private List<String> list = new ArrayList<>();
    private String key = "1把";
    public int number = 0;

    public SelectKeyDialog(@NonNull Context context) {
        this(context,0);
    }

    public SelectKeyDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configDialog();
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),R.layout.select_key_dialog,null,false);
        setContentView(binding.getRoot());
        String[] listKey = mContext.getResources().getStringArray(R.array.list_key);
        list.addAll(Arrays.asList(listKey));
        binding.list.setData(list);
        binding.list.setOnSelectListener((position,data)->{
            key = data;
            number = position;
//            Toast.makeText(mContext,position+data+"",Toast.LENGTH_SHORT).show();

        });

        binding.cancel.setOnClickListener(v->{
            dismiss();
        });
        binding.sure.setOnClickListener(v->{
            RxBus.getDefault().post(new SelectKeyEvent(key,number));
            dismiss();
        });
        binding.cancelButton.setOnClickListener(v->dismiss());


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
