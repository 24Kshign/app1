package com.maihaoche.commonbiz.module.ui;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

/**
 * 提供handler的activity。由于java不能实现多继承。所以只好写两个HandlerProvider,分别用于activiyt和fragment。用其他方式代码太散,不利于阅读。
 * * 逻辑发生修改时,请同时修改{@link HandlerProviderFragment},或者用其他方式统一逻辑。
 * 作者：yang
 * 时间：17/6/6
 * 邮箱：yangyang@maihaoche.com
 */

@SuppressLint("Registered")
public class HandlerProviderActivity extends AppCompatActivity {
    /**
     * activity, fragment 的基类里面维护的Handler,避免不正确的使用造成的内存泄漏。
     * Note: 这里不使用static类型的handler这里不使用static类型的handler,是为了多个每个Activity都拥有一个Handler实例,这样可以让多个activity各自同时处理自己的消息
     * (比如一个存活的后台的activity在用handler做一个延时任务,此时,前台的activity也需要用handler,这种情况下,static的Handler就会产生冲突)。
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HandlerProviderActivity.this.handleMessage(msg);
        }
    };

    protected Handler getHandler() {
        return mHandler;
    }

    protected void handleMessage(Message msg) {
        //可以被重写的方法,用来处理不同的msg。
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);//子类必须要调用super.onDestroy()。activity,fragment释放的时候,移除mHandler中的所有消息队列。防止内存泄漏
    }
}
