package com.maihaoche.volvo.util;


import com.maihaoche.base.log.LogUtil;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.server.dto.CarExtraInfoDTO;
import com.qiniu.android.http.CancellationHandler;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.maihaoche.volvo.view.ChoosePicImage.QINIU_IMG_HEADER;

/**
 * Created by gujian
 * Time is 2017/6/30
 * Email is gujian@maihaoche.com
 */

public class QiNiuUtil {

    private static String token;
    private static CancleUpload cancel;


    public static CancleUpload getInstence(){
        if(cancel == null){
            cancel = new QiNiuUtil().new CancleUpload();
        }
        return cancel;
    }



    //七牛上传manager
    private static UploadManager uploadManager= new UploadManager();

    public static void getToken(BaseActivity activity,TokenSucListener listener,TokenFailListener tokenFailListener){
        AppApplication.getServerAPI().getToken()
                .setOnDataGet(response -> {
                    token =response.result;
                        if(listener!=null) {
                            listener.success(token);
                        }
                })
                .setOnDataError(emsg -> {
                    if(tokenFailListener!=null){
                        tokenFailListener.failed("获取七牛token失败");
                    }
                })
                .call(activity);
    }




        public static void uploadImg(BaseActivity activity,String url ,Listener listener,CancleUpload cancel){
        if( token== null){
        AppApplication.getServerAPI().getToken()
        .setOnDataGet(response -> { token= response.result;
        upload( url ,listener,cancel);
        })
            .call(activity);
            }else{
            upload( url ,listener,cancel);
        }

    }


    private static void upload(final String url, Listener listener,CancleUpload cancel) {
        String key = UUID.randomUUID().toString() + ".jpg";

        uploadManager.put(
                url,//文件地址
                key,
                token,
                (key1, info, response) -> {
                    if (info.isOK()) {
                        String uploadUrl = QINIU_IMG_HEADER + key1;
                        if (listener != null) {
                            listener.success(uploadUrl);
                        }

                    } else {

                            if ( listener!=null){
                                listener.failed(url);
                        }
                    }
                }, new UploadOptions(null, "image/jpeg", false, null, cancel));
    }

    public interface Listener {
        void success(String url);
        void failed(String url);
    }

    public interface TokenSucListener{
        void success(String token);
    }

    public interface TokenFailListener{
        void failed(String string);
    }

    public class CancleUpload implements UpCancellationSignal {

        private boolean isCancel;

        @Override
        public boolean isCancelled() {
            return isCancel;
        }

        public void setCancel(boolean cancel) {
            isCancel = cancel;
        }

        public boolean isCancel() {
            return isCancel;
        }
    }

}




