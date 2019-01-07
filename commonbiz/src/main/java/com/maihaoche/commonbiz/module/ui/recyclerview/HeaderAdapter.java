package com.maihaoche.commonbiz.module.ui.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * 可以添加header的adapter。
 * 缺陷：1.viewhoder的泛型化被取消了。使用的是基础类型RecyclerView.ViewHolder.
 * 2.不支持返回不同类型的item
 * 作者：yang
 * 时间：17/6/27
 * 邮箱：yangyang@maihaoche.com
 */
public abstract class HeaderAdapter<DATA> extends SimpleAdapter<DATA, RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private View mHeaderView;

    public HeaderAdapter(Context context) {
        super(context);
    }

    public void setHeader(View view) {
        setHeader(view, false);
    }

    public void setHeader(View view, boolean notify) {
        mHeaderView = view;
        if (notify) {
            notifyDataSetChanged();
        }
    }


    public abstract RecyclerView.ViewHolder onCreateDataVH(ViewGroup parent, int viewType);


    protected abstract void bindDataVH(DATA data, RecyclerView.ViewHolder holder);


    @Override
    final protected void bindViewHolder(DATA data, RecyclerView.ViewHolder holder) {
        if (holder instanceof HeaderViewHoder) {
            return;
        } else {
            bindDataVH(data, holder);
        }
    }

    @Override
    final public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHoder(mHeaderView);
        }
        return onCreateDataVH(parent, viewType);
    }

    @Override
    protected DATA getData(int position) {
        if (mHeaderView != null && position == 0) {
            return null;
        }
        position = (mHeaderView != null ? position - 1 : position);
        return super.getData(position);
    }

    @Override
    final public int getItemCount() {
        int itemCount = super.getItemCount() + (mHeaderView != null ? 1 : 0);
        return itemCount;
    }

    @Override
    final public int getItemViewType(int position) {
        if (mHeaderView != null && position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private static class HeaderViewHoder extends RecyclerView.ViewHolder {
        public HeaderViewHoder(View itemView) {
            super(itemView);
        }
    }
}
