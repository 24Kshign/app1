package com.maihaoche.volvo.ui.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityPayTypeSelectBinding;
import com.maihaoche.volvo.ui.car.domain.OutStorageDetailRequest;
import com.maihaoche.volvo.ui.common.daomain.PayDetail;

import java.util.ArrayList;
import java.util.List;

public class PayTypeSelectActivity extends HeaderProviderActivity<ActivityPayTypeSelectBinding> {

    private static final String CAR_LIST = "car_list";
    public static final int REQUEST_CODE = 99;

    private ActivityPayTypeSelectBinding binding;
    private List<String> list;
    private PayDetail payDetail;

    @Override
    public int getContentResId() {
        return R.layout.activity_pay_type_select;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        initHeader("结算费用");
        binding = getContentBinding();
        list = getIntent().getStringArrayListExtra(CAR_LIST);
        binding.onlineArea.setOnClickListener(v->{

            Intent intent = QRCodeActivity.toActivity(this,payDetail,true);
            startActivityForResult(intent,REQUEST_CODE);
        });
        binding.offlineArea.setOnClickListener(v->{
            Intent intent = QRCodeActivity.toActivity(this,payDetail,false);
            startActivityForResult(intent,REQUEST_CODE);
        });
        reLoad();
    }

    @Override
    protected void reLoad() {
        OutStorageDetailRequest request = new OutStorageDetailRequest();
        request.wmsCarIdList = StringUtil.getLongList(list);
        AppApplication.getServerAPI().payMoney(request)
                .setOnDataGet(response -> {
                    showContent();
                    fillData(response.result);
                })
                .setOnDataError(msg->{
                    showEmpty();
                    AlertToast.show(msg);
                })
                .setDoOnSubscribe(disposable -> {
                    showProgress();
                })
                .call(this);
    }

    private void fillData(PayDetail result) {
        payDetail = result;
        binding.payNumber.setText("￥ "+result.finalFee);
        binding.orderNumber.setText(result.payNo);
    }

    public static void toActivity(Context context, ArrayList<String> list){
        Intent intent = new Intent(context,PayTypeSelectActivity.class);
        intent.putStringArrayListExtra(CAR_LIST,list);
        context.startActivity(intent);
    }

    private PayDetail getTestData(){
        PayDetail payDetail = new PayDetail();
        payDetail.finalFee = "2000";
        payDetail.qrcode = "你好啊";
        payDetail.settlementNo = "NO34534545654";
        return payDetail;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE){
            finish();
        }
    }
}
