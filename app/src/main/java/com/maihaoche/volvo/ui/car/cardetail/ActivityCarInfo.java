package com.maihaoche.volvo.ui.car.cardetail;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.module.ui.NormalDialog;
import com.maihaoche.commonbiz.module.ui.recyclerview.BaseRecyclerViewDecration;
import com.maihaoche.commonbiz.service.image.ImageLoader;
import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.dao.Enums;
import com.maihaoche.volvo.databinding.ActivityCarInfoBinding;
import com.maihaoche.volvo.databinding.IncludeCarBaseInfoBinding;
import com.maihaoche.volvo.databinding.IncludeCarInstoreBinding;
import com.maihaoche.volvo.databinding.IncludeOutInfoBinding;
import com.maihaoche.volvo.databinding.IncludeRecyclerviewBinding;
import com.maihaoche.volvo.databinding.IncludeStocktakeDetailListBinding;
import com.maihaoche.volvo.databinding.ShowStoreagePhotoBinding;
import com.maihaoche.volvo.server.dto.CarBaseInfoVO;
import com.maihaoche.volvo.server.dto.CarDeliveryVO;
import com.maihaoche.volvo.server.dto.CarDetailVO;
import com.maihaoche.volvo.server.dto.CarMassLossBriefVO;
import com.maihaoche.volvo.server.dto.CarStoredInfoVO;
import com.maihaoche.volvo.server.dto.PvNameVO;
import com.maihaoche.volvo.server.dto.StocktakeDetailVO;
import com.maihaoche.volvo.server.dto.request.CarIdRequest;
import com.maihaoche.volvo.ui.common.daomain.CheckCarResponse;
import com.maihaoche.volvo.ui.instorage.adapter.CarPhotoAdapter;
import com.maihaoche.volvo.ui.instorage.daomain.CarCheckInfo;
import com.maihaoche.volvo.ui.inwarehouse.record.ActivityStocktakeList;
import com.maihaoche.volvo.ui.inwarehouse.record.StocktakeListAdapter;
import com.maihaoche.volvo.ui.photo.ChooseImageItem;
import com.maihaoche.volvo.ui.photo.ImageViewerActivity;
import com.maihaoche.volvo.ui.photo.ImgInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * 类简介：车辆详情页面 包含所有信息的页面
 * 作者：  yang
 * 时间：  2017/8/16
 * 邮箱：  yangyang@maihaoche.com
 */

public class ActivityCarInfo extends HeaderProviderActivity<ActivityCarInfoBinding> {


    private CarIdRequest mCarIdRequest;

    private static final String CAR_ID_REQUEST = "car_id_request";


    public static Intent createIntent(Context context, CarIdRequest carId) {
        Intent intent = new Intent(context, ActivityCarInfo.class);
        intent.putExtra(CAR_ID_REQUEST, carId);
        return intent;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_car_info;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        init();
        reload();
    }

    private void init() {
        mCarIdRequest = (CarIdRequest) getIntent().getSerializableExtra(CAR_ID_REQUEST);
        initHeader("车辆详情");
    }


    private void reload() {
        AppApplication.getServerAPI().getCarDetailVO(mCarIdRequest)
                .setTransformer(getIOLoadingTransformer())
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setOnDataGet(carDetailVOBaseResponse -> showView(carDetailVOBaseResponse.result))
                .call(this);

    }

