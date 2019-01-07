package com.maihaoche.volvo.ui.inwarehouse.cars;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.module.ui.recyclerview.DataBindingViewHolder;
import com.maihaoche.commonbiz.module.ui.recyclerview.SimpleAdapter;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.dao.Enums;
import com.maihaoche.volvo.databinding.ActivityMailSubmitBinding;
import com.maihaoche.volvo.databinding.ItemKeyNumListBinding;
import com.maihaoche.volvo.server.dto.CarBaseInfoOnMailVO;
import com.maihaoche.volvo.server.dto.request.CarIdRequest;
import com.maihaoche.volvo.server.dto.request.SendProcedureRequest;
import com.maihaoche.volvo.ui.inwarehouse.InWarehouseBiz;
import com.maihaoche.volvo.view.dialog.DialogVerticalList;

import java.util.ArrayList;

/**
 * 类简介：邮寄手续 页面
 * 作者：  yang
 * 时间：  2017/8/15
 * 邮箱：  yangyang@maihaoche.com
 */

public class AcitivtyMailSubmit extends HeaderProviderActivity<ActivityMailSubmitBinding> {

    //剩余钥匙数量
    public static final String LEFT_KEY_NUM = "left_key_num";


    private static final String CAR_ID_REQUEST = "car_id_request";
    private static final String CAR_BASE_INFO = "car_base_info";

    private CarIdRequest mCarIdRequest;
    private int mKeyNum = 0;
    private int mChoosenKeyNum = 0;

    private CarBaseInfoOnMailVO mCarBaseInfoOnMailVO = null;

    public static Intent createIntent(Context context, CarIdRequest carIdRequest, CarBaseInfoOnMailVO onMailVO) {
        Intent intent = new Intent(context, AcitivtyMailSubmit.class);
        intent.putExtra(CAR_ID_REQUEST, carIdRequest);
        intent.putExtra(CAR_BASE_INFO, onMailVO);
        return intent;
    }


    @Override
    public int getContentResId() {
        return R.layout.activity_mail_submit;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        init();
        load();
    }

    private void init() {
        initHeader("邮寄手续");
        getContentBinding().submitBtn.setEnabled(false);
        getContentBinding().submitBtn.setOnClickListener(v -> submit());
        getContentBinding().keyChooseView.setOnClickListener(mOnKeyChooseClick);
        mCarIdRequest = (CarIdRequest) getIntent().getSerializableExtra(CAR_ID_REQUEST);
        mCarBaseInfoOnMailVO = (CarBaseInfoOnMailVO) getIntent().getSerializableExtra(CAR_BASE_INFO);
    }

    private void load() {
        if (mCarBaseInfoOnMailVO != null) {
            showView(mCarBaseInfoOnMailVO);
        }
    }


    private void showView(CarBaseInfoOnMailVO vo) {
        if (vo == null) {
            return;
        }
        ActivityMailSubmitBinding binding = getContentBinding();

        //车辆信息
        InWarehouseBiz.fillBaseCarInfo(binding.carInfo, vo);

        CarBaseInfoOnMailVO.CheckInfo4Mail checkInfo4Mail = vo.checkInfo4Mail;
        if (checkInfo4Mail == null) {
            AlertToast.show("没有可以邮寄的手续或钥匙");
            getHandler().postDelayed(() -> finish(),2000);
            return;
        }
        binding.checkCertificate.titleText.setText("关单/合格证");
        binding.checkCertificate.itemContainer.setVisibility(
                checkInfo4Mail.certificateStatus == Enums.YesOrNoEnum.YES.value() ?
                        View.VISIBLE
                        : View.GONE);

        binding.checkConstant.titleText.setText("一致性证书");
        binding.checkConstant.itemContainer.setVisibility(
                checkInfo4Mail.certificateConsistentStatus == Enums.YesOrNoEnum.YES.value() ?
                        View.VISIBLE
                        : View.GONE);

        binding.checkChecklist.titleText.setText("商检单");
        binding.checkChecklist.itemContainer.setVisibility(
                checkInfo4Mail.checklistStatus == Enums.YesOrNoEnum.YES.value() ?
                        View.VISIBLE
                        : View.GONE);

        binding.checkDocument.titleText.setText("说明书");
        binding.checkDocument.itemContainer.setVisibility(
                checkInfo4Mail.manualStatus == Enums.YesOrNoEnum.YES.value() ?
                        View.VISIBLE
                        : View.GONE);

        mKeyNum = checkInfo4Mail.keyNumber;

        getContentBinding().submitBtn.setEnabled(true);

    }

