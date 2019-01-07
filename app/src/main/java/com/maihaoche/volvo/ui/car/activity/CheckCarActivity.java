package com.maihaoche.volvo.ui.car.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;

import com.maihaoche.volvo.ui.car.fragment.CheckCarFragment;
import com.maihaoche.volvo.ui.common.activity.BaseFragmentActivity;


public class CheckCarActivity extends BaseFragmentActivity {

    @Override
    protected void onAfterViewCreated(Bundle savedInstanceState) {
        initHeader("验车");
    }

    @Override
    protected FragmentPagerAdapter getPagerAdapter() {
        return new MyFragmentPager(getSupportFragmentManager());
    }

    public static class MyFragmentPager extends FragmentPagerAdapter {

        public MyFragmentPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 1){
                return CheckCarFragment.create(CheckCarFragment.TYPE_CHECK_CAR_TODAY);
            }else{
                return CheckCarFragment.create(CheckCarFragment.TYPE_CHECK_CAR);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 1){
                return "今日已验车";
            }else{
                return "待验车";
            }
        }
    }
}
