package com.maihaoche.volvo.ui.instorage.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.module.ui.recyclerview.BaseRecyclerViewDecration;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.dao.po.WarehouseVO;
import com.maihaoche.volvo.databinding.ActivityGarageListBinding;
import com.maihaoche.volvo.ui.instorage.adapter.WarehouseListAdapter;

import java.util.List;

/**
 * 作者：yang
 * 时间：17/6/12
 * 邮箱：yangyang@maihaoche.com
 */
public class GarageListActivity extends HeaderProviderActivity<ActivityGarageListBinding> {


    public static final String CHOSEN_WAREHOUSE = "chosen_warehouse";

    private static final String CHOSEN_WAREHOUSE_ID = "chosen_warehouse_id";
    private static final String CHOSEN_WAREHOUSE_NAME = "chosen_warehouse_name";


    private WarehouseListAdapter mWarehouseListAdapter;
    private WarehouseVO mChosenWarehouse = new WarehouseVO();
    private long mInitChosenWarehouseId = 0;

    /**
     * @param context
     * @param chosenWmsGarageId 当前选中的wms仓库id
     * @return
     */
    public static Intent createIntent(Context context, long chosenWmsGarageId, String chosenWarehouseName) {
        Intent intent = new Intent(context, GarageListActivity.class);
        intent.putExtra(CHOSEN_WAREHOUSE_ID, chosenWmsGarageId);
        intent.putExtra(CHOSEN_WAREHOUSE_NAME, chosenWarehouseName);
        return intent;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_garage_list;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        init();
        load();
    }

    private void init() {
        //数据
        if (getIntent() != null) {
            mInitChosenWarehouseId = getIntent().getLongExtra(CHOSEN_WAREHOUSE_ID, 0);
            initHeader(getIntent().getStringExtra(CHOSEN_WAREHOUSE_NAME));
        }
        //view
        ActivityGarageListBinding binding = getContentBinding();
        binding.garageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.garageList.addItemDecoration(new BaseRecyclerViewDecration(10));
        mWarehouseListAdapter = new WarehouseListAdapter(this, garagePO -> {
            onChooseWarehouse(garagePO);
            ok();
        });
        mWarehouseListAdapter.setChoosenWarehouseId(mInitChosenWarehouseId);
        binding.garageList.setAdapter(mWarehouseListAdapter);
    }

    private void load() {
        AppApplication.getServerAPI().getWarehouseList()
                .setTransformer(getIOLoadingTransformer())
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setOnDataGet(response -> {
                    List<WarehouseVO> warehouseVOs = response.result;
                    if (warehouseVOs == null || warehouseVOs.size() == 0) {
                        cancelLoading(() -> AlertToast.show("未找到该用户对应的仓库信息"));
                        showEmpty();
                    } else {
                        showContent();
                        for (WarehouseVO warehouseVO : warehouseVOs) {
                            if (warehouseVO.warehouseId == mInitChosenWarehouseId) {
                                onChooseWarehouse(warehouseVO);
                                break;
                            }
                        }
                        mWarehouseListAdapter.addAll(warehouseVOs);
                    }
                })
                .setDoOnSubscribe(disposable -> showProgress())
                .call(this);
    }

    private void onChooseWarehouse(WarehouseVO warehouseVO) {
        mChosenWarehouse = warehouseVO;
        initHeader(mChosenWarehouse.warehouseName);
    }

    private void ok() {
        if (mChosenWarehouse == null) {
            AlertToast.show("请选择仓库");
            return;
        }
        if (mChosenWarehouse.warehouseId != mInitChosenWarehouseId) {
            Intent intent = new Intent();
            intent.putExtra(CHOSEN_WAREHOUSE, mChosenWarehouse);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }


}
