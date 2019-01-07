package com.maihaoche.volvo.ui;

import android.widget.EditText;

import com.maihaoche.commonbiz.module.ui.BaseFragmentActivity;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.ui.webview.WebViewActivity;

/**
 * Created by manji
 * Date：2018/9/5 下午2:58
 * Desc：
 */
public class InputWebViewActivity extends BaseFragmentActivity {
    @Override
    protected int bindLayoutRes() {
        return R.layout.activity_input_web;
    }

    private EditText mInput;

    @Override
    protected void initView() {
        super.initView();
        mInput = findViewById(R.id.webviewInput);
        findViewById(R.id.webviewConfirm).setOnClickListener(v -> {
            WebViewActivity.start(thisActivity(), mInput.getText().toString());
        });
    }
}
