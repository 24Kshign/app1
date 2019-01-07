package com.maihaoche.volvo.ui.inwarehouse.cars;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.volvo.server.dto.InWarehouseCarVO;

import java.util.List;

/**
 * 在库车辆列表的adapter
 */
public class InWarehouseCarAdpater extends PullRecyclerAdapter<InWarehouseCarVO> {

    private InWarehouseCarVH.ActionList mActionList = null;


    public InWarehouseCarAdpater(Context context, InWarehouseCarVH.ActionList action) {
        super(context);
        mActionList = action;
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new InWarehouseCarVH(parent);
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, int position) {
        super.OnBindViewHolder(holder, position);
        InWarehouseCarVH carVH  = (InWarehouseCarVH) holder;
        carVH.setData(position,getItem(position),mActionList);
    }

    public boolean addOne(InWarehouseCarVO carVO){
        List<InWarehouseCarVO> carVOs = getAllData();
        boolean flag = false;
        for(int i=0;i<carVOs.size();i++){
            if(carVOs.get(i).carId == carVO.carId){
                flag = true;
            }
        }

        if(!flag){
            carVOs.add(0,carVO);
        }
        clear();
        addAll(carVOs);
        return !flag;
    }

}
