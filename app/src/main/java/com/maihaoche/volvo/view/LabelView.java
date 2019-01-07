package com.maihaoche.volvo.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.LabelViewBinding;


/**
 * 类简介：有标题的容器view，可以点击标题 显示和隐藏容器中的内容。容器中的可以添加子view
 * 作者：  yang
 * 时间：  2017/8/16
 * 邮箱：  yangyang@maihaoche.com
 */

public class LabelView  extends LinearLayout {

    private ViewDataBinding mContentBinding;

    private View.OnClickListener mOnTabClick;

    public LabelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LabelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    public void setmOnTabClick(OnClickListener mOnTabClick) {
        this.mOnTabClick = mOnTabClick;
    }

    private void init(Context context, AttributeSet attrs) {
        View labelView = LayoutInflater.from(context).inflate(R.layout.label_view,this,false);
        LabelViewBinding labelViewBinding = DataBindingUtil.bind(labelView);
        //显示
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.LabelView);
        String titleName = attributes.getString(R.styleable.LabelView_titleName);
        boolean showToggle = attributes.getBoolean(R.styleable.LabelView_showToggle,false);
        boolean showContent = attributes.getBoolean(R.styleable.LabelView_showContentOnInit,true);
        int contentLayoutId = attributes.getResourceId(R.styleable.LabelView_contentLayout,0);
        labelViewBinding.labelText.setText(titleName);

        labelViewBinding.labelArrow.setVisibility(showToggle?VISIBLE:GONE);
        if(showToggle){
            labelViewBinding.contentContainer.setVisibility(showContent?VISIBLE:GONE);
        }
        labelViewBinding.labelArrow.setImageResource(showContent?R.drawable.ic_arrow_up:R.drawable.ic_arrow_down);

        if(contentLayoutId!=0){
            View contentView = LayoutInflater.from(context).inflate(contentLayoutId,labelViewBinding.contentContainer,false);
            mContentBinding = DataBindingUtil.bind(contentView);
            labelViewBinding.contentContainer.addView(contentView);
        }
        //点击事件
        labelViewBinding.labelView.setOnClickListener(v -> {
            if(labelViewBinding.contentContainer.getVisibility()==VISIBLE){
                labelViewBinding.contentContainer.setVisibility(GONE);
                labelViewBinding.labelArrow.setImageResource(R.drawable.ic_arrow_down);
            }else {
                labelViewBinding.contentContainer.setVisibility(VISIBLE);
                labelViewBinding.labelArrow.setImageResource(R.drawable.ic_arrow_up);
            }
            if(mOnTabClick!=null){
                mOnTabClick.onClick(v);
            }
        });
        //无视属性，垂直分布
        setOrientation(LinearLayout.VERTICAL);
        addView(labelView);
    }

    /**
     * 获取内容的binding
     */
    public <T extends ViewDataBinding>  T  getContent() {
        if(mContentBinding!=null){
            return (T) mContentBinding;
        }
        return null;
    }

}
