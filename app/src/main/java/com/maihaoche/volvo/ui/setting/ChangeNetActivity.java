package com.maihaoche.volvo.ui.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.maihaoche.base.http.RetrofitProvider;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.http.GsonConverterFactory;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityChangeNetBinding;
import com.maihaoche.volvo.server.ServerGetCallAdapterFactory;
import com.maihaoche.volvo.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;

import static com.maihaoche.volvo.server.AppDataLoader.getAppHttpClient;

public class ChangeNetActivity extends HeaderProviderActivity<ActivityChangeNetBinding> implements View.OnClickListener {

    private ActivityChangeNetBinding binding;
    private static final String PRE = "当前环境:\n";
    private static final String HAI_MAI_CHE = "https://wms.haimaiche.net/";
    private static final String MCLAREN = "http://mclaren-test-wms.haimaiche.net/";
    private static final String MAI_HAO_CHE_PRE = "https://wms-pre.maihaoche.com/";
    private static final String MAI_MAI_CHE = "https://wms.maihaoche.com/";

    @Override
    public int getContentResId() {
        return R.layout.activity_change_net;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        binding = getContentBinding();
        initHeader("网络切换");
        initListener();
    }

    private void initListener() {
        binding.changeNet.setOnClickListener(this);
        binding.haimaiche.setOnClickListener(this);
        binding.mclaren.setOnClickListener(this);
        binding.maihaochePre.setOnClickListener(this);
        binding.maihaoche.setOnClickListener(this);

        binding.haimaiche.setText(HAI_MAI_CHE);
        binding.mclaren.setText(MCLAREN);
        binding.maihaochePre.setText(MAI_HAO_CHE_PRE);
        binding.maihaoche.setText(MAI_MAI_CHE);

        binding.current.setText(PRE + AppApplication.getmRetrofitCompnent().getBaseURL());
    }

    @Override
    public void onClick(View v) {
        String url = binding.changeNetInput.getText().toString();
        switch (v.getId()) {
            case R.id.change_net:
                if (!TextUtils.isEmpty(url)) {
                    changeNet(url);
                    binding.current.setText(PRE + url);
                } else {
                    AlertToast.show("请输入地址");
                }
                break;
            case R.id.haimaiche:
                changeNet(HAI_MAI_CHE);
                binding.current.setText(PRE + HAI_MAI_CHE);
                break;
            case R.id.mclaren:
                changeNet(MCLAREN);
                binding.current.setText(PRE + MCLAREN);
                break;
            case R.id.maihaoche_pre:
                changeNet(MAI_HAO_CHE_PRE);
                binding.current.setText(PRE + MAI_HAO_CHE_PRE);
                break;
            case R.id.maihaoche:
                changeNet(MAI_MAI_CHE);
                binding.current.setText(PRE + MAI_MAI_CHE);
                break;
        }
    }

    private void changeNet(String url) {
        RetrofitProvider.RetrofitCompnent retrofitCompnent = new RetrofitProvider.RetrofitCompnent() {
            @Override
            public List<CallAdapter.Factory> getCallAdapterFactories() {
                ArrayList<CallAdapter.Factory> factories = new ArrayList<>();
                factories.add(ServerGetCallAdapterFactory.create());
                return factories;
            }

            @Override
            public String getBaseURL() {
                return url;
            }

            @Override
            public Converter.Factory getFactory() {
                return GsonConverterFactory.create();
            }

            @Override
            public OkHttpClient getClient() {
                return getAppHttpClient();
            }
        };
        AppApplication.setmRetrofitCompnent(retrofitCompnent);
        startActivity(LoginActivity.createIntent(this));
        finish();
//        SharePreferenceHandler.commitSetPref(LoginBiz.SP_LOGIN_USER_NAME, new HashSet<>());
    }
}
