package com.maihaoche.volvo.ui.common.adapter;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.InitDataItemBinding;
import com.maihaoche.volvo.databinding.OutstorageItemBinding;
import com.maihaoche.volvo.server.dto.request.CarIdRequest;
import com.maihaoche.volvo.ui.car.activity.OutStorageInfoActivity;
import com.maihaoche.volvo.ui.car.cardetail.ActivityCarInfo;
import com.maihaoche.volvo.ui.car.domain.OutStorageInfo;
import com.maihaoche.volvo.ui.car.fragment.OutStorageFragment;
import com.maihaoche.volvo.ui.common.activity.RelaBarCodeActivity;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/7/28
 * Email is gujian@maihaoche.com
 */

public class InitDataAdapter extends PullRecyclerAdapter<OutStorageInfo> {

    private ItemClickListener listener;

    public void setListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public InitDataAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new InitDataViewHolder(DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.init_data_item,parent,false));
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, int position) {
        super.OnBindViewHolder(holder, position);
        InitDataViewHolder holder1 = (InitDataViewHolder) holder;

        holder1.itemView.setOnClickListener(v->{
            CarIdRequest request = new CarIdRequest();
            request.carId = getItem(position).carId;
            request.carStoreType = getItem(position).carStoreType;
            getContext().startActivity(ActivityCarInfo.createIntent(getContext(),request));
        });
        int index = position+1;
        holder1.binding.number.setText("NO."+index);
        holder1.binding.rela.setOnClickListener(v->{
            getContext().startActivity(RelaBarCodeActivity.createIntent(getContext(),getItem(position)));
        });
    }

    private static class InitDataViewHolder extends BaseViewHolder<OutStorageInfo>{

        public InitDataItemBinding binding;


        public InitDataViewHolder(InitDataItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        @Override
        public void setData(OutStorageInfo data) {
            super.setData(data);
            binding.frameCode.setText(data.carUnique);
            binding.carAttribute.setText(data.carAttribute);
            binding.carOld.setText(data.inWarehouseDay+"天");
            binding.mail.setText(data.odometer+"公里");
            binding.key.setText(data.keyNumber+"把");
        }
    }

    public interface ItemClickListener{
        void click(String data);
    }

    @Override
    public void remove(OutStorageInfo info){
        List<OutStorageInfo> list = getAllData();
        for(int i=0;i<list.size();i++){
            if(info.carId.longValue() == list.get(i).carId.longValue()){
                remove(i);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void add(OutStorageInfo object) {
        List<OutStorageInfo> list = getAllData();
        list.add(0,object);
        clear();
        addAll(list);
    }
}
