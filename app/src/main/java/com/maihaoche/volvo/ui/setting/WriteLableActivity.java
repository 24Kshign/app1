package com.maihaoche.volvo.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.commonbiz.service.utils.SoftKeyBoardUtil;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.dao.po.LablePO;
import com.maihaoche.volvo.databinding.ActivityWriteLableBinding;
import com.maihaoche.volvo.util.TextWatcherUtil;


public class WriteLableActivity extends HeaderProviderActivity<ActivityWriteLableBinding> {

    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_LABLE_NAME = "extra_lable_name";
    public static final int EXTRA_EDIT = 1;
    public static final int EXTRA_ADD = 2;
    public static final int RESULT_OK = 0x1;

    private int mType;
    private LablePO mLablePO;


    public static Intent createIntent(Context context, int type) {
        Intent intent = new Intent(context, WriteLableActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        return intent;
    }

    public static Intent createIntent(Context context, int type, LablePO lablePO) {
        Intent intent = new Intent(context, WriteLableActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_LABLE_NAME, lablePO);
        return intent;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_write_lable;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        mType = getIntent().getIntExtra(EXTRA_TYPE, EXTRA_ADD);
        initView();

    }

    private void initView() {
        SoftKeyBoardUtil.showKeyBoardDely(getContentBinding().remark);
        mLablePO = (LablePO) getIntent().getSerializableExtra(EXTRA_LABLE_NAME);
        if (mType == EXTRA_EDIT) {
            initHeader("编辑仓库位置", "删除", v -> {
                if (mLablePO != null) {
                    AppApplication.getDaoApi().deleteLable(mLablePO.getLableId())
                            .setOnResultGet(aBoolean -> {
                                AlertToast.show("删除仓库位置成功");
                                RxBus.getDefault().post(new RefreshEvent());
                                finish();
                            })
                            .setOnDataError(emsg -> AlertToast.show("删除仓库位置失败:" + emsg))
                            .call(WriteLableActivity.this);
                }

            });

            getContentBinding().remark.setText(mLablePO.getLableName());

        } else {
            initHeader("添加仓库位置");

        }

        getContentBinding().save.setOnClickListener(v -> save());
        getContentBinding().clear.setOnClickListener(v -> {
            getContentBinding().remark.setText("");
        });
        getContentBinding().remark.addTextChangedListener(new TextWatcherUtil(0, null, getContentBinding().clear));
    }

    private void save() {

        if (mType == EXTRA_EDIT) {
            String string = getContentBinding().remark.getText().toString();
            mLablePO.setLableName(string);
            AppApplication.getDaoApi().saveLable(mLablePO)
                    .setOnResultGet(aBoolean -> {
                        AlertToast.show("添加仓库位置成功");
                        RxBus.getDefault().post(new RefreshEvent());
                    })
                    .setOnDataError(emsg -> AlertToast.show("添加仓库位置失败"))
                    .call(this);
        } else {
            String string = getContentBinding().remark.getText().toString();
            if (StringUtil.isEmpty(string)) {
                AlertToast.show("请输入仓库位置");
                return;
            }

            Intent intent = new Intent();
            intent.putExtra(EXTRA_LABLE_NAME, string);
            setResult(RESULT_OK, intent);
        }

        this.finish();
    }
}
