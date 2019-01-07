package com.maihaoche.volvo.ui.car.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.maihaoche.commonbiz.module.dto.BaseResponse;
import com.maihaoche.commonbiz.module.dto.PagerResponse;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecycleView;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.commonbiz.service.utils.SoftKeyBoardUtil;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityBatchApplyKeyBinding;
import com.maihaoche.volvo.databinding.ActivityBatchOutStorageBinding;
import com.maihaoche.volvo.ui.car.adapter.BatchAdapter;
import com.maihaoche.volvo.ui.car.adapter.BatchKeyAdapter;
import com.maihaoche.volvo.ui.car.domain.OutStorageInfo;
import com.maihaoche.volvo.ui.car.domain.OutstorageListRequest;
import com.maihaoche.volvo.ui.common.adapter.FragmentHeader;
import com.maihaoche.volvo.ui.common.daomain.ApplyBatchKeyRequest;
import com.maihaoche.volvo.ui.common.daomain.ApplyKeyRequest;
import com.maihaoche.volvo.ui.common.daomain.KeyStatus;
import com.maihaoche.volvo.ui.instorage.event.ListItemChangeEvent;
import com.maihaoche.volvo.ui.setting.RefreshEvent;
import com.maihaoche.volvo.view.dialog.SelectCarDialog;
import com.maihaoche.volvo.view.dialog.SelectReasonForKeyDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


public class BatchApplyKeyActivity extends HeaderProviderActivity<ActivityBatchApplyKeyBinding> implements PullRecycleView.OnPullRefreshListener, PullRecyclerAdapter.OnLoadMoreListener {

    private static final String REASON_TYPE = "reasonType";

    private ActivityBatchApplyKeyBinding binding;
    private BatchKeyAdapter adapter;
    protected FragmentHeader header;
    private int page = 1;
    private int totleCount;
    private String searchParam;
    private int reasonType;
    private int maxSelect;
    //首次加载，在create中load，禁止在search中load
    private boolean firstLoad = true;


    @Override
    public int getContentResId() {
        return R.layout.activity_batch_apply_key;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initHeader("批量取用");
        reasonType = getIntent().getIntExtra(REASON_TYPE,1);
        maxSelect = getMaxSelect(reasonType);
        binding = getContentBinding();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.pullList.setLayoutManager(manager);
        binding.pullList.setPullRefreshListener(this);
        binding.pullList.setEmptyView(R.layout.view_empty);
        binding.pullList.setProgressView(R.layout.view_progress);
        binding.searchUnEnable.searchText.setText(R.string.un_search_text);
        adapter = new BatchKeyAdapter(this);
        adapter.setMoreListener(this);
        header = new FragmentHeader(this,"共0辆车");
        adapter.addHeader(header);
        binding.pullList.setAdapter(adapter);
        adapter.setMaxSelect(maxSelect);
        initListener();
        initSearch();
        loadData();


    }

    private void initListener() {
        showProgress();
        binding.searchUnEnable.searchArea.setOnClickListener(v->{
            binding.searchUnEnable.searchArea.setVisibility(View.GONE);
            binding.searchEnable.searchArea.setVisibility(View.VISIBLE);
            binding.searchEnable.searchText.requestFocus();
            SoftKeyBoardUtil.showKeyBoardDely(binding.searchEnable.searchText);
        });
        binding.out.setOnClickListener(v -> {

            if(adapter.getSelectedCars() == null || adapter.getSelectSize() < 1){
                AlertToast.show("请至少选择一辆申请钥匙");
                return;
            }

            ApplyKeyRequest request = new ApplyKeyRequest();
            request.carKeyIds = new ArrayList<>();
            for(OutStorageInfo info:adapter.getSelectedCars()){
                request.carKeyIds.add(info.carKeyId);
            }
            request.riskReasonType = reasonType;
            AppApplication.getServerAPI().applyKey(request)
                    .setOnDataError(emsg -> {
                        showContent();
                        AlertToast.show(emsg);
                    })
                    .setOnDataGet(baseResponse -> {
                        AlertToast.show("申请成功");
                        //这里成功后，在库列表需要刷新
                        RxBus.getDefault().post(new RefreshEvent());
                        finish();

                    })
                    .setDoOnSubscribe(sub->{
                        showProgress();
                    })
                    .call(this);
        });
        adapter.setSelectedListener(info -> {

            binding.count.setText("["+adapter.getSelectSize()+"]"+"查看");

        });
        binding.count.setOnClickListener(v->{
            new SelectCarDialog(this,adapter.getSelectedCars()).show();
            Disposable disposable = RxBus.getDefault().register(OutStorageInfo.class,o -> {
                binding.count.setText("["+adapter.getSelectSize()+"]"+"查看");
                adapter.notifyDataSetChanged();
            });
            pend(disposable);
        });

        Disposable disposable = RxBus.getDefault().register(ListItemChangeEvent.class,o->{
            ListItemChangeEvent event = (ListItemChangeEvent) o;
            if(event.getType().equals(ListItemChangeEvent.TYPE_BATCHOUTSTORAGE)){
                if(event.getInfos()!=null){
                    totleCount-=adapter.getSelectSize();
                    header.setText("共"+totleCount+"辆车");
                    adapter.remove(event.getInfos());
                    binding.pullList.scrollToPosition(0);
                }
                adapter.clearSelect();
                binding.count.setText("["+adapter.getSelectSize()+"]"+"查看");
            }
        });
        pend(disposable);

    }

