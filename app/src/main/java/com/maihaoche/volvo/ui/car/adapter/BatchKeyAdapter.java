package com.maihaoche.volvo.ui.car.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.BatchKeyItemBinding;
import com.maihaoche.volvo.databinding.BatchOutItemBinding;
import com.maihaoche.volvo.ui.car.domain.OutStorageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujian
 * Time is 2017/7/28
 * Email is gujian@maihaoche.com
 */

public class BatchKeyAdapter extends PullRecyclerAdapter<OutStorageInfo> {

    private ItemClickListener listener;
    private SelectedListener selectedListener;
    private ArrayList<OutStorageInfo> selectedCars;
    private int maxSelect;

    public void setListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public void setMaxSelect(int maxSelect) {
        this.maxSelect = Integer.MAX_VALUE;
    }

    public void setSelectedListener(SelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    public BatchKeyAdapter(Context context) {
        super(context);
        selectedCars = new ArrayList<>();
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new RukuViewHolder(DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.batch_key_item,parent,false));
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
            OutStorageInfo info = getItem(position);

            if(!info.isSelect && selectedCars.size()>=maxSelect){
                AlertToast.show("取用数量已达上限");
                return;
            }else{
                info.isSelect = !info.isSelect;
            }

            boolean isHave = false;
            int current = 0;
            for(int i=0;i<selectedCars.size();i++){
                if(selectedCars.get(i).carId.longValue() == info.carId.longValue()){
                    isHave = true;
                    current = i;
                    break;
                }
            }
            if(info.isSelect){

                if(!isHave){
                    selectedCars.add(info);
                }

            }else{
                if(isHave){
                    selectedCars.remove(current);
                }
            }

            if(selectedListener!=null){
                selectedListener.select(getItem(position));
            }

        });

    }

    public ArrayList<OutStorageInfo> getSelectedCars() {
        return selectedCars;
    }

    public int getSelectSize(){
        if(selectedCars!=null){
            return selectedCars.size();
        }
        return 0;
    }

    public void clearSelect(){
        if(selectedCars!=null){
            selectedCars.clear();
        }
    }

    private static class RukuViewHolder extends BaseViewHolder<OutStorageInfo>{

        public BatchKeyItemBinding binding;


        public RukuViewHolder(BatchKeyItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        @Override
        public void setData(OutStorageInfo data) {
            super.setData(data);
            binding.frameCode.setText(data.carUnique);
            binding.carAttribute.setText(data.carAttribute);
            binding.key.setText(data.keyNumber+"把");
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
