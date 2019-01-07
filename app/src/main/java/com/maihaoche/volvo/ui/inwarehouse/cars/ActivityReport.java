package com.maihaoche.volvo.ui.inwarehouse.cars;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.widget.ScrollView;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityReportBinding;
import com.maihaoche.volvo.server.dto.CarBaseInfoOnMailVO;
import com.maihaoche.volvo.server.dto.InWarehouseCarVO;
import com.maihaoche.volvo.server.dto.request.CarIdRequest;
import com.maihaoche.volvo.server.dto.request.ReportAbnInfoRequest;
import com.maihaoche.volvo.ui.instorage.activity.RemarkPhotoActivity;
import com.maihaoche.volvo.ui.instorage.daomain.CarCheckInfo;
import com.maihaoche.volvo.ui.inwarehouse.InWarehouseBiz;
import com.maihaoche.volvo.ui.photo.ChooseImageItem;
import com.maihaoche.volvo.ui.photo.ChoosePicAdapter;
import com.maihaoche.volvo.ui.photo.ImageViewerActivity;
import com.maihaoche.volvo.ui.photo.PhotoWallActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 类简介：在库车辆列表上报异常
 * 作者：  yang
 * 时间：  2017/8/15
 * 邮箱：  yangyang@maihaoche.com
 */

public class ActivityReport extends HeaderProviderActivity<ActivityReportBinding> {


    private static final String CAR_ID = "car_id";

    private static final int REQUEST_TAKE_PIC = 0;


    private CarIdRequest mCarId;

    private InWarehouseCarVO carVO;

    private int mPosition;

    private ChoosePicAdapter mUpErrorAdapter;


    public static Intent createIntent(Context context, InWarehouseCarVO carVO) {
        Intent intent = new Intent(context, ActivityReport.class);
        intent.putExtra(CAR_ID, carVO);
        return intent;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_report;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        init();
        load();
    }

    private void init() {
        carVO = (InWarehouseCarVO) getIntent().getSerializableExtra(CAR_ID);
        mCarId = new CarIdRequest(carVO.carId,carVO.carStoreType);
        initHeader("上报异常");

        ActivityReportBinding binding = getContentBinding();


        mUpErrorAdapter = new ChoosePicAdapter(this, true, 20, false);


        binding.rvErrorPhoto.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvErrorPhoto.setAdapter(mUpErrorAdapter);
        mUpErrorAdapter.setOnItemClickListener(
                (view, data, position) -> {
                    if (position == mUpErrorAdapter.getSize()) {
                        Intent intent = new Intent(this, PhotoWallActivity.class);
                        intent.putExtra(PhotoWallActivity.EXTRA_MEDIA_TYPE, PhotoWallActivity.TYPE_PICTURE);
                        intent.putExtra(PhotoWallActivity.EXTRA_IS_MUTIPLE, true);
                        intent.putExtra(PhotoWallActivity.EXTRA_MAX_PHOTO, 21 - mUpErrorAdapter.getItemCount());
                        intent.putExtra(PhotoWallActivity.EXTRA_IS_LIMIT_MAX, true);
                        startActivityForResult(intent, REQUEST_TAKE_PIC);
                    } else {
                        //查看图片
                        Intent intent = new Intent(this, ImageViewerActivity.class);
                        intent.putExtra(ImageViewerActivity.EXTRA_URLS_LIST, (ArrayList<String>) mUpErrorAdapter.getShowUrl());
                        intent.putExtra(ImageViewerActivity.EXTRA_REMARK, (ArrayList<String>) mUpErrorAdapter.getRemark());
                        intent.putExtra(ImageViewerActivity.EXTRA_CUR_INDEX, position);
                        startActivity(intent);
                    }
                });
        mUpErrorAdapter.setRemarkClick((position, date) -> {
             mPosition = position;
            RxBus.getDefault().register(ChooseImageItem.class, item -> {
                ChooseImageItem items = (ChooseImageItem) item;
                if (!TextUtils.isEmpty(items.remark) && mPosition == position) {

                    mUpErrorAdapter.getData().get(position).remark = items.remark;
                    mUpErrorAdapter.notifyDataSetChanged();

                }
            });
            Intent intent = new Intent(this, RemarkPhotoActivity.class);
            intent.putExtra(RemarkPhotoActivity.EXTRA, date);
            startActivity(intent);
        });

        binding.submitBtn.setEnabled(false);
        binding.submitBtn.setOnClickListener(v -> submit());

    }

    private void load() {
        AppApplication.getServerAPI().getBaseCarInfo(mCarId)
                .setTransformer(getIOLoadingTransformer())
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setOnDataGet(carBaseInfoVOBaseResponse -> showView(carBaseInfoVOBaseResponse.result))
                .call(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_TAKE_PIC:
                if(resultCode==RESULT_OK && data!=null){
                    mUpErrorAdapter.addOriPath(data.getStringArrayListExtra("path"));
                    runOnUiThread(() -> {
                        //上传完异常照片滚动到最底部
                        getContentBinding().scrollContainer.fullScroll(ScrollView.FOCUS_DOWN);
                    });
                }
                break;
            default:
                return;
        }
    }

    private void showView(CarBaseInfoOnMailVO vo) {
        if (vo == null) {
            return;
        }
        ActivityReportBinding binding = getContentBinding();

        InWarehouseBiz.fillBaseCarInfo(binding.carInfo, vo);

        getContentBinding().submitBtn.setEnabled(true);


    }

    /**
     * 提交
     */
    private void submit() {
        List<ChooseImageItem> list = mUpErrorAdapter.getData();
        if(list==null || list.size()==0){
            AlertToast.show("请上传照片");
            return;
        }
        List<CarCheckInfo.CarMassLoss> carMassLossList = new ArrayList<>();
        for (ChooseImageItem item : list) {
            CarCheckInfo.CarMassLoss carMassLoss = new CarCheckInfo.CarMassLoss();
            carMassLoss.massLossImg = item.mServerUrl;
            carMassLoss.remark = item.remark;
            carMassLossList.add(carMassLoss);
        }

        ReportAbnInfoRequest request = new ReportAbnInfoRequest();
        request.carId = mCarId.carId;
        request.carStoreType = mCarId.carStoreType;
        request.carMassLossList =carMassLossList;
        AppApplication.getServerAPI().uploadAbnormalSituation(request)
                .setTransformer(getIOLoadingTransformer())
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setOnDataGet(baseResponse -> {
                    AlertToast.show("上传成功");
                    RxBus.getDefault().post(new ReportEvent(carVO));
                    finish();
                })
                .call(this);
    }


}
