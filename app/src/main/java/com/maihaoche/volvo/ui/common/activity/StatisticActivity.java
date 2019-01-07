package com.maihaoche.volvo.ui.common.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.maihaoche.commonbiz.module.dto.BaseResponse;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityStatisticBinding;
import com.maihaoche.volvo.ui.common.chart.BarChatUtil;
import com.maihaoche.volvo.ui.common.chart.StaticRequest;
import com.maihaoche.volvo.ui.common.chart.StaticticsData;

import java.util.ArrayList;



public class StatisticActivity extends HeaderProviderActivity<ActivityStatisticBinding>{

    private ActivityStatisticBinding binding;
    private PieChart storageChart;
    private BarChart olderChart;
    private StaticticsData staticticsData;

    @Override
    public int getContentResId() {
        return R.layout.activity_statistic;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initHeader("数据统计");
        binding = getContentBinding();
        initView();
        initClick();
        initData();
    }

    private void initView() {
        storageChart = (PieChart) binding.getRoot().findViewById(R.id.storage_chart);
        olderChart = (BarChart) binding.getRoot().findViewById(R.id.older_chart);

        binding.inOutContent.today.setOnClickListener(v->{
            initTextColor();
            binding.inOutContent.today.setTextColor(Color.parseColor("#6c94f7"));
            if(staticticsData!=null && staticticsData.outInToday!=null){
                binding.inOutContent.inStorage.setText(staticticsData.outInToday.inputCount+"");
                binding.inOutContent.outStorage.setText(staticticsData.outInToday.outputCount+"");
            }

        });

        binding.inOutContent.week.setOnClickListener(v->{
            initTextColor();
            binding.inOutContent.week.setTextColor(Color.parseColor("#6c94f7"));
            if(staticticsData!=null && staticticsData.outInThisWeek!=null){
                binding.inOutContent.inStorage.setText(staticticsData.outInThisWeek.inputCount+"");
                binding.inOutContent.outStorage.setText(staticticsData.outInThisWeek.outputCount+"");
            }

        });

        binding.inOutContent.month.setOnClickListener(v->{
            initTextColor();
            binding.inOutContent.month.setTextColor(Color.parseColor("#6c94f7"));
            if(staticticsData!=null && staticticsData.outInThisMonth!=null){
                binding.inOutContent.inStorage.setText(staticticsData.outInThisMonth.inputCount+"");
                binding.inOutContent.outStorage.setText(staticticsData.outInThisMonth.outputCount+"");
            }

        });

        binding.inOutContent.lastMonth.setOnClickListener(v->{
            initTextColor();
            binding.inOutContent.lastMonth.setTextColor(Color.parseColor("#6c94f7"));
            if(staticticsData!=null && staticticsData.outInLastMonth!=null){
                binding.inOutContent.inStorage.setText(staticticsData.outInLastMonth.inputCount+"");
                binding.inOutContent.outStorage.setText(staticticsData.outInLastMonth.outputCount+"");
            }

        });


    }

    private void initTextColor(){
        binding.inOutContent.today.setTextColor(Color.parseColor("#626262"));
        binding.inOutContent.week.setTextColor(Color.parseColor("#626262"));
        binding.inOutContent.month.setTextColor(Color.parseColor("#626262"));
        binding.inOutContent.lastMonth.setTextColor(Color.parseColor("#626262"));
    }

    private void initData() {
        StaticRequest request = new StaticRequest();
        request.warehouseId = AppApplication.getGaragePO().getWmsGarageId();
        AppApplication.getServerAPI().staticsData(request)
                .setOnDataError(emsg -> {
                    AlertToast.show(emsg);
                    showContent();
                })
                .setDoOnSubscribe(disposable -> showProgress())
                .setOnDataGet(response->{
                    showContent();
                    fillData(response);
                })
                .call(this);
    }

    private void fillData(BaseResponse<StaticticsData> response) {
        staticticsData = response.result;
        if(staticticsData!=null) {
            if(staticticsData.carAge!=null){
                ArrayList<Integer> list = new ArrayList<>();
                list.add(staticticsData.carAge.oneToSeven);
                list.add(staticticsData.carAge.eightToFifteen);
                list.add(staticticsData.carAge.sixteenToThirty);
                list.add(staticticsData.carAge.overThirty);
                BarChatUtil.initBarChart(this,olderChart,list);
            }

            if(staticticsData.capacity!=null){
                BarChatUtil.initPipChart(storageChart,staticticsData.capacity.usedCapacity,staticticsData.capacity.unusedCapacity);
            }

            if(staticticsData.outInToday!=null){
                binding.inOutContent.today.setTextColor(Color.parseColor("#6c94f7"));
                binding.inOutContent.inStorage.setText(staticticsData.outInToday.inputCount+"");
                binding.inOutContent.outStorage.setText(staticticsData.outInToday.outputCount+"");
            }

            if(staticticsData.exception!=null){
                binding.exceptionContent.allException.setText(staticticsData.exception.totalCount+"");
                binding.exceptionContent.inException.setText(staticticsData.exception.entryLossCount+"");
                binding.exceptionContent.outException.setText(staticticsData.exception.deliveryLossCount+"");
                binding.exceptionContent.inOutException.setText(staticticsData.exception.entryAndDeliveryCount+"");
            }


        }





    }

    private void initClick() {
        binding.inOut.setOnClickListener(v->{
            binding.arrow1.startAnim();
            if(binding.inOutContent.getRoot().getVisibility() == View.VISIBLE){
                binding.inOutContent.getRoot().setVisibility(View.GONE);
                binding.line1.setVisibility(View.GONE);
            }else{
                binding.inOutContent.getRoot().setVisibility(View.VISIBLE);
                binding.line1.setVisibility(View.VISIBLE);
            }

        });
        binding.storage.setOnClickListener(v->{
            binding.arrow2.startAnim();
            if(storageChart.getVisibility() == View.VISIBLE){
                storageChart.setVisibility(View.GONE);
                binding.line2.setVisibility(View.GONE);
            }else{
                storageChart.setVisibility(View.VISIBLE);
                binding.line2.setVisibility(View.VISIBLE);
            }
        });
        binding.older.setOnClickListener(v->{
            binding.arrow3.startAnim();
            if(olderChart.getVisibility() == View.VISIBLE){
                olderChart.setVisibility(View.GONE);
                binding.line3.setVisibility(View.GONE);
            }else{
                olderChart.setVisibility(View.VISIBLE);
                binding.line3.setVisibility(View.VISIBLE);
            }
        });
        binding.exception.setOnClickListener(v->{
            binding.arrow4.startAnim();
            if(binding.exceptionContent.getRoot().getVisibility() == View.VISIBLE){
                binding.exceptionContent.getRoot().setVisibility(View.GONE);
            }else{
                binding.exceptionContent.getRoot().setVisibility(View.VISIBLE);
            }
        });
    }


}
