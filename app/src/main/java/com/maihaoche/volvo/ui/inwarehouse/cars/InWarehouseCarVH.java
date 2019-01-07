package com.maihaoche.volvo.ui.inwarehouse.cars;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ItemInWarehouseCarBinding;
import com.maihaoche.volvo.server.dto.InWarehouseCarVO;
import com.maihaoche.volvo.ui.common.daomain.KeyStatus;
import com.maihaoche.volvo.view.dialog.SelectPopWindow;

import static com.maihaoche.volvo.server.dto.InWarehouseCarVO.CAR_STORE_TYPE_S;

/**
 * vh
 */
public class InWarehouseCarVH extends BaseViewHolder<InWarehouseCarVO> {

    private InWarehouseCarVH.ActionList mActionList = null;

    private ItemInWarehouseCarBinding mBinding = null;

    public InWarehouseCarVH(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_warehouse_car, parent, false));
        mBinding = DataBindingUtil.bind(itemView);
        mBinding.carUniqueAttr.attrTitleText.setText("车架号");
        mBinding.carAttributeAttr.attrTitleText.setText("车辆属性");
        mBinding.carInWarehouseDayAttr.attrTitleText.setText("车辆库龄");
        mBinding.carOdometerAttr.attrTitleText.setText("里程");
        mBinding.carKeynumAttr.attrTitleText.setText("钥匙");
        mBinding.carAreaPositionAttr.attrTitleText.setText("仓库位置");

    }

    public void setData(int position,InWarehouseCarVO data, ActionList actionList) {
        super.setData(data);
        if(mBinding==null){
            return;
        }
        SelectPopWindow popWindow = new SelectPopWindow(getContext());
        mActionList = actionList;
        mBinding.itemNoText.setText("NO." + (position+1));
        KeyStatus keyStatus;
        if(data.keyStatus == null){
            keyStatus = KeyStatus.NULL;
        }else{
            keyStatus = KeyStatus.getStatus(data.keyStatus);
        }

        if(data.carStoreType == InWarehouseCarVO.CAR_STORE_TYPE_NS){
            mBinding.unstandIcon.setVisibility(View.VISIBLE);
        }else {
            mBinding.unstandIcon.setVisibility(View.GONE);
        }

        mBinding.carUniqueAttr.attrValueText.setText(data.carUnique);
        mBinding.carAttributeAttr.attrValueText.setText(data.carAttribute);
        mBinding.carInWarehouseDayAttr.attrValueText.setText(data.inWarehouseDay + "天");
        mBinding.carOdometerAttr.attrValueText.setText(data.odometer + "公里");
        mBinding.carKeynumAttr.attrValueText.setText(data.keyNumber + "把");
        if(TextUtils.isEmpty(data.areaPositionName)){
            mBinding.carAreaPositionAttr.attrContainer.setVisibility(View.GONE);
        }else {
            mBinding.carAreaPositionAttr.attrContainer.setVisibility(View.VISIBLE);
            mBinding.carAreaPositionAttr.attrValueText.setText(data.areaPositionName);
        }
        //钥匙柜相关
        if(mActionList!=null && mActionList instanceof InWarehouseCarVH.IsAbnormal){
            boolean isAbnormal = ((IsAbnormal) mActionList).isAbnormal();
            if(!isAbnormal && keyStatus.hasDesc()){
                mBinding.carKeynumAttr.statusDesc.setText(keyStatus.getDesc());
                mBinding.carKeynumAttr.statusArea.setVisibility(View.VISIBLE);
            }else{
                mBinding.carKeynumAttr.statusArea.setVisibility(View.GONE);
            }

            if(keyStatus.hasBtn()){
                mBinding.applyKey.setVisibility(View.VISIBLE);
                mBinding.applyKey.setText(keyStatus.getBtnText());
                if(keyStatus.getCode() == KeyStatus.WAITE_CANCEL.getCode()){
                    if(data.isShowCancelKey){
                        mBinding.applyKey.setVisibility(View.VISIBLE);
                    }else{
                        mBinding.applyKey.setVisibility(View.GONE);
                    }
                }
            }else{
                mBinding.applyKey.setVisibility(View.GONE);
            }
            mBinding.applyKey.setOnClickListener(v -> ((ActionKey) mActionList).option(data));
        }

        boolean hasAction = false;
        boolean hasMore = false;
        if (mActionList != null) {
            //进入详情
            mBinding.getRoot().setOnClickListener(v -> mActionList.toCarDetail(data));
            if(mActionList instanceof InWarehouseCarVH.ActionSearch  && !TextUtils.isEmpty(data.carTagId)){
                //快速找车
                popWindow.setFindCarListener(()->((InWarehouseCarVH.ActionSearch)mActionList).search(data),"找车");
                hasAction = true;
                hasMore = true;
            }

            if(mActionList instanceof ActionBind){
                if(keyStatus.getCode() == KeyStatus.WAITE_IN.getCode() || (data.keyStatus!=null && data.keyStatus == 60)){
                    popWindow.setBindingListener(()->((InWarehouseCarVH.ActionBind)mActionList).bind(data),"重新绑定");
                }

            }

            if(mActionList instanceof InWarehouseCarVH.ActionMail
                    && data.carStoreType==CAR_STORE_TYPE_S
                    ){
                //邮寄手续
                popWindow.setSendListener(()->{
                    ((InWarehouseCarVH.ActionMail)mActionList).mail(data);
                },"邮寄");
                hasAction = true;
                hasMore = true;
            }

            if(mActionList instanceof ActionReport){
                //上报异常
                mBinding.reportActionContainter.setVisibility(View.VISIBLE);
                mBinding.reportAction.setOnClickListener(v -> ((InWarehouseCarVH.ActionReport)mActionList).report(data));
                hasAction = true;
            }else {
                mBinding.reportActionContainter.setVisibility(View.GONE);
            }

            //添加或者移动库位
            if(mActionList instanceof ActionPosition){
                mBinding.positionActionContainter.setVisibility(View.VISIBLE);
                if(data.areaPositionId>0){
                    mBinding.positionAction.setText("移位");
                    mBinding.positionAction.setOnClickListener(v -> ((ActionPosition) mActionList).move(data));
                }else {
                    mBinding.positionAction.setText("添加库位");
                    mBinding.positionAction.setOnClickListener(v -> ((ActionPosition) mActionList).add(data));
                }
                hasAction = true;
            }else {
                mBinding.positionActionContainter.setVisibility(View.GONE);
            }

            if(hasMore){
                mBinding.moreAction.setOnClickListener(v->{
                    int[] location = SelectPopWindow.calculatePopWindowPos(mBinding.moreAction,popWindow.getContentView());
                    popWindow.showAtLocation(mBinding.moreAction, Gravity.TOP| Gravity.LEFT,location[0],location[1]);
                });
            }else{
                mBinding.moreAction.setVisibility(View.GONE);
            }

            if(hasAction){
                mBinding.actionContainer.setVisibility(View.VISIBLE);
            }else {
                mBinding.actionContainer.setVisibility(View.GONE);
            }
        }
    }

    public ItemInWarehouseCarBinding getBinding() {
        return mBinding;
    }

    public interface ActionList {
        void toCarDetail(InWarehouseCarVO carVO);

    }

    public interface ActionSearch extends ActionList {
        void search(InWarehouseCarVO carVO);

    }

    public interface ActionMail extends ActionList {
        void mail(InWarehouseCarVO carVO);

    }

    public interface ActionBind extends ActionList{
        void bind(InWarehouseCarVO carVO);
    }

    public interface ActionReport extends ActionList {
        void report(InWarehouseCarVO carVO);
    }

    public interface ActionPosition extends ActionList{
        void add(InWarehouseCarVO carVO);
        void move(InWarehouseCarVO carVO);
    }
    public interface ActionKey extends ActionList{
        void option(InWarehouseCarVO carVO);

    }

    public interface IsAbnormal extends ActionList{
        boolean isAbnormal();
    }
}
