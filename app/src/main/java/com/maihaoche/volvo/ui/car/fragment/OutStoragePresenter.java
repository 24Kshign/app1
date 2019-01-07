package com.maihaoche.volvo.ui.car.fragment;

import android.util.Log;

import com.maihaoche.commonbiz.module.dto.BaseResponse;
import com.maihaoche.commonbiz.module.dto.PagerResponse;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.server.ServerGetBuilder;
import com.maihaoche.volvo.ui.car.domain.OutStorageInfo;
import com.maihaoche.volvo.ui.car.domain.OutstorageListRequest;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragmentPresenter;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragmentView;
import com.maihaoche.volvo.ui.instorage.daomain.InstorageListRequest;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by gujian
 * Time is 2017/8/2
 * Email is gujian@maihaoche.com
 */

public class OutStoragePresenter extends BaseListFragmentPresenter{


    public OutStoragePresenter(BaseListFragmentView view,String type){
        super(view,type);
        view.setPresenter(this);
    }


    @Override
    public void loadData() {
        Log.e("hehe","loadData");
        OutstorageListRequest request = new OutstorageListRequest();
        request.pageNo = page;
        if(AppApplication.getGaragePO().getWmsGarageId()==null){
            AlertToast.show("请先选择仓库");
            return;
        }
        request.warehouseIdList = new ArrayList<>();
        request.warehouseIdList.add(AppApplication.getGaragePO().getWmsGarageId());
        if(searchData!=null){
            request.searchParam = searchData;
        }
        if(type.equals(OutStorageFragment.TYPE_OUT_STORAGE)){
            getData(AppApplication.getServerAPI().canOutStorage(request));
        }else{
            getData(AppApplication.getServerAPI().haveOutStorage(request));
        }


    }

    private void getData(ServerGetBuilder<BaseResponse<PagerResponse<OutStorageInfo>>> builder){
        Disposable disposable = builder.setOnDataError(emsg->{
            AlertToast.show(emsg);
        })
                .setOnDataGet(response->{

                    if((response.result == null || response.result.result.size() == 0)&&response.result.pageNo == 1){
                        view.showEmpty();
                    }else{
                        if(response.result.pageNo == 1){
                            view.clear();
                        }
                        view.setHeader(response.result.totalCount);
                        view.fillData(response.result.result);
                        page = response.result.pageNo+1;
                        view.showMore();
                    }

                })
                .setDoOnSubscribe(disposabl -> {
                    if(page == 1){
                        view.showProgress();
                    }
                })
                .call();
        view.pend(disposable);
    }

    @Override
    public void setPage(int page){
        this.page = page;
    }
}
