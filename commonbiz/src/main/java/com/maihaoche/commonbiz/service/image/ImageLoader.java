package com.maihaoche.commonbiz.service.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.maihaoche.commonbiz.R;

import java.io.File;

/**
 * Created by gujian
 * Time is 2017/6/8
 * Email is gujian@maihaoche.com
 */

public class ImageLoader {


    /**
     * 默认通用图片加载方法
     */
    public static void with(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.color.white)
                .crossFade().into(imageView);
    }

    /**
     * 默认通用图片加载方法
     */
    public static void with(@NonNull Context context, int resId, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(resId)
                .placeholder(R.color.white)
                .crossFade().into(imageView);
    }

    /**
     * 默认通用图片加载方法
     */
    public static void with(@NonNull Context context, @NonNull File file, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(file)
                .placeholder(R.color.white)
                .crossFade().into(imageView);
    }


    /**
     * 圆角加载图片,需要ImageView为正方形,centerCrop
     */
    public static void withRound(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .transform(new GlideRoundCenterCropTransform(context))
                .into(imageView);
    }

    /**
     * 圆角加载图片,需要ImageView为正方形,centerCrop
     */
    public static void withRound(@NonNull Context context, @DrawableRes int res, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(res)
                .transform(new GlideRoundCenterCropTransform(context))
                .into(imageView);
    }

    /**
     * 圆角加载图片,需要ImageView为正方形,centerCrop
     */
    public static void withRound(@NonNull Context context, @NonNull File file, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(file)
                .transform(new GlideRoundCenterCropTransform(context))
                .into(imageView);
    }

    /**
     * 圆角加载图片,需要ImageView为正方形,centerCrop,自定义圆角大小
     */
    public static void withRound(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, int radio) {
        Glide.with(context)
                .load(url)
                .transform(new GlideRoundCenterCropTransform(context, radio))
                .into(imageView);
    }

    /**
     * 传入自定义placeholder的加载图片方法
     */
    public static void withRoundAndHolder(@NonNull Context context, @NonNull String url, int resId, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(resId)
                .transform(new GlideRoundCenterCropTransform(context))
                .crossFade().into(imageView);
    }

    /**
     * 传入自定义placeholder的加载图片方法
     */
    public static void withRoundAndHolder(@NonNull Context context, @NonNull File url, int resId, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(resId)
                .transform(new GlideRoundCenterCropTransform(context))
                .crossFade().into(imageView);
    }

    /**
     * 圆形加载图片
     */
    public static void withCircle(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .transform(new GlideCircleTransform(context))
                .into(imageView);
    }

    /**
     * 给圆角图片控件来加载图片的方法,避免drawable转换crash
     */
    public static void withSimple(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.iv_car_photo_default)
                .dontAnimate()
                .into(imageView);
    }

    /**
     * 给圆角图片控件来加载图片的方法,避免drawable转换crash,不能添加动效
     */
    public static void withSimple(@NonNull Context context, @NonNull File file, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(file)
                .dontAnimate()
                .into(imageView);
    }

    /**
     * 给圆角图片控件来加载图片的方法,避免drawable转换crash
     * 滑动列表加载
     */
    public static void withListOf(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .dontAnimate()
                .into(imageView);
    }

    /**
     * 设置缓存策略
     */
    public static void withDiskSource(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    /**
     * 设置缓存策略
     */
    public static void withDiskSource(@NonNull Context context, @NonNull String url, int resId, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(resId)
                .into(imageView);
    }

    /**
     * 传入自定义placeholder的加载图片方法
     */
    public static void withHolder(@NonNull Context context, @NonNull String url, int resId, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(resId)
                .crossFade().into(imageView);
    }

    /**
     * 缓存到本地的加载图片方法,用于固定图标的加载
     */
    public static void withDisk(@NonNull Context context, @NonNull String url, int resId, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(resId)
                .into(imageView);
    }

    /**
     * 使用CenterCrop的图片加载
     */
    public static void withCrop(@NonNull Context context, @NonNull String url, int resId, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .centerCrop()
                .placeholder(resId)
                .into(imageView);
    }

    /**
     * 使用FitCenter的图片加载
     */
    public static void withFitCenter(@NonNull Context context, @NonNull String url, int resId, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .fitCenter()
                .placeholder(resId)
                .into(imageView);
    }

    /**
     * 使用CenterCrop的图片加载,自定义圆角大小
     */
    public static void withCrop(@NonNull Context context, @NonNull String url, int resId, @NonNull ImageView imageView, int radio) {
        Glide.with(context)
                .load(url)
                .centerCrop()
                .transform(new GlideRoundCenterCropTransform(context, radio))
                .placeholder(resId)
                .into(imageView);
    }

    /**
     * 使用CenterCrop的图片加载自定义圆角大小
     */
    public static void withCrop(@NonNull Context context, @NonNull File file, int resId, @NonNull ImageView imageView, int radio) {
        Glide.with(context)
                .load(file)
                .centerCrop()
                .transform(new GlideRoundCenterCropTransform(context, radio))
                .placeholder(resId)
                .into(imageView);
    }


    /**
     * 使用CenterCrop的图片加载
     */
    public static void withCrop(@NonNull Context context, @NonNull File file, int resId, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(file)
                .centerCrop()
                .placeholder(resId)
                .into(imageView);
    }

    /**
     * 带回调的图片加载
     */
    public static void withCallback(@NonNull Context context, @NonNull String url, SimpleTarget<Bitmap> target) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .placeholder(R.color.white)
                .into(target);
    }

    /**
     * 带回调的图片加载
     */
    public static void withCallback(@NonNull Context context, @NonNull String url, BitmapImageViewTarget target) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .placeholder(R.color.white)
                .into(target);
    }

    /**
     * 带回调的图片加载
     */
    public static void withCallback(@NonNull Context context, @NonNull String url,
                                    int holderResId, ImageViewTarget<GlideDrawable> target) {
        Glide.with(context)
                .load(url)
                .placeholder(holderResId)
                .into(target);
    }

    /**
     * 本地图片加载方法
     */
    public static void withCallback(@NonNull Context context, @NonNull File file, int holderResId, ImageViewTarget<GlideDrawable> target) {
        Glide.with(context)
                .load(file)
                .placeholder(holderResId)
                .into(target);
    }

    /**
     * 指定宽高的图片加载
     */
    public static void with(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, int width, int height) {
        Glide.with(context)
                .load(url)
                .placeholder(R.color.black)
                .override(width, height)
                .into(imageView);
    }

    /**
     * 圆角加载图片
     */
    public static void withRoundIv(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, int w, int h) {
        Glide.with(context)
                .load(url)
                .transform(new GlideRoundTransform(context, 2, w, h))
                .into(imageView);
    }


}
