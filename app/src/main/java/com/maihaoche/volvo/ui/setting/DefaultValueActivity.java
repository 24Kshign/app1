package com.maihaoche.volvo.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityDefaultValueBinding;
import com.maihaoche.volvo.ui.instorage.event.SelectKeyEvent;
import com.maihaoche.volvo.ui.instorage.event.SelectLableEvent;
import com.maihaoche.volvo.util.SPUtil;
import com.maihaoche.volvo.view.dialog.SelectKeyDialog;
import com.maihaoche.volvo.view.dialog.SelectLableDialog;

import io.reactivex.disposables.Disposable;


public class DefaultValueActivity extends HeaderProviderActivity<ActivityDefaultValueBinding> {

    private static final String CHOSEN_WMS_GARAGE_ID = "chosen_wms_garage_id";

    private SelectLableDialog mDialog = null;
    private long mWmsGarageId;
    private DefauleValue defauleValue;


    public static Intent createIntent(Context context, long chosenWmsGarageId) {
        Intent intent = new Intent(context, DefaultValueActivity.class);
        intent.putExtra(CHOSEN_WMS_GARAGE_ID, chosenWmsGarageId);
        return intent;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_default_value;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initData();
        initView();
    }

    private void initData() {
        mWmsGarageId = getIntent().getLongExtra(CHOSEN_WMS_GARAGE_ID,0);
        mDialog = new SelectLableDialog(this, mWmsGarageId);
        defauleValue = SPUtil.getValueFromPrefs();
    }

    private void initView() {
        initHeader("自定义操作","保存",v->sure());
        getContentBinding().lableArea.setOnClickListener(v -> {
            if (mDialog != null && !mDialog.isShowing()) {
                mDialog.show();
            }

            Disposable disposable = RxBus.getDefault().register(SelectLableEvent.class, o -> {
                SelectLableEvent event = (SelectLableEvent) o;
                getContentBinding().lableName.setText(event.lable);
            });
            pend(disposable);
        });

        getContentBinding().keyArea.setOnClickListener(v -> {
            new SelectKeyDialog(this).show();
            Disposable disposable = RxBus.getDefault().register(SelectKeyEvent.class, o -> {
                SelectKeyEvent event = (SelectKeyEvent) o;
                getContentBinding().key.setText(event.key);
            });
            pend(disposable);
        });
        if(defauleValue.getLabe() == null || "".equals(defauleValue.getLabe())){
            defauleValue.setLabe("无");
        }
        getContentBinding().setValue(defauleValue);
    }

    private void sure() {

        /**
         * 自动打开vin码识别
         */
        if(getContentBinding().vinCheck.isChecked()){
            defauleValue.setVinCheck(true);
        }else{
            defauleValue.setVinCheck(false);
        }

        /**
         * 默认填写入库
         */
        if(getContentBinding().inStorage.isChecked()){
            defauleValue.setDefaultWrite(true);
        }else{
            defauleValue.setDefaultWrite(false);
        }

        /**
         * 是否有合格证或关单（0-否 1-是）
         */
        if (getContentBinding().certificationHave.isChecked()) {
            defauleValue.setCertification(2);
        } else if (getContentBinding().certificationNo.isChecked()) {
            defauleValue.setCertification(1);
        } else {
            defauleValue.setCertification(0);
        }
        /**
         * 是否有一致性证书（0-否 1-是）
         */
        if (getContentBinding().fitCertifiHave.isChecked()) {
            defauleValue.setFitertifi(2);
        } else if (getContentBinding().fitCertifiNo.isChecked()) {
            defauleValue.setFitertifi(1);
        } else {
            defauleValue.setFitertifi(0);
        }
        /**
         * 是否有商检书（0-否 1-是）
         */
        if (getContentBinding().comInspectHave.isChecked()) {
            defauleValue.setComnspect(2);
        } else if (getContentBinding().comInspectNo.isChecked()) {
            defauleValue.setComnspect(1);
        } else {
            defauleValue.setComnspect(0);
        }
        /**
         * 是否有说明书（0-否 1-是）
         */
        if (getContentBinding().directionHave.isChecked()) {
            defauleValue.setDirection(2);
        } else if (getContentBinding().directionNo.isChecked()) {
            defauleValue.setDirection(1);
        } else {
            defauleValue.setDirection(0);
        }

//        defauleValue.setLabe(getContentBinding().lableName.getText().toString());
//        defauleValue.setKeyNumber(getContentBinding().key.getText().toString());

        SPUtil.saveToPrefs(defauleValue);
        finish();
    }
}
