package com.maihaoche.volvo.ui;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.text.TextUtils;

import com.maihaoche.base.log.LogUtil;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.module.ui.NormalDialog;
import com.maihaoche.commonbiz.service.utils.DeviceUtil;
import com.maihaoche.scanlib.rfid.ContinuousInventoryWorker;
import com.maihaoche.scanlib.util.DataTransfer;
import com.maihaoche.volvo.AppApplication;
import com.senter.support.openapi.StUhf;

/**
 * Created by brantyu on 17/6/13.
 * 扫描基类，用来处理扫描结果后的声音和震动
 */

public abstract class AbsScanBaseActivity<T extends ViewDataBinding> extends HeaderProviderActivity<T> {


    private boolean mFinishOnStop = false;
    private NormalDialog mClosingDialog = null;

    /**
     * 是否使用条形码扫描,由于条形码扫描和RFID扫描相互不兼容，所以需要分开注册使用
     *
     * @return
     */
    protected boolean useBarcodeScan() {
        return false;
    }

    /**
     * RFID 的扫描类
     */
    private ContinuousInventoryWorker mWorker = new ContinuousInventoryWorker(new ContinuousInventoryWorker.ContinuousInventoryListener() {
        @Override
        public void onFinished() {
            if (mFinishOnStop) {
                finishAndDismiss();
            }
        }

        @Override
        public void onTagInventory(StUhf.UII uii, StUhf.InterrogatorModelDs.UmdFrequencyPoint frequencyPoint, Integer antennaId, StUhf.InterrogatorModelDs.UmdRssi rssi) {
            if(uii!=null){
                String bc = DataTransfer.xGetString(uii.getBytes());
                LogUtil.i("onTagInventory " + bc);
                //第一批来的RFID上打一个的条形码和实际RFID读出来的id相差了前面4位"2000",所以为了保持一致，我们在读到RFID的时候删除掉前4位
                //第二批来的RFID上打上的条形码和实际RFID读出来的id相差了前面4位"2800"，所以为了保持一致，我们在读到RFID的时候删除掉前4位
                //第三批来的RFID上打上的条形码和实际RFID读出来的id相差了前面4位"2400"，所以为了保持一致，我们在读到RFID的时候删除掉前4位
                if (!TextUtils.isEmpty(bc) && ((bc.startsWith("2000") && bc.length() == 20) || (bc.startsWith("2800") && bc.length() == 24) || (bc.startsWith("2400") && bc.length() == 20))) {
                    bc = bc.substring(4);
                }
                onTagResult(bc);
            }

        }
    });

    /**
     * 扫描到RFID之后的回调方法
     *
     * @param rfid
     */
    protected void onTagResult(String rfid) {

    }

    /**
     * 开始扫描RFID
     */
    protected void startInventory() {
        if(DeviceUtil.isSENTER()){
            if(!AppApplication.isUhfInit()){
                AlertToast.show("找车功能初始化失败，请重启设备");
                return;
            }
            if (mWorker != null) {
                if (!mWorker.isInventroing()) {
                    mWorker.startInventory();
                }
            } else {
                AlertToast.show("设备初始化失败，无法使用");
            }
        }

    }

    /**
     * 判断是否正在盘点
     *
     * @return
     */
    public boolean isInventroing() {
        return mWorker.isInventroing();
    }

    /**
     * 停止扫描RFID
     */
    protected void stopInventory() {
        if (mWorker.isInventroing()) {
            mWorker.stopInventory();
            //LogUtil.v("record", "扫描停止");
            //LogFileUtil.writToFile2("扫描停止");
        }
    }


    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!AppApplication.isUhfInit() && DeviceUtil.isSENTER()){
            AlertToast.show("找车功能初始化失败，请重启设备");
        }
    }


    @Override
    protected void onDestroy() {

        if (mWorker != null) {
            mWorker.clearListener();
        }
        super.onDestroy();
    }

    protected void back() {
        if (mWorker.isInventroing()) {
            mFinishOnStop = true;
            showCloseDialog();
            mWorker.stopInventory();
        } else {
            finishAndDismiss();
        }
    }

    /**
     * 展示等待对话框
     */
    private void showCloseDialog() {
        if (mClosingDialog == null) {
            mClosingDialog = new NormalDialog(this)
                    .setContent("关闭扫描中，请稍候...")
                    .setCanceledOutside(false)
                    .setBtnCancelStr("")
                    .setBtnConfirmStr("");
        }
        if (mClosingDialog.isShowing()) {
            return;
        }
        mClosingDialog.show(10000, () -> {
            finishAndDismiss();
        });
    }

    /**
     * 结束
     */
    private void finishAndDismiss() {
        runOnUiThread(() -> {
            if (mClosingDialog != null && mClosingDialog.isShowing()) {
                mClosingDialog.dismiss();
            }
            if (!isFinishing()) {
                finish();
            }
        });
    }
}
