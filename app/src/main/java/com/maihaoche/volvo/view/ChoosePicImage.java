package com.maihaoche.volvo.view;

import android.content.Context;
import android.media.ExifInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.service.image.ImageLoader;
import com.maihaoche.commonbiz.service.image.ImageUtil;
import com.maihaoche.commonbiz.service.utils.FileUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.ui.photo.ChooseImageItem;
import com.maihaoche.volvo.util.CompressUtil;
import com.maihaoche.volvo.util.QiNiuUtil;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.reactivestreams.Subscription;
import java.io.File;
import java.io.IOException;
import java.util.UUID;


/**
 * Created by wangshengru on 16/1/25.
 * 用于上传一个列表形式的图片控件,这个跟ChoosePicImage2的区别在于差号,这个差号是在列表的item里面的,
 * 点击后要刷新列表,写在这里面会有问题,什么问题忘记了= =,
 */
public class ChoosePicImage extends FrameLayout {

    private static final String TAG = "ChoosePicImage";

    public static final String QINIU_IMG_HEADER = "https://img.maihaoche.com/";

    ImageView mPhotoImage;
    View mShadowView;//阴影，所有异常状态都需要显示
    ProgressBar mProgressView;//上传的监听
    View mWaterView;//水印
    View mErrorView;//上传错误的view
    private boolean isCompress = false;

    boolean isCancel = false;
    public ChooseImageItem mChooseImageItem;
    private boolean isWithWater = false;//是否需要带水印
    private Subscription mSubscription;

    private boolean haveUpload = false;

    public boolean isCompress() {
        return isCompress;
    }

    public void setCompress(boolean compress) {
        isCompress = compress;
    }

    public ChoosePicImage(Context context) {
        this(context, null);
    }

