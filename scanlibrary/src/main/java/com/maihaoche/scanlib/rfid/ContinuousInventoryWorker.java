package com.maihaoche.scanlib.rfid;

import com.maihaoche.base.log.LogUtil;
import com.maihaoche.scanlib.ScanApplication;
import com.maihaoche.scanlib.util.DataTransfer;
import com.senter.support.openapi.StUhf;
import com.senter.support.openapi.StUhf.InterrogatorModelDs.UmdFrequencyPoint;
import com.senter.support.openapi.StUhf.InterrogatorModelDs.UmdOnIso18k6cRealTimeInventory;
import com.senter.support.openapi.StUhf.InterrogatorModelDs.UmdRssi;
import com.senter.support.openapi.StUhf.UII;

public class ContinuousInventoryWorker {
    /**
     * go on inventoring after one inventory cycle finished.
     */
    private volatile boolean goOnInventoring = true;

    private ContinuousInventoryListener mListener = null;

    private boolean isInventoring = false;

    /**
     * @param listener must no be null
     */
    public ContinuousInventoryWorker(ContinuousInventoryListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        mListener = listener;
    }

    public void startInventory() {

        isInventoring = true;

        ScanApplication.uhfInterfaceAsModelD2().iso18k6cRealTimeInventory(1, new UmdOnIso18k6cRealTimeInventory() {

            @Override
            public void onFinishedWithError(StUhf.InterrogatorModelDs.UmdErrorCode error) {
                onFinishedOnce();
            }

            @Override
            public void onFinishedSuccessfully(Integer antennaId, int readRate, int totalRead) {
                onFinishedOnce();
            }

            private void onFinishedOnce() {
                if (goOnInventoring) {
                    startInventory();
                } else {
                    isInventoring = false;
                    goOnInventoring = true;
                    mListener.onFinished();
                }
            }

            @Override
            public void onTagInventory(UII uii, UmdFrequencyPoint frequencyPoint, Integer antennaId, UmdRssi rssi) {
                if(uii!=null){
                    String bc = DataTransfer.xGetString(uii.getBytes());
                    LogUtil.e("ContinuousInventoryWorker onTagInventory " + bc + " goOnInventoring =" + goOnInventoring + " isInventoring =" + isInventoring);
                    //start一次会有多次回调，当stop了之后过滤掉剩下的tag回调
                    if (mListener!=null && goOnInventoring) {
                        mListener.onTagInventory(uii, frequencyPoint, antennaId, rssi);
                    }
                }

            }
        });
    }

    public void stopInventory() {
        goOnInventoring = false;
    }

    public boolean isInventroing() {
        return isInventoring;
    }

    public interface ContinuousInventoryListener {
        /**
         * will be called on finished completely
         */
        void onFinished();

        void onTagInventory(UII uii, UmdFrequencyPoint frequencyPoint, Integer antennaId, UmdRssi rssi);
    }

    public void clearListener(){
        mListener = null;
    }

}