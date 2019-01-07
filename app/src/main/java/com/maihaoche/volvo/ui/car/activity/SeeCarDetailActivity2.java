package com.maihaoche.volvo.ui.car.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivitySeeCarDetail2Binding;
import com.maihaoche.volvo.ui.car.adapter.CarInfoAdapter;
import com.maihaoche.volvo.ui.car.adapter.CarInfoAdapter2;
import com.maihaoche.volvo.ui.car.domain.SeeCarDetail;
import com.maihaoche.volvo.ui.car.domain.SeeCarDetailRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SeeCarDetailActivity2 extends HeaderProviderActivity<ActivitySeeCarDetail2Binding> {
    private static final String CODE = "code";

    private ActivitySeeCarDetail2Binding binding;
    private CarInfoAdapter2 adapter;
    private List<Long> appIds;

    @Override
    public int getContentResId() {
        return R.layout.activity_see_car_detail2;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initHeader("看车详情");
        binding = getContentBinding();
        initDate();
        initView();
    }

    private void initDate() {
        appIds = new ArrayList<>();
        List<String> appids = getIntent().getStringArrayListExtra(CODE);
        for(String id : appids){
            appIds.add(Long.valueOf(id));
        }
    }

    private void initView() {
        binding.carInfo.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        adapter = new CarInfoAdapter2(this);
        binding.carInfo.setAdapter(adapter);
        reLoad();
    }

    @Override
    protected void reLoad() {
        SeeCarDetailRequest request = new SeeCarDetailRequest();
        request.appIds = appIds;
        AppApplication.getServerAPI().getSeeCarDetail2(request)
                .setOnDataGet(response->{
                    if(response.result!=null){
                        showContent();
                        fillData(response.result);
                    }else{
                        showEmpty();
                    }

                })
                .setDoOnSubscribe(disposable -> showProgress())
                .setOnDataError(emsg -> {
                    AlertToast.show(emsg);
                    showEmpty();
                })
                .call(this);
    }

    private void fillData(List<SeeCarDetail> seeCarDetail){
        adapter.addAll(seeCarDetail);
        adapter.notifyDataSetChanged();
    }



    public static void toActivity(Context context, ArrayList<String> code){
        Intent intent = new Intent(context,SeeCarDetailActivity2.class);
        intent.putStringArrayListExtra(CODE,code);
        context.startActivity(intent);
    }

    private List<SeeCarDetail>  getTestData(){

        List<SeeCarDetail> seeCarDetails = new ArrayList<>();

        for(int k = 0;k<5;k++){
            SeeCarDetail seeCarDetail = new SeeCarDetail();
            seeCarDetail.appointmentDate = "2018-09-09";
            seeCarDetail.receiverPhone = "188888888";
            seeCarDetail.warehouseName = "古剑测试仓";
            List<SeeCarDetail.CarSimpleVOList> simpleVOLists = new ArrayList<>();
            for(int i=0;i<5;i++){
                SeeCarDetail.CarSimpleVOList carSimpleVOList = new SeeCarDetail.CarSimpleVOList();
                carSimpleVOList.simpleName = "高尔夫 2018款 1.6L 自动时尚型";
                carSimpleVOList.outerInner = "外观内饰：黑 / 黑";
                carSimpleVOList.subtotalCars = 5;
                List<SeeCarDetail.CarInfoVOList> carInfoVOLists = new ArrayList<>();
                for(int j=0;j<5;j++){
                    SeeCarDetail.CarInfoVOList carInfoVOList = new SeeCarDetail.CarInfoVOList();
                    carInfoVOList.carUnique = "23K298IJS566BN890";
                    if(j!=3)
                        carInfoVOList.position = "B区08排12号";
                    carInfoVOLists.add(carInfoVOList);
                }
                carSimpleVOList.carInfoVOList = carInfoVOLists;
                simpleVOLists.add(carSimpleVOList);

            }
            seeCarDetail.carSimpleVOList = simpleVOLists;
            seeCarDetails.add(seeCarDetail);
        }
        return seeCarDetails;
    }
}
