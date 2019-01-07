package com.maihaoche.volvo.ui.car.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivitySeeCarDetailBinding;
import com.maihaoche.volvo.ui.car.adapter.CarInfoAdapter;
import com.maihaoche.volvo.ui.car.domain.SeeCarDetail;
import com.maihaoche.volvo.ui.car.domain.SeeCarDetailRequest;

import java.util.ArrayList;
import java.util.List;

public class SeeCarDetailActivity extends HeaderProviderActivity<ActivitySeeCarDetailBinding> {

    private static final String CODE = "code";

    private ActivitySeeCarDetailBinding binding;
    private CarInfoAdapter adapter;

    @Override
    public int getContentResId() {
        return R.layout.activity_see_car_detail;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        initHeader("看车详情");
        binding = getContentBinding();
        initView();
        reLoad();
    }

    private void initView() {
        binding.carInfo.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        adapter = new CarInfoAdapter(this);
        binding.carInfo.setAdapter(adapter);
//        fillData(getTestData());

    }

    private void fillData(SeeCarDetail seeCarDetail){
        seeCarDetail.carSimpleVOList.add(0,new SeeCarDetail.CarSimpleVOList());
        adapter.setSeeCarDetails(seeCarDetail,seeCarDetail.carSimpleVOList);
    }

    public static void toActivity(Context context,int code){
        Intent intent = new Intent(context,SeeCarDetailActivity.class);
        intent.putExtra(CODE,code);
        context.startActivity(intent);
    }

    @Override
    protected void reLoad() {
        SeeCarDetailRequest request = new SeeCarDetailRequest();
        request.appId = getIntent().getIntExtra(CODE,0);
        AppApplication.getServerAPI().getSeeCarDetail(request)
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

    private SeeCarDetail getTestData(){
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
        return seeCarDetail;
    }

}
