package com.maihaoche.volvo.ui.car.activity;

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
import com.maihaoche.volvo.databinding.ActivityBatchOutStorageBinding;
import com.maihaoche.volvo.ui.car.adapter.BatchAdapter;
import com.maihaoche.volvo.ui.car.domain.OutStorageInfo;
import com.maihaoche.volvo.ui.car.domain.OutstorageListRequest;
import com.maihaoche.volvo.ui.common.adapter.FragmentHeader;
import com.maihaoche.volvo.ui.instorage.event.ListItemChangeEvent;
import com.maihaoche.volvo.util.TextWatcherUtil;
import com.maihaoche.volvo.view.dialog.SelectCarDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


public class BatchOutStorageActivity extends HeaderProviderActivity<ActivityBatchOutStorageBinding> implements PullRecycleView.OnPullRefreshListener, PullRecyclerAdapter.OnLoadMoreListener {

    private ActivityBatchOutStorageBinding binding;
    private BatchAdapter adapter;
    protected FragmentHeader header;
    private int page = 1;
    private int totleCount;
    private String searchParam;
    private boolean isFirst = true;


    @Override
    public int getContentResId() {
        return R.layout.activity_batch_out_storage;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initHeader("批量出库");
        binding = getContentBinding();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.pullList.setLayoutManager(manager);
        binding.pullList.setPullRefreshListener(this);
        binding.pullList.setEmptyView(R.layout.view_empty);
        binding.pullList.setProgressView(R.layout.view_progress);
        binding.searchUnEnable.searchText.setText(R.string.un_search_text);
        adapter = new BatchAdapter(this);
        adapter.setMoreListener(this);
        header = new FragmentHeader(this,"共0辆车");
        adapter.addHeader(header);
        binding.pullList.setAdapter(adapter);
        initListener();
        initSearch();
        reLoad();


    }

    private void initListener() {
        binding.searchUnEnable.searchArea.setOnClickListener(v->{
            binding.searchUnEnable.searchArea.setVisibility(View.GONE);
            binding.searchEnable.searchArea.setVisibility(View.VISIBLE);
            binding.searchEnable.searchText.requestFocus();
            SoftKeyBoardUtil.showKeyBoardDely(binding.searchEnable.searchText);
        });

        binding.searchEnable.searchText.addTextChangedListener(new TextWatcherUtil(0, null, binding.searchEnable.clear));
        binding.searchEnable.clear.setOnClickListener(v->{
            binding.searchEnable.searchText.setText("");
        });

        binding.out.setOnClickListener(v -> {

            if(adapter.getSelect() == null || adapter.getSelect().size()<=0){
                AlertToast.show("请至少选择一辆可出库车辆");
                return;
            }

            startActivity(OutStorageInfoActivity.create(this,OutStorageInfoActivity.TYPE_MULIT,adapter.getSelect()));

        });
        adapter.setSelectedListener(info -> {
            if(info.isSelect){
                if(adapter.isAllSelect()){
                    binding.selectBox.setChecked(true);
                }
                binding.count.setText("["+adapter.getSelect().size()+"]"+"查看");

            }else{
                binding.selectBox.setChecked(false);
                binding.count.setText("["+adapter.getSelect().size()+"]"+"查看");
            }

        });
        binding.count.setOnClickListener(v->{
            new SelectCarDialog(this,adapter.getSelect()).show();
            Disposable disposable = RxBus.getDefault().register(OutStorageInfo.class,o -> {
                Log.e("hehe",adapter.getSelect().size()+"");
                binding.count.setText("["+adapter.getSelect().size()+"]"+"查看");
                adapter.notifyDataSetChanged();
            });
            pend(disposable);
        });

        Disposable disposable = RxBus.getDefault().register(ListItemChangeEvent.class,o->{
            ListItemChangeEvent event = (ListItemChangeEvent) o;
            if(event.getType().equals(ListItemChangeEvent.TYPE_BATCHOUTSTORAGE)){
                if(event.getInfos()!=null){
                    totleCount-=adapter.getSelect().size();
                    header.setText("共"+totleCount+"辆车");
                    adapter.remove(event.getInfos());
                    binding.pullList.scrollToPosition(0);
                }
                adapter.getSelect().clear();
                binding.count.setText("["+adapter.getSelect().size()+"]"+"查看");
            }
        });
        pend(disposable);

        binding.allSelect.setOnClickListener(v->{
            if(binding.selectBox.isChecked()){
                binding.selectBox.setChecked(false);
                adapter.unSelect();
            }else{
                binding.selectBox.setChecked(true);
                adapter.selectAll();
            }
            binding.count.setText("["+adapter.getSelect().size()+"]"+"查看");
        });

    }

