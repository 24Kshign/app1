package com.maihaoche.volvo.ui.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

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
import com.maihaoche.volvo.databinding.ActivityRelaBarCodeBinding;
import com.maihaoche.volvo.ui.AbsScanBaseActivity;
import com.maihaoche.volvo.ui.car.domain.OutStorageInfo;
import com.maihaoche.volvo.ui.common.daomain.BindCodeRequest;
import com.maihaoche.volvo.ui.instorage.activity.InStorageActivity;
import com.maihaoche.volvo.ui.instorage.event.ListItemChangeEvent;
import com.maihaoche.volvo.ui.photo.ChooseImageItem;
import com.maihaoche.volvo.ui.photo.ImageViewerActivity;
import com.maihaoche.volvo.ui.photo.PhotoWallActivity;
import com.maihaoche.volvo.util.TextWatcherUtil;

/**
 * 绑定条码
 */
public class RelaBarCodeActivity extends AbsScanBaseActivity<ActivityRelaBarCodeBinding> {

    private static final String WMS_GARAGE_ID = "wms_garage_id";
    private static final int REQUEST_PIC_CODE = 100;

    private ActivityRelaBarCodeBinding binding;
    private OutStorageInfo info;

    public static Intent createIntent(Context context, OutStorageInfo info) {
        Intent intent = new Intent(context, RelaBarCodeActivity.class);
        intent.putExtra(WMS_GARAGE_ID, info);
        return intent;
    }


    @Override
    public int getContentResId() {
        return R.layout.activity_rela_bar_code;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        binding = getContentBinding();
        initHeader("关联条码");
        init();
        initListener();
        if(DeviceUtil.isSENTER()){
            PermissionHandler.checkWriteSetting(this, granted -> {
                if (granted) {
                    LightUtil.setLight(RelaBarCodeActivity.this, 255);
                }
            });
        }
    }

    private void init() {
        binding.defaultImage1.setVisibility(View.VISIBLE);
        if(DeviceUtil.isSENTER()){
            HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_START_IN_STORAGE);
        }
        info = (OutStorageInfo) getIntent().getSerializableExtra(WMS_GARAGE_ID);
        binding.frameCode.setText(info.carUnique);
        binding.carAttribute.setText(info.carAttribute);
    }

    //监听设置
    private void initListener() {

        binding.barCode.addTextChangedListener(new TextWatcherUtil(0, null, binding.clearSweep));


        binding.clearSweep.setOnClickListener(v -> {
            binding.barCode.setText("");
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

        binding.sweep.setOnClickListener(v -> {
            doBarCodeScan(binding.barCode);
        });

        binding.sure.setOnClickListener(v -> {

            String code = binding.barCode.getText().toString().trim();
            if(StringUtil.isEmpty(code)){
                AlertToast.show("请输入条码");
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

            BindCodeRequest request = new BindCodeRequest();
            request.carId = info.carId;
            request.carStoreType = info.carStoreType;
            request.carTagId = code;
            request.associationPicture = picUrl;
            AppApplication.getServerAPI().bindCode(request)
                    .setOnDataError(emsg -> AlertToast.show(emsg))
                    .setOnDataGet(response->{
                         AlertToast.show("绑定条码成功");
                        RxBus.getDefault().post(new ListItemChangeEvent(ListItemChangeEvent.TYPE_INIT,info));
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

            }

        });
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
        super.onDestroy();
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
