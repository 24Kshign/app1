package com.maihaoche.volvo.ui.inwarehouse.cars;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityInWarehouseCarBinding;
import com.maihaoche.volvo.ui.car.activity.BatchApplyKeyActivity;
import com.maihaoche.volvo.ui.inwarehouse.WarehouseSelecter;
import com.maihaoche.volvo.view.dialog.SelectReasonForKeyDialog;


/**
 * 类简介：在库车辆列表页面
 * 作者：  yang
 * 时间：  2017/8/10
 * 邮箱：  yangyang@maihaoche.com
 */

public class ActivityInWarehouseCar extends HeaderProviderActivity<ActivityInWarehouseCarBinding> {


    private static final int TAB_POSITION_LEFT = 0;
    private static final int TAB_POSITION_RIGHT = 1;

    //数据分组0：在库车辆。1：异常车辆
    private int mCurrentTab = TAB_POSITION_LEFT;


    private VPAdpater mVPAdpater;

    private WarehouseSelecter mWarehouseSelecter;

    @Override
    public int getContentResId() {
        return R.layout.activity_in_warehouse_car;

    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        init();
        load();
    }

    private void init() {
        initHeader("在库车辆","批量",v -> new SelectReasonForKeyDialog(this,reason->{
            BatchApplyKeyActivity.toActivity(this,reason.id);
        }).show());
        mWarehouseSelecter = new WarehouseSelecter() {
            @Override
            protected void onWarehouseChange(long warehouseId) {
                mVPAdpater.changeWarehouseId(warehouseId);

            }
        };
        mWarehouseSelecter.adapte(this);

        ActivityInWarehouseCarBinding binding = getContentBinding();

        binding.tabs.tabLeftText.setText("在库车辆");
        binding.tabs.tabLeft.setOnClickListener(new OnTabClickListener(TAB_POSITION_LEFT));
        binding.tabs.tabRightText.setText("异常车辆");
        binding.tabs.tabRight.setOnClickListener(new OnTabClickListener(TAB_POSITION_RIGHT));
        mVPAdpater = new VPAdpater(getSupportFragmentManager());
        binding.listViewPager.setAdapter(mVPAdpater);
        binding.listViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onTabChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.tabs.tabLeft.setSelected(true);
        binding.tabs.tabRight.setSelected(false);
    }


    private void load() {
    }


    private void onTabChange(int tabPostionTo){
        getContentBinding().tabs.tabLeft.setSelected(tabPostionTo==TAB_POSITION_LEFT);
        getContentBinding().tabs.tabRight.setSelected(tabPostionTo==TAB_POSITION_RIGHT);
        mCurrentTab = tabPostionTo;
    }


    /**
     * fragment的容器的adapter
     */
    private class VPAdpater extends FragmentPagerAdapter {

        private FragmentInWarehouseCar mIngarageCarApdater;
        private FragmentInWarehouseCar mIngarageAbnCarApdater;

        public VPAdpater(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case TAB_POSITION_LEFT:
                    if (mIngarageCarApdater == null) {
                        mIngarageCarApdater = FragmentInWarehouseCar.create(mWarehouseSelecter.getCurrentWarehouseId(), false);
                    }
                    return mIngarageCarApdater;
                case TAB_POSITION_RIGHT:
                    if (mIngarageAbnCarApdater == null) {
                        mIngarageAbnCarApdater = FragmentInWarehouseCar.create(mWarehouseSelecter.getCurrentWarehouseId(), true);
                    }
                    return mIngarageAbnCarApdater;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        public void changeWarehouseId(long warehoudeId) {
            if (mIngarageCarApdater != null) {
                mIngarageCarApdater.changeWarehouseId(warehoudeId);
            }
            if (mIngarageAbnCarApdater != null) {
                mIngarageAbnCarApdater.changeWarehouseId(warehoudeId);
            }
        }
    }

    /**
     * tab的点击事件
     */
    private class OnTabClickListener implements View.OnClickListener {
        //tab的点击事件
        private int tabPosition;

        public OnTabClickListener(int tabPosition) {
            this.tabPosition = tabPosition;
        }

        @Override
        public void onClick(View v) {
            if (mCurrentTab == tabPosition) {
                return;
            } else {
                getContentBinding().listViewPager.setCurrentItem(tabPosition, false);
                onTabChange(tabPosition);
            }
        }
    }


}
