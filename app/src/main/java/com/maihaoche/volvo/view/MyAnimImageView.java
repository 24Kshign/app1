package com.maihaoche.volvo.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by gujian
 * Time is 2017/8/7
 * Email is gujian@maihaoche.com
 */

public class MyAnimImageView extends android.support.v7.widget.AppCompatImageView {

    private boolean isRotation;
    private ObjectAnimator rotationAnim;
    private ObjectAnimator restoreAnim;


    public MyAnimImageView(Context context) {
        this(context,null);
    }

    public MyAnimImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyAnimImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        isRotation = false;
        rotationAnim = ObjectAnimator.ofFloat(this,"rotation",0f,180f);
        rotationAnim.setDuration(200);

        restoreAnim = ObjectAnimator.ofFloat(this,"rotation",180f,0f);
        restoreAnim.setDuration(200);
    }

    public void startAnim(){
        if(isRotation){
            startRstore();
        }else{
            startRotation();
        }
    }

    public boolean isRotation() {
        return isRotation;
    }

    private void startRotation(){
        rotationAnim.start();
        isRotation = true;
    }

    private void startRstore(){
        restoreAnim.start();
        isRotation = false;
    }

    public boolean isExpand(){

        return isRotation;
    }
}
