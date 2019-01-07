package com.maihaoche.volvo.ui.common.activity;

import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;

import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityBaseFragmentBinding;
import com.maihaoche.volvo.ui.AbsScanBaseActivity;


public abstract class BaseFragmentActivity extends AbsScanBaseActivity<ActivityBaseFragmentBinding> {

    private ActivityBaseFragmentBinding binding;
    private int position = 0;


    @Override
    public int getContentResId() {
        return R.layout.activity_base_fragment;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        binding = getContentBinding();
        binding.viewpager.setAdapter(getPagerAdapter());
        binding.tablayout.setupWithViewPager(binding.viewpager);

        binding.tablayout.addOnTabSelectedListener(new TabListener());
        if(getTablayoutListener()!=null){
            binding.tablayout.addOnTabSelectedListener(getTablayoutListener());
        }

        onAfterViewCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
    }

    protected void onAfterViewCreated(Bundle savedInstanceState){

    }

    protected final int getPosition(){
        return position;
    }

    protected final ActivityBaseFragmentBinding getBinding(){
        return binding;
    }

    protected TabLayout.OnTabSelectedListener getTablayoutListener(){
        return null;
    }

    protected abstract<T extends FragmentPagerAdapter> T  getPagerAdapter();





    class TabListener implements TabLayout.OnTabSelectedListener{

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            position = tab.getPosition();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }
}
