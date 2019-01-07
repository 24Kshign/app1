package com.maihaoche.volvo.ui.instorage.adapter;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.RukuItemBinding;
import com.maihaoche.volvo.server.dto.request.CarIdRequest;
import com.maihaoche.volvo.ui.car.cardetail.ActivityCarInfo;
import com.maihaoche.volvo.ui.common.activity.OrderDetailActivity;
import com.maihaoche.volvo.ui.instorage.activity.InStorageActivity;
import com.maihaoche.volvo.ui.common.daomain.InstorageInfo;
import com.maihaoche.volvo.ui.instorage.fragment.RuKuFragment;
import com.maihaoche.volvo.view.dialog.CarryManDialog;

import java.util.List;


/**
 * Created by gujian
 * Time is 2017/7/28
 * Email is gujian@maihaoche.com
 */

public class RukuAdapter extends PullRecyclerAdapter<InstorageInfo> {

    private ItemClickListener listener;
    private String type;

    public void setListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public RukuAdapter(Context context,String type) {
        super(context);
        this.type = type;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new InstorageHolder(DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.ruku_item,parent,false));
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, int position) {
        super.OnBindViewHolder(holder, position);
        InstorageHolder holder1 = (InstorageHolder) holder;
        InstorageInfo info = getItem(position);
        holder1.fillData(getItem(position));
        if(type.equals(RuKuFragment.TYPE_RUKU)){
            holder1.binding.button.setVisibility(View.VISIBLE);
            holder1.binding.unstandIcon.setVisibility(View.GONE);
            holder1.binding.locationArea.setVisibility(View.GONE);
            holder1.binding.arrayDate.setVisibility(View.VISIBLE);
        }else{
            holder1.binding.button.setVisibility(View.GONE);
            holder1.binding.location.setVisibility(View.VISIBLE);
            holder1.binding.arrayDate.setVisibility(View.GONE);
            if(getItem(position).carStoreType == 2){
                holder1.binding.unstandIcon.setVisibility(View.VISIBLE);
                holder1.binding.unstandIcon.setText(getItem(position).carEnterTypeName);
            }else{
                holder1.binding.unstandIcon.setVisibility(View.GONE);
            }

        }
        int index = position+1;
        holder1.binding.number.setText("NO."+index);

        holder1.itemView.setOnClickListener(v->{
            CarIdRequest request = new CarIdRequest();
            request.carId = getItem(position).carId;
            request.carStoreType = getItem(position).carStoreType;
            getContext().startActivity(ActivityCarInfo.createIntent(getContext(),request));
        });
        holder1.binding.button.setOnClickListener(v->{
            getContext().startActivity(InStorageActivity.createIntent(getContext(),getItem(position)));
        });
        holder1.binding.orderCode.setOnClickListener(v->{
            getContext().startActivity(OrderDetailActivity.create(getContext(),getItem(position).orderNo));
        });
    }

    private static class InstorageHolder extends BaseViewHolder<InstorageInfo>{

        public RukuItemBinding binding;

        public InstorageHolder(RukuItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void fillData(InstorageInfo data) {
            binding.frameCode.setText(data.carUnique);
            binding.carAttribute.setText(data.carAttribute);
            if(TextUtils.isEmpty(data.orderNo)){
                binding.orderCodeArea.setVisibility(View.GONE);
            }else{
                binding.orderCodeArea.setVisibility(View.VISIBLE);
                binding.orderCode.setText(data.orderNo);
            }
            binding.client.setText(data.customer);
            binding.contact.setText(data.salesman);
            binding.location.setText(data.areaPositionName);
            binding.orderNumber.setText(data.allotOrderNo);
            binding.arrayDate.setText(data.expectedWarehousingTime);
        }
    }

    public interface ItemClickListener{
        void click(InstorageInfo data);
    }

    @Override
    public void remove(InstorageInfo info){
        List<InstorageInfo> list = getAllData();
        for(int i=0;i<list.size();i++){
            if(info.carId.longValue() == list.get(i).carId.longValue()){
                remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public InstorageInfo remove2(InstorageInfo info){
        List<InstorageInfo> list = getAllData();
        InstorageInfo info2 = null;
        for(int i=0;i<list.size();i++){
            if(info.carUnique.equals(list.get(i).carUnique)){
                info2 = list.get(i);
                remove(i);
                notifyDataSetChanged();
                break;
            }
        }
        return info2;
    }

    @Override
    public void add(InstorageInfo object) {
        List<InstorageInfo> list = getAllData();
        list.add(0,object);
        clear();
        addAll(list);
    }

    public boolean contain(InstorageInfo object){
        List<InstorageInfo> list = getAllData();
        boolean flag = false;
        for(int i=0;i<list.size();i++){
            if(object.carUnique.equals(list.get(i).carUnique)){
                flag = true;
                break;
            }
        }
        return flag;
    }
}
