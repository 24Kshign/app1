package com.maihaoche.volvo.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;

/**
 * Created by gujian
 * Time is 2017/8/5
 * Email is gujian@maihaoche.com
 */

public class MyAniView extends HorizontalScrollView {

    private int mHeight;
    private boolean isFirst = true;

    private ValueAnimator mOpenAni;
    private ValueAnimator mOCloseAni;

    private boolean isOpen;

    public MyAniView(Context context) {
        this(context,null);
    }

    public MyAniView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyAniView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        isOpen = false;
        mOpenAni = new ValueAnimator();
        mOpenAni.setDuration(200);
        mOpenAni.setInterpolator(new LinearInterpolator());

        mOCloseAni = new ValueAnimator();
        mOCloseAni.setDuration(200);
        mOCloseAni.setInterpolator(new LinearInterpolator());
    }

    private void startOpenAni(){
        isOpen = true;
        mOpenAni.setIntValues(0,mHeight);
        mOpenAni.addUpdateListener(animation-> {
            ViewGroup.LayoutParams params = getLayoutParams();
            int value = (int) animation.getAnimatedValue();
            params.height = value;
            setLayoutParams(params);
        });
        mOpenAni.start();
    }

    private void startCloseAni(){
        isOpen = false;
        mOCloseAni.setIntValues(mHeight,0);
        mOCloseAni.addUpdateListener(animation->{
            ViewGroup.LayoutParams params = getLayoutParams();
            int value = (int) animation.getAnimatedValue();
            params.height = value;
            setLayoutParams(params);
        });
        mOCloseAni.start();
    }

    public void startAnim(){
        if(isOpen){
            startCloseAni();
        }else{
            startOpenAni();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if(isFirst){
            isFirst = false;
            measureChild(getChildAt(0),widthMeasureSpec,heightMeasureSpec);
            mHeight = getChildAt(0).getMeasuredHeight();
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = 0;
            setLayoutParams(params);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
