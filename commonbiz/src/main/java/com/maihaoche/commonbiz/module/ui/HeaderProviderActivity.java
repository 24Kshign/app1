package com.maihaoche.commonbiz.module.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.maihaoche.commonbiz.R;
import com.maihaoche.commonbiz.databinding.ActivityHeaderProviderBinding;

/**
 * 提供全局的统一header
 * 作者：yang
 * 时间：17/6/8
 * 邮箱：yangyang@maihaoche.com
 */
public abstract class HeaderProviderActivity<T extends ViewDataBinding> extends BaseActivity<ViewDataBinding> {
    private ViewDataBinding contentBinding;


    @Override
    final protected int getLayoutResId() {
        return R.layout.activity_header_provider;
    }

    @Override
    final protected void afterCreate(@Nullable Bundle savedInstanceState) {
        super.afterCreate(savedInstanceState);
        contentBinding = DataBindingUtil.inflate(getLayoutInflater(), getContentResId(), null, false);
        getLayoutBinding().content.addView(contentBinding.getRoot());
        getLayoutBinding().leftBack.setOnClickListener(v -> onBackPressed());
        afterViewCreated(savedInstanceState);
    }


    public abstract int getContentResId();


    protected void afterViewCreated(Bundle savedInstanceState) {

    }

    /**
     * 返回页面内容
     *
     * @return
     */
    protected T getContentBinding() {
        return (T) contentBinding;
    }

    /**
     * 该方法不允许被重写。
     */
    @Override
    final protected ActivityHeaderProviderBinding getLayoutBinding() {
        return (ActivityHeaderProviderBinding) super.getLayoutBinding();
    }

    /**
     * =================================操作header的api============================
     */

    protected void hiddenHeader() {
        getLayoutBinding().title.setVisibility(View.GONE);
    }

    public void initHeader(String title) {
        initTitleWithClick(title, 0, null);
    }

    public void initTitleWithClick(String title, int titleIconResId, View.OnClickListener onTitleClick) {
        getLayoutBinding().titleText.setText(title);
        if (onTitleClick != null) {
            getLayoutBinding().title.setOnClickListener(onTitleClick);
        }
        if (titleIconResId > 0) {
            ImageView d = new ImageView(this);
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

    protected void showContent(){
        getLayoutBinding().progress.getRoot().setVisibility(View.GONE);
        getLayoutBinding().content.setVisibility(View.VISIBLE);
        getLayoutBinding().empty.getRoot().setVisibility(View.GONE);
    }

    protected void showProgress(){
        getLayoutBinding().progress.getRoot().setVisibility(View.VISIBLE);
        getLayoutBinding().content.setVisibility(View.GONE);
        getLayoutBinding().empty.getRoot().setVisibility(View.GONE);
    }

    protected void showEmpty(){
        getLayoutBinding().progress.getRoot().setVisibility(View.GONE);
        getLayoutBinding().content.setVisibility(View.GONE);
        getLayoutBinding().empty.getRoot().setVisibility(View.VISIBLE);
    }

    protected void showProgressWithContent(){
        getLayoutBinding().progress.getRoot().setVisibility(View.VISIBLE);
        getLayoutBinding().content.setVisibility(View.VISIBLE);
    }

    protected void setEmptyText(String string){
        getLayoutBinding().empty.tipText.setText(string);
    }
}
