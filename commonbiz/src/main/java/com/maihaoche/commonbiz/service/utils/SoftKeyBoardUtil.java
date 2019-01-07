package com.maihaoche.commonbiz.service.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gujian
 * Time is 2017/6/16
 * Email is gujian@maihaoche.com
 */

public class SoftKeyBoardUtil {

    public static void showKeyBoardDely(EditText editText){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }

        }, 500);
    }
}
