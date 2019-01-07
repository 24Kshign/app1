package com.maihaoche.volvo.ui.instorage.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.LayoutRes;
import android.support.v7.view.ActionBarPolicy;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.decoration.StickyHeaderDecoration;
import com.maihaoche.commonbiz.module.ui.BaseRecyclerAdapter;
import com.maihaoche.commonbiz.service.image.ImageLoader;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ContactItemBinding;
import com.maihaoche.volvo.databinding.ItemSourceChooseBinding;
import com.maihaoche.volvo.ui.instorage.daomain.BrandNewInfo;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/2
 * Email is gujian@maihaoche.com
 */

public class BrandAdapter<T> extends BaseRecyclerAdapter<T, BrandAdapter.MyViewHolder>
        implements StickyHeaderDecoration.IStickyHeaderAdapter<BrandAdapter.HeaderViewHolder> {

    public BrandAdapter(Context context) {
        super(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(parent, R.layout.item_source_choose);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (getItem(position) instanceof BrandNewInfo) {
            BrandNewInfo info = (BrandNewInfo) getItem(position);
            holder.mTitleTxt.setText(info.brandName);
            holder.mBrandImage.setVisibility(View.VISIBLE);
            ImageLoader.withDisk(getContext(), info.brandPic, R.color.bg_grey, holder.mBrandImage);
            //处于末尾,分隔线和分隔块都不展示
            if (position == getSize() - 1) {
                holder.mSepView.setVisibility(View.GONE);
                holder.mMarginSepView.setVisibility(View.GONE);
            }
            //不是整体末尾,但是是品牌模块末尾,分隔线不展示,分隔块展示
            else if (position < getSize() - 1 && StringUtil.isNotEmpty(info.brandLetter) &&
                    !info.brandLetter.equals(((BrandNewInfo) getItem(position + 1)).brandLetter)) {
                holder.mSepView.setVisibility(View.GONE);
                holder.mMarginSepView.setVisibility(View.VISIBLE);
            }
            //普通的只要展示分隔线
            else {
                holder.mSepView.setVisibility(View.VISIBLE);
                holder.mMarginSepView.setVisibility(View.GONE);
            }
        }
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(v ->
                mOnItemClickListener.onClick((Integer) v.getTag()));
    }

    @Override
    public long getHeaderId(int position) {
        if (getItem(position) instanceof BrandNewInfo) {
            BrandNewInfo info = (BrandNewInfo) getItem(position);
            return info.brandLetter.charAt(0);
        }
        return 0;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new HeaderViewHolder(parent, R.layout.item_letter_header_source_margin);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {
        if (getItem(position) instanceof BrandNewInfo) {
            BrandNewInfo info = (BrandNewInfo) getItem(position);
            holder.mLetterTxt.setText(info.brandLetter);
        }
    }

    static class HeaderViewHolder extends BaseViewHolder {

        TextView mLetterTxt;

        private void findView(View itemView) {
            mLetterTxt = (TextView) itemView.findViewById(R.id.letter);
        }


        public HeaderViewHolder(ViewGroup parent, @LayoutRes int res) {
            super(parent, res);
            findView(itemView);
        }
    }

    static class MyViewHolder extends BaseViewHolder {


        TextView mTitleTxt;
        ImageView mBrandImage;
        View mSepView;
        View mMarginSepView;

        private void findView(View itemView) {
            mTitleTxt = (TextView) itemView.findViewById(R.id.tv_title);
            mBrandImage = (ImageView) itemView.findViewById(R.id.iv_brand);
            mSepView = itemView.findViewById(R.id.sep);
            mMarginSepView = itemView.findViewById(R.id.margin_sep);
        }


        public MyViewHolder(ViewGroup parent, @LayoutRes int res) {
            super(parent, res);
            findView(itemView);
        }
    }
}
