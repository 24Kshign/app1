package com.maihaoche.volvo.ui.instorage.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import com.maihaoche.volvo.databinding.ActivityInStorageBinding;
import com.maihaoche.volvo.ui.AbsScanBaseActivity;
import com.maihaoche.volvo.ui.car.activity.QrScanCodeAty;
import com.maihaoche.volvo.ui.common.daomain.InstorageInfo;
import com.maihaoche.volvo.ui.instorage.daomain.InstorageRequest;
import com.maihaoche.volvo.ui.instorage.daomain.RefuseInstorageRequest;
import com.maihaoche.volvo.ui.instorage.event.ListItemChangeEvent;
import com.maihaoche.volvo.ui.instorage.event.SelectLableEvent;
import com.maihaoche.volvo.ui.photo.ChooseImageItem;
import com.maihaoche.volvo.ui.photo.ImageViewerActivity;
import com.maihaoche.volvo.ui.photo.PhotoWallActivity;
import com.maihaoche.volvo.util.SPUtil;
import com.maihaoche.volvo.util.TextWatcherUtil;
import com.maihaoche.volvo.view.dialog.MoveStorageDialog;
import com.maihaoche.volvo.view.dialog.SelectLableDialog;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;


import io.reactivex.disposables.Disposable;

/**
 * 入库
 */
public class InStorageActivity extends AbsScanBaseActivity<ActivityInStorageBinding> {


    private static final String INFO = "info";
    private static final int REQUEST_PIC_CODE = 100;


    private SelectLableDialog mDialog = null;

    private InstorageInfo info;

    private ActivityInStorageBinding binding;
    private Long storageId = null;

    public static Intent createIntent(Context context,InstorageInfo info) {
        Intent intent = new Intent(context, InStorageActivity.class);
        intent.putExtra(INFO,info);
        return intent;
    }


    @Override
    public int getContentResId() {
        return R.layout.activity_in_storage;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        binding = getContentBinding();
        info = (InstorageInfo) getIntent().getSerializableExtra(INFO);
        if(!info.bindingKeyBoxFlag()){
            binding.keyArea.setVisibility(View.GONE);
        }
        init();
        initListener();
        if(DeviceUtil.isSENTER()){
            HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_START_IN_STORAGE);
            PermissionHandler.checkWriteSetting(this, granted -> {
                if (granted) {
                    LightUtil.setLight(InStorageActivity.this, 255);
                }
            });
        }

    }


    private void init() {

        binding.defaultImage1.setVisibility(View.VISIBLE);
        if(!DeviceUtil.isSENTER()){
            binding.sweepTip.setText("点击按钮，扫描条码");
        }
        if(info!=null){
            binding.frameCode.setText(info.carUnique);
            binding.carAttribute.setText(info.carAttribute);
            if(info.showRefuseButton!=null && info.showRefuseButton){
                initHeader("入库","拒绝入库",v->{
                    RefuseInstorageRequest request = new RefuseInstorageRequest();
                    request.carId = info.carId;
                    AppApplication.getServerAPI().refuseInstorage(request)
                            .setOnDataGet(response->{
                                AlertToast.show("拒绝入库成功");
                                RxBus.getDefault().post(new ListItemChangeEvent(ListItemChangeEvent.TYPE_UNSTANDINSTORAGE,info));
                                finish();
                            })
                            .setOnDataError(emsg -> AlertToast.show(emsg))
                            .call(this);
                });
            }else{
                initHeader("入库");
            }
        }

    }

    //监听设置
    private void initListener() {
        ActivityInStorageBinding binding = getContentBinding();

        getContentBinding().lableArea.setOnClickListener(v -> {
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

            MoveStorageDialog dialog = new MoveStorageDialog(this,info.carId,(string,id)->{
                binding.lableName.setText(string);
                storageId = id;
            });
//            dialog.setHaveListener(have -> isHaveSotrage = have);
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

        binding.barCode.addTextChangedListener(new TextWatcherUtil(0, null, binding.clearSweep));


        binding.clearSweep.setOnClickListener(v -> {
            binding.barCode.setText("");
        });

        binding.sweep.setOnClickListener(v -> {
            if(DeviceUtil.isSENTER()){
                doBarCodeScan(binding.barCode);
            }else{
                PermissionHandler.checkCamera(this, granted -> {
                    if (granted) {
                        Intent intent = new Intent(this,QrScanCodeAty.class);
                        startActivityForResult(intent, QrScanCodeAty.REQUEST_CODE_RFID);
                    }
                });
            }

        });
        binding.sweepKey.setOnClickListener(v -> {
            if(DeviceUtil.isSENTER()){
                doBarCodeScan(binding.keyCode);
            }else{
                PermissionHandler.checkCamera(this, granted -> {
                    if (granted) {
                        Intent intent = new Intent(this,QrScanCodeAty.class);
                        startActivityForResult(intent, QrScanCodeAty.REQUEST_CODE);
                    }
                });
            }


        });



        binding.sureInStorage.setOnClickListener(v -> {



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

            String keyCode = binding.keyCode.getText().toString().trim();

            if (TextUtils.isEmpty(keyCode) && info.bindingKeyBoxFlag()) {
                AlertToast.show("请输入钥匙条形码");
                return;
            }

            InstorageRequest request = new InstorageRequest();
            request.tagId = bc;
            request.carId = info.carId;
            request.areaPositionId = storageId;
            request.associationPicture = picUrl;
            if(info.bindingKeyBoxFlag()){
                request.keyId = keyCode;
            }

            AppApplication.getServerAPI().instorageStand(request)
                    .setOnDataError(msg->{
                        showContent();
                        AlertToast.show(msg);
                        binding.sureInStorage.setEnabled(true);
                    })
                    .setOnDataGet(response->{
                        showContent();
                        AlertToast.show("入库成功");
                        HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_IN_STORAGE_SUCCEED);
                        RxBus.getDefault().post(new ListItemChangeEvent(ListItemChangeEvent.TYPE_INSTORAGE,info));
                        finish();
                    })
                    .setDoOnSubscribe(disposable -> {
                        showProgressWithContent();
                        binding.sureInStorage.setEnabled(false);
                    })
                    .call(this);

        });
    }

    /**
     * 进行条码扫描
     */
    private void doBarCodeScan(TextView destEt) {
        if (destEt == null){
            return;
        }

        destEt.requestFocus();
        HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_BARCODE_SCAN);
        BarCodeHelper.scan(result -> {
            if (!TextUtils.isEmpty(result)) {
                HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.BARCODE_SCAN_SUCCEED_WITH_VIBRATOR);
                destEt.setText(result);
            }
        });
    }

    /**
     * 进行VIN码识别
     */
    private void doVinScan() {
        OcrHelper.goOcrScan((result, carAttr) -> {
                    getContentBinding().frameCode.setText(result);
                    getContentBinding().carAttribute.setText(carAttr);
                }
                , this);
        HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_VIN_SCAN);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 224:
                doBarCodeScan(getContentBinding().barCode);
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
            }else if(requestCode == QrScanCodeAty.REQUEST_CODE){
                String result = data.getStringExtra(CodeUtils.RESULT_STRING);
                binding.keyCode.setText(result);
                binding.keyCode.setTextColor(Color.BLACK);
            }else if(requestCode == QrScanCodeAty.REQUEST_CODE_RFID){
                String result = data.getStringExtra(CodeUtils.RESULT_STRING);
                binding.barCode.setText(result);
            }
        }

    }
}
