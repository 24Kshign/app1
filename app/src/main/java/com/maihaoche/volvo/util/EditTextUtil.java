package com.maihaoche.volvo.util;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * Created by gujian
 * Time is 2017/6/29
 * Email is gujian@maihaoche.com
 */

public class EditTextUtil {

    public static void editTextLimitTwoDecimalPlaces(@NonNull final EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {
            boolean deleteLastChar;// 是否需要删除末尾

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    // 如果点后面有超过三位数值,则删掉最后一位
                    int length = s.length() - s.toString().lastIndexOf(".");
                    // 说明后面有三位数值
                    deleteLastChar = length >= 3;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) {
                    return;
                }
                if (deleteLastChar) {
                    // 设置新的截取的字符串
                    editText.setText(s.toString().substring(0, s.toString().length() - 1));
                    // 光标强制到末尾
                    editText.setSelection(editText.getText().length());
                }
                // 以小数点开头，前面自动加上 "0"
                if (s.toString().startsWith(".")) {
                    editText.setText("0" + s);
                    editText.setSelection(editText.getText().length());
                }
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText1 = (EditText) v;
                // 以小数点结尾，去掉小数点
                if (!hasFocus && editText1.getText() != null && editText1.getText().toString().endsWith(".")) {
                    editText.setText(editText1.getText().subSequence(0, editText1.getText().length() - 1));
                    editText.setSelection(editText.getText().length());
                }
            }
        });

    }

}

