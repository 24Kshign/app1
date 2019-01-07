package com.maihaoche.volvo.ui.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityHelpBinding;


/**
 * Created by brantyu on 17/7/3.
 * 帮助中心
 */

public class HelpActivity extends HeaderProviderActivity<ActivityHelpBinding> {

    private static final String RUKU_URL = "https://img.maihaoche.com/assets/video/ruku.mp4";
    private static final String PANKU_URL = "https://img.maihaoche.com/assets/video/panku.mp4";
    private static final String ZHAOCHE_URL = "https://img.maihaoche.com/assets/video/zhaoche.mp4";
    private static final String XIAOJIQIAO_URL = "https://img.maihaoche.com/assets/video/xiaojiqiao.mp4";

    @Override
    public int getContentResId() {
        return R.layout.activity_help;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        initHeader("帮助中心");
        ActivityHelpBinding binding = getContentBinding();
        SpannableString spannableString = new SpannableString("详细操作请扫描二维码查看视频或点击直接查看");
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.blue_6B93F6)), spannableString.length() - 4, spannableString.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        binding.rukuDesTv.setText(spannableString);
        binding.pankuDesTv.setText(spannableString);
        binding.zhaocheDesTv.setText(spannableString);
        binding.xiaojiqiaoDesTv.setText(spannableString);

        binding.rukuDesTv.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(RUKU_URL));
            startActivity(intent);
        });

        binding.pankuDesTv.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(PANKU_URL));
            startActivity(intent);
        });

        binding.zhaocheDesTv.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(ZHAOCHE_URL));
            startActivity(intent);
        });

        binding.xiaojiqiaoDesTv.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(XIAOJIQIAO_URL));
            startActivity(intent);
        });
    }
}
