package com.maihaoche.volvo.ui.instorage.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ScrollView;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.service.utils.HintUtil;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityAddStorageInfoBinding;
import com.maihaoche.volvo.ui.AbsScanBaseActivity;
import com.maihaoche.volvo.ui.common.daomain.CheckCarResponse;
import com.maihaoche.volvo.ui.common.daomain.PhotoConfigForm;
import com.maihaoche.volvo.ui.common.daomain.VideoEvent;
import com.maihaoche.volvo.ui.common.daomain.VideoItem;
import com.maihaoche.volvo.ui.instorage.daomain.CarCheckInfo;
import com.maihaoche.volvo.ui.common.daomain.InstorageInfo;
import com.maihaoche.volvo.ui.instorage.daomain.CheckCarRequest;
import com.maihaoche.volvo.ui.instorage.event.ListItemChangeEvent;
import com.maihaoche.volvo.ui.instorage.event.SelectKeyEvent;
import com.maihaoche.volvo.ui.photo.CheckCarPhotoAdapter;
import com.maihaoche.volvo.ui.photo.CheckCarPhotoItem;
import com.maihaoche.volvo.ui.photo.ChooseImageItem;
import com.maihaoche.volvo.ui.photo.ChoosePicAdapter;
import com.maihaoche.volvo.ui.photo.ImageViewerActivity;
import com.maihaoche.volvo.ui.photo.PhotoWallActivity;
import com.maihaoche.volvo.ui.setting.DefauleValue;
import com.maihaoche.volvo.util.EditTextUtil;
import com.maihaoche.volvo.util.SPUtil;
import com.maihaoche.volvo.util.TextWatcherUtil;
import com.maihaoche.volvo.util.UpLoadUtils;
import com.maihaoche.volvo.view.dialog.SelectKeyDialog;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * 补充入库资料
 */
public class AddStorageInfoActivity extends AbsScanBaseActivity<ActivityAddStorageInfoBinding> implements View.OnClickListener {



    public static final String CAR_EXTRA = "car_extra";
    public static final String CAR = "car";
    private ChoosePicAdapter mUpErrorAdapter;
    private CheckCarPhotoAdapter checkCarPhotoAdapter;
    private int mPosition;
    private CarCheckInfo carCheckInfo;
    private InstorageInfo info;
    private ActivityAddStorageInfoBinding binding;
    private Integer upVideoStatus;

    public static Intent createIntent(Context activity, InstorageInfo info) {
        Intent intent = new Intent(activity, AddStorageInfoActivity.class);
        intent.putExtra(CAR, info);
        return intent;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_add_storage_info;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        initHeader("验车信息");
        binding = getContentBinding();
        initView();
        reLoad();
    }

    @Override
    protected void reLoad() {
        PhotoConfigForm form = new PhotoConfigForm();
        form.wmsCarId = info.carId;
        AppApplication.getServerAPI().getCheckCarConf(form)
                .setOnDataError(emsg -> {
                    showContent();
                    AlertToast.show(emsg);
                })
                .setOnDataGet(response -> {

                    fillData(response.result);
                })
                .setDoOnSubscribe(disposable -> {

                })
                .call(this);
    }

    private void fillData(CheckCarResponse result) {

        upVideoStatus = result.upVideoStatus;
        if(upVideoStatus!=null &&(upVideoStatus ==1 || upVideoStatus ==2)){
            binding.picture.videoArea.setVisibility(View.GONE);
        }
        List<CheckCarPhotoItem> list = new ArrayList<>();
        for(CheckCarResponse.PhotoConfig response : result.checkPhotos){
            list.add(new CheckCarPhotoItem(response.no,response.name));
        }
        checkCarPhotoAdapter.setmData(list);
    }

