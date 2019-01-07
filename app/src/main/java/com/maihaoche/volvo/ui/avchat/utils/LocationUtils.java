package com.maihaoche.volvo.ui.avchat.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.maihaoche.volvo.ui.avchat.log.LogUtil;

import java.util.List;

/**
 * Created by heliguang on 17-12-29.
 */

public class LocationUtils {
    @SuppressLint("MissingPermission")
    public static Location getLastKnownLocation(Context context) {
        String locationProvider;
        Location location = null;

        //获取地理位置管理器
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
            location = locationManager.getLastKnownLocation(locationProvider);

            if (location != null) {
                //不为空,显示地理位置经纬度
                String locationStr = "维度：" + location.getLatitude() +  " 经度：" + location.getLongitude();
                LogUtil.i("provider: " + locationProvider +  " " + locationStr);
                return location;
            }
        }

        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果network
            locationProvider = LocationManager.NETWORK_PROVIDER;
            location = locationManager.getLastKnownLocation(locationProvider);

            if (location != null) {

                //不为空,显示地理位置经纬度
                String locationStr = "维度：" + location.getLatitude() +  " 经度：" + location.getLongitude();
                LogUtil.i("provider: " + locationProvider +  " " + locationStr);
                return location;
            }
        }

        return location;
    }
}
