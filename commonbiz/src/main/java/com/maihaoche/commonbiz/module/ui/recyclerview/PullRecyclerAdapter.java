package com.maihaoche.commonbiz.module.ui.recyclerview;

import android.content.Context;
import android.support.annotation.LayoutRes;

import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.maihaoche.commonbiz.R;
import com.maihaoche.commonbiz.module.dto.PagerRequest;

import java.util.Collection;

public abstract class PullRecyclerAdapter<T> extends RecyclerArrayAdapter<T> {

    private int showNoMoreViewPageSize = PagerRequest.DEFAULT_PAGE_SIZE;

    private int remainder;

    public PullRecyclerAdapter(Context context) {
        super(context);
    }

    /**
     * 设置显示没有更多的分页大小
     *
     * @param showNoMoreViewPageSize 分页大小
     */
    public void setShowNoMoreViewPageSize(int showNoMoreViewPageSize) {
        this.showNoMoreViewPageSize = showNoMoreViewPageSize;
        this.remainder = 0;
    }

    /**
     * 设置显示没有更多的分页大小和余数
     *
     * @param showNoMoreViewPageSize 分页大小
     * @param remainder              余数
     */
    public void setShowNoMoreViewPageSize(int showNoMoreViewPageSize, int remainder) {
        this.showNoMoreViewPageSize = showNoMoreViewPageSize;
        this.remainder = remainder;
    }

    /**
     * 添加上拉加载更多监听事件
     *
     * @param resId    上拉加载视图
     * @param listener 上拉加载
     */
    public void setMoreListener(@LayoutRes int resId, OnLoadMoreListener listener) {
        setMore(resId, new OnMoreListener() {
            @Override
            public void onMoreShow() {
                if (listener != null) {
                    listener.onLoadMore();
                }
            }

            @Override
            public void onMoreClick() {
                if (listener != null) {
                    listener.onLoadMore();
                }
            }
        });
    }

    /**
     * 添加上拉加载更多监听事件
     *
     * @param listener 监听
     */
    public void setMoreListener(OnLoadMoreListener listener) {
        setMoreListener(R.layout.view_load_more, listener);
    }

    public void setNoMoreView(@LayoutRes int resId) {
        setNoMore(resId, new OnNoMoreListener() {
            @Override
            public void onNoMoreShow() {
                stopMore();
            }

            @Override
            public void onNoMoreClick() {
                stopMore();
            }
        });
    }

    /**
     * 设置默认的加载无更多view
     */
    public void setDefaultNoMoreView() {
        setNoMore(R.layout.view_no_more, new OnNoMoreListener() {
            @Override
            public void onNoMoreShow() {
                stopMore();
            }

            @Override
            public void onNoMoreClick() {
                stopMore();
            }
        });
    }

    /**
     * @param items           数据项
     * @param isDisplayNoMore 根据这个判断是否显示没有更多的footView
     */
    public void addAll(T[] items, boolean isDisplayNoMore) {
        super.addAll(items);
        if (items == null || items.length <= 0) return;
        if (isDisplayNoMore && (getCount() - remainder)
                % showNoMoreViewPageSize != 0) {
            this.stopMore();
        }

    }

    /**
     * @param collection      数据项
     * @param isDisplayNoMore 根据这个判断是否显示没有更多的footView
     */
    public void addAll(Collection<? extends T> collection, boolean isDisplayNoMore) {
        super.addAll(collection);
        if (collection == null || collection.size() <= 0) return;
        if (isDisplayNoMore && (getCount() - remainder)
                % showNoMoreViewPageSize != 0) {
            this.stopMore();
        }
    }

    // 上拉加载更多接口
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

}
