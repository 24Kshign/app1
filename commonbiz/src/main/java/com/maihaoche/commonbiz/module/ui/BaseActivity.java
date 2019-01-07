package com.maihaoche.commonbiz.module.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.maihaoche.commonbiz.service.permision.PermissionHandler;
import com.maihaoche.commonbiz.service.utils.HintUtil;
import com.maihaoche.commonbiz.service.utils.SizeUtil;

import io.reactivex.SingleTransformer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;


/**
 * 类简介：
 * 作者：  yang
 * 时间：  17/6/6
 * 邮箱：  yangyang@maihaoche.com
 */

public class BaseActivity<T extends ViewDataBinding> extends HandlerProviderActivity {

    protected final String TAG = BaseActivity.this.getClass().getSimpleName();
    private ViewDataBinding binding;

    /**
     * rxjava2新的接口。用来存放{@link Disposable} 队列(rxJava2中，subscribe()返回的不再是旧的Subscription，而是{@link Disposable})
     */
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    final protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        int layoutResId = getLayoutResId();
        if (layoutResId > 0) {
            binding = DataBindingUtil.setContentView(this, getLayoutResId());
        }
        afterCreate(savedInstanceState);
    }

    protected int getLayoutResId() {
        return 0;
    }

    protected void reLoad() {
    }

    /**
     * 获取布局binding
     *
     * @return
     */
    protected T getLayoutBinding() {
        if (binding == null) {
            throw new NullPointerException(TAG + ":该页面未设置正确的布局资源id：" + getLayoutResId());
        }
        return (T) binding;
    }


    protected void afterCreate(@Nullable Bundle savedInstanceState) {
    }

    protected boolean isPermissionHandler() {
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (isPermissionHandler()) {
            PermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNormalDialog != null && mNormalDialog.isShowing()) {
            mNormalDialog.cancel();
        }
        mCompositeDisposable.dispose();
        if (needStopAllPlayingMediaWhenQuit()) {
            HintUtil.getInstance().stopAllPlayingMedia();
        }
    }

    /**
     * 是否需要在退出activity的时候关闭所有当前正在播放的声音
     *
     * @return
     */
    protected boolean needStopAllPlayingMediaWhenQuit() {
        return true;
    }

    /**
     * 讲rxJava的异步任务添加到activity的队列中，会在destroy的时候注销。
     */
    public void pend(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    /**
     * 显示一个执行数据库查询的loading
     */
    public <UP> SingleTransformer<UP, UP> getIOLoadingTransformer() {
        return getIOLoadingTransformer("正在获取数据...", "获取数据出错，请重试.");
    }

    /**
     * 显示一个执行数据库查询的loading
     */
    protected <UP> SingleTransformer<UP, UP> getIOLoadingTransformer(String loadingHint, String errorHint) {
        return upstream -> upstream.doOnSubscribe((Disposable disposable) -> showLoading(loadingHint, () -> {
            AlertToast.show(errorHint);
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }))
                .doOnError(throwable -> cancelLoading(() -> AlertToast.show(errorHint)))
                .doOnSuccess((Consumer<UP>) up -> {
                    cancelLoading();
                });
    }


    /*================================activity中组装normaldialog，用于方便的显示loading===================*/

    private NormalDialog mNormalDialog;

    private static final int DEFAULT_MAX_TIME_OUT = 20000;//默认最长超时时间是8s
    private static final int DEFAULT_MAX_TIME_OUT_LONG = 400000;//默认最长超时时间是400s

    private static final int DEFAULT_DELAY_MS = 500;//默认延时500ms后，隐藏

    /**
     * 显示loading，不能取消。默认超时时间8s，超时后自动cancel
     */
    protected void showLoading(String content) {
        showLoading(content, DEFAULT_MAX_TIME_OUT, null);
    }

    /**
     * 显示loading，不能取消。超时后自动cancel
     */
    public void showLoading(String content, int timeOutMs) {
        showLoading(content, timeOutMs, null);
    }


    protected void showLongLoading(String content) {
        showLoading(content, DEFAULT_MAX_TIME_OUT_LONG);
    }

    protected void showLongLoading(String content, Action onTimeOut) {
        showLoading(content, DEFAULT_MAX_TIME_OUT_LONG, onTimeOut);
    }

    /**
     * 显示loading，不能取消。超时后自动cancel
     */
    public void showLoading(String content, Action onTimeOut) {
        showLoading(content, DEFAULT_MAX_TIME_OUT, onTimeOut);
    }

    /**
     * 显示loading，不能取消
     *
     * @param timeOutMs 超时时间
     * @param onTimeOut 超时后的动作（cancel是自动的，不需要关注）
     */
    public void showLoading(String content, int timeOutMs, Action onTimeOut) {
        if (mNormalDialog == null) {
            mNormalDialog = new NormalDialog(this);
        }
        mNormalDialog.setContent(content)
                .setCancelableOverride(false)
                .setCanceledOutside(false)
                .setShowLoading()
                .show(timeOutMs, onTimeOut);
    }

    /**
     * 取消loading的窗口，会自动个超时的handler注销掉
     */
    public synchronized void cancelLoading() {
        cancelLoading(null);
    }

    /**
     * 延迟一定时间后cancel
     */
    public synchronized void cancelLoading(Action onCancel) {
        if (mNormalDialog != null && mNormalDialog.isShowing()) {
            mNormalDialog.cancel(DEFAULT_DELAY_MS, onCancel);
        } else {
            try {
                onCancel.run();
            } catch (Exception e) {
                throw new RuntimeException("cancelLoading出错，详情:" + e.getMessage());
            }
        }
    }

    //点击外部隐藏键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            //点击键盘外使键盘消失
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
        }
//        Bugtags.onDispatchTouchEvent(this,ev);
//        屏蔽多次点击与平面图双击手势冲突
//        return EventUtil.isAllowDispatchEvent(ev) && super.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);

    }

    /**
     * 点击外部隐藏键盘必要的点击判断
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {

        if (v != null && (v instanceof EditText)) {
            int[] point = {0, 0};
            v.getLocationOnScreen(point);
            int left = point[0], top = point[1],
                    bottom = top + v.getHeight(), right = left + v.getWidth();
            //不判断右边界可以解决点击删除按钮宽度导致键盘隐藏，但会导致短的editText点击右边区域不会隐藏软键盘（可以接受）
            return !(event.getRawX() > left
                    && event.getRawY() > top
                    && event.getRawY() < right + SizeUtil.dip2px(15f)
                    && event.getRawY() < bottom);
        }
        //如果焦点不是EditText则忽略
        return false;
    }
}
