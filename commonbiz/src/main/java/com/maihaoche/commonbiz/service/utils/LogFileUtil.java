package com.maihaoche.commonbiz.service.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gujian
 * Time is 2017/9/3
 * Email is gujian@maihaoche.com
 */

public class LogFileUtil {

    public static final String FILE_NAME = "volvl_log.txt";

    /**
     * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
     *
     * @return
     */
    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡根目录路径
     *
     * @return
     */
    public static String getSdCardPath() {
        boolean exist = isSdCardExist();
        String sdpath = "";
        if (exist) {
            sdpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {
            sdpath = "不适用";
        }
        return sdpath;

    }

    /**
     * 获取默认的文件路径
     *
     * @return
     */
    public static String getDefaultFilePath() {
        String filepath = "";
        String path = Environment.getExternalStorageDirectory().getPath();
        File file = new File(path+"/"+FILE_NAME);
        if (file.exists()) {
            filepath = file.getAbsolutePath();
        }else{
            try {
                file.createNewFile();
                filepath = file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                filepath = "";
            }
        }
        return filepath;
    }

    public static String readFromFile(){
        if(!isSdCardExist()){
            Log.e("File","sdcard not exist");
            return "";
        }

        String filePath = getDefaultFilePath();
        if(TextUtils.isEmpty(filePath)){
            Log.e("File","File path 不可用");
            return "";
        }
        String string = "";
        try {
            File file = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String readline = "";
            StringBuffer sb = new StringBuffer();
            while ((readline = br.readLine()) != null) {
                System.out.println("readline:" + readline);
                sb.append(readline);
            }
            br.close();
            string = sb.toString();
            System.out.println("读取成功：" + sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    public static void writToFile(String string){

        if(!isSdCardExist()){
            Log.e("File","sdcard not exist");
            return ;
        }

        String filePath = getDefaultFilePath();
        if(TextUtils.isEmpty(filePath)){
            Log.e("File","File path 不可用");
            return;
        }

        try {
            File file = new File(filePath);
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(string.getBytes());
            fos.close();
            System.out.println("写入成功：");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writToFile2(String string){

        if(!isSdCardExist()){
            Log.e("File","sdcard not exist");
            return ;
        }

        String filePath = getDefaultFilePath();
        if(TextUtils.isEmpty(filePath)){
            Log.e("File","File path 不可用");
            return;
        }

        try {

            File file = new File(filePath);
            if(!file.exists()){
                file.createNewFile();
            }

            SimpleDateFormat dateformat2=new SimpleDateFormat("HH:mm:ss SSS");
            String a2=dateformat2.format(new Date());

            //第二个参数意义是说是否以append方式添加内容
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(a2+":"+string);
            bw.write("\r\n");
            bw.flush();
            System.out.println("写入成功");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("File","写入失败："+string);
        }
    }
}
