package com.maihaoche.volvo.ui.car.cardetail;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.maihaoche.commonbiz.module.ui.recyclerview.DataBindingViewHolder;
import com.maihaoche.commonbiz.module.ui.recyclerview.SimpleAdapter;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.IncludeRecyclerviewBinding;
import com.maihaoche.volvo.server.dto.PvNameVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 类简介：车辆详情中，其他情况的列表的adapter
 * 作者：  yang
 * 时间：  2017/8/16
 * 邮箱：  yangyang@maihaoche.com
 */

public class OtherInfoAdapter extends SimpleAdapter<List<PvNameVO>, DataBindingViewHolder> {

    public OtherInfoAdapter(Context context) {
        super(context);
    }

    @Override
    protected void bindViewHolder(@Nullable List<PvNameVO> pvNameVOs, DataBindingViewHolder holder) {
        IncludeRecyclerviewBinding binding = holder.getBinding();
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        List<CarAttr> carAttrs = new ArrayList<>();
        for (PvNameVO vo :
                pvNameVOs) {
            carAttrs.add(new CarAttr(vo.pname, vo.vname));
        }
        CarAttrAdapter listAdapter = new CarAttrAdapter(mContext, parent -> new CarAttrVH(parent).setAttrNameWidthDP(100));
        listAdapter.addAll(carAttrs);
        binding.recyclerview.setAdapter(listAdapter);
    }

    @Override
    public DataBindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DataBindingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.include_recyclerview, parent, false));
    }
}
