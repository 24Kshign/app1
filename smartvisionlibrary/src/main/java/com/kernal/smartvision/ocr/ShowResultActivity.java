package com.kernal.smartvision.ocr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.kernal.smartvision.R;
import com.kernal.smartvision.adapter.VinParseResultAdapter;
import com.kernal.vinparseengine.VinParseInfo;

import java.util.HashMap;
import java.util.List;

/**
 * Created by brantyu on 17/6/9.
 * VIN码识别结果展示页面
 */

public class ShowResultActivity extends Activity {
    private ImageView mResultIv;
    private EditText mVinResultEt;
    private ListView mVinParseLv;
    private String mCarAttrStr;
    private String mVinStr;

    private boolean mNeedCleanCache = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initView();
        initData();
    }

    private void initData() {
        String resultStr = getIntent().getStringExtra(CameraActivity.VIN_RESULT);
        String picPathStr = getIntent().getStringExtra(CameraActivity.PIC_PATH);
        if (resultStr == null) return;

        if (!TextUtils.isEmpty(picPathStr)) {
            Bitmap bitmap = BitmapFactory.decodeFile(picPathStr);
            mResultIv.setImageBitmap(bitmap);
        }

        setVinData(resultStr.split(":")[1], true);
    }

    /**
     * 把车架号对应的结果列表展现出来
     *
     * @param resultStr
     * @param needSetEditText
     */
    private void setVinData(String resultStr, boolean needSetEditText) {
        if (TextUtils.isEmpty(resultStr) || resultStr.length() != 17) {
            //校验17位车架号
            return;
        }
        mVinStr = resultStr;
        if (needSetEditText) {
            mVinResultEt.setText(mVinStr);
            mVinResultEt.setSelection(mVinStr.length());
        }

        VinParseInfo vpi = new VinParseInfo();
        List<HashMap<String, String>> vinInfo = vpi.getVinParseInfo(resultStr);
        initCarAttr(vinInfo);
        vinInfo = sort(vinInfo);
        VinParseResultAdapter VPRadapter = new VinParseResultAdapter(this,
                vinInfo);
        mVinParseLv.setAdapter(VPRadapter);
    }

    /**
     * 将"厂家信息"往前移
     *
     * @param vinInfo
     */
    private List<HashMap<String, String>> sort(List<HashMap<String, String>> vinInfo) {
        if (vinInfo == null || vinInfo.isEmpty()) return null;
        HashMap<String, String> map = vinInfo.remove(9);
        vinInfo.add(1, map);
        return vinInfo;
    }

    /**
     * 初始化车辆属性字段
     *
     * @param vinInfo
     */
    private void initCarAttr(List<HashMap<String, String>> vinInfo) {
        if (vinInfo == null || vinInfo.isEmpty()) return;
        mCarAttrStr = "";
        String place = vinInfo.get(0).get("产地");

        if (!TextUtils.isEmpty(place) && !"未知".equals(place)) {
            if (" 中国".equals(place)) {
                mCarAttrStr += "国产";
            } else {
                mCarAttrStr += "进口";
            }
        } else {
            return;
        }

        String brand = vinInfo.get(1).get("品牌");
        if (!TextUtils.isEmpty(brand)) {
            mCarAttrStr = mCarAttrStr + brand;
        } else {
            //对于无法识别的品牌车系年款，默认拼上产地+厂家名称
            String vender = vinInfo.get(9).get("厂家名称");
            if (!TextUtils.isEmpty(vender)) {
                mCarAttrStr = mCarAttrStr + vender;
            }
            return;
        }

        String model = vinInfo.get(3).get("车型");
        if (!TextUtils.isEmpty(model)) {
            mCarAttrStr = mCarAttrStr + model;
        }

        String year = vinInfo.get(4).get("年款");
        if (!TextUtils.isEmpty(year)) {
            mCarAttrStr = mCarAttrStr + year + "款";
        }

    }

    private void initView() {
        mResultIv = (ImageView) findViewById(R.id.result_iv);
        mVinResultEt = (EditText) findViewById(R.id.result_vin_et);
        mVinResultEt.addTextChangedListener(mTextWatcher);
        mVinParseLv = (ListView) findViewById(R.id.result_lv);
        findViewById(R.id.result_confirm_tv).setOnClickListener(v -> {
            if (OcrHelper.getCallBack() != null) {
                if (!TextUtils.isEmpty(mVinStr) && mVinStr.equals(mVinResultEt.getText().toString())) {
                    OcrHelper.getCallBack().onResult(mVinStr, mCarAttrStr);
                } else {
                    OcrHelper.getCallBack().onResult(mVinResultEt.getText().toString(), "");
                }
            }
            finish();
        });
        findViewById(R.id.back_iv).setOnClickListener(v -> {
            finish();
        });
        findViewById(R.id.retry_tv).setOnClickListener(v -> {
            mNeedCleanCache = false;
            startActivity(new Intent(this, CameraActivity.class));
            finish();
        });
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            setVinData(s.toString(), false);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onDestroy() {
        mResultIv = null;
        mVinResultEt = null;
        mVinParseLv = null;
//        if (mNeedCleanCache) {
//            OcrHelper.cleanCache();
//        }
        super.onDestroy();
    }
}
