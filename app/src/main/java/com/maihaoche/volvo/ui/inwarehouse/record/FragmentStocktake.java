package com.maihaoche.volvo.ui.inwarehouse.record;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.server.dto.InWarehouseCarVO;
import com.maihaoche.volvo.server.dto.StocktakeDetailCarVO;
import com.maihaoche.volvo.server.dto.request.CarIdRequest;
import com.maihaoche.volvo.server.dto.request.WarehouseIdPageRequest;
import com.maihaoche.volvo.server.dto.response.ToStocktakeDataResponse;
import com.maihaoche.volvo.ui.car.cardetail.ActivityCarInfo;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragment;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragmentPresenter;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragmentView;
import com.maihaoche.volvo.ui.inwarehouse.cars.InWarehouseCarVH;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 类简介：盘点页面列表的fragment
 * 作者：  yang
 * 时间：  2017/8/11
 * 邮箱：  yangyang@maihaoche.com
 */

public class FragmentStocktake extends BaseListFragment<StocktakeDetailCarVO> {


    private Set<Long> stockTakenCarTagIds = new HashSet<>();//已盘点详情的id

    private long mWarehouseId;//当前的仓库id


    private boolean mStocktaken = false;//数据分类：true：已盘点。false:待盘点。

    private StocktakeVOAdapter mStocktakeAdapter;
    private StocktakeDetailPresenter mPresenter;

    private IFragmentStocktake mIFragmentStocktake;

    /**
     * 某个仓库的盘点数据
     *
     * @param mWarehouseId
     * @param mStocktaken
     * @return
     */
    public static FragmentStocktake creaet(long mWarehouseId, boolean mStocktaken) {
        FragmentStocktake fragment = new FragmentStocktake();
        fragment.mWarehouseId = mWarehouseId;
        fragment.mStocktaken = mStocktaken;
        return fragment;
    }


    public void setIFragmentStocktake(IFragmentStocktake IFragmentStocktake) {
        mIFragmentStocktake = IFragmentStocktake;
    }

    /**
     * 获取该fragment的数据，不可修改。
     */
    public synchronized List<StocktakeDetailCarVO> getDetails() {
        return mStocktakeAdapter.getAllData();
    }


    /**
     * 仓库id发生变化后
     *
     * @param warehouseId
     */
    public void changeWarehouseId(long warehouseId) {
        mWarehouseId = warehouseId;
        mPresenter.reload();
    }

