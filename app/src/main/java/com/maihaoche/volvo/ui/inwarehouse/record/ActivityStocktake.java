package com.maihaoche.volvo.ui.inwarehouse.record;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.NormalDialog;
import com.maihaoche.commonbiz.service.utils.HintUtil;
import com.maihaoche.commonbiz.service.utils.MobileUtil;
import com.maihaoche.commonbiz.service.utils.SharePreferenceHandler;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityStocktakeBinding;
import com.maihaoche.volvo.server.dto.StocktakeDetailCarVO;
import com.maihaoche.volvo.server.dto.request.UploadStocktakeDetailsRequest;
import com.maihaoche.volvo.ui.AbsScanBaseActivity;
import com.maihaoche.volvo.ui.inwarehouse.WarehouseSelecter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.functions.Action;

/**
 * 类简介：盘库页面
 * 作者：  yang
 * 时间：  2017/8/11
 * 邮箱：  yangyang@maihaoche.com
 */

public class ActivityStocktake extends AbsScanBaseActivity<ActivityStocktakeBinding> {


    private static final int TAB_POSITION_LEFT = 0;
    private static final int TAB_POSITION_RIGHT = 1;

    //数据分组0：待盘点。1：已盘点
    private int mCurrentTab = TAB_POSITION_LEFT;

    private WarehouseSelecter mWarehouseSelecter;

    private VPAdpater mVPAdpater;
    //已盘点条形码的id的set
    private Set<String> stockTakenCarTagIds = new HashSet<>();

    private RecordStatus mRecordStatus = RecordStatus.TO_START;

    //存储已盘点详情,为str，可以序列化为一个map，包含了详情id和本地盘点时间。
    private static final String STOCK_TAKEN_DETAILS = "STOCK_TAKEN_DETAIL_INFO";
    //离线保存的已盘点数据。key为recordId，value为该盘库记录对应的detailId数组
    private HashMap<Long, HashMap<Long, Long>> mOfflineStocktakenData = new HashMap<>();
    //所有的待盘点的id集合
    private HashSet<Long> mToStocktakeDetailIds = new HashSet<>();

    private Type mLongLongHashMapType = new TypeToken<HashMap<Long, Long>>() {
    }.getType();

    private Gson mGson = new Gson();

