package com.maihaoche.volvo.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.ui.setting.DefauleValue;

/**
 * Created by gujian
 * Time is 2017/6/23
 * Email is gujian@maihaoche.com
 */

public class SPUtil {

    public static final String HAVESET = "haveset";
    public static final String LABLE = "lable";
    public static final String KEYNUMBER = "keyNumber";
    public static final String CERTIFICATION = "certification";
    public static final String FITERTIFI = "fitertifi";
    public static final String COMNSPECT = "comnspect";
    public static final String DIRECTION = "direction";
    public static final String VIN = "vin";
    public static final String DEFAULTWRITE = "defaultWrite";

    public static void saveToPrefs(DefauleValue defauleValue){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AppApplication.getApplication());
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(HAVESET,defauleValue.isHaveSeted());
        editor.putString(LABLE,defauleValue.getLabe());
        editor.putString(KEYNUMBER,defauleValue.getKeyNumber());
        editor.putInt(CERTIFICATION,defauleValue.getCertification());
        editor.putInt(FITERTIFI,defauleValue.getFitertifi());
        editor.putInt(COMNSPECT,defauleValue.getComnspect());
        editor.putInt(DIRECTION,defauleValue.getDirection());
        editor.putBoolean(VIN,defauleValue.isVinCheck());
        editor.putBoolean(DEFAULTWRITE,defauleValue.isDefaultWrite());

        editor.commit();

    }

    public static void saveValue(String key,Object o){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AppApplication.getApplication());
        final SharedPreferences.Editor editor = prefs.edit();
        if(o instanceof String){
            editor.putString(key,(String)o);
        }else if(o instanceof Integer){
            editor.putInt(key, (Integer) o);
        }else if(o instanceof Float){
            editor.putFloat(key,(Float)o);
        }else if(o instanceof Long){
            editor.putLong(key, (Long) o);
        }else if (o instanceof Boolean){
            editor.putBoolean(key, (Boolean) o);
        }else {
            editor.putString(key, o.toString());
        }

        editor.commit();
    }

    public static DefauleValue getValueFromPrefs(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(AppApplication.getApplication());
        DefauleValue defauleValue = null;
        try {
            defauleValue = new DefauleValue();
            defauleValue.setLabe(sharedPrefs.getString(LABLE,"无"));
            defauleValue.setKeyNumber(sharedPrefs.getString(KEYNUMBER,"1把"));
            defauleValue.setCertification(sharedPrefs.getInt(CERTIFICATION,0));
            defauleValue.setFitertifi(sharedPrefs.getInt(FITERTIFI,0));
            defauleValue.setComnspect(sharedPrefs.getInt(COMNSPECT,0));
            defauleValue.setDirection(sharedPrefs.getInt(DIRECTION,0));
            defauleValue.setDefaultWrite(sharedPrefs.getBoolean(DEFAULTWRITE,false));
            defauleValue.setVinCheck(sharedPrefs.getBoolean(VIN,false));
            return defauleValue;

        } catch (Exception e) {
            e.printStackTrace();
            return defauleValue;
        }
    }

    public static String getString(String key){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(AppApplication.getApplication());
        try {
            return sharedPrefs.getString(key,"");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getInt(String key){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(AppApplication.getApplication());
        try {
            return sharedPrefs.getInt(key,0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean getBoolean(String key){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(AppApplication.getApplication());
        try {
            return sharedPrefs.getBoolean(key,false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
