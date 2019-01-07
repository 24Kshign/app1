package com.maihaoche.commonbiz.service.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

/**
 * 作者：yang
 * 时间：17/6/9
 * 邮箱：yangyang@maihaoche.com
 */
public class ResourceUtils extends ApplicationProvider{
    @ColorInt
    public static int getColor(@ColorRes int colorResId) {
        return getApplication().getResources().getColor(colorResId);
    }

    public static Drawable getDrawable(@DrawableRes int drawableResId) {
        return getApplication().getResources().getDrawable(drawableResId);
    }
}
