package com.maihaoche.commonbiz.module.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.maihaoche.commonbiz.service.permision.PermissionHandler;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by manji
 * Date：2018/8/10 上午10:11
 * Desc：
 */
public abstract class BaseFragmentActivity extends FragmentActivity {

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (bindLayoutRes() <= 0) {
            throw new IllegalArgumentException("the xml should not be null");
        }
        setContentView(bindLayoutRes());
        initView();
    }

    protected abstract int bindLayoutRes();

    protected void initView() {

    }

    protected Activity thisActivity() {
        return this;
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseFragmentActivity.this.handleMessage(msg);
        }
    };

    protected Handler getHandler() {
        return mHandler;
    }

    protected void handleMessage(Message msg) {
        //可以被重写的方法,用来处理不同的msg。
    }

    /**
     * 讲rxJava的异步任务添加到activity的队列中，会在destroy的时候注销。
     */
    public void pend(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mCompositeDisposable) {
            mCompositeDisposable.dispose();
        }
        mHandler.removeCallbacksAndMessages(null);//子类必须要调用super.onDestroy()。activity,fragment释放的时候,移除mHandler中的所有消息队列。防止内存泄漏
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
