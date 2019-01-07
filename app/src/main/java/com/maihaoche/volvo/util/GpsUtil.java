package com.maihaoche.volvo.util;

import android.content.Context;
import android.location.LocationManager;

/**
 * Created by manji
 * Date：2018/12/27 9:19 AM
 * Desc：
 */
public class GpsUtil {

    public static final String LOCATION_LONGITUDE = "location_longitude";
    public static final String LOCATION_LATITUDE = "location_latitude";

    /**
     * 判断Gps定位是否打开
     */
    public static boolean isGpsOpen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (null != locationManager) {
            // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
            boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
            boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return gps || network;
        }
        return false;
    }
}