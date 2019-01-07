package com.maihaoche.volvo.ui.photo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityChoosePicBinding;
import com.maihaoche.commonbiz.service.image.ImageUtil;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.view.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by daxia on 16/1/14.
 * 多张图片选择页
 */
public class ChoosePicActivity extends HeaderProviderActivity<ActivityChoosePicBinding> {

    public static final String IS_LIMIT_MAX = "is_limit_max";//是否限制最大张数
    public static final String MAX_PHOTO = "max_photo";//最大张数
    public static final String OLD_PICS = "old_pics";//已有的照片
    public static final String TITLE = "title";//标题
    public static final String SUBTITLE = "subtitle";//副标题
    public static final String S_SHOW_WATER = "SHOW_WATER";
    public static final String S_JUST_LOOK = "JUST_LOOK";

    public static final String PICTURE_INFO = "info";

    public static final String RESULT_URLS = "result_urls";//返回结果

    TextView mSaveBtn;
    RecyclerView mImgGridView;


    private int mMaxCount = 9;//最大张数
    private boolean mLimitMax = true;//是否限制最大张数
    private boolean mJustLook = false;//是否仅仅是查看

    //是否显示水印
    private boolean mIsShowWater = true;

    private List<String> mOldPics = new ArrayList<>();//已有的照片地址

    private ChoosePicAdapter mChoosePicAdapter;

    private List<String> mChooseNewPicPath = new ArrayList<>();//刚选择图片的地址集合

    private PictureInfo mPictureInfo = null;
    private  ActivityChoosePicBinding binding;

    void clickToExample() {
        Intent intent = new Intent(this, ImageViewerActivity.class);
        intent.putExtra(ImageViewerActivity.EXTRA_URLS_STR, mPictureInfo.examplePicture);
        startActivity(intent);
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_choose_pic;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        mSaveBtn = binding.choosePicSave;
        mImgGridView = binding.choosePicGrid;
        Intent intent = getIntent();
        mMaxCount = intent.getIntExtra(MAX_PHOTO, 9);
        mLimitMax = intent.getBooleanExtra(IS_LIMIT_MAX, true);
        mPictureInfo = (PictureInfo) getIntent().getSerializableExtra(PICTURE_INFO);
//        String pics = intent.getStringExtra(OLD_PICS);
        if (mPictureInfo != null && mPictureInfo.imgUrls != null && mPictureInfo.imgUrls.size() > 0) {
//            mOldPics = mPictureInfo.imgUrls;
//            PictureStatusEnum pictureStatus = PictureStatusEnum.valueOf(mPictureInfo.pictureStatus.intValue());
            mJustLook = intent.getBooleanExtra(S_JUST_LOOK, false);
        }

        mIsShowWater = intent.getBooleanExtra(S_SHOW_WATER, true);
        initView();
    }


    private void initView() {
        setTitle(StringUtil.checkNotNull(getIntent().getStringExtra(TITLE), "上传图片"));
        binding.tvSubtitle.setText(StringUtil.checkNotNull(getIntent().getStringExtra(SUBTITLE), "上传图片"));
        if (mJustLook) {
            mSaveBtn.setVisibility(View.INVISIBLE);
            mSaveBtn.setEnabled(false);
        } else {
            mSaveBtn.setVisibility(View.VISIBLE);
            mSaveBtn.setEnabled(true);
        }
//        mExampleNote.setText(mPictureInfo.exampleText);
        mChoosePicAdapter = new ChoosePicAdapter(this, mLimitMax, mMaxCount, mJustLook);
        mChoosePicAdapter.setIsShowWater(mIsShowWater);
        mChoosePicAdapter.setOnDeleteListener(position -> mSaveBtn.setEnabled(true));
        if (mOldPics != null && mOldPics.size() > 0) {
            mChoosePicAdapter.addOriPath(mOldPics);
        }

        mChoosePicAdapter.setOnItemClickListener((view,data,position) -> {
            if (position == mChoosePicAdapter.getSize()) {
                Intent intent = new Intent(ChoosePicActivity.this, PhotoWallActivity.class);
                intent.putExtra(PhotoWallActivity.EXTRA_MEDIA_TYPE, PhotoWallActivity.TYPE_PICTURE);
                intent.putExtra(PhotoWallActivity.EXTRA_IS_MUTIPLE, true);
                intent.putExtra(PhotoWallActivity.EXTRA_IS_LIMIT_MAX, true);
                intent.putExtra(PhotoWallActivity.EXTRA_MAX_PHOTO, mMaxCount - (mChoosePicAdapter.getSize()));
                startActivityForResult(intent, 0);
            } else {
                //查看图片
                Intent intent = new Intent(ChoosePicActivity.this, ImageViewerActivity.class);
                intent.putExtra(ImageViewerActivity.EXTRA_URLS_LIST, (ArrayList<String>) mChoosePicAdapter.getShowUrl());
                intent.putExtra(ImageViewerActivity.EXTRA_CUR_INDEX, position);
                startActivity(intent);
            }
        });

        mImgGridView.setLayoutManager(new GridLayoutManager(this, 3));
        mImgGridView.addItemDecoration(new GridSpacingItemDecoration(3, 20, false));
        binding.choosePicGrid.setAdapter(mChoosePicAdapter);
//        mExampleClickView.setVisibility(View.GONE);
//        mStatusView.setVisibility(View.GONE);
        if (mPictureInfo != null) {
            if (!TextUtils.isEmpty(mPictureInfo.examplePicture)) {
//                mExampleClickView.setVisibility(View.VISIBLE);
            }
//            initStatus();
        }

        getContentBinding().choosePicSave.setOnClickListener(v->save(v));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0:
                    mChooseNewPicPath.clear();
                    mChooseNewPicPath.addAll(data.getStringArrayListExtra("path"));
                    if (mChooseNewPicPath != null && mChooseNewPicPath.size() > 0) {
                        mChoosePicAdapter.addOriPath(mChooseNewPicPath);
                    }
                    mSaveBtn.setEnabled(true);
                    break;
                default:
                    return;
            }
        }
    }

    private boolean isUploadComplete() {
        return mChoosePicAdapter.getUploadImgUrl().size() >= mChoosePicAdapter.getItemCount();
    }

    private void onSave() {
        Intent intent = new Intent();
        StringBuilder sb = new StringBuilder();
        List<String> mChoosePicUrls = mChoosePicAdapter.getUploadImgUrl();
        if (mChoosePicUrls != null && mChoosePicUrls.size() > 0) {
            for (int i = 0; i < mChoosePicUrls.size(); ++i) {
                String url = mChoosePicUrls.get(i);
                sb.append(url);
                if (i < mChoosePicUrls.size() - 1) {
                    sb.append(ImageUtil.SPLITE_REGIX);
                }
            }
        } else {
            showCommonDialog(this, "", "请上传图片后保存", null, null);
            return;
        }

        intent.putExtra(ChoosePicActivity.RESULT_URLS, sb.toString());

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void save(View v) {
        if (!isUploadComplete()) {
            showCommonDialog(this, "", "您还有图片正在上传,是否继续保存?", (dialog, which) -> onSave(), null);
        } else {
            onSave();
        }
    }


    private void onBack() {

        if (mOldPics.size() != mChoosePicAdapter.getItemCount()) {
            showCommonDialog(this, "", "您的信息还未提交，确认返回?", (dialog, which) -> finish(), null);
        } else {
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        onBack();
    }


    public void showCommonDialog(
            Context context, String title, String content,
            DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(content)
                .setNegativeButton("取消", negativeListener)
                .setPositiveButton("确定", positiveListener)
                .show();
    }


}
