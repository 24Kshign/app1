package com.maihaoche.commonbiz.service.utils;

import android.support.annotation.ColorRes;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangshengru on 16/1/15.
 * 字符串处理工具
 */
public class StringUtil {


    /**
     * 设置color。
     */
    public static SpannableString creatColorString(String str, @ColorRes int colorId, int startInclude, int endExclude) {
        if (TextUtils.isEmpty(str) || !isStartEndOK(startInclude, endExclude, str.length())) {
            return new SpannableString("");
        }
        SpannableString inSS = new SpannableString(str);
        inSS.setSpan(new ForegroundColorSpan(ResourceUtils.getColor(colorId)), startInclude, endExclude, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        return inSS;
    }

    private static boolean isStartEndOK(int startInclude, int endExclude, int length) {
        if (startInclude < 0 || endExclude < startInclude || endExclude > length) {
            throw new IllegalArgumentException("createStyleString 方法传入的startInclude,endExclude不合法." +
                    "startInclude:" + startInclude + ",endExclude:" + endExclude + ",总长度为:" + length);
        }
        return true;
    }

    /**
     * 字符串 md5。
     */
    public static String md5(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes());
            StringBuilder hex = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                int i = (b & 0xFF);
                if (i < 0x10) hex.append('0');
                hex.append(Integer.toHexString(i));
            }
            return hex.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String checkNotNull(String str, String nullStr) {
        return isEmpty(str) ? nullStr : str;
    }

    public static boolean isEmpty(String str) {
        return ((str == null) || str.trim().equals(""));
    }

    public static boolean isNotEmpty(String str) {
        return (str != null && !str.trim().equals(""));
    }

    public static ArrayList<String> getStringList(List<Long> list){
        ArrayList<String> lists = new ArrayList<>();
        for(Long item:list){
            lists.add(item+"");
        }
        return lists;
    }

    public static ArrayList<Long> getLongList(List<String> list){
        ArrayList<Long> lists = new ArrayList<>();
        for(String item:list){
            lists.add(Long.valueOf(item));
        }
        return lists;
    }

}
