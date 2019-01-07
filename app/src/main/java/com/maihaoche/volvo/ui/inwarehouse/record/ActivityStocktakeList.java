package com.maihaoche.volvo.ui.inwarehouse.record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.module.ui.recyclerview.BaseRecyclerViewDecration;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityStocktakeListBinding;
import com.maihaoche.volvo.server.dto.request.CarIdPageRequest;
import com.maihaoche.volvo.server.dto.request.CarIdRequest;

/**
 * 类简介：盘点信息列表
 * 作者：  yang
 * 时间：  2017/8/17
 * 邮箱：  yangyang@maihaoche.com
 */

public class ActivityStocktakeList extends HeaderProviderActivity<ActivityStocktakeListBinding>{


    private CarIdPageRequest mCarIdRequest;

    private static final String CARID_REQUEST = "carid_request";


    private StocktakeListAdapter mAdapter;

    public static Intent createIntent(Context context,CarIdRequest carIdRequest){
        Intent intent = new Intent(context,ActivityStocktakeList.class);
        intent.putExtra(CARID_REQUEST,carIdRequest);
        return intent;
    }


    @Override
    public int getContentResId() {
        return R.layout.activity_stocktake_list;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        init();
        reload();
    }

    private void init(){
        CarIdRequest carIdRequest = (CarIdRequest) getIntent().getSerializableExtra(CARID_REQUEST);
        mCarIdRequest = new CarIdPageRequest();
        mCarIdRequest.carId = carIdRequest.carId;
        mCarIdRequest.carStoreType = carIdRequest.carStoreType;


        initHeader("盘点信息");


        mAdapter = new StocktakeListAdapter(this);
        mAdapter.setDefaultNoMoreView();
        mAdapter.setMoreListener(() -> {
            mCarIdRequest.pageNo++;
            reload();
        });

        ActivityStocktakeListBinding binding = getContentBinding();
        binding.stocktakeList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        binding.stocktakeList.addItemDecoration(BaseRecyclerViewDecration.createLineDecration());
        binding.stocktakeList.setPullRefreshListener(() -> {
            mCarIdRequest.pageNo=1;
            mAdapter.clear();
            reload();
        });
        binding.stocktakeList.setAdapter(mAdapter);

    }

    private void reload(){
        AppApplication.getServerAPI().getStocktakeDetailList(mCarIdRequest)
                .setTransformer(getIOLoadingTransformer())
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setOnDataGet(pagerResponseBaseResponse -> mAdapter.addAll(pagerResponseBaseResponse.result.result))
                .call(this);
    }


}
