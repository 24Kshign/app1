package com.maihaoche.commonbiz.module.ui.recyclerview;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.maihaoche.commonbiz.R;
import com.maihaoche.commonbiz.module.dto.PagerRequest;
import com.maihaoche.commonbiz.service.utils.SizeUtil;

/**
 * Created by sanji on 16/4/29.
 * 对EasyRecycler进行简单包装,兼容showError方法
 * change by xianguo  on 17/02/08
 * 优化EasyRecycler 剥离业务直接使用第三方控件
 */
public class PullRecycleView extends EasyRecyclerView {

    public PullRecycleView(Context context) {
        this(context, null);
    }

    public PullRecycleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRecycleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 根据DefaultPageSize 判断是否显示更多(默认)
     */
    public void showNoMoreViewIfDefaultPageSize() {
        showNoMoreViewByPageSize(PagerRequest.DEFAULT_PAGE_SIZE);
    }

    /**
     * 根据pageSize 判断是否显示更多
     */
    public void showNoMoreViewByPageSize(int pageSize) {
        showNoMoreViewByPageSize(pageSize, 0);
    }

    /**
     * 根据pageSize 和 remainder 判断是否显示更多
     *
     * @param pageSize  分页大小
     * @param remainder 余数
     */
    public void showNoMoreViewByPageSize(int pageSize, int remainder) {
        if (pageSize <= 0) pageSize = PagerRequest.DEFAULT_PAGE_SIZE;
        if (!(getAdapter() instanceof RecyclerArrayAdapter)) return;
        RecyclerArrayAdapter adapter = (RecyclerArrayAdapter) getAdapter();
        if (adapter == null || (adapter.getCount() - remainder) == 0) {
            this.showEmpty();
        } else if ((adapter.getCount() - remainder) % pageSize != 0) {
            adapter.stopMore();
        }
    }

    /**
     * 下拉刷新错误时底部加载一条错误提示(防止已有的数据被干掉)
     * 没有刷新功能或者不是分页加载时可以使用此方法
     * 如果刷新之前或者加载错误会清除adapter可以使用此方法,否则会导致加载更多错误时再取刷新列表会时不会展示错误页面,
     * 可以使用showError(boolean isReload)代替,传入PagerRequest.mStart==0即可
     */
    @Override
    public void showError() {
        showRecyclerErrorView(R.string.common_net_error_click);
    }

    private void showRecyclerErrorView(@StringRes int resId) {
        RecyclerArrayAdapter adapter;
        if ((getAdapter() instanceof RecyclerArrayAdapter)
                && (adapter = (RecyclerArrayAdapter) getAdapter()).getCount() > 0) {
            FrameLayout container = new FrameLayout(getContext());
            container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            LayoutInflater.from(getContext()).inflate(R.layout.view_recycle_error, container);
            ((TextView) container.findViewById(R.id.tv_msg)).setText(resId);
            adapter.setError(container, new RecyclerArrayAdapter.OnErrorListener() {
                @Override
                public void onErrorShow() {

                }

                @Override
                public void onErrorClick() {
                    adapter.resumeMore();
                }
            });
            adapter.pauseMore();
        } else {
            super.showError();
        }
    }

//    /**
//     * @param isReload 传入PagerRequest.mStart==0
//     */
//    public void showNetError(boolean isReload) {
//        if (isReload) {
//            if (getContext() instanceof BaseActivity) {
//                ((AbsActivity) getContext()).showNetErrorView();
//            } else {
//                super.showError();
//            }
//        } else {
//            showError();
//        }
//    }
//
//    public void showServerError(boolean isReload, String desc) {
//        if (isReload) {
//            if (getContext() instanceof AbsActivity) {
//                ((AbsActivity) getContext()).showServerErrorView(desc);
//            } else {
//                super.showError();
//            }
//        } else {
//            showRecyclerErrorView(R.string.common_server_error);
//        }
//    }

    /**
     * 只显示RecyclerView 设置的错误页面
     *
     * @param isReload
     * @param desc
     */
    public void showRecyclerServerError(boolean isReload, String desc) {
        if (isReload) {
            super.showError();
        } else {
            showRecyclerErrorView(R.string.common_server_error);
        }
    }

    /**
     * 配合 RecyclerView 的setErrorView(resId) 使用
     * 显示RecyclerView 的错误页面
     *
     * @param isReload 是否重新加载
     */
    public void showRecyclerViewNetError(boolean isReload) {
        if (isReload) {
            super.showError();
        } else {
            showError();
        }
    }

    /**
     * 配置默认的views
     */
//    public void setDefaultViews() {
//        setEmptyView(R.layout.view_empty_data);
//        setProgressView(R.layout.view_progress);
//        setErrorView(R.layout.view_error);
//    }

    /**
     * 设置向上内间距
     *
     * @param dipValue 密度
     */
    public void setPaddingTop(float dipValue) {
        this.setRecyclerPadding(0, SizeUtil.dip2px(dipValue), 0, 0);
    }

    /**
     * 设置向上内间距
     *
     * @param dipValue 密度
     */
    public void setPaddingBottom(float dipValue) {
        this.setRecyclerPadding(0, 0, 0, SizeUtil.dip2px(dipValue));
    }

    /**
     * 设置内间距 单位：密度
     *
     * @param left   左边
     * @param top    上边
     * @param right  右边
     * @param bottom 下边
     */
    public void setPadding(float left, float top, float right, float bottom) {
        this.setRecyclerPadding(SizeUtil.dip2px(left), SizeUtil.dip2px(top), SizeUtil.dip2px(right), SizeUtil.dip2px(bottom));
    }

    /**
     * 下拉刷新
     *
     * @param listener 监听
     */
    public void setPullRefreshListener(OnPullRefreshListener listener) {
        if (listener == null) return;
        super.setRefreshListener(listener::onPullRefresh);
    }

    public interface OnPullRefreshListener {
        void onPullRefresh();
    }
}
