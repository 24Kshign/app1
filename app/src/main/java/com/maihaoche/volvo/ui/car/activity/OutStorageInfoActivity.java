package com.maihaoche.volvo.ui.car.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityOutStorageInfoBinding;
import com.maihaoche.volvo.ui.car.adapter.OutStorageInfoAdapter;
import com.maihaoche.volvo.ui.car.domain.DepartingCarList;
import com.maihaoche.volvo.ui.car.domain.OutStorageInfo;
import com.maihaoche.volvo.ui.car.domain.OutStorageDetailRequest;
import com.maihaoche.volvo.ui.car.domain.OutStorageRequest;
import com.maihaoche.volvo.ui.common.activity.PayTypeSelectActivity;
import com.maihaoche.volvo.ui.instorage.event.ListItemChangeEvent;
import com.maihaoche.volvo.ui.photo.ChooseImageItem;
import com.maihaoche.volvo.ui.photo.ImageViewerActivity;
import com.maihaoche.volvo.ui.photo.PhotoWallActivity;
import com.maihaoche.volvo.view.ChoosePicImage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class OutStorageInfoActivity extends HeaderProviderActivity<ActivityOutStorageInfoBinding> implements View.OnClickListener {

    private static final String CAR_INFO = "car_info";
    private static final String CAR_INFO_LIST = "car_info";
    private static final String TYPE = "type";
    public static final String TYPE_SINGLE = "type_single";
    public static final String TYPE_MULIT = "type_mulit";

    private static final int TYPE_LETTER = 1;
    private static final int TYPE_PIC = 2;


    private ActivityOutStorageInfoBinding binding;
    private OutStorageInfoAdapter adapter;
    private OutStorageInfo info;
    private List<Long> infos;
    private ArrayList<OutStorageInfo> outStorageInfos;
    private String mType;
    private boolean backShouldRefresh = true;//onResume中判断是否需要请求网络

    public static Intent create(Context activity,String type,OutStorageInfo info){
        Intent intent = new Intent(activity,OutStorageInfoActivity.class);
        intent.putExtra(CAR_INFO,info);
        intent.putExtra(TYPE,type);
        return intent;
    }

    public static Intent create(Context activity,String type, ArrayList<OutStorageInfo> info){
        Intent intent = new Intent(activity,OutStorageInfoActivity.class);
        intent.putExtra(CAR_INFO,info);
        intent.putExtra(TYPE,type);
        return intent;
    }


    @Override
    public int getContentResId() {
        return R.layout.activity_out_storage_info;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initHeader("出库");
        binding = getContentBinding();
        mType = getIntent().getStringExtra(TYPE);
        if(mType.equals(TYPE_SINGLE)){
            info = (OutStorageInfo) getIntent().getSerializableExtra(CAR_INFO);
        }else{
            outStorageInfos = (ArrayList<OutStorageInfo>) getIntent().getSerializableExtra(CAR_INFO);
            infos = getlist(outStorageInfos);
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.list.setLayoutManager(manager);
        adapter = new OutStorageInfoAdapter(this,new ArrayList<>());
        binding.list.setAdapter(adapter);
        binding.choosePicItem1.showAdd();
        binding.choosePicItem2.showAdd();
        initClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(backShouldRefresh){
            reLoad();
        }

    }

    @Override
    protected void reLoad() {
        OutStorageDetailRequest request = new OutStorageDetailRequest();
        request.wmsCarIdList = new ArrayList<>();
        if(mType.equals(TYPE_SINGLE)){
            request.wmsCarIdList.add(info.carId);
        }else{
            request.wmsCarIdList.addAll(infos);
        }
        AppApplication.getServerAPI().outStorageDetail(request)
                .setOnDataGet(response -> {
                    showContent();
                    if(response.result.departingCarDetailVOList!=null){
                        fillData(response.result);
                    }
                })
                .setOnDataError(msg->{
                    showEmpty();
                    AlertToast.show(msg);
                })
                .setDoOnSubscribe(disposable -> {
                    showProgress();
                })
                .call(this);
    }

    private void fillData(DepartingCarList result) {
        adapter.setLists(result.departingCarDetailVOList);
        if(!result.isPayed()){
            binding.picArea.setVisibility(View.GONE);
            binding.save.setVisibility(View.GONE);
            binding.closeAccount.setVisibility(View.VISIBLE);
            String total = "仓储费合计：¥"+result.totalPaymentWarehouse;
            binding.totleCost.setText(total);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.list.getLayoutParams();
            params.setMargins(0,0,0, SizeUtil.dip2px(50));
            binding.list.setLayoutParams(params);
            binding.titleTip.setVisibility(View.GONE);
        }else{
            binding.picArea.setVisibility(View.VISIBLE);
            binding.save.setVisibility(View.VISIBLE);
            binding.closeAccount.setVisibility(View.GONE);
            binding.titleTip.setVisibility(View.VISIBLE);
        }
        if(!result.uploadPickLetter && StringUtil.isNotEmpty(result.imgPickLetter)){
            ChooseImageItem imageItem = new ChooseImageItem();
            imageItem.mIsJustLook = true;
            imageItem.useCrop = false;
            imageItem.mServerUrl = result.imgPickLetter;
            imageItem.mOriPath = result.imgPickLetter;

            binding.choosePicItem1.setUrl2(this,imageItem);
        }

    }

    private void initClick() {
        binding.choosePicItem1.setOnClickListener(this);
        binding.choosePicItem2.setOnClickListener(this);
        binding.choosePicItemDelete1.setOnClickListener(v -> deletePhoto(v));
        binding.choosePicItemDelete2.setOnClickListener(v -> deletePhoto(v));
        binding.closeBtn.setOnClickListener(v->{
            backShouldRefresh = true;
            List<Long> list = new ArrayList<>();
            if(mType.equals(TYPE_SINGLE)){
                list.add(info.carId);
            }else{
                list.addAll(infos);
            }
            PayTypeSelectActivity.toActivity(this,StringUtil.getStringList(list));
        });

        binding.save.setOnClickListener(v->{
            String imgDelivery = null;
            String imgPickLetter = null;

            if(binding.choosePicItem1.mChooseImageItem!=null){
                if(StringUtil.isNotEmpty(binding.choosePicItem1.mChooseImageItem.mServerUrl)){
                    imgPickLetter = binding.choosePicItem1.mChooseImageItem.mServerUrl;
                }
            }

            if(binding.choosePicItem2.mChooseImageItem!=null){
                if(StringUtil.isNotEmpty(binding.choosePicItem2.mChooseImageItem.mServerUrl)){
                    imgDelivery = binding.choosePicItem2.mChooseImageItem.mServerUrl;
                }
            }

            if(StringUtil.isEmpty(imgDelivery)){
                AlertToast.show("请上传交车照片");

                return ;
            }

            if(StringUtil.isEmpty(imgPickLetter)){
                AlertToast.show("请上传提车委托函");
                return ;
            }

            OutStorageRequest request = new OutStorageRequest();
            request.wmsCarIdList = new ArrayList<Long>();
            if(mType.equals(TYPE_SINGLE)){
                request.wmsCarIdList.add(info.carId);
            }else{
                request.wmsCarIdList.addAll(infos);
            }
            request.imgDelivery = imgDelivery;
            request.imgPickLetter = imgPickLetter;
            AppApplication.getServerAPI().outStorage(request)
                    .setOnDataError(emsg -> AlertToast.show(emsg))
                    .setOnDataGet(baseResponse -> {
                        AlertToast.show("出库成功");
                        if(mType.equals(TYPE_SINGLE)){
                            RxBus.getDefault().post(new ListItemChangeEvent(ListItemChangeEvent.TYPE_OUTSTORAGE,info));
                        }else{
                            RxBus.getDefault().post(new ListItemChangeEvent(ListItemChangeEvent.TYPE_BATCHOUTSTORAGE,outStorageInfos));
                        }

                        finish();
                    })
                    .call(this);

        });
    }

    private void deletePhoto(View v) {
        if(v.getId() == R.id.choose_pic_item_delete1){
            binding.choosePicItem1.mChooseImageItem.mOriPath = null;
            binding.choosePicItem1.showAdd();
            binding.choosePicItemDelete1.setVisibility(View.GONE);
        }else{
            binding.choosePicItem2.showAdd();
            binding.choosePicItem2.mChooseImageItem.mOriPath = null;
            binding.choosePicItemDelete2.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        backShouldRefresh = false;
        if(v.getId() == binding.choosePicItem1.getId()){
            showPicOrSelect(binding.choosePicItem1,TYPE_LETTER);
        }else{
            showPicOrSelect(binding.choosePicItem2,TYPE_PIC);
        }
    }

    private void showPicOrSelect(ChoosePicImage item,int type){
        if (item.mChooseImageItem == null || StringUtil.isEmpty(item.mChooseImageItem.mOriPath)) {
            Intent intent = new Intent(this, PhotoWallActivity.class);
            intent.putExtra(PhotoWallActivity.EXTRA_MEDIA_TYPE, PhotoWallActivity.TYPE_PICTURE);
            intent.putExtra(PhotoWallActivity.EXTRA_IS_MUTIPLE, true);
            intent.putExtra(PhotoWallActivity.EXTRA_MAX_PHOTO, 1);
            intent.putExtra(PhotoWallActivity.EXTRA_IS_LIMIT_MAX, true);
            startActivityForResult(intent, type);
        } else {
            //查看图片
            Intent intent = new Intent(this, ImageViewerActivity.class);
            intent.putExtra(ImageViewerActivity.EXTRA_URLS_STR,
                    item.mChooseImageItem.mOriPath);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ArrayList<String> path = data.getStringArrayListExtra("path");
            if(path == null || path.size() == 0)return;
            switch (requestCode) {
                case TYPE_LETTER:
                    binding.choosePicItemDelete1.setVisibility(View.VISIBLE);
                    binding.choosePicItem1.setUrl(this, new ChooseImageItem(data.getStringArrayListExtra("path").get(0), false));
                    binding.defaultImage1.setVisibility(View.GONE);
                    break;
                case TYPE_PIC:
                    binding.choosePicItemDelete2.setVisibility(View.VISIBLE);
                    binding.choosePicItem2.setUrl(this, new ChooseImageItem(data.getStringArrayListExtra("path").get(0), false));
                    binding.defaultImage2.setVisibility(View.GONE);
                    break;
                default:
                    return;

            }
        }
    }

    public List<Long> getInfos() {
        return infos;
    }

    public void setInfos(List<Long> infos) {
        this.infos = infos;
    }

    private ArrayList<Long> getlist(ArrayList<OutStorageInfo> ll){
        ArrayList<Long> list = new ArrayList<>();
        for(OutStorageInfo info : ll){
            list.add(info.carId);
        }
        return list;
    }
}
