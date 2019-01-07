package com.maihaoche.volvo.ui.car.cardetail;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ItemInwarehouseExceptionBinding;
import com.maihaoche.volvo.server.dto.CarMassLossBriefVO;
import com.maihaoche.volvo.ui.instorage.adapter.CarPhotoAdapter;
import com.maihaoche.volvo.ui.instorage.daomain.CarCheckInfo;
import com.maihaoche.volvo.ui.photo.ImgInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 类简介：在库异常的adapter
 * 作者：  yang
 * 时间：  2017/8/16
 * 邮箱：  yangyang@maihaoche.com
 */

public class InWarehouseExceptionAdapter extends PullRecyclerAdapter<CarMassLossBriefVO> {


    public InWarehouseExceptionAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new InWarehouseExceptionVH(parent);
    }


    private static final class InWarehouseExceptionVH extends BaseViewHolder<CarMassLossBriefVO> {
        private ItemInwarehouseExceptionBinding mBinding;

        public InWarehouseExceptionVH(ViewGroup parent) {
            super(parent, R.layout.item_inwarehouse_exception);
            mBinding = DataBindingUtil.bind(itemView);
            mBinding.reportTime.attrTitleText.setText("上报时间");
            mBinding.reportTime.attrTitleText.setWidth(SizeUtil.dip2px(100));
            mBinding.operatorName.attrTitleText.setText("在库管理员");
            mBinding.operatorName.attrTitleText.setWidth(SizeUtil.dip2px(100));
        }

        @Override
        public void setData(CarMassLossBriefVO data) {
            super.setData(data);
            if (mBinding == null || data == null) {
                return;
            }
            mBinding.reportTime.attrValueText.setText(data.reportDate);
            mBinding.operatorName.attrValueText.setText(data.transactorName);
            List<CarCheckInfo.CarMassLoss> carMassLosses = data.carMassLossList;
            if (carMassLosses == null || carMassLosses.size() == 0) {
                mBinding.exceptionList.setVisibility(View.GONE);
            } else {
                mBinding.exceptionList.setVisibility(View.VISIBLE);
                List<ImgInfo> imgInfoList = new ArrayList<>();
                for (CarCheckInfo.CarMassLoss massLoss :
                        carMassLosses) {
                    imgInfoList.add(new ImgInfo(massLoss.massLossImg, "", massLoss.remark));

                }
                mBinding.exceptionList.setLayoutManager(new GridLayoutManager(getContext(), 2));
                CarPhotoAdapter mErrorAdapter = new CarPhotoAdapter(getContext(), false);
                mErrorAdapter.addData(imgInfoList);
                mBinding.exceptionList.setAdapter(mErrorAdapter);
            }
        }
    }

}
