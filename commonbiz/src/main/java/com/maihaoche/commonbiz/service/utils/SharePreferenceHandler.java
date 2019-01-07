package com.maihaoche.commonbiz.service.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SharePreference 的工具类
 * 作者：yang
 * 时间：17/6/12
 * 邮箱：yangyang@maihaoche.com
 */
public class SharePreferenceHandler extends ApplicationProvider {

    private static SharedPreferences preferences;


    public static SharedPreferences getPreferences() {
        if (null == preferences) {
            preferences = PreferenceManager
                    .getDefaultSharedPreferences(getApplication());
        }
        return preferences;
    }
    public static SharedPreferences getPreferences(String pref){
        if(TextUtils.isEmpty(pref)){
            return getPreferences();
        }
        return getApplication().getSharedPreferences(pref, Context.MODE_PRIVATE);
    }

    public static void commitBooleanPref(String key, Boolean value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
        editor.clear();
    }

    public static void commitLongPref(String key, Long value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putLong(key, value);
        editor.apply();
        editor.clear();
    }

    public static void commitStringPref(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        editor.apply();
        editor.clear();
    }

    public static void commitIntegerPref(String key, Integer value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(key, value);
        editor.apply();
        editor.clear();
    }

    public static void commitSetPref(String key, Set<String> value) {
        // removePref(key);
        StringBuilder sb = new StringBuilder();
        for (String str : value) {
            sb.append(str);
            sb.append("#$#");
        }
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, sb.toString());
        editor.apply();
        editor.clear();
    }

    public static void commitListPref(String key, List<String> value) {
        // removePref(key);
        StringBuilder sb = new StringBuilder();
        for (String str : value) {
            sb.append(str);
            sb.append("#$#");
        }
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, sb.toString());
        editor.apply();
        editor.clear();
    }

    public static void removePref(String key) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.remove(key);
        editor.apply();
        editor.clear();
    }

    public static int getPrefIntegerValue(String key, int defVal) {
        return getPreferences().getInt(key, defVal);
    }

    public static String getPrefStringValue(String key, String defVal) {
        return getPreferences().getString(key, defVal);
    }

    public static Long getPrefLongValue(String key, long defVal) {
        return getPreferences().getLong(key, defVal);
    }


    public static boolean getPrefBooleanValue(String key, boolean defVal) {
        return getPreferences().getBoolean(key, defVal);
    }

    public static List<String> getPrefListValue(String key, List<String> defVal) {
        String value = getPreferences().getString(key, null);
        if (value != null) {
            String[] strs = value.split("#\\$#");
            List<String> list = new ArrayList<>();
            Collections.addAll(list, strs);
            return list;
        }
        return defVal;
    }

    public static Set<String> getPrefSetValue(String key, Set<String> defVal) {
        String value = getPreferences().getString(key, null);
        if (value != null) {
            String[] strs = value.split("#\\$#");
            Set<String> set = new HashSet<>();
            Collections.addAll(set, strs);
            return set;
        }
        return defVal;
    }

    public static void serializableDelete(String key) {
        getApplication().deleteFile(key);
    }


    public static void serializableOut(Serializable serializable, String key) {
        ObjectOutputStream out = null;
        try {
            getApplication().deleteFile(key);
            out = new ObjectOutputStream(getApplication().openFileOutput(
                    key, Activity.MODE_PRIVATE));
            out.writeObject(serializable);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Serializable serializableIn(String key) {
        Serializable obj = null;
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(
                    getApplication().openFileInput(key));
            obj = (Serializable) in.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in)
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return obj;
    }

    public static long getFileCreateDate(String key) {
        File file = getApplication().getFileStreamPath(key);
        if (file.exists()) {
            return file.lastModified();
        } else {
            return 0;
        }
    }
}