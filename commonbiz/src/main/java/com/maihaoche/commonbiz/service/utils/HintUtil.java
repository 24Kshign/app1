package com.maihaoche.commonbiz.service.utils;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Vibrator;

import com.maihaoche.commonbiz.BizApplication;
import com.maihaoche.commonbiz.R;

import java.util.HashMap;
import java.util.Map;

import static com.maihaoche.commonbiz.service.utils.HintUtil.TYPE.BARCODE_SCAN_SUCCEED_WITH_VIBRATOR;
import static com.maihaoche.commonbiz.service.utils.HintUtil.TYPE.HINT_BARCODE_SCAN;
import static com.maihaoche.commonbiz.service.utils.HintUtil.TYPE.HINT_CAR_IS_NEARBY_WITH_VIBRATOR;
import static com.maihaoche.commonbiz.service.utils.HintUtil.TYPE.HINT_EXINFO_SAVED;
import static com.maihaoche.commonbiz.service.utils.HintUtil.TYPE.HINT_FIND_CAR;
import static com.maihaoche.commonbiz.service.utils.HintUtil.TYPE.HINT_FLASH_TURN_ON;
import static com.maihaoche.commonbiz.service.utils.HintUtil.TYPE.HINT_IN_STORAGE_SUCCEED;
import static com.maihaoche.commonbiz.service.utils.HintUtil.TYPE.HINT_PAUSE_INVENTORY;
import static com.maihaoche.commonbiz.service.utils.HintUtil.TYPE.HINT_START_INVENTORY;
import static com.maihaoche.commonbiz.service.utils.HintUtil.TYPE.HINT_START_IN_STORAGE;
import static com.maihaoche.commonbiz.service.utils.HintUtil.TYPE.HINT_STOP_INVENTORY;
import static com.maihaoche.commonbiz.service.utils.HintUtil.TYPE.HINT_VIN_SCAN;
import static com.maihaoche.commonbiz.service.utils.HintUtil.TYPE.INVENTORY_SCAN;

/**
 * Created by brantyu on 17/6/17.
 * 语音提示和震动提示的帮助类
 */

public class HintUtil {

    private Map<TYPE, Accompaniment> mAccompanimentMap = new HashMap<>();

    public enum TYPE {
        /**
         * 条码扫描成功并震动
         */
        BARCODE_SCAN_SUCCEED_WITH_VIBRATOR,
        /**
         * 盘库时盘到一台车的声音
         */
        INVENTORY_SCAN,
        /**
         * 开始盘库提示音
         */
        HINT_START_INVENTORY,
        /**
         * 暂停盘库提示音
         */
        HINT_PAUSE_INVENTORY,
        /**
         * 停止盘库提示音
         */
        HINT_STOP_INVENTORY,
        /**
         * 入库成功提示音
         */
        HINT_IN_STORAGE_SUCCEED,
        /**
         * 找车时，找到车的提示音
         */
        HINT_CAR_IS_NEARBY_WITH_VIBRATOR,
        /**
         * 条码如何扫描的提示音
         */
        HINT_BARCODE_SCAN,
        /**
         * VIN码如何扫描的提示音
         */
        HINT_VIN_SCAN,
        /**
         * 如何开始入库的提示音
         */
        HINT_START_IN_STORAGE,
        /**
         * 如何进行找车的提示音
         */
        HINT_FIND_CAR,
        /**
         * 验车信息保存成功的提示音
         */
        HINT_EXINFO_SAVED,
        /**
         * 闪光灯打开时，如何避开光斑进行夜间识别的提示音
         */
        HINT_FLASH_TURN_ON
    }

    private Context mContext;

    private static HintUtil sInstance = new HintUtil();

    private Vibrator mVibrator;
    private Handler mAccompanimentsHandler;
    private Accompaniment mLastAccmpaniment;

    public static final HintUtil getInstance() {
        return sInstance;
    }

    private HintUtil() {
    }

    public void init() {
        HandlerThread htHandlerThread = new HandlerThread("");
        htHandlerThread.start();
        mContext = BizApplication.getApplication();
        mAccompanimentsHandler = new Handler(htHandlerThread.getLooper());
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        loadAccompaniment();
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mAccompanimentsHandler != null) {
            mAccompanimentsHandler.getLooper().quit();
            mAccompanimentsHandler = null;
        }

