package com.maihaoche.volvo.ui;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseFragment;
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

public abstract class AbsScanBaseFragment<T extends ViewDataBinding> extends BaseFragment<T> {


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
            onInventoryStop();
        }

        @Override
        public void onTagInventory(StUhf.UII uii, StUhf.InterrogatorModelDs.UmdFrequencyPoint frequencyPoint, Integer antennaId, StUhf.InterrogatorModelDs.UmdRssi rssi) {
            String bc = DataTransfer.xGetString(uii.getBytes());
//            LogUtil.i("onTagInventory " + bc);
            //第一批来的RFID上打一个的条形码和实际RFID读出来的id相差了前面4位"2000",所以为了保持一致，我们在读到RFID的时候删除掉前4位
            //第二批来的RFID上打上的条形码和实际RFID读出来的id相差了前面4位"2800"，所以为了保持一致，我们在读到RFID的时候删除掉前4位
            if (!TextUtils.isEmpty(bc) && ((bc.startsWith("2000") && bc.length() == 20) || (bc.startsWith("2800") && bc.length() == 24) || (bc.startsWith("2400") && bc.length() == 20))) {
                bc = bc.substring(4);
            }
            onTagResult(bc);
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
            if (!mWorker.isInventroing()) {
                mWorker.startInventory();
            }
        }

    }

    /**
     * 停止扫描RFID
     */
    protected void stopInventory(boolean showHint) {
        if (mWorker != null) {
            if (mWorker.isInventroing()) {
                mWorker.stopInventory();
                if (showHint) {
                    showCloseDialog();
                }
            }
        } else {
            if (showHint) {
                AlertToast.show("请先关闭'超高频演示'应用");
            }
        }
    }


    @Override
    protected void onAfterCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onAfterCreateView(inflater, container, savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
//        if (!DeviceUtil.isSENTER()) {
//            AlertToast.show("非指定设备，部分功能无法正常使用");
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        if (mWorker != null) {
            mWorker.clearListener();
        }
        super.onDestroy();
    }


    /**
     * 展示等待对话框
     */
    private void showCloseDialog() {
        getActivity().runOnUiThread(() -> {
            if (mClosingDialog == null) {
                mClosingDialog = new NormalDialog(getActivity())
                        .setContent("关闭扫描中，请稍候...")
                        .setCanceledOutside(false)
                        .setBtnCancelStr("")
                        .setBtnConfirmStr("")
                ;
            }
            if (mClosingDialog.isShowing()) {
                return;
            }
            mClosingDialog.show(10000, () -> {
                onInventoryStop();
            });
        });

    }

    /**
     * 结束
     */
    private void onInventoryStop() {
        getActivity().runOnUiThread(() -> {
            if (mClosingDialog != null && mClosingDialog.isShowing()) {
                mClosingDialog.dismiss();
            }
        });
    }

    @Override
    public void onDetach() {
        stopInventory(false);
        super.onDetach();
    }
}
