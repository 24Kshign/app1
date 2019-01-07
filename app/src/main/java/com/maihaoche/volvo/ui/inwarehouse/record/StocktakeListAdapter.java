package com.maihaoche.volvo.ui.inwarehouse.record;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.image.ImageLoader;
import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ItemStocktakeDetailBinding;
import com.maihaoche.volvo.server.dto.StocktakeDetailVO;

/**
 * 类简介：盘点详情列表的adapter。
 * 作者：  yang
 * 时间：  2017/8/16
 * 邮箱：  yangyang@maihaoche.com
 */

public class StocktakeListAdapter extends PullRecyclerAdapter<StocktakeDetailVO>{

    public StocktakeListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new StocktakeDetailListVH(parent);
    }


    /**
     * 盘点列表VH
     */
    public static class StocktakeDetailListVH extends BaseViewHolder<StocktakeDetailVO>{

        private ItemStocktakeDetailBinding mBinding;

        public StocktakeDetailListVH(ViewGroup parent) {
            super(parent, R.layout.item_stocktake_detail);
            mBinding = DataBindingUtil.bind(itemView);
            int titleWidth = SizeUtil.dip2px(100);
            mBinding.stocktakeTime.attrName.setWidth(titleWidth);
            mBinding.stocktakeTime.attrName.setText("盘点时间");
            mBinding.operatorName.attrName.setWidth(titleWidth);
            mBinding.operatorName.attrName.setText("在库管理员");
            mBinding.stocktakeType.attrName.setWidth(titleWidth);
            mBinding.stocktakeType.attrName.setText("盘点类型");
        }

        @Override
        public void setData(StocktakeDetailVO data) {
            super.setData(data);
            if(mBinding==null){
                return;
            }
            mBinding.stocktakeTime.attrValue.setText(data.stockTakeTime);
            mBinding.operatorName.attrValue.setText(data.operatorName);
            mBinding.stocktakeType.attrValue.setText(data.stocktakeType);
            if(TextUtils.isEmpty(data.imgCar)  && TextUtils.isEmpty(data.imgNameplate)){
                mBinding.imgView.setVisibility(View.GONE);
            }else {
                mBinding.imgView.setVisibility(View.VISIBLE);
                ImageLoader.with(getContext(),data.imgCar,mBinding.imgCar);
                ImageLoader.with(getContext(),data.imgNameplate,mBinding.imgNameplate);
            }
        }
    }


}