    /**
     * 查看某个盘库记录对应的盘库数据
     *
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, ActivityStocktake.class);
        return intent;
    }


    @Override
    public int getContentResId() {
        return R.layout.activity_stocktake;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        super.afterViewCreated(savedInstanceState);
        init();
        load();
    }


    private void init() {
        initHeader("盘点");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
        }

        //顶部
        mWarehouseSelecter = new WarehouseSelecter() {
            @Override
            protected void onWarehouseChange(long warehouseId) {
                mVPAdpater.changeWarehouseId(warehouseId);
            }
        };
        mWarehouseSelecter.adapte(this);
        setRightText("盘库记录", v -> startActivity(RecordListActivity.createIntent(ActivityStocktake.this, mWarehouseSelecter.getCurrentWarehouseId())));
        ActivityStocktakeBinding binding = getContentBinding();
        binding.tabs.tabLeftText.setText("待盘点");
        binding.tabs.tabLeft.setOnClickListener(new OnTabClickListener(TAB_POSITION_LEFT));
        binding.tabs.tabRightText.setText("已盘点");
        binding.tabs.tabRight.setOnClickListener(new OnTabClickListener(TAB_POSITION_RIGHT));
        binding.tabs.tabLeft.setSelected(true);
        binding.tabs.tabRight.setSelected(false);

        //开始
        binding.recordBtnLeft.setText("开始");

    }

    private void load() {
        ActivityStocktakeBinding binding = getContentBinding();
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
    }

    /**
     * 当rfid 盘到某条记录时
     *
     * @param rfid
     */
    @Override
    protected void onTagResult(String rfid) {
        //Log.e("record", "找到rfid-------->" + rfid);
        //LogFileUtil.writToFile2(rfid);
        StocktakeDetailCarVO detailVO;
        synchronized (ActivityStocktake.class) {
            //检查重复
            if (stockTakenCarTagIds.contains(rfid)) {
                //LogFileUtil.writToFile2(rfid + "重复");
                return;
            }
            detailVO = mVPAdpater.findToStocktakeDtail(rfid);
            if (detailVO != null) {
                stockTakenCarTagIds.add(rfid);
            }
        }
        if (detailVO != null) {
            //LogFileUtil.writToFile2(rfid + "对应到车辆");
            HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.INVENTORY_SCAN);
            runOnUiThread(() -> {
                onStocktaken(detailVO);
            });
        }
    }


    /**
     * 切换数据分类
     *
     * @param tabPostionTo
     */
    private void onTabChange(int tabPostionTo) {
        if (mCurrentTab == tabPostionTo) {
            return;
        }
        getContentBinding().tabs.tabLeft.setSelected(tabPostionTo == TAB_POSITION_LEFT);
        getContentBinding().tabs.tabRight.setSelected(tabPostionTo == TAB_POSITION_RIGHT);
        mCurrentTab = tabPostionTo;
    }

    /**
     * 根据状态来切换"开始"和"暂停"
     */
    private void startOrPauseRecord() {
        ActivityStocktakeBinding binding = getContentBinding();
        switch (mRecordStatus) {
            //点击开始盘库
            case TO_START:
                binding.recordBtnLeft.setText("暂停");
                //LogFileUtil.writToFile2("暂停");
                HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_START_INVENTORY);
                startInventory();
                mRecordStatus = RecordStatus.RECORDING;
                break;
            //点击暂停盘库
            case RECORDING:
                binding.recordBtnLeft.setText("继续");
                //LogFileUtil.writToFile2("继续");
                HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_PAUSE_INVENTORY);
                stopInventory();
                mRecordStatus = RecordStatus.PAUSED;
                break;
            //点击继续盘库
            case PAUSED:
                binding.recordBtnLeft.setText("暂停");
                //LogFileUtil.writToFile2("暂停");
                HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_START_INVENTORY);
                startInventory();
                mRecordStatus = RecordStatus.RECORDING;
                break;
            default:
                break;
        }
    }

    /**
     * 停止盘点.只有当盘点全部完成后才调用。
     */
    private void stopRecord() {
        stopInventory();
        HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_STOP_INVENTORY);
        mRecordStatus = RecordStatus.TO_START;
        getContentBinding().recordBtnLeft.setVisibility(View.GONE);
    }

    /**
     * 上传离线盘点数据。
     */

    private void uploadOfflineData() {
        if (hasOfflineData()) {
            //上传
            if (MobileUtil.isNetOn()) {
                List<UploadStocktakeDetailsRequest.StocktakeDetailInfo> detailInfoList = new ArrayList<>();
                for (Long recordId :
                        mOfflineStocktakenData.keySet()) {
                    HashMap<Long, Long> detailStrs = mOfflineStocktakenData.get(recordId);
                    Iterator<Map.Entry<Long, Long>> iterator = detailStrs.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Long, Long> entry = iterator.next();
                        detailInfoList.add(new UploadStocktakeDetailsRequest.StocktakeDetailInfo(entry.getKey(), entry.getValue()));
                    }

                }
                if (detailInfoList.size() > 0) {
                    saveOnlineData(detailInfoList, () -> {
                        clearOfflineData();
                        checkCompleted();
                        AlertToast.show("数据上传成功");
                    }, null);
                }
            } else {
                new NormalDialog(ActivityStocktake.this)
                        .setTilte("无网络")
                        .setContent("请检查网络信号后重试")
                        .setBtnCancelStr("")
                        .setBtnConfirmStr("确定")
                        .show();
            }
        }

    }


    /**
     * 检查是否盘库完成。可能正在刷新的时候，数据为空，不支持多线程
     *
     * @return
     */
    private boolean checkCompleted() {
        boolean hasOfflineData = hasOfflineData();
        if (hasOfflineData) {
            getContentBinding().recordBtnRight.setVisibility(View.VISIBLE);
            getContentBinding().recordBtnRight.setOnClickListener(v -> {
                uploadOfflineData();
            });
        } else {
            getContentBinding().recordBtnRight.setVisibility(View.GONE);
        }
        if (mToStocktakeDetailIds.size() == 0) {
            getContentBinding().recordBtnLeft.setVisibility(View.GONE);
            if (mVPAdpater.mStocktakenFragment.getDetails() != null
                    && mVPAdpater.mStocktakenFragment.getDetails().size() > 0) {
                getContentBinding().tabs.tabRight.performClick();
            }
            return true;
        }
        return false;
    }

    /**
     * 判断是否有离线数据
     */
    private boolean hasOfflineData() {
        if (mOfflineStocktakenData.size() > 0) {
            for (Long recordId :
                    mOfflineStocktakenData.keySet()) {
                if (!mOfflineStocktakenData.get(recordId).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 当某个盘点记录被盘到时
     * 该方法可能会被高频调用。很多同步的问题需要考虑
     */
    private void onStocktaken(StocktakeDetailCarVO detailVO) {
        //如果有网络，请求网络
        if (MobileUtil.isNetOn()) {
            saveOnlineData(
                    Collections.singletonList(new UploadStocktakeDetailsRequest.StocktakeDetailInfo(detailVO.stocktakeDetailId, System.currentTimeMillis()))
                    , () -> {
                        mOfflineStocktakenData.remove(detailVO.stocktakeDetailId);
                        mToStocktakeDetailIds.remove(detailVO.stocktakeDetailId);
                        if (checkCompleted()) {
                            stopRecord();
                        }
                    }
                    , () -> {
                        stockTakenCarTagIds.remove(detailVO.inWarehouseCarVO.carTagId);
                    });
        } else {
            if (mOfflineStocktakenData.containsKey(detailVO.stocktakeRecordId)) {
                mOfflineStocktakenData.get(detailVO.stocktakeRecordId).put(detailVO.stocktakeDetailId, System.currentTimeMillis());
            } else {
                HashMap<Long, Long> detailTimeMap = new HashMap<>();
                detailTimeMap.put(detailVO.stocktakeDetailId, System.currentTimeMillis());
                mOfflineStocktakenData.put(detailVO.stocktakeRecordId, detailTimeMap);
            }
            mToStocktakeDetailIds.remove(detailVO.stocktakeDetailId);
            mVPAdpater.mStocktakenFragment.addData(mVPAdpater.mToStocktakeFragment.removeData(new HashSet<>(Collections.singleton(detailVO.stocktakeDetailId))));
            //保存离线数据
            saveSingleData(detailVO);
            if (checkCompleted()) {
                stopRecord();
            }
        }
    }

    /**
     * 保存单个的离线数据
     */
    private void saveSingleData(StocktakeDetailCarVO detailVO) {
        //保存离线数据
        SharedPreferences sp = SharePreferenceHandler.getPreferences(STOCK_TAKEN_DETAILS);
        HashMap<Long, Long> detailInfoMap = new HashMap<>();
        Gson gson = new Gson();
        if (sp.contains(detailVO.stocktakeRecordId + "")) {
            String detailInfoMapStr = sp.getString(detailVO.stocktakeRecordId + "", "");
            detailInfoMap = gson.fromJson(detailInfoMapStr, mLongLongHashMapType);
            if (detailInfoMap == null) {
                detailInfoMap = new HashMap<>();
            }
        }
        detailInfoMap.put(detailVO.stocktakeDetailId, System.currentTimeMillis());
        sp.edit().putString(detailVO.stocktakeRecordId + "", gson.toJson(detailInfoMap, mLongLongHashMapType)).apply();
    }

    /**
     * 保存mOfflineStocktakenData中的数据
     */
    private void saveAllOfflineData() {
        if (mOfflineStocktakenData.size() == 0) {
            return;
        }
        //保存离线数据
        SharedPreferences sp = SharePreferenceHandler.getPreferences(STOCK_TAKEN_DETAILS);
        for (Long recordId :
                mOfflineStocktakenData.keySet()) {
            HashMap<Long, Long> map = mOfflineStocktakenData.get(recordId);
            if (map == null) {
                map = new HashMap<>();
            }
            sp.edit().putString(recordId + "", mGson.toJson(map, mLongLongHashMapType)).apply();
        }
    }

    /**
     * 上传
     */
    private void saveOnlineData(List<UploadStocktakeDetailsRequest.StocktakeDetailInfo> detailInfoList, Action onSuccess, Action onError) {
        UploadStocktakeDetailsRequest request = new UploadStocktakeDetailsRequest();
        request.stocktakeDetailInfoList = detailInfoList;
        AppApplication.getServerAPI().uploadStocktakeDetails(request)
                .setOnDataError(emsg -> {
                    AlertToast.show(emsg);
                    if (onError != null) {
                        onError.run();
                    }
                })
                .setOnDataGet(baseResponse -> {
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                    mVPAdpater.reload();
                })
                .call(ActivityStocktake.this);
    }

    /**
     * 清除本地未盘点数据
     */
    private void clearOfflineData() {
        SharedPreferences sp = SharePreferenceHandler.getPreferences(STOCK_TAKEN_DETAILS);
        for (Long recordId :
                mOfflineStocktakenData.keySet()) {
            sp.edit().remove(recordId + "").commit();
        }
        mOfflineStocktakenData.clear();
    }

    /**
     * 数据列表的容器，adapter
     */
    private class VPAdpater extends FragmentPagerAdapter {

        private FragmentStocktake mToStocktakeFragment;
        private FragmentStocktake mStocktakenFragment;
        private FragmentStocktakeImp mFragmentStocktakeImp = new FragmentStocktakeImp();

        public VPAdpater(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case TAB_POSITION_LEFT:
                    if (mToStocktakeFragment == null) {
                        mToStocktakeFragment = FragmentStocktake.creaet(mWarehouseSelecter.getCurrentWarehouseId(), false);
                        mToStocktakeFragment.setIFragmentStocktake(mFragmentStocktakeImp);
                    }
                    return mToStocktakeFragment;
                case TAB_POSITION_RIGHT:
                    if (mStocktakenFragment == null) {
                        mStocktakenFragment = FragmentStocktake.creaet(mWarehouseSelecter.getCurrentWarehouseId(), true);
                        mStocktakenFragment.setIFragmentStocktake(mFragmentStocktakeImp);
                    }
                    return mStocktakenFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        /**
         * 变换数据的仓库id
         */
        public void changeWarehouseId(long warehoudeId) {
            if (mToStocktakeFragment != null) {
                mToStocktakeFragment.changeWarehouseId(warehoudeId);
            }
            if (mStocktakenFragment != null) {
                mStocktakenFragment.changeWarehouseId(warehoudeId);
            }
        }

        /**
         * 根据rfid搜索某个盘库记录
         */
        public StocktakeDetailCarVO findToStocktakeDtail(String rfid) {
            if (TextUtils.isEmpty(rfid)) {
                return null;
            }
            List<StocktakeDetailCarVO> detailVOs = mToStocktakeFragment.getDetails();
            for (int i = 0; i < detailVOs.size(); i++) {
                StocktakeDetailCarVO vo = detailVOs.get(i);
                if (vo.inWarehouseCarVO != null && rfid.equals(vo.inWarehouseCarVO.carTagId)) {
                    return vo;
                }
            }
            return null;
        }

        /**
         * 重新加载数据
         */
        public void reload() {
            if (mStocktakenFragment != null) {
                mStocktakenFragment.onPullRefresh();
            }
            if (mToStocktakeFragment != null) {
                mToStocktakeFragment.onPullRefresh();
            }
        }
    }

    /**
     * tab的点击事件
     */
    private class OnTabClickListener implements View.OnClickListener {
        private int tabPosition;//tab的点击事件

        public OnTabClickListener(int tabPosition) {
            this.tabPosition = tabPosition;
        }

        @Override
        public void onClick(View v) {
            getContentBinding().listViewPager.setCurrentItem(tabPosition, false);
            onTabChange(tabPosition);
        }
    }


    /**
     * 盘库状态
     */
    public enum RecordStatus {
        //开始盘库
        TO_START,
        //盘库进行中
        RECORDING,
        //盘库暂停
        PAUSED
    }


    /**
     * fragment的接口实现
     */
    public class FragmentStocktakeImp implements FragmentStocktake.IFragmentStocktake {

        private List<StocktakeDetailCarVO> mOfflineStocktakenVOs = new ArrayList<>();
        private volatile boolean mToStocktakeInited = false;

        @Override
        public void onToStocktakeDataInit(List<StocktakeDetailCarVO> list, boolean hasAvailable) {
            //从内存中找到盘点记录对应的盘点详情。保存到内存中去。
            synchronized (ActivityStocktake.class) {
                if (mToStocktakeInited) {
                    return;
                }
            }
            HashSet<Long> mToStocktakeRecordIds = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                mToStocktakeDetailIds.add(list.get(i).stocktakeDetailId);
                mToStocktakeRecordIds.add(list.get(i).stocktakeRecordId);
            }
            SharedPreferences sp = SharePreferenceHandler.getPreferences(STOCK_TAKEN_DETAILS);
            HashSet<Long> stocktakenDetailIds = new HashSet<>();
            for (Long recrodId : mToStocktakeRecordIds) {
                if (sp.contains(recrodId + "")) {
                    HashMap<Long, Long> detailTimeMap = new HashMap<>();
                    String detailInfoStr = sp.getString(recrodId + "", "");
                    if (!TextUtils.isEmpty(detailInfoStr)) {
                        detailTimeMap = mGson.fromJson(detailInfoStr, mLongLongHashMapType);
                    }
                    if (detailTimeMap.size() > 0) {
                        Iterator<Map.Entry<Long, Long>> iterator = detailTimeMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Long, Long> entry = iterator.next();
                            stocktakenDetailIds.add(entry.getKey());
                        }
                    }
                    mOfflineStocktakenData.put(recrodId, detailTimeMap);
                }
            }
            mToStocktakeDetailIds.removeAll(stocktakenDetailIds);
            saveAllOfflineData();

            if (stocktakenDetailIds.size() > 0) {
                mOfflineStocktakenVOs.clear();
                mOfflineStocktakenVOs.addAll(mVPAdpater.mToStocktakeFragment.removeData(stocktakenDetailIds));
                moveOfflineStocktaken();
            }
            //盘点未结束
            if (!checkCompleted()) {
                getContentBinding().recordBtnLeft.setVisibility(View.VISIBLE);
                //有当前时间内的盘点记录
                if (hasAvailable) {
                    getContentBinding().recordBtnLeft.setOnClickListener(v -> {
                        startOrPauseRecord();
                    });
                } else {
                    getContentBinding().recordBtnLeft.setOnClickListener(v -> {
                        AlertToast.show("盘点时间已过");
                    });
                }
            }
            mToStocktakeInited = true;

        }

        @Override
        public void onStocktakenDataGet(List<StocktakeDetailCarVO> list) {
            if (hasOfflineData()) {
                for (int i = 0; i < list.size(); i++) {
                    StocktakeDetailCarVO detailCarVO = list.get(i);
                    if (mOfflineStocktakenData.containsKey(detailCarVO.stocktakeRecordId)
                            ) {
                        mOfflineStocktakenData.get(detailCarVO.stocktakeRecordId).remove(detailCarVO.stocktakeDetailId);
                    }
                }
                saveAllOfflineData();
                moveOfflineStocktaken();
            }
            if (mToStocktakeInited) {
                checkCompleted();
            }
        }

        /**
         * 把离线的盘点数据，显示在右边的fragment里面
         */
        private void moveOfflineStocktaken() {
            mVPAdpater.mStocktakenFragment.addData(mOfflineStocktakenVOs);
        }

    }


}
