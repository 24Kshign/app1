package com.maihaoche.volvo.ui.photo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.widget.TextView;

import com.maihaoche.volvo.R;
import com.maihaoche.commonbiz.service.image.ImageUtil;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.view.ImageViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by wangshengru on 16/1/14.
 * 大图查看页
 */
public class ImageViewerActivity extends BaseFragmentActivity {

    public static final String EXTRA_REMARK = "remark"; //备注
    public static String EXTRA_URLS_STR  = "urls_str";   //链接是字符串形式,用分号隔开的形式
    public static String EXTRA_URLS_LIST = "urls_list"; //list的url
    public static String EXTRA_CUR_INDEX = "curIndex";

    public static String EXTRA_IS_WATER  = "isWater";
    public static String EXTRA_TITLE     = "title";

    public static String EXTRA_URLS_ARRAY = "urls_array";

    TextView mIndicatorTxt;

    ImageViewPager mImageViewPager;

    TextView mNoteView;

    private int mCurIndex = 0;
    private ArrayList<String> mUrls = new ArrayList<>();
    private String mUrlStr = "";

    private boolean mIsWithWater = false;

    private String mTitle = "";

    List<String> remarks ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        mIndicatorTxt = (TextView) this.findViewById(R.id.image_viewer_indicator);
        mImageViewPager = (ImageViewPager) this.findViewById(R.id.image_view_pager);
        mNoteView = (TextView) this.findViewById(R.id.image_viewer_note);

        this.findViewById(R.id.image_viewer_back).setOnClickListener(v -> finish());
        if (savedInstanceState != null) {
            mUrlStr = savedInstanceState.getString(EXTRA_URLS_STR);
            if (StringUtil.isNotEmpty(mUrlStr)) {
                mUrls = new ArrayList<>(Arrays.asList(mUrlStr.split(ImageUtil.SPLITE_REGIX)));
            }
            if (mUrls == null || mUrls.size() < 1) {
                mUrls = savedInstanceState.getStringArrayList(EXTRA_URLS_LIST);
            }
            mCurIndex = savedInstanceState.getInt(EXTRA_CUR_INDEX, 0);
            mIsWithWater = savedInstanceState.getBoolean(EXTRA_IS_WATER, false);
            mTitle = savedInstanceState.getString(EXTRA_TITLE);
        } else {
            initData();
        }
        initView();
        remarks = (List<String>) getIntent().getSerializableExtra(EXTRA_REMARK);
        if (remarks != null){
            mNoteView.setText(remarks.get(mCurIndex)); //设置异常图片备注
        }
    }



    private void initData() {
        Intent intent = getIntent();
        mUrlStr = intent.getStringExtra(EXTRA_URLS_STR);
        if (StringUtil.isNotEmpty(mUrlStr)) {
            mUrls = new ArrayList<>(Arrays.asList(mUrlStr.split(ImageUtil.SPLITE_REGIX)));
        }
        if (mUrls == null || mUrls.size() < 1) {
            mUrls = intent.getStringArrayListExtra(EXTRA_URLS_LIST);
        }
        mCurIndex = intent.getIntExtra(EXTRA_CUR_INDEX, 0);

        mIsWithWater = intent.getBooleanExtra(EXTRA_IS_WATER, false);

        mTitle = intent.getStringExtra(EXTRA_TITLE);
    }

    private void initView() {
        ImagePagerAdapter mImagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), mUrls);
        mImageViewPager.setAdapter(mImagePagerAdapter);
        CharSequence text = getString(R.string.image_viewer_indicator, 1, mImageViewPager.getAdapter().getCount());
        mIndicatorTxt.setText(text);
        mImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                CharSequence text = getString(R.string.image_viewer_indicator, position + 1, mImageViewPager.getAdapter().getCount());
                mIndicatorTxt.setText(text);
                mCurIndex = position;

                //设置备注信息
                if (remarks != null){
                    mNoteView.setText(remarks.get(position)); //设置异常图片备注
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mImageViewPager.setCurrentItem(mCurIndex);

        mNoteView.setText(mTitle);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_CUR_INDEX, mImageViewPager.getCurrentItem());
        outState.putStringArrayList(EXTRA_URLS_LIST, mUrls);
        outState.putString(EXTRA_URLS_STR, mUrlStr);
        outState.putBoolean(EXTRA_IS_WATER, mIsWithWater);
        outState.putString(EXTRA_TITLE, mTitle);

    }


    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public ArrayList<String> mUrls;

        public ImagePagerAdapter(FragmentManager fm, ArrayList<String> urls) {
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

            return ImagePreviewFragment.newInstance(url, true, mIsWithWater);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }
}
