package com.maihaoche.volvo.ui.car.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.ui.car.adapter.OutListAdapter;
import com.maihaoche.volvo.ui.car.domain.OutStorageInfo;
import com.maihaoche.volvo.ui.common.daomain.ApplyKeyRequest;
import com.maihaoche.volvo.ui.common.daomain.CancelApplyKeyRequest;
import com.maihaoche.volvo.ui.common.daomain.KeyStatus;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragment;
import com.maihaoche.volvo.ui.instorage.event.ListItemChangeEvent;
import com.maihaoche.volvo.view.dialog.SelectReasonForKeyDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by gujian
 * Time is 2017/7/28
 * Email is gujian@maihaoche.com
 */

public class OutStorageFragment extends BaseListFragment<OutStorageInfo> {

    public static final String TYPE_OUT_STORAGE = "type_out_storage";
    public static final String TYPE_OUT_STORAGE_TODAY = "type_out_storage_today";

    private OutListAdapter adapter;

    public static OutStorageFragment create(String type){
        OutStorageFragment fragment = new OutStorageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TYPE,type);
        fragment.setArguments(bundle);
        new OutStoragePresenter(fragment,type);
        return fragment;
    }

    @Override
    protected void onAfterCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onAfterCreateView(inflater, container, savedInstanceState);

        setSearchUnEnableText(R.string.un_search_text);

        //当列表数据发生变化，需要刷新的时候调用
        Disposable disposable = RxBus.getDefault().register(ListItemChangeEvent.class, o->{
            ListItemChangeEvent event = (ListItemChangeEvent) o;
            if(event.getType().equals(ListItemChangeEvent.TYPE_OUTSTORAGE)){
                if(event.getOutStorageInfo()!=null && mType.equals(OutStorageFragment.TYPE_OUT_STORAGE)){
                    totleCount--;
                    header.setText("共"+totleCount+"辆车");
                    adapter.remove(event.getOutStorageInfo());

                }else{
                    showContent();
                    totleCount++;
                    header.setText("共"+totleCount+"辆车");
                    adapter.add(event.getOutStorageInfo());
                }
            }else if(event.getType().equals(ListItemChangeEvent.TYPE_BATCHOUTSTORAGE)){

                if(event.getInfos()!=null && mType.equals(OutStorageFragment.TYPE_OUT_STORAGE)){
                    totleCount-=event.getInfos().size();
                    header.setText("共"+totleCount+"辆车");
                    adapter.remove(event.getInfos());

                }else if(event.getInfos()!=null){
                    showContent();
                    totleCount+=event.getInfos().size();
                    header.setText("共"+totleCount+"辆车");
                    adapter.add(event.getInfos());
                }

            }
            scrollToFirst();

        });
        mCompositeDisposable.add(disposable);
    }


    @Override
    public PullRecyclerAdapter getAdapter() {
        adapter = new OutListAdapter(getContext(),mType);
        adapter.setListener(info -> {
            KeyStatus status = KeyStatus.getStatus(info.keyStatus);
            if(status.getCode() == 30){
                applyKey(info);
            }else{
                cancelApplyKey(info);
            }
        });

        return adapter;
    }

    private void applyKey(OutStorageInfo info){
        ApplyKeyRequest request = new ApplyKeyRequest();
        request.carKeyIds = new ArrayList<>();
        request.carKeyIds.add(info.carKeyId);
        request.riskReasonType = SelectReasonForKeyDialog.Reason.REASON_OUT;
        AppApplication.getServerAPI().applyKey(request)
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setOnDataGet(baseResponse -> {
                    AlertToast.show("申请成功");
                    info.keyStatus = KeyStatus.WAITE_CANCEL.getCode();
                    adapter.notifyDataSetChanged();
                })
                .call(activity);
    }

    private void cancelApplyKey(OutStorageInfo info){
        CancelApplyKeyRequest request = new CancelApplyKeyRequest();
        request.carKeyId= info.carKeyId;
        AppApplication.getServerAPI().cancelApplyKey(request)
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setOnDataGet(baseResponse -> {
                    AlertToast.show("取消成功");
                    info.keyStatus = KeyStatus.WAITE_APPLY.getCode();
                    adapter.notifyDataSetChanged();
                })
                .call(activity);
    }


    @Override
    public void fillData(List<OutStorageInfo> list) {
        adapter.addAll(list);
    }

}
