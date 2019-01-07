package com.maihaoche.volvo.ui.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.maihaoche.commonbiz.module.dto.BaseResponse;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityOrderDetailBinding;
import com.maihaoche.volvo.ui.common.adapter.OrderDetailAdapter;
import com.maihaoche.volvo.ui.common.daomain.OrderCarList;
import com.maihaoche.volvo.ui.common.daomain.OrderCarListRequest;

import java.util.ArrayList;
import java.util.List;


public class OrderDetailActivity extends HeaderProviderActivity<ActivityOrderDetailBinding> {

    private static final String TYPE_ID = "type_id";

    private ActivityOrderDetailBinding binding;
    private OrderDetailAdapter inAdapter;
    private OrderDetailAdapter haveInAdapter;


    public static Intent create(Context context, String bizOrderNo){
        Intent intent = new Intent(context,OrderDetailActivity.class);
        intent.putExtra(TYPE_ID,bizOrderNo);
        return intent;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initHeader("订单详情");
        binding = getContentBinding();
        initAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reLoad();
    }

    private void initAdapter() {

        haveInAdapter = new OrderDetailAdapter(this,OrderDetailAdapter.TYPE_HAVE_IN);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.haveInstorageList.setLayoutManager(manager);
        binding.haveInstorageList.setAdapter(haveInAdapter);


        inAdapter = new OrderDetailAdapter(this,OrderDetailAdapter.TYPE_IN);
        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        manager1.setOrientation(LinearLayoutManager.VERTICAL);
        binding.instoragedList.setLayoutManager(manager1);
        binding.instoragedList.setAdapter(inAdapter);
    }

    @Override
    protected void reLoad() {
        OrderCarListRequest request = new OrderCarListRequest();
        request.bizOrderNo = getIntent().getStringExtra(TYPE_ID);
        request.warehouseIdList = new ArrayList<>();
        request.warehouseIdList.add(AppApplication.getGaragePO().getWmsGarageId());
        AppApplication.getServerAPI().getOrderCarList(request)
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setDoOnSubscribe(disposable -> showProgress())
                .setOnDataGet(response->{
                    showContent();
                    fillData(response);
                })
                .call(this);
    }

    private void fillData(BaseResponse<OrderCarList> response) {
        OrderCarList list = response.result;
        binding.orderCode.setText(list.orderNo);
        binding.salesman.setText(list.salesman);
        binding.salesPhone.setText(list.salesmanPhone);
        binding.client.setText(list.customer);
        binding.clientMan.setText(list.customerContact);
        binding.clientPhone.setText(list.customerContactPhone);
        binding.company.setText(list.logisticsCompany);
        binding.companyMan.setText(list.logisticsContact);
        binding.companyPhone.setText(list.logisticsContactPhone);
        binding.instoraged.setText(list.storedNum+"辆");
        binding.uninstorage.setText(list.unstoredNum+"辆");
        if(list.unstoredCarVOList==null || list.unstoredCarVOList.size() == 0){
            binding.uninstorageTitle.setVisibility(View.GONE);
            inAdapter.setLists(new ArrayList<>());
        }else{
            binding.uninstorageTitle.setVisibility(View.VISIBLE);
            inAdapter.setLists(list.unstoredCarVOList);
        }

        if(list.storedCarVOList==null || list.storedCarVOList.size() == 0){
            binding.instorageTitle.setVisibility(View.GONE);
            haveInAdapter.setLists(new ArrayList<>());
        }else{
            binding.instorageTitle.setVisibility(View.VISIBLE);
            haveInAdapter.setLists(list.storedCarVOList);
        }
    }
}