    @Override
    protected void onAfterCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onAfterCreateView(inflater, container, savedInstanceState);
        mPresenter = new StocktakeDetailPresenter(this);
        mPresenter.loadData();
    }

    @Override
    public void onResume() {
        shouldLoad = false;
        super.onResume();
    }

    @Override
    public void onPullRefresh() {
        mPresenter.setPage(1);
        reload();
    }

    @Override
    public void onLoadMore() {
        super.onLoadMore();
        mPresenter.setPage(mPresenter.getPage() + 1);
        reload();

    }

    /**
     * 把数据移除掉
     */
    public List<StocktakeDetailCarVO> removeData(HashSet<Long> detialIds) {
        List<StocktakeDetailCarVO> detailCarVOList = new ArrayList<>();
        int removeCount = 0;
        if (mStocktakeAdapter.getCount() > 0) {
            List<StocktakeDetailCarVO> allDatas = mStocktakeAdapter.getAllData();
            for (int i = 0; i < allDatas.size(); i++) {
                StocktakeDetailCarVO vo = allDatas.get(i);
                if (detialIds.contains(vo.stocktakeDetailId)) {
                    mStocktakeAdapter.remove(vo);
                    detailCarVOList.add(vo);
                    allDatas.remove(i);
                    i--;
                    removeCount++;
                }
            }
        }
        setTotalCount();
        return detailCarVOList;
    }

    /**
     * 设置总数
     */
    public void setTotalCount() {
        header.setText("共" + mStocktakeAdapter.getCount() + "辆车");
    }


    public void addData(List<StocktakeDetailCarVO> list) {
        fillListData(list);
    }

    @Override
    public void fillData(List<StocktakeDetailCarVO> list) {
        fillListData(list);
    }

    private void fillListData(List<StocktakeDetailCarVO> list) {
        if (mPresenter.getPage() <= 1) {
            getAdapter().setNotifyOnChange(false);
            getAdapter().clear();
            getAdapter().setNotifyOnChange(true);
        }
        //数据处理
        if (list != null) {
            int addCount = list.size();
            List<StocktakeDetailCarVO> dataOld = mStocktakeAdapter.getAllData();
            //去重
            HashSet<Long> newIds = new HashSet<>();
            for (StocktakeDetailCarVO vo :
                    list) {
                newIds.add(vo.stocktakeDetailId);
            }
            for (int i = 0; i < dataOld.size(); i++) {
                if (newIds.contains(dataOld.get(i).stocktakeDetailId)) {
                    mStocktakeAdapter.remove(dataOld.remove(i));
                    i--;
                    addCount--;
                }
            }
            //add
            mStocktakeAdapter.addAll(list, true);
            setTotalCount();
            mStocktakeAdapter.sort((o1, o2) -> {
                if (o1.gmtModifiedTimeStamp == o2.gmtModifiedTimeStamp) {
                    return 0;
                } else if (o1.gmtModifiedTimeStamp > o2.gmtModifiedTimeStamp) {
                    return -1;
                } else {
                    return 1;
                }
            });
        }
        if (mStocktakeAdapter.getCount() == 0) {
            showEmpty();
        }
    }

    @Override
    public PullRecyclerAdapter getAdapter() {
        if (mStocktakeAdapter == null) {
            mStocktakeAdapter = new StocktakeVOAdapter(getActivity(), new StocktakeListAction());
            mStocktakeAdapter.setShowNoMoreViewPageSize(5000);
            mStocktakeAdapter.setDefaultNoMoreView();
        }
        return mStocktakeAdapter;
    }

    /**
     * 盘点下详情的数据处理类
     */
    private class StocktakeDetailPresenter extends BaseListFragmentPresenter {

        public StocktakeDetailPresenter(BaseListFragmentView view) {
            super(view);
        }

        @Override
        public void loadData() {
            if (mStocktaken) {
                //获取某个仓库当前的已盘点列表数据
                WarehouseIdPageRequest request = new WarehouseIdPageRequest();
                request.warehouseId = mWarehouseId;
                request.pageNo = page;
                request.pageSize = 5000;
                request.searchParam = searchData;
                AppApplication.getServerAPI().getStocktakenCars(request)
                        .setOnDataError(emsg -> AlertToast.show(emsg))
                        .setOnDataGet(pagerResponseBaseResponse -> {
                            page++;
                            fillData(pagerResponseBaseResponse.result.result);
                            setTotalCount();
                            if (mIFragmentStocktake != null) {
                                mIFragmentStocktake.onStocktakenDataGet(pagerResponseBaseResponse.result.result);
                            }
                        })
                        .call((BaseActivity) getActivity());
            } else {
                WarehouseIdPageRequest request = new WarehouseIdPageRequest();
                request.warehouseId = mWarehouseId;
                request.pageNo = page;
                request.searchParam = searchData;
                request.pageSize = 5000;
                AppApplication.getServerAPI().getToStocktakeCars(request)
                        .setOnDataError(emsg -> AlertToast.show(emsg))
                        .setOnDataGet(toStocktakeDataResponse -> {
                            ToStocktakeDataResponse response = toStocktakeDataResponse.result;
                            if (response == null) {
                                return;
                            }
                            if (response.toStocktakeCarVOs != null) {
                                fillData(response.toStocktakeCarVOs.result);
                                setTotalCount();
                            }
                            if (page == 1 && TextUtils.isEmpty(searchData)) {
                                if (mIFragmentStocktake != null) {
                                    mIFragmentStocktake.onToStocktakeDataInit(response.toStocktakeCarVOs.result, response.hasAvailable);
                                }
                            }

                        })
                        .setDoOnSubscribe(disposable -> {
                            if (page == 1) {
                                showProgress();
                            }
                        })
                        .call((BaseActivity) getActivity());
            }
        }

    }

    /**
     * 盘点列表的
     */
    private class StocktakeListAction implements InWarehouseCarVH.ActionList {

        @Override
        public void toCarDetail(InWarehouseCarVO carVO) {
            startActivity(ActivityCarInfo.createIntent(getActivity(), new CarIdRequest(carVO.carId, carVO.carStoreType)));
        }

    }

    /**
     * 盘点fragment与activity交互的接口
     */
    public interface IFragmentStocktake {

        /**
         * 待盘点数据第一次初始化
         *
         * @param list
         * @param hasAvailable 是否有可以盘点的数据
         */
        void onToStocktakeDataInit(List<StocktakeDetailCarVO> list, boolean hasAvailable);

        void onStocktakenDataGet(List<StocktakeDetailCarVO> list);

    }

    /**
     * 盘库列表的adapter
     */
    public static class StocktakeVOAdapter extends PullRecyclerAdapter<StocktakeDetailCarVO> {

        private InWarehouseCarVH.ActionList mActionList = null;

        public StocktakeVOAdapter(Context context, InWarehouseCarVH.ActionList actionList) {
            super(context);
            mActionList = actionList;
        }

        @Override
        public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            InWarehouseCarVH carVH = new InWarehouseCarVH(parent);
            carVH.getBinding().carInWarehouseDayAttr.attrContainer.setVisibility(View.GONE);
            carVH.getBinding().carOdometerAttr.attrContainer.setVisibility(View.GONE);
            carVH.getBinding().carKeynumAttr.attrContainer.setVisibility(View.GONE);
            return carVH;
        }

        @Override
        public void OnBindViewHolder(BaseViewHolder holder, int position) {
            super.OnBindViewHolder(holder, position);
            InWarehouseCarVH carVH = (InWarehouseCarVH) holder;
            carVH.setData(position, getItem(position).inWarehouseCarVO, mActionList);
        }
    }

}
