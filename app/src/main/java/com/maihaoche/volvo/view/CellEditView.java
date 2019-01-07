package com.maihaoche.volvo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.CellItemViewBinding;
import com.maihaoche.commonbiz.service.utils.SizeUtil;

/**
 * Created by gujian
 * Time is 2017/6/8
 * Email is gujian@maihaoche.com
 */

public class CellEditView extends RelativeLayout {
    private CellItemViewBinding binding;

    public CellEditView(Context context) {
        this(context, null);
    }

    public CellEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CellEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.cell_item_view, this, true);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CellEditView);
        String keyName = ta.getString(R.styleable.CellEditView_keyName);
        String content = ta.getString(R.styleable.CellEditView_content);
        String contenthidden = ta.getString(R.styleable.CellEditView_contenthidden);
        int keyTextColor = ta.getColor(R.styleable.CellEditView_keyTextColor, Color.parseColor("#afafaf"));
        int contentTextColor = ta.getColor(R.styleable.CellEditView_contentColor, Color.parseColor("#373737"));
        boolean contentEnable = ta.getBoolean(R.styleable.CellEditView_contentEnable, false);
        boolean showClearIcon = ta.getBoolean(R.styleable.CellEditView_showClearIcon, false);
        int rightIcon = ta.getResourceId(R.styleable.CellEditView_rightIcon, 0);
        int margin = ta.getDimensionPixelSize(R.styleable.CellEditView_keyContentMargin, SizeUtil.dip2px(20));
        int cellHeight = ta.getDimensionPixelSize(R.styleable.CellEditView_keyWidth, SizeUtil.dip2px(60));

        binding.key.setText(keyName);
        binding.content.setText(content);
        binding.key.setTextColor(keyTextColor);
        binding.content.setTextColor(contentTextColor);
        binding.content.setEnabled(contentEnable);
        RelativeLayout.LayoutParams param = (LayoutParams) binding.key.getLayoutParams();
        param.width = cellHeight;
        binding.getRoot().setLayoutParams(param);
        if (rightIcon != 0) {
            binding.rightIcon.setImageResource(rightIcon);
        }
        if (showClearIcon) {
            binding.clear.setVisibility(VISIBLE);
            binding.clear.setOnClickListener(v -> clearClick());
        } else {
            binding.clear.setVisibility(GONE);
        }

        RelativeLayout.LayoutParams params = (LayoutParams) binding.content.getLayoutParams();
        params.setMargins(margin, 0, 0, 0);
        binding.content.setLayoutParams(params);

    }

    private void clearClick() {
        binding.content.setText("");
    }

    public void showClearIcon() {
        binding.clear.setVisibility(VISIBLE);
    }

    public void hiddenClearIcon() {
        binding.clear.setVisibility(GONE);
    }

    public String getContent() {
        return binding.content.getText().toString();
    }
}
