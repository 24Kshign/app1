package com.maihaoche.volvo.ui.inwarehouse.record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.module.ui.recyclerview.BaseRecyclerViewDecration;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecycleView;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityRecordlistBinding;
import com.maihaoche.volvo.server.dto.StocktakeRecordVO;
import com.maihaoche.volvo.server.dto.request.WarehouseIdPageRequest;

/**
 * 盘库列表页面
 * 作者：yang
 * 时间：17/6/9
 * 邮箱：yangyang@maihaoche.com
 */
public class RecordListActivity extends HeaderProviderActivity<ActivityRecordlistBinding> {

    private static final String WAREHOUSE_ID = "warehouse_id";


    private long mWarehouseId = 0;//仓库id
    private RecordListAdapter mRecordListAdapter;

    public static Intent createIntent(Context context, long warehouseId) {
        Intent intent = new Intent(context, RecordListActivity.class);
        intent.putExtra(WAREHOUSE_ID, warehouseId);
        return intent;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_recordlist;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        init();
        load();
    }

    private void init() {
        //数据
        if (getIntent() != null && getIntent().getExtras() != null) {
            mWarehouseId = getIntent().getExtras().getLong(WAREHOUSE_ID);
        }
        //ui
        initHeader("盘库列表");
        ActivityRecordlistBinding binding = getContentBinding();
        binding.rvRecordlist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvRecordlist.addItemDecoration(new BaseRecyclerViewDecration(10));
        binding.rvRecordlist.setPullRefreshListener(mOnPullRefreshListener);
        mRecordListAdapter = new RecordListAdapter(this);
        binding.rvRecordlist.setAdapter(mRecordListAdapter);
        mRecordListAdapter.setOnItemClickListener(position -> {
            StocktakeRecordVO recordVO = mRecordListAdapter.getAllData().get(position);
            startActivity(ActivityStocktakeDetail.create(RecordListActivity.this,recordVO.stocktakeRecordId));
        });
        mRecordListAdapter.setMoreListener(mLoadMoreListener);
        mRecordListAdapter.setDefaultNoMoreView();

    }
    private WarehouseIdPageRequest mWarehouseIdPageRequest = new WarehouseIdPageRequest();


    /**
     * 下拉刷新
     */
    private PullRecycleView.OnPullRefreshListener mOnPullRefreshListener = () -> {
        mWarehouseIdPageRequest.pageNo=1;
        mRecordListAdapter.clear();
        load();
    };

    /**
     * 上拉加载更多
     */
    private PullRecyclerAdapter.OnLoadMoreListener mLoadMoreListener = () -> {
        load();
    };


    private void load() {
        //获取盘库记录
        mWarehouseIdPageRequest.warehouseId = mWarehouseId;
        AppApplication.getServerAPI().getStocktakeRecords(mWarehouseIdPageRequest)
                .setTransformer(getIOLoadingTransformer())
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setOnDataGet(pagerResponse -> {
                    mRecordListAdapter.addAll(pagerResponse.result.result);
                    mWarehouseIdPageRequest.pageNo++;
                })
                .call(this);
    }




}