    private void showView(CarDetailVO carDetailVO) {
        ActivityCarInfoBinding mBinding = getContentBinding();

        //车辆信息
        showCarInfo(carDetailVO, mBinding.carInfo.getContent());

        //基本信息
        if (carDetailVO.carBaseInfoVO != null) {
            mBinding.baseInfo.setVisibility(View.VISIBLE);
            showBaseInfo(carDetailVO.carBaseInfoVO, mBinding.baseInfo.getContent());
        } else {
            mBinding.baseInfo.setVisibility(View.GONE);
        }

        //入库信息
        if (carDetailVO.carStoredInfoVO != null) {
            mBinding.entryInfo.setVisibility(View.VISIBLE);
            showInstoreInfo(carDetailVO.carStoredInfoVO, carDetailVO.carCheckedInfoVO, mBinding.entryInfo.getContent());
        } else {
            mBinding.entryInfo.setVisibility(View.GONE);
        }

        //在库盘点
        if (carDetailVO.stocktakeDetailVOList != null && carDetailVO.stocktakeDetailVOList.size() > 0) {
            mBinding.stocktakeInfo.setVisibility(View.VISIBLE);
            showStocktakeDetails(carDetailVO.stocktakeDetailVOList, mBinding.stocktakeInfo.getContent());
        } else {
            mBinding.stocktakeInfo.setVisibility(View.GONE);
        }
        //在库异常
        if (carDetailVO.carMassLossBriefVOList != null && carDetailVO.carMassLossBriefVOList.size() > 0) {
            mBinding.inWarehouseException.setVisibility(View.VISIBLE);
            showInWarehouseException(carDetailVO.carMassLossBriefVOList, mBinding.inWarehouseException.getContent());
        } else {
            mBinding.inWarehouseException.setVisibility(View.GONE);
        }
        //出库信息
        if (carDetailVO.deliveryVO == null) {
            mBinding.carOutInfo.setVisibility(View.GONE);
        } else {
            mBinding.carOutInfo.setVisibility(View.VISIBLE);
            showOutInfo(carDetailVO.deliveryVO, mBinding.carOutInfo.getContent());
        }
        //其他情况
        if (carDetailVO.sendRecordList == null || carDetailVO.sendRecordList.size() == 0) {
            mBinding.otherInfo.setVisibility(View.GONE);
        } else {
            mBinding.otherInfo.setVisibility(View.VISIBLE);
            showOhterInfo(carDetailVO.sendRecordList, mBinding.otherInfo.getContent());
        }
    }


    /**
     * 车辆信息
     */
    private void showCarInfo(CarDetailVO carDetailVO, IncludeRecyclerviewBinding binding) {
        List<CarAttr> carAttrs = new ArrayList<>();
        carAttrs.add(new CarAttr("车架号", carDetailVO.carUnique));
        carAttrs.add(new CarAttr("车辆属性", carDetailVO.carAttribute));
        if(StringUtil.isNotEmpty(carDetailVO.carTagId)){
            carAttrs.add(new CarAttr("条形码", carDetailVO.carTagId));
        }

        if(StringUtil.isNotEmpty(carDetailVO.associationPicture)){
            carAttrs.add(new CarAttr("条形码图片", carDetailVO.associationPicture));
        }
        showCarAttrs(carAttrs, binding.recyclerview);

    }

    /**
     * 基本信息
     */
    private void showBaseInfo(CarBaseInfoVO carBaseInfoVO, IncludeCarBaseInfoBinding binding) {
        if (!TextUtils.isEmpty(carBaseInfoVO.orderNo)) {
            List<CarAttr> carAttrsBaseOrder = new ArrayList<>();
            carAttrsBaseOrder.add(new CarAttr("订单编号", carBaseInfoVO.orderNo));
            carAttrsBaseOrder.add(new CarAttr("业务员", carBaseInfoVO.salesman));
            carAttrsBaseOrder.add(new CarAttr("联系方式", carBaseInfoVO.salesmanPhone, new CallPhoneClick()));
            showCarAttrs(carAttrsBaseOrder, binding.recyclerviewOrder);
        }

        if (!TextUtils.isEmpty(carBaseInfoVO.customer)) {
            List<CarAttr> carAttrsBaseCustomer = new ArrayList<>();
            carAttrsBaseCustomer.add(new CarAttr("客户", carBaseInfoVO.customer));
            carAttrsBaseCustomer.add(new CarAttr("联系人", carBaseInfoVO.customerContact));
            carAttrsBaseCustomer.add(new CarAttr("联系方式", carBaseInfoVO.customerPhone, new CallPhoneClick()));
            showCarAttrs(carAttrsBaseCustomer, binding.recyclerviewCustomer);
        }

        if (!TextUtils.isEmpty(carBaseInfoVO.logisticsCompany)) {
            List<CarAttr> carAttrsBaseLogistics = new ArrayList<>();
            carAttrsBaseLogistics.add(new CarAttr("物流公司", carBaseInfoVO.logisticsCompany));
            carAttrsBaseLogistics.add(new CarAttr("联系人", carBaseInfoVO.logisticsContact));
            carAttrsBaseLogistics.add(new CarAttr("联系方式", carBaseInfoVO.logisticsContactPhone, new CallPhoneClick()));
            showCarAttrs(carAttrsBaseLogistics, binding.recyclerviewLogistics);
        }

    }


