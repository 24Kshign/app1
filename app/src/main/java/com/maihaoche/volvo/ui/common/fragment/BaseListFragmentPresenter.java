package com.maihaoche.volvo.ui.common.fragment;

import com.maihaoche.commonbiz.module.ui.mvp.BasePresenter;
import com.maihaoche.volvo.AppApplication;

/**
 * Created by gujian
 * Time is 2017/8/4
 * Email is gujian@maihaoche.com
 */

public abstract class BaseListFragmentPresenter<T> implements BasePresenter {

    protected BaseListFragmentView<BaseListFragmentPresenter,T> view;
    protected int page = 1;
    protected String searchData;
    protected Long wmsId;
    protected String type;

    public BaseListFragmentPresenter(BaseListFragmentView view){
        this.view = view;
        view.setPresenter(this);
        wmsId = AppApplication.getGaragePO().getWmsGarageId();
    }

    public BaseListFragmentPresenter(BaseListFragmentView view,String type){
        this.view = view;
        this.type = type;
        view.setPresenter(this);
        wmsId = AppApplication.getGaragePO().getWmsGarageId();
    }

    public void setPage(int page){
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public void searchLoad(String string) {
        page = 1;
        searchData = string;
        loadData();
    }

    public void reload(){
        page=1;
        searchData="";
        loadData();
    }

    public void setWmsId(Long wmsId) {
        this.wmsId = wmsId;
        loadData();
    }

    public void setSearchData(String searchData) {
        this.searchData = searchData;
    }

    @Override
    public void clear() {
        view = null;
    }
}