    protected void loadData() {

        ApplyBatchKeyRequest request = new ApplyBatchKeyRequest();
        request.pageNo = page;
        if (AppApplication.getGaragePO().getWmsGarageId() == null) {
            AlertToast.show("请先选择仓库");
            return;
        }
        if(StringUtil.isNotEmpty(searchParam)){
            request.searchParam = searchParam;
        }
        request.warehouseIdList = new ArrayList<>();
        request.warehouseIdList.add(AppApplication.getGaragePO().getWmsGarageId());
        request.riskReasonType = reasonType;
        AppApplication.getServerAPI().getBatchKeyList(request)
                .setOnDataError(emsg -> {
                    showContent();
                    AlertToast.show(emsg);
                })
                .setOnDataGet(response -> {
                    if ((response.result == null || response.result.result.size() == 0) && response.result.pageNo == 1) {
                        showContent();
                        binding.pullList.showEmpty();
                    } else {
                        showContent();
                        if (response.result.pageNo == 1) {
                            adapter.clear();
                        }
                        header.setText("共"+response.result.totalCount+"辆车");
                        totleCount = response.result.totalCount;
                        fillData(response.result.result);
                        binding.pullList.showNoMoreViewIfDefaultPageSize();
                    }
                    page = response.result.pageNo + 1;

                })
                .setDoOnSubscribe(sub->{
                    if (page == 1) {
                        showProgress();
                    }
                })
                .call(this);
    }

    private void fillData(List<OutStorageInfo> infos) {
        if(adapter.getSelectedCars()!=null && adapter.getSelectSize()!=0){
            for(int i=0;i<adapter.getSelectSize();i++){
                for(int j=0;j<infos.size();j++){
                    if(infos.get(j).carId.longValue() == adapter.getSelectedCars().get(i).carId.longValue()){
                        infos.get(j).isSelect = true;
                    }
                }
            }
        }
        adapter.addAll(infos);
    }

    @Override
    public void onPullRefresh() {
        page = 1;
        loadData();
    }

    @Override
    public void onLoadMore() {
        loadData();
    }

    protected void initSearch() {
        RxTextView.textChanges(binding.searchEnable.searchText)
                // 表示延时多少秒后执行，当你敲完字之后停下来的半秒就会执行下面语句
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text->{
                    searchParam = text.toString();
                    page = 1;
                    if(!firstLoad){
                        loadData();
                    }
                    firstLoad = !firstLoad;
                });
    }

    private int getMaxSelect(int reasonType){
        switch (reasonType){
            case SelectReasonForKeyDialog.Reason.REASON_TEMP:
                return 10;
            case SelectReasonForKeyDialog.Reason.REASON_MOVE:
                return 50;
            case SelectReasonForKeyDialog.Reason.REASON_OUT:
                return Integer.MAX_VALUE;
            default:
                return 50;
        }
    }

    public static void toActivity(Context context,int reasonType){
        Intent intent = new Intent(context,BatchApplyKeyActivity.class);
        intent.putExtra(REASON_TYPE,reasonType);
        context.startActivity(intent);
    }
}