    @Override
    protected void reLoad() {
        OutstorageListRequest request = new OutstorageListRequest();
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
        AppApplication.getServerAPI().canOutStorage(request)
                .setOnDataError(emsg -> {
                    AlertToast.show(emsg);
                    showContent();
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
                .setDoOnSubscribe(disposable -> {
                    if (page == 1) {
                        showProgress();
                    }
                })
                .call(this);
    }

    private void fillData(List<OutStorageInfo> infos) {
        if(adapter.getSelect()!=null && adapter.getSelect().size()!=0){
            for(int i=0;i<adapter.getSelect().size();i++){
                for(int j=0;j<infos.size();j++){
                    if(infos.get(j).carId.longValue() == adapter.getSelect().get(i).carId.longValue()){
                        infos.get(j).isSelect = true;
                    }
                }
            }
        }
        adapter.addAll(infos);
        binding.selectBox.setChecked(false);
    }

    @Override
    public void onPullRefresh() {
        page = 1;
//        selectedCars.clear();
//        binding.count.setText("["+selectedCars.size()+"]"+"查看");
        reLoad();
    }

    @Override
    public void onLoadMore() {
        reLoad();
    }

    protected void initSearch() {
        RxTextView.textChanges(binding.searchEnable.searchText)
                // 表示延时多少秒后执行，当你敲完字之后停下来的半秒就会执行下面语句
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // 数据转换 flatMap: 当同时多个数据请求访问的时候，前面的网络数据会覆盖后面的网络数据
                // 数据转换 switchMap: 当同时多个网络请求访问的时候，会以最后一个发送请求为准，前面网络数据会被最后一个覆盖
                .filter(new Predicate<CharSequence>() {
                    @Override
                    public boolean test(CharSequence charSequence) throws Exception {
                        if(isFirst) {
                            isFirst = false;
                            return false;
                        }
                        else return charSequence.length() >= 0;
                    }
                })
                .switchMapSingle(new Function<CharSequence, Single<BaseResponse<PagerResponse<OutStorageInfo>>>>() {
                    @Override
                    public Single<BaseResponse<PagerResponse<OutStorageInfo>>> apply(
                            @NonNull CharSequence charSequence) throws Exception {
                        // 网络请求操作，获取我们需要的数据
                        OutstorageListRequest request = new OutstorageListRequest();
                        request.pageNo = 1;
                        request.warehouseIdList = new ArrayList<>();
                        request.warehouseIdList.add(AppApplication.getGaragePO().getWmsGarageId());
                        request.searchParam = charSequence.toString();
                        searchParam = request.searchParam;
                        return AppApplication.getServerAPI().canOutStorage(request)
                                .getSingle();

                    }
                })
                .doOnError(error->{
                    AlertToast.show(error.getMessage());
                })
                .doOnSubscribe(disposable -> {
                    showProgress();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->{
                    showContent();
                    if(response.result!=null){
                        adapter.clear();
                        header.setText("共"+response.result.totalCount+"辆车");
                        adapter.addAll(response.result.result);
                        binding.selectBox.setChecked(false);
                    }

                });
    }
}
