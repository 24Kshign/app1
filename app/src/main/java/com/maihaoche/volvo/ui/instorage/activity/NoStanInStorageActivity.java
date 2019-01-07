package com.maihaoche.volvo.ui.instorage.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.kernal.smartvision.ocr.OcrHelper;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.service.permision.PermissionHandler;
import com.maihaoche.commonbiz.service.utils.DeviceUtil;
import com.maihaoche.commonbiz.service.utils.HintUtil;
import com.maihaoche.commonbiz.service.utils.LightUtil;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.scanlib.BarCodeHelper;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityNoStanInStorageBinding;
import com.maihaoche.volvo.ui.AbsScanBaseActivity;
import com.maihaoche.volvo.ui.common.daomain.InstorageInfo;
import com.maihaoche.volvo.ui.instorage.daomain.BrandSeriesInfo;
import com.maihaoche.volvo.ui.instorage.daomain.NoStandInstoRequest;
import com.maihaoche.volvo.ui.instorage.event.BrandEvent;
import com.maihaoche.volvo.ui.instorage.event.ListItemChangeEvent;
import com.maihaoche.volvo.ui.instorage.event.SelectLableEvent;
import com.maihaoche.volvo.ui.photo.ChooseImageItem;
import com.maihaoche.volvo.ui.photo.ImageViewerActivity;
import com.maihaoche.volvo.ui.photo.PhotoWallActivity;
import com.maihaoche.volvo.util.SPUtil;
import com.maihaoche.volvo.util.TextWatcherUtil;
import com.maihaoche.volvo.view.dialog.MoveStorageDialog;
import com.maihaoche.volvo.view.dialog.SelectLableDialog;

import io.reactivex.disposables.Disposable;

/**
 * 入库
 */
public class NoStanInStorageActivity extends AbsScanBaseActivity<ActivityNoStanInStorageBinding> {

    private static final int REQUEST_PIC_CODE = 100;

    private static final String WMS_GARAGE_ID = "wms_garage_id";

    private long mWmsGarageId = 0;//仓库id

    private SelectLableDialog mDialog = null;

    private ActivityNoStanInStorageBinding binding;

    private Long storageId = null;

    private boolean isHaveSotrage = true;

    private NoStandInstoRequest request;

    public static Intent createIntent(Context context, long wmsGarageId) {
        Intent intent = new Intent(context, NoStanInStorageActivity.class);
        intent.putExtra(WMS_GARAGE_ID, wmsGarageId);
        return intent;
    }


    @Override
    public int getContentResId() {
        return R.layout.activity_no_stan_in_storage;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        initHeader("非标入库");
        binding = getContentBinding();
        init();
        initListener();
        if(DeviceUtil.isSENTER()){
            PermissionHandler.checkWriteSetting(this, granted -> {
                if (granted) {
                    LightUtil.setLight(NoStanInStorageActivity.this, 255);
                }
            });
        }
    }