    private void initView() {

        carCheckInfo = (CarCheckInfo) getIntent().getSerializableExtra(CAR_EXTRA);
        info = (InstorageInfo) getIntent().getSerializableExtra(CAR);
        EditTextUtil.editTextLimitTwoDecimalPlaces(binding.mileage);
        binding.mileage.requestFocus();
        binding.dateArea.setOnClickListener(v->selectDate());
        binding.keyArea.setOnClickListener(v -> {
            new SelectKeyDialog(this).show();
            Disposable disposable = RxBus.getDefault().register(SelectKeyEvent.class, o -> {
                SelectKeyEvent event = (SelectKeyEvent) o;
                binding.key.setText(event.key);
                if (carCheckInfo == null){
                    carCheckInfo = new CarCheckInfo();
                }
                carCheckInfo.keyNumber = event.number;
            });
            pend(disposable);
        });

        binding.picture.remark.addTextChangedListener(new TextWatcherUtil(200, count -> {
            binding.picture.textNumber.setText(count + "/200");
        }));
        binding.picture.save.setOnClickListener(v -> videoUpload());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(this,2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        binding.picture.rvErrorPhoto.setLayoutManager(gridLayoutManager2);

        binding.picture.rvNormalPhoto.setLayoutManager(gridLayoutManager);
        mUpErrorAdapter = new ChoosePicAdapter(this, true, 20, false);
        checkCarPhotoAdapter = new CheckCarPhotoAdapter(this);
        binding.picture.rvNormalPhoto.setAdapter(checkCarPhotoAdapter);
//        checkCarPhotoAdapter.setmData(getList());
        binding.picture.rvErrorPhoto.setAdapter(mUpErrorAdapter);
        checkCarPhotoAdapter.setClickItem((data,position)->{
            if (TextUtils.isEmpty(data.mOriPath)) {
                    Intent intent = new Intent(this, PhotoWallActivity.class);
                    intent.putExtra(PhotoWallActivity.EXTRA_MEDIA_TYPE, PhotoWallActivity.TYPE_PICTURE);
                    intent.putExtra(PhotoWallActivity.EXTRA_IS_MUTIPLE, true);
                    intent.putExtra(PhotoWallActivity.EXTRA_MAX_PHOTO, 1);
                    intent.putExtra(PhotoWallActivity.EXTRA_IS_LIMIT_MAX, true);
                    startActivityForResult(intent, position);
                } else {
                    //查看图片
                    Intent intent = new Intent(this, ImageViewerActivity.class);
                    intent.putExtra(ImageViewerActivity.EXTRA_URLS_STR,
                            data.mOriPath);
                    startActivity(intent);
                }
        });
        mUpErrorAdapter.setOnItemClickListener(
                (view, data, position) -> {
                    if (position == mUpErrorAdapter.getSize()) {
                        Intent intent = new Intent(this, PhotoWallActivity.class);
                        intent.putExtra(PhotoWallActivity.EXTRA_MEDIA_TYPE, PhotoWallActivity.TYPE_PICTURE);
                        intent.putExtra(PhotoWallActivity.EXTRA_IS_MUTIPLE, true);
                        intent.putExtra(PhotoWallActivity.EXTRA_MAX_PHOTO, 21 - mUpErrorAdapter.getItemCount());
                        intent.putExtra(PhotoWallActivity.EXTRA_IS_LIMIT_MAX, true);
                        startActivityForResult(intent, 100);
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

        //监听录制
        Disposable disposable = RxBus.getDefault().toObserverable(VideoEvent.class)
                .subscribe(event -> {
                    VideoEvent event1 = (VideoEvent) event;
                    VideoItem videoItem = new VideoItem();
                    videoItem.isJustLook = false;
                    videoItem.bitmap = event1.bitmap;
                    videoItem.mOriPath = event1.videoPath;
                    binding.picture.guaranteeVideo.setVideoItem(videoItem);
                });

        binding.picture.descript.setOnClickListener(v->{
            startActivity(new Intent(this,VideoIntroduceActivity.class));
        });
        pend(disposable);
        DefauleValue defauleValue = SPUtil.getValueFromPrefs();
        if (carCheckInfo != null) {
            binding.mileage.setText(carCheckInfo.odometer);
            defauleValue.setDirection(carCheckInfo.manualStatus + 1);
            defauleValue.setFitertifi(carCheckInfo.certificateConsistentStatus + 1);
            defauleValue.setCertification(carCheckInfo.customsClearanceStatus + 1);
            defauleValue.setComnspect(carCheckInfo.checklistStatus + 1);
            defauleValue.setKeyNumber(carCheckInfo.keyNumber + "把");
            binding.setValue(defauleValue);

            binding.picture.remark.setText(carCheckInfo.remark);
            if (carCheckInfo.carMassLossList != null) {
                List<ChooseImageItem> list = new ArrayList<>();
                ChooseImageItem item;
                for (CarCheckInfo.CarMassLoss photo : carCheckInfo.carMassLossList) {
                    item = new ChooseImageItem(photo.massLossImg, false);
                    item.remark = photo.remark;
                    list.add(item);
                }
                mUpErrorAdapter.addData(list);
            }
        } else {
            if (defauleValue.isDefaultWrite()) {
                binding.setValue(defauleValue);
            } else {
                binding.key.setText("1把");
            }
        }

        if (info == null) {
            info = new InstorageInfo();
        }
        binding.key.setText(defauleValue.getKeyNumber());
        binding.frameCode.setText(info.carUnique);
        binding.carAttribute.setText(info.carAttribute);


    }

    private void selectDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month+=1;
                String strMonth = month+"";
                if(strMonth.length() <2){
                    strMonth = "0"+strMonth;
                }

                String strDay = dayOfMonth+"";
                if(strDay.length() <2){
                    strDay = "0"+strDay;
                }

                binding.date.setText(year+"-"+strMonth+"-"+strDay);
            }
        }, year, month, day).show();
    }

