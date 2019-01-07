package com.maihaoche.volvo.ui.car.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.permision.PermissionHandler;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityQrScanCodeAtyBinding;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class QrScanCodeAty extends HeaderProviderActivity<ActivityQrScanCodeAtyBinding> {

    public static final int REQUEST_CODE = 101;
    public static final int REQUEST_CODE_RFID = 102;
    private boolean isFlash = false;
    private ActivityQrScanCodeAtyBinding binding;

    @Override
    public int getContentResId() {
        return R.layout.activity_qr_scan_code_aty;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        binding = getContentBinding();
        initHeader("二维码扫描");
        CaptureFragment captureFragment = new CaptureFragment();
        captureFragment.setAnalyzeCallback(analyzeCallback);
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        /**
         * 替换我们的扫描控件
         */
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
        binding.flash.setOnClickListener(v->{
            if(isFlash){
                isFlash = !isFlash;
                binding.flashText.setText("打开闪光灯");
                CodeUtils.isLightEnable(false);
                binding.flashIcon.setImageResource(R.drawable.close_flash);
            }else{
                isFlash = !isFlash;
                binding.flashText.setText("关闭闪光灯");
                CodeUtils.isLightEnable(true);
                binding.flashIcon.setImageResource(R.drawable.open_flash);
            }
        });
    }

    private CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            QrScanCodeAty.this.setResult(RESULT_OK, resultIntent);
            QrScanCodeAty.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            QrScanCodeAty.this.setResult(RESULT_OK, resultIntent);
            QrScanCodeAty.this.finish();
        }
    };


}
