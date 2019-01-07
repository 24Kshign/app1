package com.maihaoche.volvo.ui.car.adapter;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.BatchOutItemBinding;
import com.maihaoche.volvo.databinding.OutstorageItemBinding;
import com.maihaoche.volvo.ui.car.activity.OutStorageInfoActivity;
import com.maihaoche.volvo.ui.car.domain.OutStorageInfo;
import com.maihaoche.volvo.ui.car.fragment.OutStorageFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gujian
 * Time is 2017/7/28
 * Email is gujian@maihaoche.com
 */

public class BatchAdapter extends PullRecyclerAdapter<OutStorageInfo> {

    private ItemClickListener listener;
    private SelectedListener selectedListener;
    private boolean isSelectAll = false;

    public void setListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public void setSelectedListener(SelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    public boolean getIsSelectAll(){
        return isSelectAll;
    }

    public BatchAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new RukuViewHolder(DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.batch_out_item,parent,false));
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, int position) {
        super.OnBindViewHolder(holder, position);
        RukuViewHolder holder1 = (RukuViewHolder) holder;

        int index = position+1;
        holder1.binding.number.setText("NO."+index);

        holder1.itemView.setOnClickListener(v->{
            if(listener!=null){
                listener.click("hehe");
            }
        });

        if(getItem(position).isSelect){
            holder1.binding.checkButton.setChecked(true);
        }else{
            holder1.binding.checkButton.setChecked(false);
        }

        holder1.binding.checkButton.setOnClickListener(v->{

            if(holder1.binding.checkButton.isChecked()){
                getItem(position).isSelect = true;
                if(isAllSelect()){
                    isSelectAll = true;
                }

            }else{
                getItem(position).isSelect = false;
                isSelectAll = false;
            }

            if(selectedListener!=null){
                selectedListener.select(getItem(position));
            }

        });

    }

    public ArrayList<OutStorageInfo> getSelect(){
        ArrayList<OutStorageInfo> list = new ArrayList<>();
        for(OutStorageInfo info : getAllData()){
            if(info.isSelect){
                list.add(info);
            }
        }
        return list;
    }

    public boolean isAllSelect(){
        for(OutStorageInfo info : getAllData()){
            if(info.isSelect == false){
                return false;
            }
        }
        return true;
    }

    public void selectAll(){
        isSelectAll = true;
        for(OutStorageInfo info : getAllData()){
            info.isSelect = true;
        }
        notifyDataSetChanged();
    }

    public void unSelect(){
        isSelectAll = false;
        for(OutStorageInfo info : getAllData()){
            info.isSelect = false;
        }
        notifyDataSetChanged();
    }

    private static class RukuViewHolder extends BaseViewHolder<OutStorageInfo>{

        public BatchOutItemBinding binding;


        public RukuViewHolder(BatchOutItemBinding binding) {
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
            binding.location.setText(data.areaPositionName);
        }
    }

    public interface ItemClickListener{
        void click(String data);
    }

    public interface SelectedListener{
        void select(OutStorageInfo info);
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

    public void remove(List<OutStorageInfo> infos){
        List<OutStorageInfo> list = getAllData();
        for(int i=0;i<infos.size();i++){
            for(int j=0;j<list.size();j++){
                if(infos.get(i).carId.longValue() == list.get(j).carId.longValue()){
                    list.remove(j);
                    continue;
                }
            }
        }
        clear();
        addAll(list);
        notifyDataSetChanged();

    }

    @Override
    public void add(OutStorageInfo object) {
        List<OutStorageInfo> list = getAllData();
        list.add(0,object);
        clear();
        addAll(list);
    }

}
