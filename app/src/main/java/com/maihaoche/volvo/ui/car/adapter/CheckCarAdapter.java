package com.maihaoche.volvo.ui.car.adapter;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.utils.DeviceUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.CheckCarItemBinding;
import com.maihaoche.volvo.databinding.RukuItemBinding;
import com.maihaoche.volvo.server.dto.request.CarIdRequest;
import com.maihaoche.volvo.ui.car.cardetail.ActivityCarInfo;
import com.maihaoche.volvo.ui.car.fragment.CheckCarFragment;
import com.maihaoche.volvo.ui.common.activity.OrderDetailActivity;
import com.maihaoche.volvo.ui.common.daomain.InstorageInfo;
import com.maihaoche.volvo.ui.instorage.activity.AddStorageInfoActivity;
import com.maihaoche.volvo.ui.instorage.activity.InStorageActivity;
import com.maihaoche.volvo.ui.instorage.daomain.CarCheckInfo;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/7/28
 * Email is gujian@maihaoche.com
 */

public class CheckCarAdapter extends PullRecyclerAdapter<InstorageInfo> {

    private ItemClickListener listener;
    private FindCarClick click;
    private String type;

    public void setListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public void setClick(FindCarClick click) {
        this.click = click;
    }

    public CheckCarAdapter(Context context, String type) {
        super(context);
        this.type = type;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new RukuViewHolder(DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.check_car_item,parent,false));
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, int position) {
        super.OnBindViewHolder(holder, position);
        RukuViewHolder holder1 = (RukuViewHolder) holder;

        if(getItem(position).priorCheck()){
            holder1.binding.firstCheckIcon.setVisibility(View.VISIBLE);
        }else{
            holder1.binding.firstCheckIcon.setVisibility(View.GONE);
        }

        if(type.equals(CheckCarFragment.TYPE_CHECK_CAR)){
            holder1.binding.checkCar.setVisibility(View.VISIBLE);
        }else{
            holder1.binding.checkCar.setVisibility(View.GONE);
        }

        int index = position+1;
        holder1.binding.number.setText("NO."+index);

        holder1.itemView.setOnClickListener(v->{
            CarIdRequest request = new CarIdRequest();
            request.carId = getItem(position).carId;
            request.carStoreType = getItem(position).carStoreType;
            getContext().startActivity(ActivityCarInfo.createIntent(getContext(),request));
        });
        holder1.binding.quickSearch.setOnClickListener(v->{
            if(!DeviceUtil.isSENTER()){
                AlertToast.show("非指定设备，找车功能无法使用");
                return;
            }
            if(click!=null){
                click.click(getItem(position));
            }
        });
        holder1.binding.checkCar.setOnClickListener(v->{
            getContext().startActivity(AddStorageInfoActivity.createIntent(getContext(),getItem(position)));
        });
    }

    private static class RukuViewHolder extends BaseViewHolder<InstorageInfo>{

        public CheckCarItemBinding binding;


        public RukuViewHolder(CheckCarItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        @Override
        public void setData(InstorageInfo data) {
            super.setData(data);
            binding.frameCode.setText(data.carUnique);
            binding.carAttribute.setText(data.carAttribute);
            binding.location.setText(data.areaPositionName);
            if(data.carTagId == null || data.carTagId.equals("")){
                binding.quickSearch.setVisibility(View.GONE);
            }else{
                binding.quickSearch.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface ItemClickListener{
        void click(String data);
    }

    public interface FindCarClick{
        void click(InstorageInfo data);
    }

    @Override
    public void remove(InstorageInfo info){
        List<InstorageInfo> list = getAllData();
        for(int i=0;i<list.size();i++){
            if(info.carId.longValue() == list.get(i).carId.longValue()){
                remove(i);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void add(InstorageInfo object) {
        List<InstorageInfo> list = getAllData();
        list.add(0,object);
        clear();
        addAll(list);
    }
}
