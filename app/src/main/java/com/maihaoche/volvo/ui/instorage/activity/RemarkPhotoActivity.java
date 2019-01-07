package com.maihaoche.volvo.ui.instorage.activity;

import android.os.Bundle;

import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.SoftKeyBoardUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityRemarkPhotoBinding;
import com.maihaoche.volvo.ui.photo.ChooseImageItem;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.util.TextWatcherUtil;


public class RemarkPhotoActivity extends HeaderProviderActivity<ActivityRemarkPhotoBinding> {

    public static final String EXTRA = "extra";
    private ChooseImageItem item;

    @Override
    public int getContentResId() {
        return R.layout.activity_remark_photo;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initHeader("输入备注");
        SoftKeyBoardUtil.showKeyBoardDely(getContentBinding().remark);
        getContentBinding().save.setOnClickListener(v->save());
        item = (ChooseImageItem) getIntent().getSerializableExtra(EXTRA);
        getContentBinding().remark.setText(item.remark);
        getContentBinding().remark.addTextChangedListener(new TextWatcherUtil(50, new TextWatcherUtil.TextCount() {
            @Override
            public void getTextCount(int count) {
                getContentBinding().textNumber.setText(count+"/50");
            }
        }));
    }

    private void save() {
        if (item != null) {
            item.remark = getContentBinding().remark.getText().toString();
            RxBus.getDefault().post(item);
        }
        this.finish();
    }
}
