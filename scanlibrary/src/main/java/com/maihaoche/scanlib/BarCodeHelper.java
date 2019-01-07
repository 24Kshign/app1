package com.maihaoche.scanlib;

import android.os.Handler;
import android.os.Looper;

import com.maihaoche.scanlib.rfid.ExceptionForToast;
import com.senter.support.openapi.StBarcodeScanner;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.maihaoche.scanlib.ScanApplication.uhfUninit;

/**
 * Created by brantyu on 17/6/12.
 * 条形码扫描工具类
 */

public class BarCodeHelper {
    private static AtomicBoolean isScanning = new AtomicBoolean(false);

    public interface CallBack {
        void onResult(String result);
    }

    public static void scan(CallBack cb) {
        if (cb == null) return;
        //接触rfid模块，两个模块冲突。
        uhfUninit();
        new Thread() {
            public void run() {
                if (isScanning.compareAndSet(false, true) == false) {//at the same time only one thread can be allowed to scan
                    return;
                }

                try {
                    StBarcodeScanner scanner = StBarcodeScanner.getInstance();
                    if (scanner == null) {
                        return;
                    }
                    String rslt = scanner.scan();//scan ,if failed,null will be return

                    final String show = (rslt != null) ? rslt : "";

                    //update ui
                    mHander.post(() -> cb.onResult(show));
                    ScanApplication.uhfInit();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExceptionForToast exceptionForToast) {
                    exceptionForToast.printStackTrace();
                } finally {
                    isScanning.set(false);
                }
            }

        }.start();
    }

    private static Handler mHander = new Handler(Looper.getMainLooper());
}