    /**
     * 入库信息
     */
    private void showInstoreInfo(CarStoredInfoVO carStoredInfoVO, CarCheckInfo carCheckInfo, IncludeCarInstoreBinding binding) {
        List<CarAttr> carAttrs = new ArrayList<>();
        carAttrs.add(new CarAttr("入库类型", carStoredInfoVO.carEnterTypeDesc));
        carAttrs.add(new CarAttr("入库时间", carStoredInfoVO.entryDate));
        carAttrs.add(new CarAttr("入库管理员", carStoredInfoVO.transactorName));
        if (carCheckInfo != null) {
            carAttrs.add(new CarAttr("验车时间", carCheckInfo.checkDate));
            carAttrs.add(new CarAttr("验车人员", carCheckInfo.operatorName));
            carAttrs.add(new CarAttr("里程", carCheckInfo.odometer + "公里"));
            carAttrs.add(new CarAttr("钥匙", carCheckInfo.keyNumber + "把"));
            carAttrs.add(new CarAttr("合格证/关单", carCheckInfo.customsClearanceStatus.intValue() == Enums.YesOrNoEnum.YES.value() ? "有" : "无"));
            carAttrs.add(new CarAttr("一致性证书", carCheckInfo.certificateConsistentStatus.intValue() == Enums.YesOrNoEnum.YES.value() ? "有" : "无"));
            carAttrs.add(new CarAttr("商检书", carCheckInfo.checklistStatus.intValue() == Enums.YesOrNoEnum.YES.value() ? "有" : "无"));
            carAttrs.add(new CarAttr("说明书", carCheckInfo.manualStatus.intValue() == Enums.YesOrNoEnum.YES.value() ? "有" : "无"));
        }
        //入库信息
        showCarAttrs(carAttrs, binding.recyclerviewInfo, 110);

        //常规照片
        ShowStoreagePhotoBinding photoBinding = binding.instorePhoto;
        if (carCheckInfo == null) {
            photoBinding.containerView.setVisibility(View.GONE);
            return;
        } else {
            photoBinding.containerView.setVisibility(View.VISIBLE);
        }

        List<ImgInfo> photoList = new ArrayList<>();
        for(CheckCarResponse.PhotoConfig response:carCheckInfo.checkPhotos){
            photoList.add(new ImgInfo(response.imgUrl,response.name));
        }
        photoBinding.photoList.setLayoutManager(new GridLayoutManager(this, 2));
        CarPhotoAdapter mPhotoAdapter = new CarPhotoAdapter(this, false);
        mPhotoAdapter.addData(photoList);
        photoBinding.photoList.setAdapter(mPhotoAdapter);

        if (TextUtils.isEmpty(carCheckInfo.remark)) {
            photoBinding.remarkArea.setVisibility(View.GONE);
        } else {
            photoBinding.remarkArea.setVisibility(View.VISIBLE);
            photoBinding.remark.setText(carCheckInfo.remark);
        }

        if (carCheckInfo.carMassLossList != null && carCheckInfo.carMassLossList.size() > 0) {
            photoBinding.errorPicArea.setVisibility(View.VISIBLE);
            List<ImgInfo> imgInfoList = new ArrayList<>();
            for (CarCheckInfo.CarMassLoss massLoss :
                    carCheckInfo.carMassLossList) {
                imgInfoList.add(new ImgInfo(massLoss.massLossImg, "", massLoss.remark));

            }
            photoBinding.rvErrorPhoto.setLayoutManager(new GridLayoutManager(this, 2));
            CarPhotoAdapter mErrorAdapter = new CarPhotoAdapter(this, false);
            mErrorAdapter.addData(imgInfoList);
            photoBinding.rvErrorPhoto.setAdapter(mErrorAdapter);
        } else {
            photoBinding.errorPicArea.setVisibility(View.GONE);
        }

    }

    /**
     * 显示在库盘点
     */
    private void showStocktakeDetails(List<StocktakeDetailVO> stocktakeDetailList, IncludeStocktakeDetailListBinding binding) {
        if (stocktakeDetailList.size() > 2) {
            binding.toAllDetails.setVisibility(View.VISIBLE);
            binding.toAllDetails.setOnClickListener(v -> {
                startActivity(ActivityStocktakeList.createIntent(this, mCarIdRequest));
            });

        } else {
            binding.toAllDetails.setVisibility(View.GONE);
        }

        binding.detailList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.detailList.addItemDecoration(BaseRecyclerViewDecration.createLineDecration());
        StocktakeListAdapter adapter = new StocktakeListAdapter(this);
        if (stocktakeDetailList.size() > 1) {
            adapter.addAll(stocktakeDetailList.subList(0, 2));
        } else {
            adapter.addAll(stocktakeDetailList.subList(0, 1));
        }
        binding.detailList.setAdapter(adapter);
    }


