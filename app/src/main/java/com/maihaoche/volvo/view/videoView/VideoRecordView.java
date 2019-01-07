package com.maihaoche.volvo.view.videoView;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maihaoche.base.log.LogUtil;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.ui.common.daomain.VideoItem;
import com.maihaoche.volvo.ui.photo.VideoPlayerActivity;
import com.maihaoche.volvo.ui.photo.VideoRecorderActivity;
import com.maihaoche.volvo.util.permission.PermissionHandler;
import com.maihaoche.volvo.view.dialog.CommonDialog;


/**
 * 视频录制view
 */
public class VideoRecordView extends FrameLayout {
    public static final String TAG = VideoRecordView.class.getSimpleName();

    ImageView mImageView;
    ProgressBar mProgressView;
    View mDeleteView;
    TextView mWaterView;
    View mShadowView;

    boolean isCancel = false;
    private VideoItem mVideoItem = new VideoItem();

    public VideoItem getmVideoItem() {
        return mVideoItem;
    }

    private OnClickListener mChooseListener;
    private OnClickListener mDeleteListener;

    private boolean mIsShowAdd = true;
    private boolean mIsJustLook = false;
    private boolean mIsWithWater = true; //是否需要带水印,默认会带

    private String mTag;

    public VideoRecordView(Context context) {
        this(context, null);
    }

    public VideoRecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 自己设置选择图片的事件，可以拦截默认从图库选择事件
     *
     * @param chooseListener 选择事件
     */
    public void setChooseListener(OnClickListener chooseListener) {
        mChooseListener = chooseListener;
    }

    /**
     * 删除点击的监听事件，不会拦截原来的逻辑
     *
     * @param deleteListener 删除事件
     */
    public void setDeleteListener(OnClickListener deleteListener) {
        mDeleteListener = deleteListener;
    }

    private void init(final Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.auth_view_video_record, this, true);
        mImageView = view.findViewById(R.id.choose_pic_img_img);
        mProgressView = view.findViewById(R.id.choose_pic_img_progress);
        mDeleteView = view.findViewById(R.id.choose_pic_delete);
        mWaterView = view.findViewById(R.id.choose_pic_water);
        mShadowView = view.findViewById(R.id.choose_pic_img_shadow);
        //选择事件
        view.findViewById(R.id.choose_pic_img_img).setOnClickListener(v -> {

            PermissionHandler.checkVideo((BaseActivity)context, granted -> {
                if (granted) {
                    if (!StringUtil.isEmpty(mVideoItem.mOriPath)) {
                        //跳转播放页面
                        VideoPlayerActivity.toActivity(getContext(), mVideoItem.mOriPath);
                    } else {
                        //跳转录制页面
                        getContext().startActivity(new Intent(getContext(), VideoRecorderActivity.class));
                    }
                } else {
                    CommonDialog dialog = new CommonDialog(context,"","当前应用缺少视频相关多媒体权限\n请点击\"设置\"-\"权限\"-打开所需权限",
                             () -> PermissionHandler.toSettings(getContext()),
                            null);
                    dialog.show();
                }
            });
            if (mChooseListener != null) {
                mChooseListener.onClick(v);
            }
        });
        //删除事件
        mDeleteView.setOnClickListener(v -> {
            delete();
            if (mDeleteListener != null) {
                mDeleteListener.onClick(v);
            }
        });

        LogUtil.e(TAG, "init: " + mTag);
        if (TextUtils.isEmpty(mTag)) {
            mTag = "" + getId();
        }
        LogUtil.e(TAG, "initTagById: " + mTag);

        setSaveEnabled(true);
    }

    public void setDefaultImg(int id) {
        mImageView.setImageResource(id);
    }

    public void setVideoItem(VideoItem videoItem) {
        this.mVideoItem = videoItem;
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageView.setImageBitmap(videoItem.bitmap);
        mIsJustLook = videoItem.isJustLook;
        mDeleteView.setVisibility(VISIBLE);
    }


    public void delete() {
        showAdd();
        isCancel = true;
        mVideoItem.clear();
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public VideoItem getImageItem() {
        return mVideoItem;
    }

    public boolean isAdd() {
        return mIsShowAdd;
    }

    public void setIsWithWater(boolean isWithWater) {
        mIsWithWater = isWithWater;
    }


    public void showAdd() {
        mProgressView.setVisibility(GONE);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mImageView.setImageResource(R.drawable.vidoe_icon);
        mDeleteView.setVisibility(GONE);
        mWaterView.setVisibility(GONE);
        mShadowView.setVisibility(GONE);

        mIsShowAdd = true;
    }
}