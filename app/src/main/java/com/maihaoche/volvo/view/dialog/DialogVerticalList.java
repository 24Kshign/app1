package com.maihaoche.volvo.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.maihaoche.commonbiz.module.ui.recyclerview.SimpleAdapter;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.DialogVerticallistBinding;

/**
 * 类简介：
 * 作者：  yang
 * 时间：  17/6/16
 * 邮箱：  yangyang@maihaoche.com
 */

public class DialogVerticalList extends Dialog {


    private String mTitle = "";
    private DialogVerticallistBinding mBinding;
    private SimpleAdapter mListAdapter;

    private int mShowLocationX = -1;
    private int mShowLocationY = -1;


    public DialogVerticalList(@NonNull Context context) {
        super(context, R.style.NormalDialogStyle);
    }

    public DialogVerticalList setListAdapter(SimpleAdapter listAdapter) {
        mListAdapter = listAdapter;
        showData();
        return this;
    }

    public DialogVerticalList setTitle(String title) {
        mTitle = title;
        showData();
        return this;
    }

    public DialogVerticalList setShowAtLocation(int x, int y) {
        mShowLocationX = x;
        mShowLocationY = y;
        return this;
    }

    @Override
    public void show() {
        if(mShowLocationX>=0 || mShowLocationY>=0){
            Window dialogWindow = getWindow();
            if (dialogWindow != null) {
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.x = mShowLocationX;
                lp.y = mShowLocationY;
                lp.gravity = Gravity.TOP | Gravity.LEFT;
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            }
        }
        super.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_verticallist, null, false);
        setContentView(mBinding.getRoot());
        init();
        showData();
    }

    private void init() {

        mBinding.dialogRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private SimpleAdapter.OnItemClickListener dismiss = (view, o) -> DialogVerticalList.this.dismiss();

    private void showData() {
        if (mBinding == null) {
            return;
        }
        if (TextUtils.isEmpty(mTitle)) {
            mBinding.dialogTitle.setVisibility(View.GONE);
        } else {
            mBinding.dialogTitle.setText(mTitle);
            mBinding.dialogTitle.setVisibility(View.VISIBLE);

        }
        if (mListAdapter != null) {
            mBinding.dialogRecyclerList.setAdapter(mListAdapter);
            SimpleAdapter.OnItemClickListener onItemClickListener = mListAdapter.getOnItemClickListener();
            mListAdapter.clearOnItemClick();
            mListAdapter.setOnItemClickListener((view, o) -> {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(view, o);
                }
                dismiss.onItemClick(view, o);
            });
        }
    }

}
