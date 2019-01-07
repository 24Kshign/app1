package com.maihaoche.volvo.ui.car.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.volvo.ui.car.fragment.OutStorageFragment;
import com.maihaoche.volvo.ui.common.activity.BaseFragmentActivity;
import com.maihaoche.volvo.view.dialog.SelectPopWindow;
import com.maihaoche.volvo.view.dialog.SelectReasonForKeyDialog;


public class OutStorageListActivity extends BaseFragmentActivity {

    @Override
    protected void onAfterViewCreated(Bundle savedInstanceState) {
        SelectPopWindow popWindow = new SelectPopWindow(this);
        popWindow.setFindCarListener(()->{
            startActivity(new Intent(this,BatchOutStorageActivity.class));
        },"出库");
        popWindow.setSendListener(()->{
            BatchApplyKeyActivity.toActivity(this, SelectReasonForKeyDialog.Reason.REASON_OUT);
        },"取钥匙");
        popWindow.setArrowRight();
        initHeader("出库","批量",v->{
            popWindow.showAsDropDown(v, -SizeUtil.dip2px(20),0);
        });
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
                return OutStorageFragment.create(OutStorageFragment.TYPE_OUT_STORAGE_TODAY);
            }else{
                return OutStorageFragment.create(OutStorageFragment.TYPE_OUT_STORAGE);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 1){
                return "今日已出库";
            }else{
                return "出库";
            }
        }
    }
}
