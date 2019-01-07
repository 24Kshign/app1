package com.maihaoche.volvo.ui.photo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.TextView;

import com.maihaoche.volvo.R;
import com.maihaoche.volvo.view.ImageViewPager;

import java.util.ArrayList;



/**
 * Created by waitou on 16/11/14.
 * 大图预览查看 可选择页面
 */

public class ImagePreviewActivity extends BaseFragmentActivity {

    public static String EXTRA_SIZE_URLS_LIST = "size_urls_list"; //全部的图片list的url
    public static String EXTRA_SELECTED_URLS_LIST = "selected_urls_list"; //选中list的url
    public static String EXTRA_IS_WATER = "mIsWithWater";//是否带水印
    public static String EXTRA_POSITION = "position";

    CheckBox mCheckBox;
    ImageViewPager mImageViewPager;
    TextView mSubmitTv;

    private boolean mIsWithWater; //是否带水印
    private ArrayList<String> mSizeUrlsList; //全部的图片
    private ArrayList<String> mSelectedUrlsList; //已经选中的图片
    private int mCurIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgage_preview);
        mCheckBox = (CheckBox) this.findViewById(R.id.cb);
        mImageViewPager = (ImageViewPager)this.findViewById(R.id.image_view_pager);
        mSubmitTv = (TextView) this.findViewById(R.id.submit_tv);

        mSubmitTv.setOnClickListener(v->submit());

        this.findViewById(R.id.back_iv).setOnClickListener(v->back());


        if (savedInstanceState != null) {
            mSizeUrlsList = savedInstanceState.getStringArrayList(EXTRA_SIZE_URLS_LIST);
            mSelectedUrlsList = savedInstanceState.getStringArrayList(EXTRA_SELECTED_URLS_LIST);
            mIsWithWater = savedInstanceState.getBoolean(EXTRA_IS_WATER);
            mCurIndex = savedInstanceState.getInt(EXTRA_POSITION, 0);
        } else {
            init();
        }
        initViews();
    }

    private void init() {
        Intent intent = getIntent();
        mSizeUrlsList = intent.getStringArrayListExtra(EXTRA_SIZE_URLS_LIST);
        mSelectedUrlsList = intent.getStringArrayListExtra(EXTRA_SELECTED_URLS_LIST);
        mIsWithWater = intent.getBooleanExtra(EXTRA_IS_WATER, false);
        mCurIndex = intent.getIntExtra(EXTRA_POSITION, 0);
    }

    private void initViews() {

        ImagePagerAdapter mImagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), mSizeUrlsList);
        mImageViewPager.setAdapter(mImagePagerAdapter);
        //预加载2张
        mImageViewPager.setOffscreenPageLimit(2);
        mImageViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mSelectedUrlsList.contains(mSizeUrlsList.get(position))) {
                    mCheckBox.setChecked(true);
                } else {
                    mCheckBox.setChecked(false);
                }
            }
        });

        mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int currentItem = mImageViewPager.getCurrentItem();
            if (isChecked) {
                if (mSelectedUrlsList.contains(mSizeUrlsList.get(currentItem))) {
                    return;
                }
                mSelectedUrlsList.add(mSizeUrlsList.get(currentItem));
            } else {
                if (!mSelectedUrlsList.contains(mSizeUrlsList.get(currentItem))) {
                    return;
                }
                mSelectedUrlsList.remove(mSizeUrlsList.get(currentItem));
            }
            mSubmitTv.setText("( " + mSelectedUrlsList.size() + " ) 完成");
        });

        mImageViewPager.setCurrentItem(mCurIndex);
        if (mSelectedUrlsList.contains(mSizeUrlsList.get(mCurIndex))) {
            mCheckBox.setChecked(true);
        }
        mSubmitTv.setText("( " + mSelectedUrlsList.size() + " ) 完成");
    }

    public void submit() {
        setResult(true);
        finish();
    }

    public void back() {
        setResult(false);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(false);
        super.onBackPressed();
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> mUrls;

        ImagePagerAdapter(FragmentManager fm, ArrayList<String> urls) {
            super(fm);
            this.mUrls = urls;
        }

        @Override
        public int getCount() {
            return mUrls == null ? 0 : mUrls.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = mUrls.get(position);
            return ImagePreviewFragment.newInstance(url, false, mIsWithWater);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(EXTRA_SIZE_URLS_LIST, mSizeUrlsList);
        outState.putStringArrayList(EXTRA_SELECTED_URLS_LIST, mSelectedUrlsList);
        outState.putBoolean(EXTRA_IS_WATER, mIsWithWater);
        outState.putInt(EXTRA_POSITION, mCurIndex);
    }

    private void setResult(boolean isCommit) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(PhotoWallActivity.EXTRA_SELECTED_URLS_LIST, mSelectedUrlsList);
        intent.putExtra(PhotoWallActivity.EXTRA_IS_COMMIT, isCommit);
        setResult(RESULT_OK, intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

}
