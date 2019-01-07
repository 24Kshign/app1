//package com.maihaoche.volvo.ui;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.Nullable;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.MenuItem;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.google.gson.Gson;
//import com.google.zxing.Result;
//import com.kernal.smartvision.ocr.OcrHelper;
//import com.maihaoche.commonbiz.service.http.OkHttpHandler;
//import com.maihaoche.commonbiz.service.utils.HintUtil;
//import com.maihaoche.commonbiz.service.utils.StringUtil;
//import com.maihaoche.volvo.BuildConfig;
//import com.maihaoche.volvo.R;
//
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.HashMap;
//
//import me.dm7.barcodescanner.core.IViewFinder;
//import me.dm7.barcodescanner.core.ViewFinderView;
//import me.dm7.barcodescanner.zxing.ZXingScannerView;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
///**
// * Created by brantyu on 17/10/19.
// */
//
//public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
//
//
//    private static final String DAILY_URL = "http://stable-faw.haimaiche.net/tesla/api/v1/qrCode";
//
//    private static final String ONLINE_URL = "https://faw.maihaoche.com/tesla/api/v1/qrCode";
//    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//
//    private ZXingScannerView mZXingScannerView = null;
//    private String bizCode;
//    private String pageId;
//    private String carNumber;
//    private String url;
//
//    @Override
//    public void handleResult(Result rawResult) {
//        HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_BARCODE_SCAN);
////        Toast.makeText(this, "Contents = " + rawResult.getText() +
////                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
//        String[] address = rawResult.getText().split("\\?");
//        String params;
//        if (address.length > 1) {
//            params = address[1];
//        } else {
//            return;
//        }
//        String[] strings = params.split("&");
//        if (strings != null) {
//            String[] str1 = strings[0].split("=");
//            if (str1.length > 1) {
//                bizCode = str1[1];
//            }
//
//        }
//        if (strings.length > 1) {
//            String[] str2 = strings[1].split("=");
//            if (str2.length > 1) {
//                pageId = str2[1];
//            }
//
//        }
//
//        toast("扫描成功，正在进入车架号识别...");
//        // Note:
//        // * Wait 2 seconds to resume the preview.
//        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
//        // * I don't know why this is the case but I don't have the time to figure out.
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                OcrHelper.goOcrScan(mCallBack, ScannerActivity.this);
//
////                mZXingScannerView.resumeCameraPreview(ScannerActivity.this);
//            }
//        }, 1000);
//    }
//
//    OcrHelper.CallBack mCallBack = new OcrHelper.CallBack() {
//        @Override
//        public void onResult(String result, String carAttr) {
//            carNumber = result;
//            Log.e("Scan", "onResult = " + result);
////            toast("车架号" + result + "已发送给网页");
//            postData();
//            OcrHelper.goOcrScan(mCallBack, ScannerActivity.this);
//        }
//    };
//
//    private void toast(String msg) {
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        HintUtil.getInstance().init();
//        url = DAILY_URL;
//        if(BuildConfig.DEBUG){
//            url = DAILY_URL;
//        }else{
//            url = ONLINE_URL;
//        }
//        setContentView(R.layout.activity_scanner);
//        setupToolbar();
//        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
//        mZXingScannerView = new ZXingScannerView(this) {
//            @Override
//            protected IViewFinder createViewFinderView(Context context) {
//                return new CustomViewFinderView(context);
//            }
//        };
//        contentFrame.addView(mZXingScannerView);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mZXingScannerView.setResultHandler(this);
//        mZXingScannerView.startCamera();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mZXingScannerView.stopCamera();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        HintUtil.getInstance().release();
//    }
//
//    public void setupToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        final ActionBar ab = getSupportActionBar();
//        if (ab != null) {
//            ab.setDisplayHomeAsUpEnabled(true);
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            // Respond to the action bar's Up/Home button
//            case android.R.id.home:
//                finish();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private static class CustomViewFinderView extends ViewFinderView {
//        public static final String TRADE_MARK_TEXT = "请扫描网页二维码进行绑定";
//        public static final int TRADE_MARK_TEXT_SIZE_SP = 20;
//        public final Paint PAINT = new Paint();
//
//        public CustomViewFinderView(Context context) {
//            super(context);
//            init();
//        }
//
//        public CustomViewFinderView(Context context, AttributeSet attrs) {
//            super(context, attrs);
//            init();
//        }
//
//        private void init() {
//            PAINT.setColor(Color.WHITE);
//            PAINT.setAntiAlias(true);
//            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
//                    TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
//            PAINT.setTextSize(textPixelSize);
//            setSquareViewFinder(true);
//        }
//
//        @Override
//        public void onDraw(Canvas canvas) {
//            super.onDraw(canvas);
//            drawTradeMark(canvas);
//        }
//
//        private void drawTradeMark(Canvas canvas) {
//            Rect framingRect = getFramingRect();
//            float tradeMarkTop;
//            float tradeMarkLeft;
//            if (framingRect != null) {
//                tradeMarkTop = framingRect.bottom + PAINT.getTextSize() + 10;
//                tradeMarkLeft = framingRect.left;
//            } else {
//                tradeMarkTop = 10;
//                tradeMarkLeft = canvas.getHeight() - PAINT.getTextSize() - 10;
//            }
//            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT);
//        }
//    }
//
//    private void postData() {
//        Gson gson = new Gson();
//        PostModel postModel = new PostModel();
//        if (StringUtil.isNotEmpty(pageId)) {
//            postModel.setPageId(pageId);
//
//        } else {
//            toast("pageId为空，上传失败");
//            return;
//        }
//        if (StringUtil.isNotEmpty(carNumber)) {
//            postModel.setCarNumber(carNumber);
//        } else {
//            toast("carNumber为空，上传失败");
//            return;
//        }
//        String content = gson.toJson(postModel);
//        OkHttpClient client = OkHttpHandler.getCommonClient();
//        RequestBody body = RequestBody.create(JSON, content);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(() -> {
//                    Toast.makeText(ScannerActivity.this, "上传失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response){
//                runOnUiThread(() -> {
//                    try {
//                        Toast.makeText(ScannerActivity.this, "请求成功:"+response.body().string(), Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                });
//
//            }
//        });
//    }
//
//    private static class PostModel implements Serializable {
//
//        private String pageId;
//
//        private String carNumber;
//
//        public String getPageId() {
//            return pageId;
//        }
//
//        public void setPageId(String pageId) {
//            this.pageId = pageId;
//        }
//
//        public String getCarNumber() {
//            return carNumber;
//        }
//
//        public void setCarNumber(String carNumber) {
//            this.carNumber = carNumber;
//        }
//    }
//}
