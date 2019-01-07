package com.maihaoche.commonbiz.module.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.maihaoche.commonbiz.R;
import com.maihaoche.commonbiz.databinding.FragmentHeaderProviderBinding;

/**
 * Created by gujian
 * Time is 2017/7/27
 * Email is gujian@maihaoche.com
 */

public abstract class HeaderProviderFragment<T extends ViewDataBinding> extends BaseFragment<FragmentHeaderProviderBinding> {

    private ViewDataBinding contentBinding;

    final protected int getLayoutResId(){
        return R.layout.fragment_header_provider;
    }

    @Override
    protected void onAfterCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        contentBinding = DataBindingUtil.inflate(inflater,getContentLayout(),null,false);
        getLayoutBinding().content.addView(contentBinding.getRoot());
    }



    protected abstract int getContentLayout();

    protected T getContentBinding(){
        return (T)contentBinding;
    }


    /**
     * =================================操作header的api============================
     */

    protected void hiddenHeader() {
        getLayoutBinding().title.setVisibility(View.GONE);
    }

    protected void initHeader(String title) {
        initTitleWithClick(title, 0, null);
    }

    protected void initTitleWithClick(String title, int titleIconResId, View.OnClickListener onTitleClick) {
        getLayoutBinding().titleText.setText(title);
        if (onTitleClick != null) {
            getLayoutBinding().title.setOnClickListener(onTitleClick);
        }
        if (titleIconResId > 0) {
            ImageView d = new ImageView(getActivity());
            getLayoutBinding().titleIcon.setVisibility(View.VISIBLE);
            getLayoutBinding().titleIcon.setImageResource(titleIconResId);
        }
    }

    protected void initHeader(String title, String rightText, View.OnClickListener listener) {
        getLayoutBinding().titleText.setText(title);
        getLayoutBinding().rightText.setText(rightText);
        getLayoutBinding().rightText.setOnClickListener(listener);
    }

    protected void hiddenLeftArrow() {
        getLayoutBinding().leftBack.setVisibility(View.GONE);
    }

    protected void hiddenRightText() {
        getLayoutBinding().rightText.setVisibility(View.GONE);
    }

    protected void setRightText(String rightText) {
        getLayoutBinding().rightText.setText(rightText);
    }

    protected void setRightText(String rightText, View.OnClickListener listener) {
        getLayoutBinding().rightText.setText(rightText);
        getLayoutBinding().rightText.setOnClickListener(listener);
    }
}
