package com.maihaoche.volvo.ui.common.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityQrcodeBinding;
import com.maihaoche.volvo.ui.common.daomain.PayDetail;
import com.maihaoche.volvo.ui.common.daomain.PayUrlRequest;
import com.maihaoche.volvo.ui.common.daomain.QueryOrderRequest;
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class QRCodeActivity extends HeaderProviderActivity<ActivityQrcodeBinding> {

    private static final String PAYDETAIL = "payDetail";
    private static final String PAY_TYPE = "pay_type";

    private ActivityQrcodeBinding binding;
    private PayDetail payDetail;
    private boolean isZhiFuBao;
    @Override
    public int getContentResId() {
        return R.layout.activity_qrcode;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        initHeader("扫码支付");
        binding = getContentBinding();
        payDetail = (PayDetail) getIntent().getSerializableExtra(PAYDETAIL);
        isZhiFuBao = getIntent().getBooleanExtra(PAY_TYPE,true);
        initView();
        reLoad();


    }

    @Override
    protected void reLoad() {
        super.reLoad();
        if(payDetail!=null){
            refreshCode(payDetail,true);
        }

    }

    private void initView() {
        if(payDetail!=null){

            if(isZhiFuBao){
                binding.tip.setText("打开支付宝扫一扫支付");
            }else{
                binding.tip.setText("打开微信扫一扫支付");
            }

            binding.qrcodeArea.setOnClickListener(v->{
                refreshCode(payDetail,false);
            });
        }


    }

    private void refreshCode(PayDetail payDetail,boolean isFirst) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("二维码刷新...");
        PayUrlRequest request = new PayUrlRequest();
        request.settlementNo = payDetail.settlementNo;
        request.immediate = payDetail.immediate;
        if(isZhiFuBao){
            request.payType = PayUrlRequest.ZHIFUBAO;
        }else{
            request.payType = PayUrlRequest.WEIXIN;
        }
        AppApplication.getServerAPI().getPayUrl(request)
                .setOnDataGet(response->{
                    if(isFirst){
                        binding.sure.setOnClickListener(v->{
                            queryStatus();
                        });
                    }
                    Bitmap bitmap = CodeUtils.createImage(response.result, 400, 400, BitmapFactory.decodeResource(getResources(), R.drawable.ic_loge));
                    binding.qrImage.setImageBitmap(bitmap);
                    dialog.dismiss();
                })
                .setOnDataError(error->{
                    dialog.dismiss();
                    AlertToast.show("刷新失败");
                })
                .setDoOnSubscribe(consum->{
                    dialog.show();
                })
                .call(this);
    }

    public static Intent toActivity(Context context, PayDetail payDetail,boolean isZhiFuBao){
        Intent intent = new Intent(context,QRCodeActivity.class);
        intent.putExtra(PAYDETAIL,payDetail);
        intent.putExtra(PAY_TYPE,isZhiFuBao);
        return intent;
    }

    private void queryStatus(){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("支付查询中");
        QueryOrderRequest request  = new QueryOrderRequest();
        request.settlementNo = payDetail.settlementNo;
        AppApplication.getServerAPI().queryOrderStatus(request)
                .setOnDataGet(response->{
                    dialog.dismiss();
                    if(response.result == 0){
                        AlertToast.show("支付失败");
                    }else if(response.result == 1){
                        setResult(RESULT_OK);
                        finish();
                    }else{
                        AlertToast.show("支付中，请稍等");
                    }
                })
                .setOnDataError(error->{
                    dialog.dismiss();
                    AlertToast.show("查询失败");
                })
                .setDoOnSubscribe(consum->{
                    dialog.show();
                })
                .call(this);
    }
}
