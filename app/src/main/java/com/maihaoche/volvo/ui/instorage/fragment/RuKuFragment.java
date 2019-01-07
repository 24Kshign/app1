package com.maihaoche.volvo.ui.instorage.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragment;
import com.maihaoche.volvo.ui.instorage.adapter.RukuAdapter;
import com.maihaoche.volvo.ui.common.daomain.InstorageInfo;
import com.maihaoche.volvo.ui.instorage.event.ListItemChangeEvent;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by gujian
 * Time is 2017/7/27
 * Email is gujian@maihaoche.com
 */

public class RuKuFragment extends BaseListFragment<InstorageInfo>{

    public static final String TYPE_RUKU = "type_ruku";
    public static final String TYPE_RUKU_TODAY = "type_ruku_today";

    public static InstorageInfo info;


    private RukuAdapter adapter;

    public static RuKuFragment create(String type){
        RuKuFragment fragment = new RuKuFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TYPE,type);
        fragment.setArguments(bundle);
        new InstoragePresenter(fragment,type);
        return fragment;
    }

    @Override
    protected void onAfterCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onAfterCreateView(inflater,container,savedInstanceState);
        //当列表数据发生变化，需要刷新的时候调用
        Disposable disposable = RxBus.getDefault().register(ListItemChangeEvent.class, o->{
            ListItemChangeEvent event = (ListItemChangeEvent) o;
            if(event.getType().equals(ListItemChangeEvent.TYPE_INSTORAGE)){
                if(event.getInstorageInfo()!=null && mType.equals(RuKuFragment.TYPE_RUKU)){
                    totleCount--;
                    header.setText("共"+totleCount+"辆车");
                    adapter.remove(event.getInstorageInfo());

                }else{
                    showContent();
                    totleCount++;
                    header.setText("共"+totleCount+"辆车");
                    adapter.add(event.getInstorageInfo());
                }

                scrollToFirst();
            //非标入库
            }else if(event.getType().equals(ListItemChangeEvent.TYPE_UNSTANDINSTORAGE)){
                if(event.getInstorageInfo()!=null && mType.equals(RuKuFragment.TYPE_RUKU)){
                    //非标入库的车辆刚好在待入库列表
                    info = adapter.remove2(event.getInstorageInfo());
                    if(info!=null){
                        totleCount--;
                        header.setText("共"+totleCount+"辆车");
                    }
                    
                }else if(event.getInstorageInfo()!=null){
                    if(info!=null&&!adapter.contain(info)){
                        showContent();
                        totleCount++;
                        header.setText("共"+totleCount+"辆车");
                        adapter.add(info);
                    }
                }
            }

        });
        mCompositeDisposable.add(disposable);
    }

    @Override
    public PullRecyclerAdapter getAdapter() {
        adapter = new RukuAdapter(getActivity(),mType);

        return adapter;
    }

    @Override
    public void fillData(List<InstorageInfo> list) {
        adapter.addAll(list);
    }

}
