package com.maihaoche.volvo.ui.photo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.maihaoche.volvo.R;
import com.maihaoche.commonbiz.service.image.ImageLoader;

import java.io.File;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by waitou on 16/11/14.
 * 图片查看的framgnet
 */

public class ImagePreviewFragment extends Fragment {

    private static final String EXTRA_URL = "url"; //url路径
    private static final String EXTRA_ISHTTP = "isHttpUrl"; //是否是http的路径还是 sd卡路径
    public static final String EXTRA_ISWITHWATER = "isWithWater";//是否需要带水印

    private boolean isWithWater;
    private boolean isHttpUrl;
    private String mUrl;

    PhotoView mImageView;

    ProgressBar mProgressBar;
    TextView mWaterView;

    private PhotoViewAttacher mAttacher;

    public static ImagePreviewFragment newInstance(String imageUrl, boolean isHttpUrl, boolean isWithWater) {
        final ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_ISWITHWATER, isWithWater);
        bundle.putBoolean(EXTRA_ISHTTP, isHttpUrl);
        bundle.putString(EXTRA_URL, imageUrl);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            isWithWater = arguments.getBoolean(EXTRA_ISWITHWATER);
            isHttpUrl = arguments.getBoolean(EXTRA_ISHTTP);
            mUrl = arguments.getString(EXTRA_URL);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_image_detail, container, false);
        mImageView = v.findViewById(R.id.image_detail_image);
        mProgressBar = v.findViewById(R.id.image_detail_loading);
        mWaterView = v.findViewById(R.id.image_detail_water);
        mAttacher = new PhotoViewAttacher(mImageView);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isHttpUrl) { //如果是网络图片
            mProgressBar.setVisibility(View.VISIBLE);
            ImageLoader.withCallback(getActivity(), mUrl, R.color.no_color, new ImageViewTarget<GlideDrawable>(mImageView) {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    super.onResourceReady(resource, glideAnimation);
                    mProgressBar.setVisibility(View.GONE);
//                  int vw = mImageView.getWidth();
//                  int vy = mImageView.getHeight();
                    int iw = resource.getIntrinsicWidth();
                    int ih = resource.getIntrinsicHeight();
                    ViewGroup.LayoutParams params = mImageView.getLayoutParams();
                    if (iw < 500 || ih < 500) {
                    } else {
                        mAttacher.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    }

                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;


//                    mAttacher.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    mImageView.setLayoutParams(params);
//                  Log.e("hehe","viewWidth:"+vw+"---viewHeight:"+vy+"----imageWidth"+iw+"-----imageHeight"+ih);

                }

                @Override
                protected void setResource(GlideDrawable resource) {
                    mImageView.setImageDrawable(resource);
                    if (isWithWater) {
                        mWaterView.setVisibility(View.VISIBLE);
                    } else {
                        mWaterView.setVisibility(View.GONE);
                    }
                }
            });

        } else { //否则显示本地图片

            ImageLoader.withCallback(getActivity(), new File(mUrl), R.color.no_color, new ImageViewTarget<GlideDrawable>(mImageView) {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    super.onResourceReady(resource, glideAnimation);
                    mAttacher.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }

                @Override
                protected void setResource(GlideDrawable resource) {
                    mImageView.setImageDrawable(resource);
                    if (isWithWater) {
                        mWaterView.setVisibility(View.VISIBLE);
                    } else {
                        mWaterView.setVisibility(View.GONE);
                    }
                }
            });
        }

        mAttacher.update();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mAttacher != null) {
//            mAttacher.cleanup();
//        }
    }
}
