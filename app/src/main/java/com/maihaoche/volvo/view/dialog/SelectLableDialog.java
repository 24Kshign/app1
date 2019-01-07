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

import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.dao.po.LablePO;
import com.maihaoche.volvo.databinding.SelectLableDialogBinding;
import com.maihaoche.volvo.ui.instorage.event.SelectLableEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by gujian
 * Time is 2017/6/12
 * Email is gujian@maihaoche.com
 */

public class SelectLableDialog extends Dialog {

    private Context mContext;
    private SelectLableDialogBinding binding;
    private List<String> list = new ArrayList<>();
    private List<LablePO> lablePOList = new ArrayList<>();
    private Long mGarageId;
    private int index;

    public SelectLableDialog(@NonNull Context context, Long mGarageId) {
        super(context);
        mContext = context;
        this.mGarageId = mGarageId;
        this.list = new ArrayList<>();
        initData();
    }

    public SelectLableDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configDialog();
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.select_lable_dialog, null, false);
        setContentView(binding.getRoot());
        binding.list.setData(list);


        binding.list.setOnSelectListener((position, data) -> {
            index = position;

        });

        binding.cancel.setOnClickListener(v -> {
            dismiss();
        });
        binding.sure.setOnClickListener(v -> {
            RxBus.getDefault().post(new SelectLableEvent(list.get(index)));
            dismiss();
        });
        binding.cancelButton.setOnClickListener(v -> dismiss());


    }

    private void initData() {
        Disposable disposable = AppApplication.getDaoApi().getLable(mGarageId)
                .setOnResultGet(lablePOs -> {
                    lablePOList = lablePOs;
                    for (LablePO lablePO : lablePOs) {
                        list.add(lablePO.getLableName());
                    }
                    list.add(0,"无");
                })
                .call();
        if (getContext() instanceof BaseActivity) {
            ((BaseActivity) getContext()).pend(disposable);
        }
    }

    public boolean isNoLabel() {
        return list == null || list.size() == 0 || (list.size() == 1 && "无".equals(list.get(0)));
    }

    private void configDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setWindowAnimations(R.style.anim_slide_share_from_bottom);
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            setCancelable(true);
        }
    }
}