    private void init() {
        binding.defaultImage1.setVisibility(View.VISIBLE);
        request = new NoStandInstoRequest();
        HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_START_IN_STORAGE);
        if (getIntent() != null) {
            mWmsGarageId = getIntent().getLongExtra(WMS_GARAGE_ID, 0);
        }
        mDialog = new SelectLableDialog(this, mWmsGarageId);

    }

    //监听设置
    private void initListener() {

        binding.frameCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==17){
                    getBrandSeris(s.toString());
                }
            }
        });

        binding.lableArea.setOnClickListener(v -> {
            if (mDialog != null && !mDialog.isShowing()) {
                mDialog.show();
            }

            Disposable disposable = RxBus.getDefault().register(SelectLableEvent.class, o -> {
                SelectLableEvent event = (SelectLableEvent) o;
                getContentBinding().lableName.setText(event.lable);
            });
            pend(disposable);
        });

        binding.lableArea.setOnClickListener(v->{
            MoveStorageDialog dialog = new MoveStorageDialog(this,(string,id)->{
                binding.lableName.setText(string);
                storageId = id;
            });
            dialog.setHaveListener(have -> isHaveSotrage = have);
            dialog.show();
        });

        binding.choosePicItem1.setOnClickListener(v->{
            if (binding.choosePicItem1.mChooseImageItem == null || StringUtil.isEmpty(binding.choosePicItem1.mChooseImageItem.mOriPath)) {
                Intent intent = new Intent(this, PhotoWallActivity.class);
                intent.putExtra(PhotoWallActivity.EXTRA_MEDIA_TYPE, PhotoWallActivity.TYPE_PICTURE);
                intent.putExtra(PhotoWallActivity.EXTRA_IS_MUTIPLE, true);
                intent.putExtra(PhotoWallActivity.EXTRA_MAX_PHOTO, 1);
                intent.putExtra(PhotoWallActivity.EXTRA_IS_LIMIT_MAX, true);
                intent.putExtra(PhotoWallActivity.IS_ONLY_FROM_CAMERA,true);
                startActivityForResult(intent, REQUEST_PIC_CODE);
            } else {
                //查看图片
                Intent intent = new Intent(this, ImageViewerActivity.class);
                intent.putExtra(ImageViewerActivity.EXTRA_URLS_STR,
                        binding.choosePicItem1.mChooseImageItem.mOriPath);
                startActivity(intent);
            }
        });

        binding.choosePicItemDelete1.setOnClickListener(v->{
            binding.choosePicItem1.mChooseImageItem = null;
            binding.choosePicItem1.showAdd();
            binding.choosePicItemDelete1.setVisibility(View.GONE);
        });

        binding.frameCode.addTextChangedListener(new TextWatcherUtil(0, null, binding.clearFrame));
        binding.barCode.addTextChangedListener(new TextWatcherUtil(0, null, binding.clearSweep));
        binding.keyCode.addTextChangedListener(new TextWatcherUtil(0, null, binding.clearKey));

        binding.clearFrame.setOnClickListener(v -> {
            binding.frameCode.setText("");
        });

        binding.clearSweep.setOnClickListener(v -> {
            binding.barCode.setText("");
        });
        binding.clearKey.setOnClickListener(v -> {
            binding.keyCode.setText("");
        });

        binding.camera.setOnClickListener(v ->
                doVinScan());

        binding.sweep.setOnClickListener(v -> {
            doBarCodeScan(binding.barCode);
        });

        binding.sweepFrame.setOnClickListener(v -> {
            doBarCodeScan(binding.frameCode);
        });
        binding.sweep.setOnClickListener(v -> {
            doBarCodeScan(binding.keyCode);
        });

        binding.brandArea.setOnClickListener(v->{
                Disposable disposable = RxBus.getDefault().register(BrandEvent.class, o->{
                   BrandEvent event = (BrandEvent) o;
                    if(event.getType() == BrandEvent.TYPE_BRAND){
                        binding.brand.setText(event.getString());
                        request.brandId = event.getCode();
                        request.brandName = event.getString();

                        binding.carSeries.setText("");
                        request.seriesId = null;
                        request.seriesName = null;
                    }


                });
            pend(disposable);

            startActivity(new Intent(this, SelectBrandActivity.class));

        });
        binding.carSeriesArea.setOnClickListener(v->{
            Disposable disposable = RxBus.getDefault().register(BrandEvent.class, o->{
                BrandEvent event = (BrandEvent) o;
                if(event.getType() == BrandEvent.TYPE_CAR_SERIES){
                    binding.carSeries.setText(event.getString());
                    request.seriesId = event.getCode();
                    request.seriesName = event.getString();
                }
            });
            pend(disposable);

            if(request.brandId == null){
                AlertToast.show("请先选择品牌");
            }else{
                startActivity(SelectSeriesActivity.create(this,request.brandId));
            }

        });
        binding.clientArea.setOnClickListener(v->{
            Disposable disposable = RxBus.getDefault().register(BrandEvent.class, o->{
                BrandEvent event = (BrandEvent) o;
                if(event.getType() == BrandEvent.TYPE_CLIENT){
                    binding.client.setText(event.getString());
                    request.customerId = event.getCode();
                    request.customer = event.getString();

                    binding.man.setText("");
                    request.customerContactId = null;
                    request.customerContact = "";
                }


            });
            pend(disposable);
            startActivity(new Intent(this,SelectClientActivity.class));

        });
        binding.manArea.setOnClickListener(v->{
            Disposable disposable = RxBus.getDefault().register(BrandEvent.class, o->{
                BrandEvent event = (BrandEvent) o;
                if(event.getType() == BrandEvent.TYPE_MAN){
                    binding.man.setText(event.getString());
                    request.customerContactId = event.getCode();
                    request.customerContact = event.getString();
                    request.customerContactPhone = event.getPhone();
                }
            });
            pend(disposable);
            if(request.customerId == null){
                AlertToast.show("请先选择客户");
            }else if(request.customerId == 0){
                BrandEvent brandEvent = null;
                if(request.customerContact!=null){
                    brandEvent = new BrandEvent(request.customerContact,0L,BrandEvent.TYPE_MAN);
                    brandEvent.setPhone(request.customerContactPhone);
                    startActivity(WriteContactActivity.create(this,brandEvent));
                }else{
                    startActivity(WriteContactActivity.create(this,null));
                }
            }else{
                startActivity(SeletcContactActivity.create(this,request.customerId));
            }
        });

        binding.sureInStorage.setOnClickListener(v -> {


            String vin = binding.frameCode.getText().toString().trim();
            String bc = binding.barCode.getText().toString().trim();

            if (TextUtils.isEmpty(bc)) {
                AlertToast.show("请输入条形码");
                return;
            }

            if(binding.choosePicItem1.mChooseImageItem==null){
                AlertToast.show("请上传条码照片");
                return;
            }

            String picUrl = binding.choosePicItem1.mChooseImageItem.mServerUrl;
            if(TextUtils.isEmpty(picUrl)){
                AlertToast.show("请上传条码照片");
                return;
            }

            if (TextUtils.isEmpty(vin)) {
                AlertToast.show("请输入车架号");
                return;
            }

            if(StringUtil.isEmpty(request.brandName)){
                AlertToast.show("请选择品牌");
                return;
            }
            if(StringUtil.isEmpty(request.seriesName)){
                AlertToast.show("请选择车系");
                return;
            }
            if(StringUtil.isEmpty(request.customer)){
                AlertToast.show("请选择客户");
                return;
            }
            if(StringUtil.isEmpty(request.customerContact)){
                AlertToast.show("请选择客户联系人");
                return;
            }
            String keyCode = binding.keyCode.getText().toString().trim();

            if (TextUtils.isEmpty(keyCode)) {
                AlertToast.show("请输入钥匙条形码");
                return;
            }

            request.areaPositionId = storageId;
            request.carTagId = bc;
            request.carUnique = vin;
            request.associationPicture = picUrl;
            request.warehouseId = AppApplication.getGaragePO().getWmsGarageId();
            request.keyId = keyCode;

            AppApplication.getServerAPI().instorageNoStand(request)
                    .setOnDataError(msg->{
                        showContent();
                        AlertToast.show(msg);
                        binding.sureInStorage.setEnabled(true);
                    })
                    .setDoOnSubscribe(disposable -> {
                        showProgressWithContent();
                        binding.sureInStorage.setEnabled(false);

                    })
                    .setOnDataGet(response->{
                        AlertToast.show("入库成功");
                        HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_IN_STORAGE_SUCCEED);
                        showContent();
                        InstorageInfo info = new InstorageInfo();
                        info.carUnique = request.carUnique;
                        RxBus.getDefault().post(new ListItemChangeEvent(ListItemChangeEvent.TYPE_UNSTANDINSTORAGE,info));
                        finish();
                    })
                    .call(this);
        });


    }


    /**
     * 进行条码扫描
     */
    private void doBarCodeScan(EditText destEt) {
        if (destEt == null){
            return;
        }

        destEt.requestFocus();
        HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_BARCODE_SCAN);
        BarCodeHelper.scan(result -> {
            if (!TextUtils.isEmpty(result)) {
                HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.BARCODE_SCAN_SUCCEED_WITH_VIBRATOR);
                destEt.setText(result);
                if (SPUtil.getBoolean(SPUtil.VIN) && destEt.equals(getContentBinding().barCode)) {
                    doVinScan();
                }

            }

        });
    }

    /**
     * 进行VIN码识别
     */
    private void doVinScan() {
        OcrHelper.goOcrScan((result, carAttr) -> {
                    getContentBinding().frameCode.setText(result);
                }
                , this);
        HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_VIN_SCAN);
    }

    private void getBrandSeris(String unique){
        AppApplication.getServerAPI().getBrandSeri(unique)
                .setOnDataGet(response->{
                    fillBrandSeriesData(response.result);
                })
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .call(this);
    }

    private void fillBrandSeriesData(BrandSeriesInfo response) {
        if(response!=null){
            binding.brand.setText(response.brandName);
            request.brandId = response.brandId;
            request.brandName = response.brandName;
            binding.carSeries.setText(response.seriesName);
            request.seriesId = response.seriesId;
            request.seriesName = response.seriesName;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 224:
                doBarCodeScan(getContentBinding().frameCode.isFocused() ? getContentBinding().frameCode : getContentBinding().barCode);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected boolean useBarcodeScan() {
        return true;
    }

    @Override
    protected void onDestroy() {
        SPUtil.saveValue(SPUtil.LABLE, getContentBinding().lableName.getText().toString());
        OcrHelper.cleanCache();
        super.onDestroy();
    }

    @Override
    protected boolean needStopAllPlayingMediaWhenQuit() {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if(requestCode == REQUEST_PIC_CODE){
                binding.choosePicItemDelete1.setVisibility(View.VISIBLE);
                binding.choosePicItem1.setUrl(this, new ChooseImageItem(data.getStringArrayListExtra("path").get(0), false));
                binding.defaultImage1.setVisibility(View.GONE);
            }
        }
    }
}
