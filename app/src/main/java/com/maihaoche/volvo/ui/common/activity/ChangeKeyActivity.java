package com.maihaoche.volvo.ui.common.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.permision.PermissionHandler;
import com.maihaoche.commonbiz.service.utils.DeviceUtil;
import com.maihaoche.commonbiz.service.utils.HintUtil;
import com.maihaoche.commonbiz.service.utils.LightUtil;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.scanlib.BarCodeHelper;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityChangeKeyBinding;
import com.maihaoche.volvo.databinding.ActivityInStorageBinding;
import com.maihaoche.volvo.server.dto.InWarehouseCarVO;
import com.maihaoche.volvo.ui.AbsScanBaseActivity;
import com.maihaoche.volvo.ui.car.activity.QrScanCodeAty;
import com.maihaoche.volvo.ui.common.daomain.BindKeyRequest;
import com.maihaoche.volvo.ui.setting.RefreshEvent;
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class ChangeKeyActivity extends AbsScanBaseActivity<ActivityChangeKeyBinding> {

    private static final String WMS_GARAGE_ID = "wms_garage_id";

    private ActivityChangeKeyBinding binding;

    private InWarehouseCarVO info;

    public static Intent createIntent(Context context, InWarehouseCarVO carVO) {
        Intent intent = new Intent(context, ChangeKeyActivity.class);
        intent.putExtra(WMS_GARAGE_ID, carVO);
        return intent;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_change_key;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        initHeader("钥匙换绑");
        binding = getContentBinding();
        init();
        initListener();
        PermissionHandler.checkWriteSetting(this, granted -> {
            if (granted) {
                LightUtil.setLight(ChangeKeyActivity.this, 255);
            }
        });

    }

    private void init() {
        info = (InWarehouseCarVO) getIntent().getSerializableExtra(WMS_GARAGE_ID);
        binding.oldBarCode.setText(info.keyId+"");
    }

    //监听设置
    private void initListener() {

        binding.sweep.setOnClickListener(v -> {
            if(DeviceUtil.isSENTER()){
                doBarCodeScan(binding.barCode);
            }else{
                PermissionHandler.checkCamera(this, granted -> {
                    if (granted) {
                        Intent intent = new Intent(this,QrScanCodeAty.class);
                        startActivityForResult(intent, QrScanCodeAty.REQUEST_CODE_RFID);
                    }
                });
            }

        });

        binding.sure.setOnClickListener(v -> {
            String code = binding.barCode.getText().toString().trim();
            if (StringUtil.isEmpty(code)) {
                AlertToast.show("请输入条码");
                return;
            }

            String reason = binding.changeReason.getText().toString().trim();

            BindKeyRequest request = new BindKeyRequest();
            request.carId = info.carId;
            request.carStoreType = info.carStoreType;
            request.keyId = code;
            request.oldKeyId = info.keyId;
            request.warehouseId = AppApplication.getGaragePO().getWmsGarageId();
            if(reason!=null){
                request.changeReason = reason;
            }
            AppApplication.getServerAPI().changeCarKey(request)
                    .setOnDataError(emsg -> {
                        AlertToast.show(emsg);
                        showContent();
                    })
                    .setOnDataGet(response -> {
                        showContent();
                        AlertToast.show("重新绑定IC卡成功");
                        RxBus.getDefault().post(new RefreshEvent());
                        finish();
                    })
                    .setDoOnSubscribe(progress->{
                        showProgress();
                    })
                    .call(this);

        });
    }

    /**
     * 进行条码扫描
     */
    private void doBarCodeScan(TextView destEt) {
        if (destEt == null){
            return;
        }

        destEt.requestFocus();
        HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_BARCODE_SCAN);
        BarCodeHelper.scan(result -> {
            if (!TextUtils.isEmpty(result)) {
                HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.BARCODE_SCAN_SUCCEED_WITH_VIBRATOR);
                destEt.setText(result);

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra(CodeUtils.RESULT_STRING);
            binding.barCode.setText(result);
            binding.barCode.setTextColor(Color.BLACK);
        }

    }
}
