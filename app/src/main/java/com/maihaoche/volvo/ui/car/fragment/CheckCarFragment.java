package com.maihaoche.volvo.ui.car.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.utils.HintUtil;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.ui.car.adapter.CheckCarAdapter;
import com.maihaoche.volvo.ui.common.daomain.InstorageInfo;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragment;
import com.maihaoche.volvo.ui.instorage.event.ListItemChangeEvent;
import com.maihaoche.volvo.view.dialog.SearchCarDialog;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by gujian
 * Time is 2017/7/28
 * Email is gujian@maihaoche.com
 */

public class CheckCarFragment extends BaseListFragment<InstorageInfo>{

    public static final String TYPE_CHECK_CAR = "type_check_car";
    public static final String TYPE_CHECK_CAR_TODAY = "type_check_car_today";

    private CheckCarAdapter adapter;
    private SearchCarDialog mDialog;
    private InstorageInfo currentSearchCar;

    public static CheckCarFragment create(String type){
        CheckCarFragment fragment = new CheckCarFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TYPE,type);
        fragment.setArguments(bundle);
        new CheckCaredPresenter(fragment,type);

        return fragment;
    }

    @Override
    protected void onAfterCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onAfterCreateView(inflater, container, savedInstanceState);
        //当列表数据发生变化，需要刷新的时候调用
        Disposable disposable = RxBus.getDefault().register(ListItemChangeEvent.class, o->{
            ListItemChangeEvent event = (ListItemChangeEvent) o;
            if(event.getType().equals(ListItemChangeEvent.TYPE_CHECK_CAR)){
                if(event.getInstorageInfo()!=null && mType.equals(CheckCarFragment.TYPE_CHECK_CAR)){
                    totleCount--;
                    header.setText("共"+totleCount+"辆车");
                    adapter.remove(event.getInstorageInfo());

                }else{
                    showContent();
                    totleCount++;
                    header.setText("共"+totleCount+"辆车");
                    adapter.add(event.getInstorageInfo());
                }
            }

            scrollToFirst();
        });
        mCompositeDisposable.add(disposable);
    }

    @Override
    public PullRecyclerAdapter getAdapter() {
        adapter = new CheckCarAdapter(getContext(),mType);
        adapter.setClick(info->{
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
    public void fillData(List<InstorageInfo> list) {
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
