package com.maihaoche.volvo.ui.avchat.utils;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.maihaoche.volvo.AppApplication;

/**
 * Created by manji
 * Date：2018/11/27 11:27 AM
 * Desc：
 */
public class LocationManager {

    private LocationClient mLocationClient;
    private OnLocationListener mOnLocationListener;

    public LocationManager() {
        //声明LocationClient类
        mLocationClient = new LocationClient(AppApplication.getInstance());
        //注册监听函数
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                stopLocation();
                if (null != mOnLocationListener) {
                    if (null != bdLocation) {
                        if (bdLocation.getLocType() == 161) {
                            mOnLocationListener.onSuccess(bdLocation);
                        } else {
                            mOnLocationListener.onFailure(bdLocation.getLocType(), bdLocation.getLocTypeDescription());
                        }
                    } else {
                        mOnLocationListener.onFailure(-1, "定位失败，bdLocation为null");
                    }
                }
            }
        });
        initOption();
    }

    private void initOption() {
        LocationClientOption mOption = new LocationClientOption();

        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        mOption.setScanSpan(2000);//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
        mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mOption.setOpenGps(true);//可选，默认false，设置是否开启Gps定位
        mOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用

        mLocationClient.setLocOption(mOption);
    }

    public void startLocation() {
        if (null != mLocationClient) {
            mLocationClient.start();
        }
    }

    public void stopLocation() {
        if (null != mLocationClient) {
            mLocationClient.stop();
        }
    }

    public void setOnLocationListener(OnLocationListener onLocationListener) {
        mOnLocationListener = onLocationListener;
    }

    public void onDestroy() {
        if (null != mLocationClient) {
            stopLocation();
            mLocationClient = null;
        }
    }

    public interface OnLocationListener {
        void onSuccess(BDLocation bdLocation);

        void onFailure(int errorCode, String errorInfo);
    }
}