    private void videoUpload() {
        if (!collection()) {
            return;
        }

        List<CheckCarPhotoItem> lists = checkCarPhotoAdapter.getmData();
        List<CheckCarResponse.PhotoConfig> checkCarResponses = new ArrayList<>();
        int count = 0;
        for(CheckCarPhotoItem item:lists){
            count++;
            if(TextUtils.isEmpty(item.mServerUrl)){
                AlertToast.show("第"+count+"张图片上传有误，请删除重新上传");
                return;
            }
            checkCarResponses.add(new CheckCarResponse.PhotoConfig(item.imgId,item.mServerUrl));
        }

        if (StringUtil.isEmpty(binding.picture.guaranteeVideo.getmVideoItem().mOriPath)) {
            save(checkCarResponses,null);
            return;
        }
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("绕车视频上传中:"+0 + "%，请稍后");
        pd.show();
        UpLoadUtils.uploadFile(this,
                binding.picture.guaranteeVideo.getmVideoItem().mOriPath,
                new UpLoadUtils.UpLoadListener() {
                    @Override
                    public void success(String path) {
                        save(checkCarResponses,path);
                        pd.dismiss();
                    }

                    @Override
                    public void fail(String message) {
                        AlertToast.show("绕车视频上传失败："+message);
                    }

                    @Override
                    public void progress(int progress) {
                        pd.setMessage("绕车视频上传中:"+ progress + "%，请稍后");
                    }
                });
    }

