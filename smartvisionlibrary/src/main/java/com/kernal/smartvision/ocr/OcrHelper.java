package com.kernal.smartvision.ocr;

import android.content.Context;
import android.content.Intent;

/**
 * Created by brantyu on 17/6/9.
 * VIN码识别的外部调用类
 */

public class OcrHelper {

    /**
     * VIN码识别后的回调接口，返回结果VIN码
     */
    public interface CallBack {
        void onResult(String result, String carAttr);
    }

    //临时存储的回调接口对象
    private static CallBack sCallBack;

    /**
     * 进行OCR识别的方法
     *
     * @param callback
     * @param context
     */
    public static void goOcrScan(CallBack callback, Context context) {
        sCallBack = callback;
        context.startActivity(new Intent(context, CameraActivity.class));
    }

    /**
     * 获取临时存储的接口回调对象
     *
     * @return
     */
    public static CallBack getCallBack() {
        return sCallBack;
    }

    /**
     * 销毁临时存储的接口回调对象
     */
    public static void cleanCache() {
        sCallBack = null;
    }
}
