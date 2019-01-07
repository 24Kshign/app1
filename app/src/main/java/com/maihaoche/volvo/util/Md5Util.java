package com.maihaoche.volvo.util;

import android.text.TextUtils;

import java.security.MessageDigest;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/5/14
 * Email is gujian@maihaoche.com
 */
public class Md5Util {
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
}
