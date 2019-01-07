package com.maihaoche.volvo.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;

import com.maihaoche.volvo.R;

/**
 * Created by gujian
 * Time is 2017/6/15
 * Email is gujian@maihaoche.com
 */

public class DeleteLableDialog extends Dialog {

    private DeleteListener listener;

    public DeleteLableDialog(@NonNull Context context,DeleteListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.delete_lable_dialog);
        findViewById(R.id.delete).setOnClickListener(v->{
            if(listener!=null) {
                listener.delete();
            }
        });
    }

    public interface DeleteListener{
        void delete();
    }
}
