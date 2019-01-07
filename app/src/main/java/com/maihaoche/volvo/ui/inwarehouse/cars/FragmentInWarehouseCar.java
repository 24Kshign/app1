package com.maihaoche.volvo.ui.inwarehouse.cars;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.utils.DeviceUtil;
import com.maihaoche.commonbiz.service.utils.HintUtil;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.server.dto.CarBaseInfoOnMailVO;
import com.maihaoche.volvo.server.dto.InWarehouseCarVO;
import com.maihaoche.volvo.server.dto.request.AreaPositionMoveRequest;
import com.maihaoche.volvo.server.dto.request.CarIdRequest;
import com.maihaoche.volvo.server.dto.request.WarehouseIdPageRequest;
import com.maihaoche.volvo.ui.car.cardetail.ActivityCarInfo;
import com.maihaoche.volvo.ui.common.activity.ChangeKeyActivity;
import com.maihaoche.volvo.ui.common.activity.RelaKeyActivity;
import com.maihaoche.volvo.ui.common.daomain.ApplyKeyRequest;
import com.maihaoche.volvo.ui.common.daomain.CancelApplyKeyRequest;
import com.maihaoche.volvo.ui.common.daomain.KeyStatus;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragment;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragmentPresenter;
import com.maihaoche.volvo.ui.common.fragment.BaseListFragmentView;
import com.maihaoche.volvo.ui.setting.RefreshEvent;
import com.maihaoche.volvo.view.dialog.MoveStorageDialog;
import com.maihaoche.volvo.view.dialog.SearchCarDialog;
import com.maihaoche.volvo.view.dialog.SelectReasonForKeyDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;

import static com.maihaoche.volvo.server.dto.InWarehouseCarVO.CAR_STORE_TYPE_NS;

/**
 * 类简介：在库车辆的列表的fragment
 * 作者：  yang
 * 时间：  2017/8/10
 * 邮箱：  yangyang@maihaoche.com
 */

public class FragmentInWarehouseCar extends BaseListFragment<InWarehouseCarVO> {

    private IngarageCarPresenter mPresenter;
    private long mWarehouseId;
    private boolean mAbnormal;
    private InWarehouseCarAdpater mInWarehouseCarAdpater;


    private SearchCarDialog mSearchCarDialog;

    public static FragmentInWarehouseCar create(long warehouseId, boolean abnormal) {
        FragmentInWarehouseCar fragment = new FragmentInWarehouseCar();
        fragment.mWarehouseId = warehouseId;
        fragment.mAbnormal = abnormal;
        return fragment;
    }


    public void changeWarehouseId(long warehouseId) {
        mWarehouseId = warehouseId;
        mPresenter.reload();
    }

    @Override
    protected void onAfterCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onAfterCreateView(inflater, container, savedInstanceState);
        mPresenter = new IngarageCarPresenter(FragmentInWarehouseCar.this);
        mPresenter.loadData();
        Disposable disposable = RxBus.getDefault().register(ReportEvent.class, o->{
           ReportEvent event = (ReportEvent) o;
            if(mAbnormal&&event.carVO!=null){
                boolean flag = mInWarehouseCarAdpater.addOne(event.carVO);
                if(flag){
                    totleCount++;
                    header.setText("共"+totleCount+"辆车");
                    mInWarehouseCarAdpater.notifyItemChanged(0);
                }
            }
        });

        Disposable disposable1 = RxBus.getDefault().register(RefreshEvent.class,event->{
            mPresenter.reload();
        });