    public ChoosePicImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChoosePicImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.view_choose_pic_image, this);
        mPhotoImage = (ImageView) view.findViewById(R.id.iv_photo);
        mShadowView = view.findViewById(R.id.view_shadow);//阴影，所有异常状态都需要显示
        mProgressView = (ProgressBar) view.findViewById(R.id.view_progress);//上传的监听
        mWaterView = view.findViewById(R.id.view_water);//水印
        mErrorView = view.findViewById(R.id.view_error);//上传错误的view
        mErrorView.setOnClickListener(v -> reUpload());
    }

    public void reUpload() {
        mErrorView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);
        mProgressView.setProgress(0);
        //getTokenAndUpload();
    }

    public void setIsWithWater(boolean isWithWater) {
        this.isWithWater = isWithWater;
    }

    public void setUrl(Context context, ChooseImageItem chooseImageItem) {

        mChooseImageItem = chooseImageItem;

        if (mChooseImageItem.isFromServer()) {
            //服务器上的图片直接显示
            mChooseImageItem.mServerUrl = mChooseImageItem.mOriPath;
            mProgressView.setVisibility(GONE);
            mWaterView.setVisibility(isWithWater ? VISIBLE : GONE);
            if (mChooseImageItem.useCrop) {
                ImageLoader.withCrop(context,
                        ImageUtil.getThumbUrl(mChooseImageItem.mOriPath),
//                    R.color.grey_F3F4F5,
                        R.drawable.iv_car_photo_default,
                        mPhotoImage);
            } else {
                ImageLoader.withFitCenter(context,
                        ImageUtil.getThumbUrl(mChooseImageItem.mOriPath),
//                    R.color.grey_F3F4F5,
                        R.drawable.iv_car_photo_default,
                        mPhotoImage);
            }

        } else {
            //本地的图片
            //先查询一下是否上传过，是，直接返回保存的url，否：上传
            if (mChooseImageItem.useCrop) {
                ImageLoader.withCrop(context,
                        mChooseImageItem.mOriPath,
                        R.color.grey_F3F4F5,
                        mPhotoImage);
            } else {
                ImageLoader.withFitCenter(context,
                        mChooseImageItem.mOriPath,
                        R.color.grey_F3F4F5,
                        mPhotoImage);
            }


            if (!mChooseImageItem.mIsUploading && !mChooseImageItem.haveServerUrl()) {
                mChooseImageItem.mIsUploading = true;
                mShadowView.setVisibility(VISIBLE);
                mProgressView.setVisibility(VISIBLE);
                mProgressView.setProgress(0);
                getTokenAndUpload();
            }
        }
    }

    public void setUrl2(Context context, ChooseImageItem chooseImageItem) {

        mChooseImageItem = chooseImageItem;

        if (mChooseImageItem.isFromServer()) {
            Log.e(TAG, "服务器图片，直接显示：" + mChooseImageItem.mServerUrl);
            //服务器上的图片直接显示
            mChooseImageItem.mServerUrl = mChooseImageItem.mOriPath;
            mProgressView.setVisibility(GONE);
            mWaterView.setVisibility(isWithWater ? VISIBLE : GONE);
            if (mChooseImageItem.useCrop) {
                ImageLoader.withCrop(context,
                        mChooseImageItem.mOriPath,
//                    R.color.grey_F3F4F5,
                        R.drawable.iv_car_photo_default,
                        mPhotoImage);
            } else {
                ImageLoader.withFitCenter(context,
                        mChooseImageItem.mOriPath,
//                    R.color.grey_F3F4F5,
                        R.drawable.iv_car_photo_default,
                        mPhotoImage);
            }

        } else {
            //本地的图片
            //先查询一下是否上传过，是，直接返回保存的url，否：上传
            Log.e(TAG, "本地图片，：" + mChooseImageItem.mServerUrl);
            if (mChooseImageItem.useCrop) {
                ImageLoader.withCrop(context,
                        mChooseImageItem.mOriPath,
                        R.color.grey_F3F4F5,
                        mPhotoImage);
            } else {
                ImageLoader.withFitCenter(context,
                        mChooseImageItem.mOriPath,
                        R.color.grey_F3F4F5,
                        mPhotoImage);
            }


            if (!mChooseImageItem.mIsUploading && !mChooseImageItem.haveServerUrl()) {
                mChooseImageItem.mIsUploading = true;
                mShadowView.setVisibility(VISIBLE);
                mProgressView.setVisibility(VISIBLE);
                mProgressView.setProgress(0);
                getTokenAndUpload();
            }
        }
    }


    private void getTokenAndUpload() {
        QiNiuUtil.getToken((BaseActivity) getContext(), token -> {
            if (isCompress) {
                uploadImgCompress(mChooseImageItem.mOriPath, token);
            } else {

                uploadImg(mChooseImageItem.mOriPath, token);
            }

        }, string -> {
            mErrorView.setVisibility(View.VISIBLE);
            mProgressView.setVisibility(GONE);
        });
    }

    public void showAdd() {
        mShadowView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.GONE);
        mWaterView.setVisibility(View.GONE);
        mErrorView.setVisibility(GONE);
        mPhotoImage.setImageResource(R.drawable.content_icon_photo);
    }

    private void uploadImgCompress(final String url, String token) {
        if (token == null) {
            return;
        }
        String key = UUID.randomUUID().toString() + ".jpg";
        UploadManager uploadManager = new UploadManager();

        byte[] bytes = CompressUtil.compressWHSrcGetByte(url);


        ExifInterface exifInterface;
        String orValue = "";
        try {
            exifInterface = new ExifInterface(url);
            orValue = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
        } catch (IOException e) {
            e.printStackTrace();
            orValue = "6";
        }

        FileUtil.save(getContext(), bytes, key);
        File file = new File(FileUtil.getExternalFilePath(getContext(), key));
        String filePath = "";
        filePath = file.getAbsolutePath();
        try {
            exifInterface = new ExifInterface(filePath);
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, orValue);
            exifInterface.saveAttributes();
        } catch (IOException e) {
            FileUtil.deleteFile(filePath);
            e.printStackTrace();
        }
        if (file.exists()) {
            String finalFilePath = filePath;
            uploadManager.put(
                    file,//文件地址
                    key,
                    token,
                    (key1, info, response) -> {
                        FileUtil.deleteFile(finalFilePath);
                        mChooseImageItem.mIsUploading = false;
                        if (!isCancel) {
                            if (info.isOK()) {
                                haveUpload = true;
                                mProgressView.setProgress(100);
                                mProgressView.setVisibility(GONE);
                                mShadowView.setVisibility(GONE);
                                mWaterView.setVisibility(isWithWater ? VISIBLE : GONE);
                                String uploadUrl = QINIU_IMG_HEADER + key1;
                                //将图片的上传服务器地址保存
                                Log.e(TAG, "服务器图片，上传成功：" + uploadUrl);
                                mChooseImageItem.mServerUrl = uploadUrl;
                                //将上传过的图片插入数据库
                            } else {
                                mProgressView.setVisibility(GONE);
                                mErrorView.setVisibility(VISIBLE);
                            }
                        }
                    },
                    new UploadOptions(null, null, false, (key1, percent) -> {
                        int p = Double.valueOf(percent * 100).intValue();
                        mProgressView.setProgress(p);
                    }, () -> {
                        FileUtil.deleteFile(finalFilePath);
                        if (isCancel) {
                            Log.i("wsr", "取消上传");
                        }
                        return isCancel;
                    }));
        }
    }

    private void uploadImg(final String url, String token) {
        if (token == null) {
            return;
        }
        String key = UUID.randomUUID().toString() + ".jpg";
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(
                url,//文件地址
                key,
                token,
                (key1, info, response) -> {
                    mChooseImageItem.mIsUploading = false;
                    if (!isCancel) {
                        if (info.isOK()) {
                            haveUpload = true;
                            mProgressView.setProgress(100);
                            mProgressView.setVisibility(GONE);
                            mShadowView.setVisibility(GONE);
                            mWaterView.setVisibility(isWithWater ? VISIBLE : GONE);
                            String uploadUrl = QINIU_IMG_HEADER + key1;
                            //将图片的上传服务器地址保存
                            Log.e(TAG, "服务器图片，上传成功：" + uploadUrl);
                            mChooseImageItem.mServerUrl = uploadUrl;
                            //将上传过的图片插入数据库
                        } else {
                            mProgressView.setVisibility(GONE);
                            mErrorView.setVisibility(VISIBLE);
                        }
                    }
                },
                new UploadOptions(null, null, false, (key1, percent) -> {
                    int p = Double.valueOf(percent * 100).intValue();
                    mProgressView.setProgress(p);
                }, () -> {
                    if (isCancel) {
                        Log.i("wsr", "取消上传");
                    }
                    return isCancel;
                }));
    }
//
}
