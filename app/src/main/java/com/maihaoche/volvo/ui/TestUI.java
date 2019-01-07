package com.maihaoche.volvo.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.maihaoche.volvo.R;
import com.maihaoche.volvo.dao.po.CarPO;
import com.maihaoche.volvo.databinding.ActivityTestUiBinding;
import com.maihaoche.volvo.view.dialog.MoveStorageDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Function;


public class TestUI extends AppCompatActivity {

    ActivityTestUiBinding binding;
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_test_ui);

    }

    private void initData() {
        for(int i=0;i<10;i++){
            list.add(i+"");
        }

    }

}
