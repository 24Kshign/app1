package com.maihaoche.volvo.ui.instorage.fragment;



import android.util.Log;

import com.maihaoche.commonbiz.module.dto.BaseResponse;
import com.maihaoche.commonbiz.module.dto.PagerResponse;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.server.ServerGetBuilder;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragmentPresenter;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragmentView;
import com.maihaoche.volvo.ui.common.daomain.InstorageInfo;
import com.maihaoche.volvo.ui.instorage.daomain.InstorageListRequest;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

/**
 * Created by gujian
 * Time is 2017/7/31
 * Email is gujian@maihaoche.com
 */

public class InstoragePresenter extends BaseListFragmentPresenter<InstorageInfo> {

    public InstoragePresenter(BaseListFragmentView view,String type){
        super(view,type);
    }


    @Override
    public void loadData() {
        InstorageListRequest request = new InstorageListRequest();
        request.pageNo = page;
        if(wmsId==null){
            AlertToast.show("请先选择仓库");
            return;
        }
        request.warehouseIdList = new ArrayList<>();
        request.warehouseIdList.add(wmsId);
        if(searchData!=null){
            request.searchParam = searchData;
        }
        if(type.equals(RuKuFragment.TYPE_RUKU)){
            getData(AppApplication.getServerAPI().pendingToStored(request));
        }else{
            getData(AppApplication.getServerAPI().haveInstoraged(request));
        }

    }

    private void getData(ServerGetBuilder<BaseResponse<PagerResponse<InstorageInfo>>> builder){
        Disposable disposable = builder.setOnDataError(emsg->{
            AlertToast.show(emsg);
        })
                .setOnDataGet(response->{
                    page = response.result.pageNo+1;
                    if((response.result == null || response.result.result.size() == 0)&&response.result.pageNo == 1){
                        view.showEmpty();
                    }else{
                        if(response.result.pageNo == 1){
                            view.clear();
                        }
                        view.setHeader(response.result.totalCount);
                        view.fillData(response.result.result);
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

}
