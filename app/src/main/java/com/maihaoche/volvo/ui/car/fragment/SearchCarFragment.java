package com.maihaoche.volvo.ui.car.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.utils.HintUtil;
import com.maihaoche.volvo.server.dto.InWarehouseCarVO;
import com.maihaoche.volvo.ui.car.adapter.SearchCarAdapter;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragment;
import com.maihaoche.volvo.ui.inwarehouse.cars.IngarageCarPresenter;
import com.maihaoche.volvo.view.dialog.SearchCarDialog;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/7/28
 * Email is gujian@maihaoche.com
 */

public class SearchCarFragment extends BaseListFragment<InWarehouseCarVO> {

    private SearchCarAdapter adapter;
    private SearchCarDialog mDialog;
    private InWarehouseCarVO currentSearchCar;
    private IngarageCarPresenter mPresenter;

    public static SearchCarFragment create() {
        return new SearchCarFragment();
    }

    @Override
    protected void onAfterCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onAfterCreateView(inflater, container, savedInstanceState);
        mPresenter = new IngarageCarPresenter(this);
        mPresenter.loadData();
    }

    @Override
    public PullRecyclerAdapter getAdapter() {
        adapter = new SearchCarAdapter(getContext());
        adapter.setClick(info -> {
            HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_FIND_CAR);
            startInventory();
            SearchCarDialog.SearchInfo searchInfo = new SearchCarDialog.SearchInfo();
            searchInfo.caraUnique = info.carUnique;
            searchInfo.carAttribut = info.carAttribute;
            searchInfo.carPosition = info.areaPositionName;
            currentSearchCar = info;
            mDialog = new SearchCarDialog(getContext(), searchInfo, false);
            mDialog.setOnDismissListener(dialog -> stopInventory(false));
            mDialog.show();
        });
        return adapter;
    }

    @Override
    public void fillData(List<InWarehouseCarVO> list) {
        adapter.addAll(list);
    }

    @Override
    protected void onTagResult(String rfid) {
        if (!TextUtils.isEmpty(rfid) && currentSearchCar != null && rfid.equals(currentSearchCar.carTagId)) {
            getActivity().runOnUiThread(() -> mDialog.setFind(true));
            HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_CAR_IS_NEARBY_WITH_VIBRATOR);
            stopInventory(false);
        }
    }
}
