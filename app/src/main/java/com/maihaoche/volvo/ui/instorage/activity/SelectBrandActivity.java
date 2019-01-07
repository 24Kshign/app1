package com.maihaoche.volvo.ui.instorage.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jude.easyrecyclerview.decoration.StickyHeaderDecoration;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivitySelectBrandBinding;
import com.maihaoche.volvo.ui.common.daomain.Customer;
import com.maihaoche.volvo.ui.instorage.adapter.BrandAdapter;
import com.maihaoche.volvo.ui.instorage.daomain.BrandNewInfo;
import com.maihaoche.volvo.ui.instorage.event.BrandEvent;
import com.maihaoche.volvo.ui.instorage.event.ContactEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class SelectBrandActivity extends HeaderProviderActivity<ActivitySelectBrandBinding> {

    public static final int TYPE_BRAND = 1;
    public static final int TYPE_SERIES = 2;

    private static final String TYPE = "type";
    private static final String ID = "id";

    private ActivitySelectBrandBinding binding;
    private BrandAdapter<BrandNewInfo> adapter;
    private int type;
    private Long brandId;
    private LinearLayoutManager mLayoutManager;
    private LinkedHashMap<String, Integer> mIndexMap = new LinkedHashMap<>();

    public static Intent create(BaseActivity activity,int type){
        Intent intent = new Intent(activity,SelectBrandActivity.class);
        intent.putExtra(TYPE,type);
        return intent;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_select_brand;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initHeader("选择品牌");
        binding = getContentBinding();
        type = getIntent().getIntExtra(TYPE,1);
        if(type == TYPE_SERIES){
            brandId = getIntent().getLongExtra(ID,0);
        }
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.list.setLayoutManager(mLayoutManager);
        binding.list.setHasFixedSize(true);
        adapter = new BrandAdapter(this);
        adapter.setOnItemClickListener(contact -> {
            BrandNewInfo info = adapter.getItem(contact);
            BrandEvent event = new BrandEvent(info.brandName,info.brandId,BrandEvent.TYPE_BRAND);
            RxBus.getDefault().post(event);
            finish();
        });

        binding.list.setAdapter(adapter);
        StickyHeaderDecoration sticky =
                new StickyHeaderDecoration(adapter);
        binding.list.addItemDecoration(sticky);
        initListener();
        setIndexView();
        reLoad();
    }

    private void initListener() {

        binding.indexBrand.setOnIndexTouchListener((key,pos) ->
                mLayoutManager.scrollToPositionWithOffset(pos, 0));

        binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();
                int pos = layoutManager.findFirstVisibleItemPosition();
                long id = ((BrandAdapter) recyclerView.getAdapter()).getHeaderId(pos);
                binding.indexBrand.setSelectPosition(id);
            }
        });
    }

    private void setIndexView() {
        mIndexMap.clear();
        for (int i = 0; i < adapter.getSize(); i++) {
            BrandNewInfo info = adapter.getData().get(i);
            String letter = info.brandLetter;
            String commonBrand = "常用品牌";
            if (commonBrand.equals(letter)) {
                String common = "常";
                if (!mIndexMap.containsKey(common)) {
                    mIndexMap.put(common, i);
                }
            } else if (!mIndexMap.containsKey(info.brandLetter)) {
                mIndexMap.put(info.brandLetter, i);
            }
        }
        binding.indexBrand.setIndexList(mIndexMap.entrySet());
    }

    @Override
    protected void reLoad() {

        AppApplication.getServerAPI().getBrand()
                .setOnDataGet(response->{
                        adapter.addData(response.result);
                        setIndexView();
                })
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .call(this);



//        List<BrandNewInfo> list = new ArrayList<>();
//        for(int i=0;i<40;i++){
//            BrandNewInfo info = new BrandNewInfo();
//            info.brandId = 1L;
//            info.brandName = i+"宝马";
//            char aa = (char) (66+i%26);
//            info.brandLetter = aa+"";
//            list.add(info);
//        }
//
//        adapter.addData(list);
//        setIndexView();
    }
}