    /**
     * 在库异常
     */
    private void showInWarehouseException(List<CarMassLossBriefVO> carMassLossBriefVOList, IncludeRecyclerviewBinding binding) {
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerview.addItemDecoration(BaseRecyclerViewDecration.createLineDecration());
        InWarehouseExceptionAdapter adapter = new InWarehouseExceptionAdapter(this);
        adapter.addAll(carMassLossBriefVOList);
        binding.recyclerview.setAdapter(adapter);
    }

    /**
     * 出库信息
     */
    private void showOutInfo(CarDeliveryVO deliveryVO, IncludeOutInfoBinding binding) {
        int width = SizeUtil.dip2px(100);
        binding.outTime.attrName.setWidth(width);
        binding.outTime.attrName.setText("出库时间");
        binding.outTime.attrValue.setText(deliveryVO.deliveryTime);

        binding.operatorName.attrName.setWidth(width);
        binding.operatorName.attrName.setText("在库管理员");
        binding.operatorName.attrValue.setText(deliveryVO.transactorName);

        binding.feeService.attrName.setWidth(width);
        binding.feeService.attrName.setText("存储服务费");
        binding.feeService.attrValue.setText(deliveryVO.servicePayment);

        String imgURLEntrust = getImgURL(deliveryVO.imgEntrustmentLetter);
        ImageLoader.with(this, imgURLEntrust, binding.imgEntrustmentLetter);
        binding.imgEntrustmentLetter.setOnClickListener(v -> {
            //查看图片
            Intent intent = new Intent(ActivityCarInfo.this, ImageViewerActivity.class);
            intent.putExtra(ImageViewerActivity.EXTRA_URLS_STR, imgURLEntrust);
            startActivity(intent);
        });
        String imgURLDelivery = getImgURL(deliveryVO.imgDeliveryOrder);
        ImageLoader.with(this, imgURLDelivery, binding.imgDeliveryOrder);
        binding.imgDeliveryOrder.setOnClickListener(v -> {
            //查看图片
            Intent intent = new Intent(ActivityCarInfo.this, ImageViewerActivity.class);
            intent.putExtra(ImageViewerActivity.EXTRA_URLS_STR, imgURLDelivery);
            startActivity(intent);
        });

    }

    /**
     * 获取图片url
     */
    public String getImgURL(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        if (url.startsWith("//")) {
            return "http:" + url;
        }
        return "http://" + url;
    }


    /**
     * 其他情况
     */
    private void showOhterInfo(List<List<PvNameVO>> lists, IncludeRecyclerviewBinding binding) {
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerview.addItemDecoration(BaseRecyclerViewDecration.createLineDecration());
        OtherInfoAdapter adapter = new OtherInfoAdapter(this);
        adapter.addAll(lists);
        binding.recyclerview.setAdapter(adapter);

    }

    private ChooseImageItem createChooseImageItem(String url) {
        ChooseImageItem imageItem = new ChooseImageItem();
        imageItem.mIsJustLook = true;
        imageItem.mOriPath = url;
        return imageItem;
    }


    private void showCarAttrs(List<CarAttr> carAttrs, RecyclerView recyclerView) {
        showCarAttrs(carAttrs, recyclerView, 80);
    }

    private void showCarAttrs(List<CarAttr> carAttrs, RecyclerView recyclerView, int nameWidthDP) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setPadding(0, SizeUtil.dip2px(5), 0, SizeUtil.dip2px(5));
        CarAttrAdapter carInfoAdapter = new CarAttrAdapter(this, parent -> new CarAttrVH(parent).setAttrNameWidthDP(nameWidthDP));
        carInfoAdapter.addAll(carAttrs);
        recyclerView.setAdapter(carInfoAdapter);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * 点击弹窗打电话的事件
     */
    private class CallPhoneClick implements CarAttr.OnCarAttrClick {

        @Override
        public void onAttrClick(CarAttr carAttr) {
            if (carAttr != null && !TextUtils.isEmpty(carAttr.mAttrValue)) {

                NormalDialog.create(ActivityCarInfo.this, "是否拨打手机号码:" + carAttr.mAttrValue + "?")
                        .setBtnConfirmStr("确定")
                        .setOnOKClickListener(() -> {
                            if (ActivityCompat.checkSelfPermission(ActivityCarInfo.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                AlertToast.show("拨打电话权限被禁用");
                                return;
                            }
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            Uri data = Uri.parse("tel:" + carAttr.mAttrValue);
                            intent.setData(data);
                            startActivity(intent);
                        })
                        .show();
            }
        }
    }


}
