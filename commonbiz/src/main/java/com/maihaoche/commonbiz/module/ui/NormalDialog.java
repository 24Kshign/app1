package com.maihaoche.commonbiz.module.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.maihaoche.commonbiz.R;
import com.maihaoche.commonbiz.databinding.DialogNormalBinding;

import io.reactivex.functions.Action;

/**
 * 作者：yang
 * 时间：17/6/10
 * 邮箱：yangyang@maihaoche.com
 */
public class NormalDialog extends Dialog {

    private String mTilte = "";//标题
    private String mContent = "";//内容
    private String mSingleConfirmStr = "";//单个确认按钮的文本
    private String mBtnCancelStr = "取消";//取消按钮的文本
    private String mBtnConfirmStr = "确定";//确定按钮的文本
    private boolean mShowLoading = false;

    private OnOkClickListener mOnOKClickListener = null;
    private OnCancelClickListener mOnCancelClickListener = null;

    private Action mTimeOutAction = null;
    private Action mOnCancelAction = null;


    private DialogNormalBinding binding;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static NormalDialog create(Context context, String content) {
        NormalDialog normalDialog = new NormalDialog(context);
        return normalDialog.setContent(content);
    }


    public NormalDialog setTilte(String tilte) {
        mTilte = tilte;
        return this;
    }

    public NormalDialog setContent(String content) {
        mContent = content;
        setContent();
        return this;
    }

    public NormalDialog setSingleConfirmStr(String singleConfirmStr) {
        mSingleConfirmStr = singleConfirmStr;
        setContent();
        return this;
    }

    public NormalDialog setBtnCancelStr(String btnCancelStr) {
        mBtnCancelStr = btnCancelStr;
        setContent();
        return this;
    }

    public NormalDialog setBtnConfirmStr(String btnConfirmStr) {
        mBtnConfirmStr = btnConfirmStr;
        setContent();
        return this;
    }

    public NormalDialog setOnOKClickListener(OnOkClickListener onOKClickListener) {
        mOnOKClickListener = onOKClickListener;
        setListener();
        return this;
    }

    public NormalDialog setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        mOnCancelClickListener = onCancelClickListener;
        setListener();
        return this;
    }


    public NormalDialog setCancelableOverride(boolean cancel) {
        super.setCancelable(cancel);
        return this;
    }


    public NormalDialog setCanceledOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public NormalDialog setIsCancelable(boolean cancel) {
        super.setCancelable(cancel);
        return this;
    }

    public NormalDialog setShowLoading() {
        mShowLoading = true;
        mBtnCancelStr = "";
        mBtnConfirmStr = "";
        return this;
    }

    public synchronized void show(int timeOutMs) {
        show(timeOutMs, null);
    }

    /**
     * 最多显示多长时间。并且在超时后有回调
     *
     * @param timeOutMs 最多显示的时间。单位 ms
     * @param onTimeOut 超时后的回调。
     */
    public synchronized void show(int timeOutMs, Action onTimeOut) {
        show();
        if (timeOutMs > 0) {
            mTimeOutAction = onTimeOut;
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(() -> {
                cancel();
                try {
                    if (mTimeOutAction != null) {
                        mTimeOutAction.run();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, timeOutMs);
        }
    }

    /**
     * 延迟一定时间后cancel
     */
    public synchronized void cancel(int delayMs) {
        cancel(delayMs, null);
    }

    /**
     * 延迟一定时间后cancel
     */
    public synchronized void cancel(int delayMs, Action onCancel) {
        mOnCancelAction = onCancel;
        if (delayMs <= 0) {
            cancel();
        } else {
            mHandler.postDelayed(this::cancel, delayMs);
        }
    }

    /**
     * 默认的style
     *
     * @param context
     */
    public NormalDialog(@NonNull Context context) {
        super(context, R.style.NormalDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_normal, null, false);
        setContentView(binding.getRoot());
        initDialog();
        setContent();
        setListener();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cleanUp();
    }



    @Override
    public void cancel() {
        try {
            if (mOnCancelAction != null) {
                mOnCancelAction.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cleanUp();
        if (isShowing()) {
            super.cancel();
        }
    }

    @Override
    public void show() {
        if(getContext() instanceof Activity){
            if(((Activity) getContext()).isFinishing()){
                return;
            }
        }
        super.show();
    }

    private void initDialog() {
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.CENTER);
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
    }

    private void setContent() {
        if (binding == null) {
            return;
        }
        checkVisible(binding.title, mTilte);
        checkVisible(binding.content, mContent);
        if (checkVisible(binding.singleConfirm, mSingleConfirmStr)) {
            binding.cancelBtn.setVisibility(View.GONE);
            binding.confirmBtn.setVisibility(View.GONE);
        } else {
            checkVisible(binding.cancelBtn, mBtnCancelStr);
            checkVisible(binding.confirmBtn, mBtnConfirmStr);
        }
        if (mShowLoading) {
            binding.progressContainer.setVisibility(View.VISIBLE);
            binding.progress.setClickable(true);
            binding.progress.setFocusable(true);
            binding.progress.requestFocus();
            RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setRepeatCount(Integer.MAX_VALUE);
            rotateAnimation.setDuration(800);
            rotateAnimation.setInterpolator(new LinearInterpolator());
//            binding.progress.setAnimation(rotateAnimation);
            binding.progress.startAnimation(rotateAnimation);
        } else {
            binding.progressContainer.setVisibility(View.GONE);
        }
    }

    private boolean checkVisible(TextView textView, String str) {
        if (!TextUtils.isEmpty(str)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(str);
            return true;
        } else {
            textView.setVisibility(View.GONE);
            return false;
        }
    }

    private void setListener() {
        if (binding == null) {
            return;
        }
        binding.singleConfirm.setOnClickListener(v -> {
            if (mOnOKClickListener != null) {
                mOnOKClickListener.onClick();
            }
            this.dismiss();
        });
        binding.confirmBtn.setOnClickListener(v -> {
            if (mOnOKClickListener != null) {
                mOnOKClickListener.onClick();
            }
            this.dismiss();
        });
        binding.cancelBtn.setOnClickListener(v -> {
            if (mOnCancelClickListener != null) {
                mOnCancelClickListener.onClick(this);
            }
            this.dismiss();
        });
    }


    /**
     * 清除资源
     */
    private void cleanUp() {
        mOnCancelAction = null;
        mTimeOutAction = null;
        if(mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }


    public interface OnOkClickListener {
        void onClick();
    }

    public interface OnCancelClickListener {
        void onClick(Dialog dialog);
    }
}