    /**
     * 选择钥匙的点击事件
     */
    private View.OnClickListener mOnKeyChooseClick = v -> {
        if (mKeyNum <= 0) {
            return;
        }
        KeyNumChooseAdapter adapter = new KeyNumChooseAdapter(AcitivtyMailSubmit.this);
        ArrayList<Integer> keyNumList = new ArrayList<>();
        for (int i = 0; i <= mKeyNum; i++) {
            keyNumList.add(i);
        }
        adapter.addAll(keyNumList);
        adapter.setOnItemClickListener((view, integer) -> {
            mChoosenKeyNum = integer;
            getContentBinding().choosenKeyNum.setText(mChoosenKeyNum + "把");
        });
        new DialogVerticalList(AcitivtyMailSubmit.this)
                .setListAdapter(adapter)
                .show();
    };

    /**
     * 提交
     */
    private void submit() {
        ActivityMailSubmitBinding binding = getContentBinding();
        if (mChoosenKeyNum == 0
                && !binding.checkCertificate.checkBox.isChecked()
                && !binding.checkConstant.checkBox.isChecked()
                && !binding.checkChecklist.checkBox.isChecked()
                && !binding.checkDocument.checkBox.isChecked()
                ) {
            AlertToast.show("请至少选择一个邮寄手续");
            return;
        }
        if (TextUtils.isEmpty(binding.expressNoInput.getText().toString())) {
            AlertToast.show("请输入快递单号");
            return;
        }
        SendProcedureRequest request = new SendProcedureRequest();
        request.carId = mCarIdRequest.carId;
        request.carStoreType = mCarIdRequest.carStoreType;
        request.certificateStatus = binding.checkCertificate.checkBox.isChecked() ? 1 : 0;
        request.certificateConsistentStatus = binding.checkConstant.checkBox.isChecked() ? 1 : 0;
        request.checklistStatus = binding.checkChecklist.checkBox.isChecked() ? 1 : 0;
        request.manualStatus = binding.checkDocument.checkBox.isChecked() ? 1 : 0;
        request.keyQuantity = mChoosenKeyNum;
        request.expressNo = binding.expressNoInput.getText().toString();
        AppApplication.getServerAPI().uploadMailInfo(request)
                .setTransformer(getIOLoadingTransformer())
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setOnDataGet(baseResponse -> {
                    AlertToast.show("上传成功");
                    if(mChoosenKeyNum>0){
                        RxBus.getDefault().post(new KeyNumChangeEvent(mKeyNum-mChoosenKeyNum));
                    }

                    finish();
                })
                .call(this);
    }

    /**
     * 钥匙选择的adapter
     */
    private class KeyNumChooseAdapter extends SimpleAdapter<Integer, DataBindingViewHolder> {

        public KeyNumChooseAdapter(Context context) {
            super(context);
        }

        @Override
        protected void bindViewHolder(@Nullable Integer integer, DataBindingViewHolder holder) {
            if (integer == null) {
                return;
            }
            ItemKeyNumListBinding binding = holder.getBinding();
            binding.keyNum.setText(integer + "把");
        }

        @Override
        public DataBindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DataBindingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_key_num_list, parent, false));
        }
    }

}
