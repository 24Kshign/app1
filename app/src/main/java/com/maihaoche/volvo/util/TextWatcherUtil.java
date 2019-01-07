package com.maihaoche.volvo.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * Created by gujian
 * Time is 2017/6/9
 * Email is gujian@maihaoche.com
 */

public class TextWatcherUtil implements TextWatcher {
    private int limitCount;
    private View clear;
    private CharSequence temp;
    private TextCount textCount;

    public TextWatcherUtil(int limitCount,TextCount textCount){
        this.limitCount = limitCount;
        this.textCount = textCount;
    }

    public TextWatcherUtil(int limitCount,TextCount textCount,View clear){
        this.limitCount = limitCount;
        this.textCount = textCount;
        this.clear = clear;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        temp = s;
    }

    @Override
    public void afterTextChanged(Editable s) {
        //字数是否超限
        if(limitCount!=0 && temp.length() > limitCount){
            s.delete(temp.length()-1,temp.length());
        }
        //回调字数
        if(textCount!=null){
            textCount.getTextCount(s.length());
        }
        //是否有清除按钮
        if(clear!=null){
            if(s.length()!=0){
                clear.setVisibility(View.VISIBLE);
            }else{
                clear.setVisibility(View.GONE);
            }
        }
    }

    public interface TextCount{
        void getTextCount(int count);
    }
}
