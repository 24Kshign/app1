package com.maihaoche.volvo.ui.instorage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivitySelectClientBinding;
import com.maihaoche.volvo.databinding.ActivitySelectSeriesBinding;
import com.maihaoche.volvo.ui.instorage.adapter.SeriesListAdapter;
import com.maihaoche.volvo.ui.instorage.event.BrandEvent;

import java.util.ArrayList;


public class SelectSeriesActivity extends HeaderProviderActivity<ActivitySelectSeriesBinding> {

    private static final String BREAN_ID = "brand_id";

    private ActivitySelectSeriesBinding binding;
    private SeriesListAdapter adapter;

    public static Intent create(BaseActivity activity,Long id){
        Intent intent = new Intent(activity,SelectSeriesActivity.class);
        intent.putExtra(BREAN_ID,id);
        return intent;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_select_series;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initHeader("选择车系");
        binding = getContentBinding();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.list.setLayoutManager(manager);
        adapter = new SeriesListAdapter(this,new ArrayList<>());
        adapter.setListener(contact -> {
            BrandEvent event = new BrandEvent(contact.seriesName,contact.seriesId,BrandEvent.TYPE_CAR_SERIES);
            RxBus.getDefault().post(event);
            finish();
        });
        binding.list.setAdapter(adapter);
        reLoad();
    }

    @Override
    protected void reLoad() {
        AppApplication.getServerAPI().getSeries(getIntent().getLongExtra(BREAN_ID,0))
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setOnDataGet(response->{
                    adapter.setContacts(response.result);
                })
                .call(this);
    }

//    protected void showProgress(){
//        binding.progress.progressView.setVisibility(View.VISIBLE);
//        binding.list.setVisibility(View.GONE);
//    }
//    protected void showContent(){
//        binding.progress.progressView.setVisibility(View.GONE);
//        binding.list.setVisibility(View.VISIBLE);
//    }


}
