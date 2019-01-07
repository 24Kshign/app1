package com.maihaoche.volvo.ui.common.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.module.ui.BaseFragmentActivity;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecycleView;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.utils.SoftKeyBoardUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.FragmentBaseListBinding;
import com.maihaoche.volvo.ui.AbsScanBaseFragment;
import com.maihaoche.volvo.ui.common.adapter.FragmentHeader;
import com.maihaoche.volvo.util.TextWatcherUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gujian
 * Time is 2017/8/4
 * Email is gujian@maihaoche.com
 */

public abstract class BaseListFragment<T> extends AbsScanBaseFragment<FragmentBaseListBinding> implements BaseListFragmentView<BaseListFragmentPresenter,T>,
        PullRecycleView.OnPullRefreshListener, PullRecyclerAdapter.OnLoadMoreListener {

    protected static final String TYPE = "type";

    protected FragmentBaseListBinding binding;
    protected String mType;
    private PullRecyclerAdapter adapter;
    protected BaseListFragmentPresenter presenter;
    protected FragmentHeader header;
    protected int totleCount;
    protected boolean isVisible = false;
    protected boolean shouldLoad = true;
    private boolean isFirst = true;//搜索初始化时默认会发送一个空字符串，过滤掉
    protected BaseActivity activity;
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_base_list;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseFragmentActivity){
        }else {
            this.activity = (BaseActivity) context;
        }
    }

    @Override
    protected void onAfterCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onAfterCreateView(inflater,container,savedInstanceState);
        binding = getLayoutBinding();
        Bundle bundle = getArguments();
        if(bundle!=null){
            mType = getArguments().getString(TYPE);
        }
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
        initAdapter();
        initSearch();
        if(isVisible){
            reload();
            shouldLoad = false;
        }

    }

    protected void setSearchUnEnableText(int id){
        binding.searchUnEnable.searchText.setText(id);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = getUserVisibleHint();
        if(isVisible && isVisible()&&shouldLoad){
            reload();
            shouldLoad = false;
        }
    }

    private void initAdapter() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.pullList.setLayoutManager(manager);
        binding.pullList.setPullRefreshListener(this);
        binding.pullList.setEmptyView(R.layout.recycle_view_empty);
        binding.pullList.setProgressView(R.layout.view_progress);

        adapter = getAdapter();
        adapter.setDefaultNoMoreView();
        header = new FragmentHeader(getContext(),"共0辆车");
        adapter.addHeader(header);
        binding.pullList.setAdapter(adapter);
        adapter.setMoreListener(R.layout.view_load_more,this);

    }

    public abstract PullRecyclerAdapter getAdapter();

    protected void setTotalCount(String totalCount){
        header.setText(totalCount);
    }

    @Override
    protected void reload() {
        if(presenter!=null){
            presenter.loadData();
        }
    }

    @Override
    public void onPullRefresh() {
        if(presenter!=null){
            presenter.setPage(1);
        }
        reload();
    }

    protected void clearSearchText(){
        binding.searchEnable.searchText.setText("");
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
                        if(isFirst){
                            isFirst = !isFirst;
                        }else{
                            getSearchValue(charSequence.toString());
                        }


                        return true;
                    }
                })
                .subscribe();
    }

    @Override
    public void setHeader(int count) {
        header.setText("共"+count+"辆车");
        this.totleCount = count;
        adapter.notifyItemChanged(0);
    }

    @Override
    public void clear() {
        adapter.clear();
    }

    @Override
    public void getSearchValue(String string) {
        adapter.clear();
        header.setText("");
        adapter.notifyItemChanged(0);
        if(presenter!=null){
            presenter.searchLoad(string);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if(mCompositeDisposable!=null){
            mCompositeDisposable.dispose();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = AppApplication.getRefWatcher(getActivity());
//        refWatcher.watch(this);
    }

    protected void scrollToFirst(){
        binding.pullList.scrollToPosition(0);
    }

    @Override
    public void onLoadMore() {
        reload();
    }

    @Override
    public void setPresenter(BaseListFragmentPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showEmpty() {
        binding.pullList.showEmpty();
    }

    @Override
    public void showContent() {
        binding.pullList.showRecycler();
    }

    @Override
    public void showProgress() {
        binding.pullList.showProgress();
    }

    @Override
    public void pend(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void showMore() {
        binding.pullList.showNoMoreViewIfDefaultPageSize();
    }
}
