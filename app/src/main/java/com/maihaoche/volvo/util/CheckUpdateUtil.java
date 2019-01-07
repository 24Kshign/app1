package com.maihaoche.volvo.util;

import android.os.Handler;

import com.maihaoche.base.http.ProgressCall;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.module.ui.NormalDialog;
import com.maihaoche.commonbiz.service.utils.FileUtil;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.BuildConfig;
import com.maihaoche.volvo.server.dto.request.CheckUpdateRequestDTO;
import com.maihaoche.volvo.server.dto.response.CheckUpdateResponseDTO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/3/6
 * Email is gujian@maihaoche.com
 */

public class CheckUpdateUtil {
    private static final String APK_FILE_NAME = "volvo.apk";

    public static void checkUpdate(BaseActivity context, Handler handler, boolean fromLaunch, CallBack callBack) {
        CheckUpdateRequestDTO request = new CheckUpdateRequestDTO();
        request.versionId = BuildConfig.VERSION_CODE;
        AppApplication.getServerAPI().checkUpdate(request)
                .setOnDataGet(response -> {
                    CheckUpdateResponseDTO result = response.result;
                    long versionId = result.versionId;
                    if (versionId > BuildConfig.VERSION_CODE) {
                        //有新的版本，需要更新
                        if (response.result.isForce()) {
                            //强更
                            new NormalDialog(context)
                                    .setTilte("新版本")
                                    .setContent(result.description)
                                    .setSingleConfirmStr("立即下载")
                                    .setCanceledOutside(false)
                                    .setIsCancelable(false)
                                    .setOnOKClickListener(() -> {
                                        new APKCall(context, handler, FileUtil.getExternalFilePath(context, APK_FILE_NAME), result.md5)
                                                .call(result.downloadUrl);
                                    }).show();
                        } else {
                            //不强更
                            new NormalDialog(context)
                                    .setTilte("新版本")
                                    .setContent(result.description)
                                    .setBtnConfirmStr("立即下载")
                                    .setBtnCancelStr("取消")
                                    .setOnCancelClickListener(dialog -> {
                                        if (callBack != null) {
                                            callBack.callBack(100, "success");
                                        }
                                        dialog.dismiss();
                                    })
                                    .setOnOKClickListener(() -> {
                                        new APKCall(context, handler, FileUtil.getExternalFilePath(context, APK_FILE_NAME), result.md5)
                                                .call(result.downloadUrl);
                                    }).show();
                        }
                    } else {
                        //最新版本，不需要更新
                        if (callBack != null) {
                            callBack.callBack(100, "success");
                        }
                    }
                })
                .setOnDataError(emsg -> {
                    if (!fromLaunch) {
                        AlertToast.show(emsg);
                    } else {
                        if (callBack != null) {
                            callBack.callBack(999, emsg);
                        }
                    }

                })
                .call(context);
    }

    /**
     * 下载apk的请求
     */
    private static class APKCall extends ProgressCall {

        String mFilePath;
        String mExceptedMD5;
        Handler handler;
        BaseActivity context;

        public APKCall(BaseActivity context, Handler handler, String filePath, String exceptedMD5) {
            mFilePath = filePath;
            mExceptedMD5 = exceptedMD5;
            this.handler = handler;
            this.context = context;
        }

        @Override
        public OkHttpClient getOkhttpClient() {
            //日志打印拦截工具
            OkHttpClient client = new OkHttpClient.Builder()
                    //超时设置
                    .connectTimeout(60000, TimeUnit.MILLISECONDS)
                    .readTimeout(60000, TimeUnit.MILLISECONDS)
                    .writeTimeout(60000, TimeUnit.MILLISECONDS)
                    .build();
            return client;
        }

        @Override
        public void onResponse(ResponseBody responseBody) {
            //              把返回的数据保存到文件中。
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mFilePath);
                fos.write(responseBody.bytes());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e1) {
                    }
                }
            }
        }

        @Override
        public void onError(Exception exception) {
            handler.post(() -> context.cancelLoading(() -> AlertToast.show("下载出错:" + exception.getMessage())));
        }

        @Override
        public void update(long bytesRead, long contentLength, boolean done) {
            handler.post(() -> {
                if (done) {
                    context.cancelLoading();
//                    if (FileUtil.isTheSameMD5File(mFilePath, mExceptedMD5)) {
//                        FileUtil.installApk(SettingActivity.this, mFilePath);
//                    }
                    FileUtil.installApk(context, mFilePath);
                } else {
                    int pecent = (int) (((float) bytesRead / (float) contentLength) * 100);
                    context.showLoading("下载中:" + pecent + "%", Integer.MAX_VALUE);
                }
            });

        }
    }

    public interface CallBack {

        void callBack(int code, String msg);
    }
}
