package com.maihaoche.volvo.ui.instorage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.maihaoche.volvo.ui.common.activity.BaseFragmentActivity;
import com.maihaoche.volvo.ui.instorage.fragment.RuKuFragment;

/**
 * 入库，今日已入库列表
 */
public class InstorageListActivity extends BaseFragmentActivity {

    @Override
    protected FragmentPagerAdapter getPagerAdapter() {
        return new MyFragmentPager(getSupportFragmentManager());
    }

    @Override
    protected void onAfterViewCreated(Bundle savedInstanceState) {
        initHeader("入库");
    }

    public static class MyFragmentPager extends FragmentPagerAdapter {

        public MyFragmentPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 1){
                return RuKuFragment.create(RuKuFragment.TYPE_RUKU_TODAY);
            }else{
                return RuKuFragment.create(RuKuFragment.TYPE_RUKU);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 1){
                return "今日已入库";
            }else{
                return "待入库";
            }
        }
    }
}
