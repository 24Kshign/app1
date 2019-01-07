package com.maihaoche.volvo.util;

import android.content.Context;
import android.util.Log;

import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.persistent.FileRecorder;
import com.qiniu.android.utils.UrlSafeBase64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2017/12/5
 * Email is gujian@maihaoche.com
 */

public class UpLoadUtils {

    /**
     * 七牛的图片链接头
     */
    public static final String QINIU_IMG_HEADER = "https://img.maihaoche.com/";
    /**
     * 七牛私密空间图片链接头
     */
    public static final String SECURE_IMG_HEADER = "https://secure.maihaoche.com/";


    private static UploadManager uploadManager;

    private static void upload(String filePath, String token, UpLoadListener listener) {

        String key = Md5Util.md5(filePath + System.currentTimeMillis());
        key = key + ".mp4";

        if (uploadManager == null) {
            initUploadManager();
        }

        uploadManager.put(filePath, key, token,
                (key12, info, res) -> {
                    Log.i("qiniu", key12 + ",\r\n " + info + ",\r\n " + res);
                    if (info.isOK()) {
                        if (listener != null) {
                            key12 = QINIU_IMG_HEADER + key12;
                            listener.success(key12);
                        }
                    } else {
                        if (listener != null) {
                            listener.fail(info.error);
                        }
                    }
                },
                new UploadOptions(null, null, false,
                        (key1, percent) -> {
                            Log.i("qiniu", key1 + ": " + percent);

                            int progress = (int) (percent * 100);

                            if (listener != null) {
                                listener.progress(progress);
                            }
                        },
                        () -> false));

    }

    
    public static void uploadFile(Context context, String filePath, final UpLoadListener listener) {
        QiNiuUtil.getToken((BaseActivity) context, token->{
            upload(filePath, token, listener);
        }, string -> {
//            mErrorView.setVisibility(View.VISIBLE);
//            mProgressView.setVisibility(GONE);
        });

    }

    private static void initUploadManager() {

        //断点上传
        String dirPath = "/storage/emulated/0/Download";
        Recorder recorder = null;
        try {
            File f = File.createTempFile("qiniu_xxxx", ".tmp");
            Log.d("qiniu", f.getAbsolutePath());
            dirPath = f.getParent();
            recorder = new FileRecorder(dirPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String dirPath1 = dirPath;
        //默认使用 key 的url_safe_base64编码字符串作为断点记录文件的文件名。
        //避免记录文件冲突（特别是key指定为null时），也可自定义文件名(下方为默认实现)：

        KeyGenerator keyGen = (key, file) -> {
            // 不必使用url_safe_base64转换，uploadManager内部会处理
            // 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
            String path = key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
            Log.d("qiniu", path);
            File f = null;
            try {
                f = File.createTempFile(UrlSafeBase64.encodeToString(path), "");
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(f));
                String tempString = null;
                int line = 1;
                try {
                    while ((tempString = reader.readLine()) != null) {
                        Log.d("qiniu", "line " + line + ": " + tempString);
                        line++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return path;
        };

        Configuration config = new Configuration.Builder()
                // recorder 分片上传时，已上传片记录器
                // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .recorder(recorder, keyGen)
                .build();
        // 实例化一个上传的实例
        uploadManager = new UploadManager(config);
    }

    public interface UpLoadListener {

        void success(String path);

        void fail(String message);

        void progress(int progress);
    }
}
