package com.maihaoche.volvo.ui.inwarehouse.cars;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseFragmentActivity;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.dao.po.GaragePO;
import com.maihaoche.volvo.server.dto.request.WarehouseIdPageRequest;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragment;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragmentPresenter;

import java.util.Objects;

/**
 * Created by manji
 * Date：2018/12/4 2:30 PM
 * Desc：
 */
public class IngarageCarPresenter extends BaseListFragmentPresenter {

    private long mWarehouseId;
    private BaseListFragment mFragment;

    public IngarageCarPresenter(BaseListFragment fragment) {
        super(fragment);
        GaragePO garagePO = AppApplication.getGaragePO();
        mWarehouseId = garagePO.getWmsGarageId();
        mFragment = fragment;
    }

    @Override
    public void loadData() {
        WarehouseIdPageRequest request = new WarehouseIdPageRequest();
        request.warehouseId = mWarehouseId;
        request.pageNo = page;
        request.searchParam = searchData;
        AppApplication.getServerAPI().getInGarageCars(request)
                .setOnDataError(AlertToast::show)
                .setOnDataGet(response -> {

                    if ((response.result == null || response.result.result.size() == 0) && Objects.requireNonNull(response.result).pageNo == 1) {
                        view.showEmpty();
                    } else {
                        if (response.result.pageNo == 1) {
                            view.clear();
                        }
                        mFragment.setHeader(response.result.totalCount);
                        mFragment.fillData(response.result.result);
                        page = response.result.pageNo + 1;
                        mFragment.showMore();
                    }
                })
                .setDoOnSubscribe(disposable -> {
                    if (page == 1) {
                        mFragment.showProgress();
                    }
                })
                .call((BaseFragmentActivity) mFragment.getActivity());
    }
}