    //确认提交按钮
    private void save(List<CheckCarResponse.PhotoConfig> checkCarResponses,String path) {

        CheckCarRequest request = new CheckCarRequest();

        carCheckInfo.checkPhotos = checkCarResponses;
        request.carCheckInfoForm = carCheckInfo;
        request.carId = info.carId;
        request.carStoreType = info.carStoreType;
        if(upVideoStatus !=null && upVideoStatus == 3){
            request.carCheckInfoForm.aroundCarVideoUrl = path;
        }
        request.carCheckInfoForm.upVideoStatus = upVideoStatus;
        AppApplication.getServerAPI().checkCar(request)
                .setOnDataError(msg->{
                    showContent();
                    AlertToast.show(msg);
                    binding.picture.save.setEnabled(true);
                })
                .setDoOnSubscribe(disposable -> {
                    showProgressWithContent();
                    binding.picture.save.setEnabled(false);
                })
                .setOnDataGet(response->{
                    HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_EXINFO_SAVED);
                    AlertToast.show("验车成功");
                    showContent();
                    RxBus.getDefault().post(new ListItemChangeEvent(ListItemChangeEvent.TYPE_CHECK_CAR,info));
                    finish();
                })
                .call(this);

    }

    //获取UI数据
    private boolean collection() {
        if (carCheckInfo == null) {
            carCheckInfo = new CarCheckInfo();
        }

        if (StringUtil.isEmpty(binding.date.getText().toString())) {
            AlertToast.show("请填写生产日期");

            return false;
        }
        carCheckInfo.productionDate = binding.date.getText().toString();

        if (StringUtil.isEmpty(binding.mileage.getText().toString())) {
            AlertToast.show("请填写里程");

            return false;
        }

        carCheckInfo.odometer = binding.mileage.getText().toString();

        //钥匙默认1把
        if (StringUtil.isNotEmpty(binding.key.getText().toString())) {
            String sp = binding.key.getText().toString().substring(0, 1);
            carCheckInfo.keyNumber = Integer.valueOf(sp);
        }


        /**
         * 是否有合格证或关单（0-否 1-是）
         */
        if (binding.certificationHave.isChecked()) {
            carCheckInfo.customsClearanceStatus = 1;
        } else if (binding.certificationNo.isChecked()) {
            carCheckInfo.customsClearanceStatus = 0;
        } else {
            AlertToast.show("请选择是否有合格证或关单");
            return false;
        }
        /**
         * 是否有一致性证书（0-否 1-是）
         */
        if (binding.fitCertifiHave.isChecked()) {
            carCheckInfo.certificateConsistentStatus = 1;
        } else if (binding.fitCertifiNo.isChecked()) {
            carCheckInfo.certificateConsistentStatus = 0;
        } else {
            AlertToast.show("请选择是否有一致性证书");
            return false;
        }
        /**
         * 是否有商检书（0-否 1-是）
         */
        if (binding.comInspectHave.isChecked()) {
            carCheckInfo.checklistStatus = 1;
        } else if (binding.comInspectNo.isChecked()) {
            carCheckInfo.checklistStatus = 0;
        } else {
            AlertToast.show("请选择是否有商检书");
            return false;
        }
        /**
         * 是否有说明书（0-否 1-是）
         */
        if (binding.directionHave.isChecked()) {
            carCheckInfo.manualStatus = 1;
        } else if (binding.directionNo.isChecked()) {
            carCheckInfo.manualStatus = 0;
        } else {
            AlertToast.show("请选择是否有说明书");
            return false;
        }



        //车况说明
        carCheckInfo.remark = binding.picture.remark.getText().toString();

        //异常照片
        carCheckInfo.carMassLossList = new ArrayList<>();
        List<ChooseImageItem> list = mUpErrorAdapter.getData();
        CarCheckInfo.CarMassLoss carMassLoss;
        for (ChooseImageItem item : list) {
            if(StringUtil.isNotEmpty(item.mServerUrl)){
                carMassLoss = new CarCheckInfo.CarMassLoss();
                carMassLoss.massLossImg = item.mServerUrl;
                carMassLoss.remark = item.remark;
                carCheckInfo.carMassLossList.add(carMassLoss);
            }

        }

        return true;

    }

    @Override
    public void onClick(View v) {

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == 100){
                mUpErrorAdapter.addOriPath(data.getStringArrayListExtra("path"));
                binding.scrollView.post(() -> {
                    binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN); //上传完异常照片滚动到最底部
                });
            }else{
                checkCarPhotoAdapter.setUrl(data.getStringArrayListExtra("path").get(0),requestCode);
            }
        }
    }

    @Override
    protected boolean useBarcodeScan() {
        return true;
    }


    @Override
    protected boolean needStopAllPlayingMediaWhenQuit() {
        return false;
    }

    private List<CheckCarPhotoItem> getList(){
        List<CheckCarPhotoItem> list = new ArrayList<>();
        for(int i=0;i<10;i++){
            list.add(new CheckCarPhotoItem(i,"name"+i));
        }
        return list;
    }
}
