package com.maihaoche.scanlib;

import com.maihaoche.base.log.LogUtil;
import com.maihaoche.commonbiz.BizApplication;
import com.maihaoche.scanlib.rfid.ExceptionForToast;
import com.senter.support.openapi.StUhf;
import com.senter.support.openapi.StUhf.InterrogatorModel;

import static com.senter.support.openapi.StUhf.InterrogatorModel.InterrogatorModelD2;


/**
 * Created by brantyu on 17/6/7.
 */

public class ScanApplication extends BizApplication {
    public static final String TAG = ScanApplication.class.getSimpleName();

    private static ScanApplication sInstance;

    private static StUhf uhf;

    private static InterrogatorModel uhfInterfaceAsModel;
    private static boolean sIsUfiInited = false;

    @Override
    public void onCreate() {
        super.onCreate();
        getUhf(InterrogatorModelD2);
        sInstance = this;
    }

    public static ScanApplication getInstance() {
        return sInstance;
    }

    /**
     * create a uhf instance with the specified model if need
     */
    public static StUhf getUhf(InterrogatorModel interrogatorModel) {
        if (uhf == null) {
            uhf = StUhf.getUhfInstance(interrogatorModel);
            uhfInterfaceAsModel = interrogatorModel;
            LogUtil.e("ScanApplication " + uhf + " " + uhfInterfaceAsModel);
        }
        return uhf;
    }

    private static void assetUhfInstanceObtained() {
        if (uhf == null || uhfInterfaceAsModel == null) {
            throw new IllegalStateException();
        }
    }

    public static StUhf.InterrogatorModel uhfInterfaceAsModel() {
        if (uhf == null || uhfInterfaceAsModel == null) {
            throw new IllegalStateException();
        }
        return uhfInterfaceAsModel;
    }

    public static StUhf.InterrogatorModelDs.InterrogatorModelD2 uhfInterfaceAsModelD2() {
        assetUhfInstanceObtained();
        assert (uhfInterfaceAsModel() == InterrogatorModelD2);
        return uhf.getInterrogatorAs(StUhf.InterrogatorModelDs.InterrogatorModelD2.class);
    }

    public static boolean isUhfInit() {
        return sIsUfiInited;
    }

    public static boolean uhfInit() throws ExceptionForToast {
        LogUtil.i(TAG, "App.uhfInit() isInited = " + sIsUfiInited);
        if (sIsUfiInited) {
            LogUtil.e(TAG, "uhf is inited!");
            return false;
        }
        if (uhf == null) {
            throw new ExceptionForToast("no device found ,so can not init it ");
        }
        sIsUfiInited = uhf.init();
        LogUtil.i(TAG, "App.uhfInit() isInited = " + sIsUfiInited);
        if (sIsUfiInited == false) {
            throw new ExceptionForToast("can not init uhf module,please try again");
        }
        return sIsUfiInited;
    }

    public static void uhfUninit() {
        LogUtil.i(TAG, "App.uhfUninit() isInited = " + sIsUfiInited);
        if (!sIsUfiInited) {
            LogUtil.e(TAG, "uhf is uninited!");
            return;
        }
        if (uhf == null) {
            return;
        }
        LogUtil.i(TAG, "App.uhfUninit().uninit");
        uhf.uninit();
        sIsUfiInited = false;
    }

    public static void uhfClear() {
        uhf = null;
        uhfInterfaceAsModel = null;
    }

}
