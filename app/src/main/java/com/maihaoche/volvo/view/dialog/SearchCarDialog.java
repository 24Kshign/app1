package com.maihaoche.volvo.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.SearchCarDialogBinding;

/**
 * Created by gujian
 * Time is 2017/6/13
 * Email is gujian@maihaoche.com
 */

public class SearchCarDialog extends Dialog {

    private Context context;
    private boolean isFind;
    private SearchCarDialogBinding binding;
    private SearchInfo mSearchInfo;

    public SearchCarDialog(@NonNull Context context, SearchInfo searchInfo, boolean isFind) {
        this(context, 0);
        this.mSearchInfo = searchInfo;
        this.isFind = isFind;
    }

    public SearchCarDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public void setFind(boolean isFind) {
        if (isFind == false) {
            binding.textArea.setVisibility(View.VISIBLE);
            binding.frameCode.setText(mSearchInfo.caraUnique);
            binding.carAttribute.setText(mSearchInfo.carAttribut);
            binding.lable.setText(mSearchInfo.carPosition);
            binding.sure.setText("取消");
        } else {
            binding.textArea.setVisibility(View.GONE);
            binding.title.setText("已识别到该车辆！");
            binding.tip.setVisibility(View.GONE);
            binding.sure.setText("确定");
            binding.image.setImageResource(R.drawable.success_search_car);
        }
        binding.sure.setOnClickListener(v -> dismiss());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.search_car_dialog, null, false);
        setContentView(binding.getRoot());
        setFind(isFind);
    }

    /**
     * 获取正在搜索的tagId
     */
    public String getTagId() {
        return mSearchInfo != null ? mSearchInfo.carTagId : "";
    }

    private void configDialog() {

        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setWindowAnimations(R.style.anim_slide_share_from_bottom);
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            setCancelable(true);
        }
    }

    /**
     * 快速找车需要的信息
     */
    public static class SearchInfo {
        public String caraUnique;//车辆的车架号
        public String carAttribut;//车辆的属性
        public String carTagId;//车辆的条形码
        public String carPosition;//位置
    }
}
