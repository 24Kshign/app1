package com.maihaoche.volvo.ui.setting;


import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseFragmentActivity;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.server.AppDataLoader;
import com.maihaoche.volvo.server.AppServerAPI;
import com.maihaoche.volvo.ui.avchat.log.LogUtil;
import com.maihaoche.volvo.ui.avchat.utils.LocationManager;
import com.maihaoche.volvo.ui.common.daomain.GaoDeResponse;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by manji
 * Date：2018/11/27 1:43 PM
 * Desc：
 */
public class LocationActivity extends BaseFragmentActivity {

    public static final int REQUEST_LOCATION_PERMISSION = 0x0111;

    private TextView tvBaiDuLocation;
    private TextView tvGaoDeDuLocation;

    @Override
    protected int bindLayoutRes() {
        return R.layout.activity_location;
    }

    private LocationManager mLocationManager;

    @Override
    protected void initView() {
        super.initView();

        tvBaiDuLocation = findViewById(R.id.al_tv_bai_du_location);
        tvGaoDeDuLocation = findViewById(R.id.al_tv_gaode_location);

        mLocationManager = new LocationManager();

        checkLocationPermission();

        mLocationManager.setOnLocationListener(new LocationManager.OnLocationListener() {
            @Override
            public void onSuccess(BDLocation bdLocation) {
                tvBaiDuLocation.setText("百度经度：" + bdLocation.getLongitude() + "\n百度纬度：" + bdLocation.getLatitude());
                getGaoDeLocation(bdLocation.getLongitude(), bdLocation.getLatitude());
            }

            @Override
            public void onFailure(int errorCode, String errorInfo) {
                LogUtil.e("定位失败：errorCode = " + errorInfo + " errorInfo = " + errorInfo);
            }
        });

        findViewById(R.id.al_iv_back).setOnClickListener(v -> {
            finish();
        });
    }

    private void getGaoDeLocation(double longitude, double latitude) {
        AppServerAPI sServerAPI = AppDataLoader.load(AppServerAPI.class, AppDataLoader.GAODE);
        Map<String, String> params = new HashMap<>();
        params.put("key", getString(R.string.gaode_key));
        params.put("coordsys", "baidu");
        params.put("locations", longitude + "," + latitude);
        params.put("output", "json");
        sServerAPI.getGaoDeLocation(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GaoDeResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LocationActivity.this.pend(d);
                    }

                    @Override
                    public void onNext(GaoDeResponse response) {
                        if (response.status.equals("1")) {
                            if (!TextUtils.isEmpty(response.locations)) {
                                String[] locations = response.locations.split(",");
                                if (locations.length > 0) {
                                    tvGaoDeDuLocation.setText("高德经度：" + locations[0] + "\n高德纬度：" + locations[1]);
                                }
                            }
                        } else {
                            LogUtil.e("高德定位转换失败：" + response.info);
                            AlertToast.show("高德定位转换失败：" + response.info);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e("高德定位转换失败：" + e.getMessage());
                        AlertToast.show("高德定位转换失败：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            mLocationManager.startLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//用户同意权限,执行我们的操作
                mLocationManager.startLocation();
            } else {//用户拒绝之后,当然我们也可以弹出一个窗口,直接跳转到系统设置页面
                AlertToast.show("未开启定位权限，请手动到设置去开启权限");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mLocationManager) {
            mLocationManager.onDestroy();
            mLocationManager = null;
        }
    }
}
