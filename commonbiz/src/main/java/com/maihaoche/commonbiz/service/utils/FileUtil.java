package com.maihaoche.commonbiz.service.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.maihaoche.commonbiz.BuildConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 作者：yang
 * 时间：17/7/3
 * 邮箱：yangyang@maihaoche.com
 */
public class FileUtil {


    public static final String FILE_PROVIDER_NAME =
            BuildConfig.DEBUG ?
                    "com.maihaoche.volvo.debug.android.fileprovider"
                    : "com.maihaoche.volvo.android.fileprovider";

    public static final String FILE_PROVIDER_NAME1 = "com.maihaoche.volvo.android.fileprovider";

    /**
     * 获取外部的文件路径
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getExternalFilePath(Context context, String fileName) {
        File file = context.getExternalFilesDir(null);
        if (file != null && file.exists()) {
            return file.getAbsolutePath() + File.separator + fileName;
        } else {
            file = context.getFilesDir();
            return file.getAbsolutePath() + File.separator + fileName;
        }
    }

    /**
     * 安装某个目录路径下的apk文件
     *
     * @param context
     * @param filePath
     * @return
     */
    public static boolean installApk(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) return false;
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(FileProvider.getUriForFile(context, FileUtil.FILE_PROVIDER_NAME1, file));
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//        context.startActivity(intent);
        return true;
    }

    /**
     * 检查一个文件的md5是否与目标路径的文件一致
     */
    public static boolean isTheSameMD5File(String filePath, String exceptedMD5) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String md5 = bigInt.toString(16);
        //不满足32位补0
        if (md5.length() < 32) {
            for (int i = 0; i < 32 - md5.length(); i++) {
                md5 = "0" + md5;
            }
        }
        return md5.equals(exceptedMD5);

    }

    public static void save(Context context, byte[] bytes,String fileName) {
        String filePath = getExternalFilePath(context, fileName);
        /**
         * 创建File对象，其中包含文件所在的目录以及文件的命名
         */
        File file = new File(filePath);
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
            // 如果文件存在则删除
            if (file.exists()) {
                file.delete();
            }
            // 在文件系统中根据路径创建一个新的空文件
            file.createNewFile();
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(file);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
}
