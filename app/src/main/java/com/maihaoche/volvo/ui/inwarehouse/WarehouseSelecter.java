package com.maihaoche.volvo.ui.inwarehouse;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.dao.po.GaragePO;
import com.maihaoche.volvo.dao.po.WarehouseVO;
import com.maihaoche.volvo.ui.instorage.adapter.WarehouseListAdapter;
import com.maihaoche.volvo.view.dialog.DialogVerticalList;

import java.util.List;


/**
 * 类简介： 顶部显示 仓库选择器 的工具类
 * 作者：  yang
 * 时间：  2017/8/11
 * 邮箱：  yangyang@maihaoche.com
 */

public abstract class WarehouseSelecter {


    private WarehouseListAdapter mWarehouseListAdapter;
    private long mCurrentWarehouseId = 0;


    /**
     * 将一个HeaderProviderActivity 添加头部选择仓库点击下拉
     *
     * @param activity
     */
    public void adapte(HeaderProviderActivity activity) {
        if (true) {
            // TODO: 2017/8/21 这个版本先不加数据筛选。下个版本再来。
            return;
        }
        GaragePO garagePO = AppApplication.getGaragePO();
        mCurrentWarehouseId = garagePO.getWmsGarageId();
        activity.initTitleWithClick(garagePO.getGarageName(), R.drawable.ic_triangle_down
                , v -> showGrageListDialog(activity)
        );
    }

    private void showGrageListDialog(HeaderProviderActivity activity) {
        if (mWarehouseListAdapter == null) {
            // TODO: 2017/8/14 这里需要使用最新的仓库分区
            AppApplication.getServerAPI().getWarehouseList()
                    .setOnDataGet(response -> {
                        List<WarehouseVO> warehouseVOs = response.result;
                        mWarehouseListAdapter = new WarehouseListAdapter(activity, warehouseVO -> {
                            if (warehouseVO.warehouseId == mCurrentWarehouseId) {
                                return;
                            }
                            mWarehouseListAdapter.setChoosenWarehouseId(warehouseVO.warehouseId);
                            activity.initHeader(warehouseVO.warehouseName);
                            onWarehouseChange(warehouseVO.warehouseId);
                        });
                        mWarehouseListAdapter.addAll(warehouseVOs);
                        mWarehouseListAdapter.setChoosenWarehouseId(mCurrentWarehouseId);
                        showGraggeListDialog(activity, mWarehouseListAdapter);
                    }).call(activity);
        } else {
            showGraggeListDialog(activity, mWarehouseListAdapter);
        }

    }

    private void showGraggeListDialog(Activity activity, @NonNull WarehouseListAdapter adapter) {
        new DialogVerticalList(activity)
                .setShowAtLocation(0, SizeUtil.dip2px(60))
                .setListAdapter(adapter)
                .show();
    }

    /**
     * @return
     */
    public long getCurrentWarehouseId() {
        return AppApplication.getGaragePO().getWmsGarageId();
    }

    /**
     * 需要子类实现，当garage发生变化后要做的事情。
     *
     * @param warehouseId
     */
    abstract protected void onWarehouseChange(long warehouseId);


}