        mCompositeDisposable.add(disposable);
        mCompositeDisposable.add(disposable1);
    }

    @Override
    public void onResume() {
        shouldLoad = false;
        super.onResume();
        clearSearchText();
    }

    @Override
    public synchronized PullRecyclerAdapter getAdapter() {
        if (mInWarehouseCarAdpater == null) {
            //异常车辆
            if(mAbnormal){
                mInWarehouseCarAdpater = new InWarehouseCarAdpater(getContext(), new ExceptionListActionImp());

            }else {
                mInWarehouseCarAdpater = new InWarehouseCarAdpater(getContext(), new ListActionImp());

            }
            mInWarehouseCarAdpater.setDefaultNoMoreView();
        }
        return mInWarehouseCarAdpater;
    }

    @Override
    public void fillData(List<InWarehouseCarVO> list) {
        if (list != null && list.size() >= 0) {
            getAdapter().addAll(list, true);
        }
        if (getAdapter().getCount() == 0) {
            showEmpty();
        }
    }

    @Override
    protected void onTagResult(String rfid) {
        super.onTagResult(rfid);
        if (!TextUtils.isEmpty(rfid) && mSearchCarDialog != null && rfid.equals(mSearchCarDialog.getTagId())) {
            getHandler().post(() -> mSearchCarDialog.setFind(true));
            HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_CAR_IS_NEARBY_WITH_VIBRATOR);
            stopInventory(false);
        }
    }


    @Override
    public void onLoadMore() {
        mPresenter.setPage(mPresenter.getPage() + 1);
        reload();
    }

    /**
     * 数据的获取
     */
    public class IngarageCarPresenter extends BaseListFragmentPresenter {

        public IngarageCarPresenter(BaseListFragmentView view) {
            super(view);
        }

        @Override
        public void loadData() {
            WarehouseIdPageRequest request = new WarehouseIdPageRequest();
            request.warehouseId = mWarehouseId;
            request.pageNo = page;
            request.searchParam = searchData;
            BiConsumer<List<InWarehouseCarVO>, Integer> onDataGet = (inWarehouseCarVOs, totalCount) -> {
                if (page <= 1) {
                    getAdapter().clear();
                }
                fillData(inWarehouseCarVOs);
                setHeader(totalCount);
//                setTotalCount("共" + totalCount + "辆车");
            };
            if (mAbnormal) {
                AppApplication.getServerAPI().getInGarageAbnCars(request)
                        .setOnDataError(emsg -> AlertToast.show(emsg))
                        .setOnDataGet(pagerResponseBaseResponse -> {
                            showContent();
                            onDataGet.accept(pagerResponseBaseResponse.result.result, pagerResponseBaseResponse.result.totalCount);
                        })
                        .setDoOnSubscribe(disposable -> showProgress())
                        .call((BaseActivity) getActivity());
            } else {
                AppApplication.getServerAPI().getInGarageCars(request)
                        .setOnDataError(emsg -> AlertToast.show(emsg))
                        .setOnDataGet(pagerResponseBaseResponse -> {
                            showContent();
                            onDataGet.accept(pagerResponseBaseResponse.result.result, pagerResponseBaseResponse.result.totalCount);
                        })
                        .setDoOnSubscribe(disposable -> showProgress())
                        .call((BaseActivity) getActivity());
            }
        }
    }


    /**
     * 异常车辆列表的action
     */
    private class ExceptionListActionImp implements InWarehouseCarVH.ActionList,InWarehouseCarVH.IsAbnormal{

        @Override
        public void toCarDetail(InWarehouseCarVO carVO) {
            startActivity(ActivityCarInfo.createIntent(getActivity(), new CarIdRequest(carVO.carId, carVO.carStoreType)));
        }

        @Override
        public boolean isAbnormal() {
            return mAbnormal;
        }
    }


    /**
     * 在库车辆列表的回调
     */
    private class ListActionImp extends ExceptionListActionImp implements
            InWarehouseCarVH.ActionSearch
            , InWarehouseCarVH.ActionMail
            , InWarehouseCarVH.ActionBind
            , InWarehouseCarVH.ActionReport
            , InWarehouseCarVH.ActionPosition,InWarehouseCarVH.ActionKey{

        @Override
        public void search(InWarehouseCarVO carVO) {
            if(!DeviceUtil.isSENTER()){
                AlertToast.show("非指定设备，无法使用找车功能");
                return;
            }
            HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_FIND_CAR);
            startInventory();
            SearchCarDialog.SearchInfo searchInfo = new SearchCarDialog.SearchInfo();
            searchInfo.caraUnique = carVO.carUnique;
            searchInfo.carAttribut = carVO.carAttribute;
            searchInfo.carTagId = carVO.carTagId;
            mSearchCarDialog = new SearchCarDialog(getActivity(), searchInfo, false);
            mSearchCarDialog.setOnDismissListener(dialog -> stopInventory(false));
            mSearchCarDialog.show();
        }


        @Override
        public void mail(InWarehouseCarVO carVO) {
            if (carVO.carStoreType == CAR_STORE_TYPE_NS) {
                AlertToast.show("非标在库的车辆，不能邮寄手续");
                return;
            }
            BaseActivity activity = (BaseActivity) getActivity();
            CarIdRequest request = new CarIdRequest(carVO.carId, carVO.carStoreType);
            AppApplication.getServerAPI().getBaseCarInfo(request)
                    .setOnDataError(emsg -> AlertToast.show(emsg))
                    .setOnDataGet(response -> {
                        if (response == null || response.result == null) {
                            AlertToast.show("未找到该车辆");
                            return;
                        }
                        CarBaseInfoOnMailVO.CheckInfo4Mail checkInfo4Mail = response.result.checkInfo4Mail;
                        if (checkInfo4Mail == null) {
                            AlertToast.show("没有可以邮寄的手续或钥匙");
                            return;
                        }
                        startActivity(AcitivtyMailSubmit.createIntent(activity, request, response.result));
                        activity.pend(RxBus.getDefault().register(KeyNumChangeEvent.class, (Consumer<KeyNumChangeEvent>) keyNumChangeEvent -> {
                            carVO.keyNumber = keyNumChangeEvent.keyNumLeft;
                            mInWarehouseCarAdpater.notifyDataSetChanged();
                        }));
                    })
                    .call(activity);
        }

        @Override
        public void report(InWarehouseCarVO carVO) {
            startActivity(ActivityReport.createIntent(getActivity(), carVO));
        }

        //添加库位
        @Override
        public void add(InWarehouseCarVO carVO) {
            move(carVO, "添加库位成功");
        }

        //移动库位
        @Override
        public void move(InWarehouseCarVO carVO) {
            move(carVO, "移位成功");
        }

        private void move(InWarehouseCarVO carVO, String successHint) {
            BaseActivity activity = (BaseActivity) getActivity();
            new MoveStorageDialog(activity,carVO.carId,carVO.areaPositionName, (name, id) -> {
                AreaPositionMoveRequest request = new AreaPositionMoveRequest();
                request.carId = carVO.carId;
                request.carStoreType = carVO.carStoreType;
                request.areaPositionId = id;
                AppApplication.getServerAPI().areaPosition(request)
                        .setOnDataError(emsg -> AlertToast.show(emsg))
                        .setOnDataGet(baseResponse -> {
                            carVO.areaPositionId = id;
                            carVO.areaPositionName = name;
                            mInWarehouseCarAdpater.notifyDataSetChanged();
                            AlertToast.show(successHint);
                        })
                        .call(activity);

            }).show();
        }

        @Override
        public void option(InWarehouseCarVO carVO) {
            KeyStatus status = KeyStatus.getStatus(carVO.keyStatus);
            if(status.getCode() == 0){
                Intent intent = RelaKeyActivity.createIntent(getContext(),carVO);
                startActivity(intent);
                RxBus.getDefault().register(RefreshEvent.class,event->{
                    reload();
                });
            }else if(status.getCode() == 30){
                applyKey(carVO);
            }else{
                cancelApplyKey(carVO);
            }
        }

        @Override
        public boolean isAbnormal() {
            return mAbnormal;
        }

        @Override
        public void bind(InWarehouseCarVO carVO) {
            if(carVO.keyStatus!=null && carVO.keyStatus == KeyStatus.WAITE_BIND.getCode()){
                startActivity( RelaKeyActivity.createIntent(getContext(),carVO));
            }else{
                startActivity( ChangeKeyActivity.createIntent(getContext(),carVO));
            }

        }
    }

    private void applyKey(final InWarehouseCarVO carVO){
        BaseActivity activity = (BaseActivity) getActivity();
        new SelectReasonForKeyDialog(getContext(), reason->{
            ApplyKeyRequest request = new ApplyKeyRequest();
            request.carKeyIds = new ArrayList<>();
            request.carKeyIds.add(carVO.carKeyId);
            request.riskReasonType = reason.id;
            AppApplication.getServerAPI().applyKey(request)
                    .setOnDataError(emsg -> {
                        showContent();
                        AlertToast.show(emsg);
                    })
                    .setOnDataGet(baseResponse -> {
                        showContent();
                        carVO.keyStatus = KeyStatus.getStatus(KeyStatus.WAITE_CANCEL.getCode()).getCode();
                        mInWarehouseCarAdpater.notifyDataSetChanged();

                    })
                    .setDoOnSubscribe(sub->{
                        showProgress();
                    })
                    .call(activity);
        }).show();

    }

    private void cancelApplyKey(final InWarehouseCarVO carVO){
        BaseActivity activity = (BaseActivity) getActivity();
        CancelApplyKeyRequest request = new CancelApplyKeyRequest();
        request.carKeyId= carVO.carKeyId;
        AppApplication.getServerAPI().cancelApplyKey(request)
                .setOnDataError(emsg -> {
                    showContent();
                    AlertToast.show(emsg);
                })
                .setOnDataGet(baseResponse -> {
                    showContent();
                    carVO.keyStatus = KeyStatus.getStatus(KeyStatus.WAITE_APPLY.getCode()).getCode();
                    mInWarehouseCarAdpater.notifyDataSetChanged();
                })
                .setDoOnSubscribe(sub->{
                    showProgress();
                })
                .call(activity);
    }

}
