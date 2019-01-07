package com.maihaoche.volvo.ui.instorage.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.maihaoche.commonbiz.module.dto.BaseResponse;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.dao.po.CarPO;
import com.maihaoche.volvo.databinding.ActivitySelectClientBinding;
import com.maihaoche.volvo.server.ServerGetBuilder;
import com.maihaoche.volvo.ui.common.daomain.Customer;
import com.maihaoche.volvo.ui.instorage.adapter.ClientListAdapter;
import com.maihaoche.volvo.ui.instorage.adapter.ContactListAdapter;
import com.maihaoche.volvo.ui.instorage.event.BrandEvent;
import com.maihaoche.volvo.ui.instorage.event.ContactEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


public class SelectClientActivity extends HeaderProviderActivity<ActivitySelectClientBinding> {

    private ActivitySelectClientBinding binding;
    private ClientListAdapter adapter;
    //初始化搜索的时候会默认调用一次
    private boolean isFirstSearch = true;

    @Override
    public int getContentResId() {
        return R.layout.activity_select_client;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initHeader("选择客户");
        binding = getContentBinding();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.list.setLayoutManager(manager);
        adapter = new ClientListAdapter(this,new ArrayList<>());
        adapter.setListener(contact -> {
            BrandEvent event = new BrandEvent(contact.name,contact.id,BrandEvent.TYPE_CLIENT);
            RxBus.getDefault().post(event);
            finish();
        });
        binding.list.setAdapter(adapter);
        initSearch();
    }

    @Override
    protected void showProgress(){
        binding.progressView.setVisibility(View.VISIBLE);
        binding.list.setVisibility(View.GONE);
    }
    @Override
    protected void showContent(){
        binding.progressView.setVisibility(View.GONE);
        binding.list.setVisibility(View.VISIBLE);
    }

    protected void initSearch() {
        RxTextView.textChanges(binding.customerName)
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
                .switchMapSingle(new Function<CharSequence, Single<BaseResponse<List<Customer>>>>() {
                    @Override
                    public Single<BaseResponse<List<Customer>>> apply(
                            @NonNull CharSequence charSequence) throws Exception {
                        // 网络请求操作，获取我们需要的数据

                        return AppApplication.getServerAPI().getCustomer(charSequence.toString())
                                .getSingle();

                    }
                })
                .doOnError(error->{
                    AlertToast.show(error.getMessage());
                })
                .doOnSubscribe(disposable -> {
                    if(isFirstSearch){
                        isFirstSearch = !isFirstSearch;
                    }else{
                        showProgress();
                    }

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->{
                    showContent();
                    if(response.result!=null){
                        Customer customer = new Customer();
                        customer.name = "其他客户";
                        customer.id = 0L;
                        response.result.add(0,customer);
                        adapter.setContacts(response.result);
                    }

                });
    }
}