        for (Accompaniment accompaniment : mAccompanimentMap.values()) {
            if (accompaniment != null) {
                accompaniment.release();
            }
        }
        mAccompanimentMap.clear();
        mContext = null;
    }

    /**
     * 加载所有音频对象
     */
    private void loadAccompaniment() {
        mAccompanimentMap.put(BARCODE_SCAN_SUCCEED_WITH_VIBRATOR, Accompaniment.newInstanceOfResource(mContext, R.raw.barcodeaudiosucced));
        mAccompanimentMap.put(INVENTORY_SCAN, Accompaniment.newInstanceOfResource(mContext, R.raw.tag_inventoried));
        mAccompanimentMap.put(HINT_START_INVENTORY, Accompaniment.newInstanceOfResource(mContext, R.raw.start_inventory));
        mAccompanimentMap.put(HINT_PAUSE_INVENTORY, Accompaniment.newInstanceOfResource(mContext, R.raw.pause_inventory));
        mAccompanimentMap.put(HINT_STOP_INVENTORY, Accompaniment.newInstanceOfResource(mContext, R.raw.stop_inventory));
        mAccompanimentMap.put(HINT_IN_STORAGE_SUCCEED, Accompaniment.newInstanceOfResource(mContext, R.raw.instorage_succed));
        mAccompanimentMap.put(HINT_CAR_IS_NEARBY_WITH_VIBRATOR, Accompaniment.newInstanceOfResource(mContext, R.raw.car_is_nearby));
        mAccompanimentMap.put(HINT_BARCODE_SCAN, Accompaniment.newInstanceOfResource(mContext, R.raw.barcode_scan_hint));
        mAccompanimentMap.put(HINT_VIN_SCAN, Accompaniment.newInstanceOfResource(mContext, R.raw.vin_scan_hint));
        mAccompanimentMap.put(HINT_START_IN_STORAGE, Accompaniment.newInstanceOfResource(mContext, R.raw.start_instorage_hint));
        mAccompanimentMap.put(HINT_FIND_CAR, Accompaniment.newInstanceOfResource(mContext, R.raw.find_car_hint));
        mAccompanimentMap.put(HINT_EXINFO_SAVED, Accompaniment.newInstanceOfResource(mContext, R.raw.exinfo_saved));
        mAccompanimentMap.put(HINT_FLASH_TURN_ON, Accompaniment.newInstanceOfResource(mContext, R.raw.flash_turn_on_hint));
    }


    /**
     * 播放提示音或者震动
     *
     * @param type
     */
    public void playAudioOrVibrator(TYPE type) {
        if(!DeviceUtil.isSENTER() && type == TYPE.HINT_START_IN_STORAGE){
            return;
        }
        Accompaniment accompaniment = mAccompanimentMap.get(type);
        if (accompaniment != null && mAccompanimentsHandler != null) {
            mAccompanimentsHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (type != INVENTORY_SCAN && type != BARCODE_SCAN_SUCCEED_WITH_VIBRATOR) {
                        //当播放扫到RFID和条码的时候不需要暂停当前播放
                        stopAllPlayingMedia();
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (accompaniment != null) {
                        accompaniment.start();
                    }
                    if (BARCODE_SCAN_SUCCEED_WITH_VIBRATOR == type || HINT_CAR_IS_NEARBY_WITH_VIBRATOR == type) {
                        if(mVibrator!=null){
                            mVibrator.vibrate(200);
                        }

                    }
                    //快速退出mainAty的时候，此变量为null
                    if(mAccompanimentsHandler!=null){
                        mAccompanimentsHandler.removeCallbacks(this);
                    }

                }
            });
        }
    }

    /**
     * 停止所有播放
     */
    public void stopAllPlayingMedia() {
        for (Accompaniment accompaniment : mAccompanimentMap.values()) {
            if (accompaniment!=null && accompaniment.isPlaying()) {
                accompaniment.stop();
                accompaniment.prepare();
            }
        }
    }
}
