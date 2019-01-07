package com.maihaoche.volvo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.CellTextViewBinding;

/**
 * Created by gujian
 * Time is 2017/8/2
 * Email is gujian@maihaoche.com
 */

public class CellTextView extends LinearLayout {

    private CellTextViewBinding binding;

    public CellTextView(Context context) {
        this(context,null);
    }

    public CellTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CellTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }


    private void init(Context context,AttributeSet attrs){
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.cell_text_view,null,false);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CellTextView);
        String leftName = ta.getString(R.styleable.CellTextView_leftName);
        String rightName = ta.getString(R.styleable.CellTextView_rightName);
        int leftColor = ta.getColor(R.styleable.CellTextView_leftColor, Color.parseColor("#afafaf"));
        int rightColor = ta.getColor(R.styleable.CellTextView_rightColor, Color.parseColor("#373737"));
        int leftRightMargin = ta.getDimensionPixelSize(R.styleable.CellTextView_leftRightMargin, SizeUtil.dip2px(10));
        int cellHeight = ta.getDimensionPixelSize(R.styleable.CellTextView_celHeight, SizeUtil.dip2px(60));
        int leftWidth = ta.getDimensionPixelSize(R.styleable.CellTextView_leftWidth, SizeUtil.dip2px(80));
        int leftSize = ta.getDimensionPixelSize(R.styleable.CellTextView_leftSize, SizeUtil.dip2px(17));
        int rightSize = ta.getDimensionPixelSize(R.styleable.CellTextView_rightSize, SizeUtil.dip2px(17));

        binding.left.setText(leftName);
        binding.left.setTextColor(leftColor);
        binding.left.setTextSize(leftSize);

        binding.right.setText(rightName);
        binding.right.setTextColor(rightColor);
        binding.right.setTextSize(rightSize);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.cell.getLayoutParams();
        params.height = cellHeight;
        binding.cell.setLayoutParams(params);

        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) binding.left.getLayoutParams();
        params1.setMargins(0,0,leftRightMargin,0);
        params1.width = leftWidth;
        binding.left.setLayoutParams(params1);
    }

}
