package com.maihaoche.volvo.ui.inwarehouse.record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.utils.HintUtil;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityStocktakeDetailBinding;
import com.maihaoche.volvo.server.dto.InWarehouseCarVO;
import com.maihaoche.volvo.server.dto.StocktakeDetailCarVO;
import com.maihaoche.volvo.server.dto.request.CarIdRequest;
import com.maihaoche.volvo.server.dto.request.StocktakeRecordIdRequest;
import com.maihaoche.volvo.ui.AbsScanBaseActivity;
import com.maihaoche.volvo.ui.car.cardetail.ActivityCarInfo;
import com.maihaoche.volvo.ui.inwarehouse.cars.InWarehouseCarVH;
import com.maihaoche.volvo.view.dialog.SearchCarDialog;

import java.util.List;

/**
 * 类简介：盘库详情的车辆列表页面
 * 作者：  yang
 * 时间：  2017/8/14
 * 邮箱：  yangyang@maihaoche.com
 */

public class ActivityStocktakeDetail extends AbsScanBaseActivity<ActivityStocktakeDetailBinding> {


    private static final String STOCKTAKE_RECORD_ID = "stocktake_record_id";
    //盘库记录id
    private long mStocktakeRecordId;

    private StocktakeRecordIdRequest mRecordIdRequest = new StocktakeRecordIdRequest();
    private StocktakeDetailListAdapter mAdapter;

    private SearchCarDialog mSearchCarDialog;


    public static Intent create(Context context, long stocktakeRecordId) {
        Intent intent = new Intent(context, ActivityStocktakeDetail.class);
        intent.putExtra(STOCKTAKE_RECORD_ID, stocktakeRecordId);
        return intent;

    }


    @Override
    public int getContentResId() {
        return R.layout.activity_stocktake_detail;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        init();
        load();
    }

    private void init() {
        initHeader("盘库详情");
        Bundle data = getIntent().getExtras();
        if (data != null) {
            mStocktakeRecordId = data.getLong(STOCKTAKE_RECORD_ID);
        }
        mAdapter = new StocktakeDetailListAdapter(this, new StocktakeDetailListAction());
        mAdapter.setDefaultNoMoreView();
        mAdapter.setMoreListener(() -> {
            mRecordIdRequest.pageNo++;
            load();
        });
        ActivityStocktakeDetailBinding binding = getContentBinding();
        binding.rvStocktakeDetailList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvStocktakeDetailList.setAdapter(mAdapter);
        binding.rvStocktakeDetailList.setPullRefreshListener(() -> {
            mRecordIdRequest.pageNo = 1;
            load();
        });
    }


    private void load() {
        mRecordIdRequest.stocktakeRecordId = mStocktakeRecordId;
        AppApplication.getServerAPI().getStocktakeDetails(mRecordIdRequest)
                .setTransformer(getIOLoadingTransformer())
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setOnDataGet(pagerResponseBaseResponse -> {
                    List<StocktakeDetailCarVO> voList = pagerResponseBaseResponse.result.result;
                    if (mRecordIdRequest.pageNo <= 1) {
                        mAdapter.setNotifyOnChange(false);
                        mAdapter.clear();
                        mAdapter.setNotifyOnChange(true);
                    }
                    mAdapter.addAll(voList, true);
                })
                .call(this);
    }

    private class StocktakeDetailListAction implements InWarehouseCarVH.ActionList
            , InWarehouseCarVH.ActionSearch {

        @Override
        public void toCarDetail(InWarehouseCarVO carVO) {
            startActivity(ActivityCarInfo.createIntent(ActivityStocktakeDetail.this, new CarIdRequest(carVO.carId, carVO.carStoreType)));
        }

        @Override
        public void search(InWarehouseCarVO carVO) {
            HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_FIND_CAR);
            startInventory();
            SearchCarDialog.SearchInfo searchInfo = new SearchCarDialog.SearchInfo();
            searchInfo.caraUnique = carVO.carUnique;
            searchInfo.carAttribut = carVO.carAttribute;
            searchInfo.carTagId = carVO.carTagId;
            mSearchCarDialog = new SearchCarDialog(ActivityStocktakeDetail.this, searchInfo, false);
            mSearchCarDialog.setOnDismissListener(dialog -> stopInventory());
            mSearchCarDialog.show();
        }
    }

    @Override
    protected void onTagResult(String rfid) {
        super.onTagResult(rfid);
        if (!TextUtils.isEmpty(rfid) && mSearchCarDialog != null && rfid.equals(mSearchCarDialog.getTagId())) {
            getHandler().post(() -> mSearchCarDialog.setFind(true));
            HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_CAR_IS_NEARBY_WITH_VIBRATOR);
            stopInventory();
        }
    }

    /**
     * 盘库详情列表的adapter
     */
    public class StocktakeDetailListAdapter extends PullRecyclerAdapter<StocktakeDetailCarVO> {

        private InWarehouseCarVH.ActionList mActionList = null;


        public StocktakeDetailListAdapter(Context context, InWarehouseCarVH.ActionList action) {
            super(context);
            mActionList = action;
        }


        @Override
        public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            return new InWarehouseCarVH(parent);
        }

        @Override
        public void OnBindViewHolder(BaseViewHolder holder, int position) {
            super.OnBindViewHolder(holder, position);
            StocktakeDetailCarVO stocktakeDetailCarVO = getItem(position);
            InWarehouseCarVH carVH = (InWarehouseCarVH) holder;
            carVH.setData(position, stocktakeDetailCarVO.inWarehouseCarVO, mActionList);
            if (stocktakeDetailCarVO.stocktakeDetailStatus == StocktakeDetailCarVO.STOCK_TAKE_STATUS_TO) {
                carVH.getBinding().stocktakeStatusIcon.setImageResource(R.drawable.vector_cross);
            } else if (stocktakeDetailCarVO.stocktakeDetailStatus == StocktakeDetailCarVO.STOCK_TAKE_STATUS_DONE) {
                carVH.getBinding().stocktakeStatusIcon.setImageResource(R.drawable.vector_tick);
            } else {
                carVH.getBinding().stocktakeStatusIcon.setImageDrawable(null);
            }
        }
    }

}
