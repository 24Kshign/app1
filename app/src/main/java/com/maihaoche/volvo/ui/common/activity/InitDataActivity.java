package com.maihaoche.volvo.ui.common.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
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
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityInitDataBinding;
import com.maihaoche.volvo.ui.car.domain.OutStorageInfo;
import com.maihaoche.volvo.ui.car.domain.OutstorageListRequest;
import com.maihaoche.volvo.ui.common.adapter.FragmentHeader;
import com.maihaoche.volvo.ui.common.adapter.InitDataAdapter;
import com.maihaoche.volvo.ui.instorage.event.ListItemChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


public class InitDataActivity extends HeaderProviderActivity<ActivityInitDataBinding> implements PullRecycleView.OnPullRefreshListener, PullRecyclerAdapter.OnLoadMoreListener {

    private ActivityInitDataBinding binding;
    private InitDataAdapter adapter;
    protected FragmentHeader header;
    private int totleCount;
    private int page = 1;

    @Override
    public int getContentResId() {
        return R.layout.activity_init_data;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initHeader("条码绑定");
        binding = getContentBinding();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.pullList.setLayoutManager(manager);
        binding.pullList.setPullRefreshListener(this);
        binding.pullList.setEmptyView(R.layout.view_empty);
        binding.pullList.setProgressView(R.layout.view_progress);
        adapter = new InitDataAdapter(this);
        adapter.setDefaultNoMoreView();
        adapter.setMoreListener(R.layout.view_load_more,this);
        header = new FragmentHeader(this,"共0辆车");
        adapter.addHeader(header);
        binding.pullList.setAdapter(adapter);
        initView();
        initSearch();
        reLoad();
    }

    private void initView() {
        binding.searchUnEnable.searchArea.setOnClickListener(v->{
            binding.searchUnEnable.searchArea.setVisibility(View.GONE);
            binding.searchEnable.searchArea.setVisibility(View.VISIBLE);
            binding.searchEnable.searchText.requestFocus();
            SoftKeyBoardUtil.showKeyBoardDely(binding.searchEnable.searchText);
        });
        Disposable disposable = RxBus.getDefault().register(ListItemChangeEvent.class,o->{
            ListItemChangeEvent event = (ListItemChangeEvent) o;
           if(event.getType().equals(ListItemChangeEvent.TYPE_INIT)){
               if(event.getOutStorageInfo()!=null){
                   showContent();
                   totleCount--;
                   header.setText("共"+totleCount+"辆车");
                   adapter.remove(event.getOutStorageInfo());
                   binding.pullList.scrollToPosition(0);
               }
           }
        });
        pend(disposable);
    }

    @Override
    protected void reLoad() {
        OutstorageListRequest request = new OutstorageListRequest();
        request.pageNo = page;
        request.warehouseIdList = new ArrayList<>();
        request.warehouseIdList.add(AppApplication.getGaragePO().getWmsGarageId());
        AppApplication.getServerAPI().getUnCodeList(request)
                .setOnDataError(emsg -> {
                    showContent();
                    AlertToast.show(emsg);
                })
                .setOnDataGet(response -> {
                    showContent();
                    if ((response.result == null || response.result.result.size() == 0) && response.result.pageNo == 1) {
                        binding.pullList.showEmpty();
                    } else {
                        if (response.result.pageNo == 1) {
                            adapter.clear();
                        }
                        setTotalCount("共"+response.result.totalCount+"辆车");
                        totleCount = response.result.totalCount;
                        fillData(response.result.result);showContent();
                        binding.pullList.showNoMoreViewIfDefaultPageSize();

                    }
                    page = response.result.pageNo + 1;

                })
                .setDoOnSubscribe(disposable -> {
                    if (page == 1) {
                        binding.pullList.showProgress();
                    }
                })
                .call(this);
    }

    private void fillData(List<OutStorageInfo> result) {
        adapter.addAll(result);
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
                        return charSequence.length() > 0;
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
                        return AppApplication.getServerAPI().getUnCodeList(request)
                                .getSingle();

                    }
                })
                .doOnError(error->{
                    showContent();
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
                        setTotalCount("共"+response.result.totalCount+"辆车");
                        adapter.addAll(response.result.result);
                        binding.pullList.showNoMoreViewIfDefaultPageSize();
                    }

                });
    }

    @Override
    public void onPullRefresh() {
        page = 1;
        reLoad();
    }

    @Override
    public void onLoadMore() {
        reLoad();
    }

    protected void setTotalCount(String totalCount){
        header.setText(totalCount);
    }
}
