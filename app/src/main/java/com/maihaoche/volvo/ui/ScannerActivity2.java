package com.maihaoche.volvo.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.Result;
import com.kernal.smartvision.ocr.OcrHelper;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.service.http.OkHttpHandler;
import com.maihaoche.commonbiz.service.utils.HintUtil;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.BuildConfig;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.ui.car.activity.QrScanCodeAty;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScannerActivity2 extends AppCompatActivity {
    private static final String DAILY_URL = "http://stable-faw.haimaiche.net/tesla/api/v1/qrCode";

    private static final String ONLINE_URL = "https://faw.maihaoche.com/tesla/api/v1/qrCode";
    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String pageId;
    private String carNumber;
    private String url;
    private String bizCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner2);

        url = DAILY_URL;
        if(BuildConfig.DEBUG){
            url = DAILY_URL;
        }else{
            url = ONLINE_URL;
        }

        setupToolbar();

        CaptureFragment captureFragment = new CaptureFragment();
        captureFragment.setAnalyzeCallback(analyzeCallback);
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, captureFragment).commit();

    }

    private CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            handleResult(result);
        }

        @Override
        public void onAnalyzeFailed() {
            AlertToast.show("扫描失败");
        }
    };

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    OcrHelper.CallBack mCallBack = new OcrHelper.CallBack() {
        @Override
        public void onResult(String result, String carAttr) {
            carNumber = result;
            Log.e("Scan", "onResult = " + result);
//            toast("车架号" + result + "已发送给网页");
            postData();
            OcrHelper.goOcrScan(mCallBack, ScannerActivity2.this);
        }
    };

    private void postData() {
        Gson gson = new Gson();
        ScannerActivity2.PostModel postModel = new ScannerActivity2.PostModel();
        if (StringUtil.isNotEmpty(pageId)) {
            postModel.setPageId(pageId);

        } else {
            AlertToast.show("pageId为空，上传失败");
            return;
        }
        if (StringUtil.isNotEmpty(carNumber)) {
            postModel.setCarNumber(carNumber);
        } else {
            AlertToast.show("carNumber为空，上传失败");
            return;
        }
        String content = gson.toJson(postModel);
        OkHttpClient client = OkHttpHandler.getCommonClient();
        RequestBody body = RequestBody.create(JSON, content);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(ScannerActivity2.this, "上传失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(Call call, Response response){
                runOnUiThread(() -> {
                    try {
                        Toast.makeText(ScannerActivity2.this, "请求成功:"+response.body().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            }
        });
    }

    public void handleResult(String rawResult) {
        HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_BARCODE_SCAN);
//        Toast.makeText(this, "Contents = " + rawResult.getText() +
//                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
        String[] address = rawResult.split("\\?");
        String params;
        if (address.length > 1) {
            params = address[1];
        } else {
            return;
        }
        String[] strings = params.split("&");
        if (strings != null) {
            String[] str1 = strings[0].split("=");
            if (str1.length > 1) {
                bizCode = str1[1];
            }

        }
        if (strings.length > 1) {
            String[] str2 = strings[1].split("=");
            if (str2.length > 1) {
                pageId = str2[1];
            }

        }

        AlertToast.show("扫描成功，正在进入车架号识别...");
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                OcrHelper.goOcrScan(mCallBack, ScannerActivity2.this);

//                mZXingScannerView.resumeCameraPreview(ScannerActivity.this);
            }
        }, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HintUtil.getInstance().release();
    }

    private static class PostModel implements Serializable {

        private String pageId;

        private String carNumber;

        public String getPageId() {
            return pageId;
        }

        public void setPageId(String pageId) {
            this.pageId = pageId;
        }

        public String getCarNumber() {
            return carNumber;
        }

        public void setCarNumber(String carNumber) {
            this.carNumber = carNumber;
        }
    }
}
