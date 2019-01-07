package com.maihaoche.volvo.ui.car.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.CarUniqueLayoutBinding;
import com.maihaoche.volvo.databinding.SeeCarDetailItemBinding;
import com.maihaoche.volvo.databinding.SeeCarInfoHeaderBinding;
import com.maihaoche.volvo.ui.car.domain.SeeCarDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/4/9
 * Email is gujian@maihaoche.com
 */

public class CarInfoAdapter extends RecyclerView.Adapter{

    private static final int HEADER = 1;
    private static final int CONTENT = 2;

    private Context context;
    private List<SeeCarDetail.CarSimpleVOList> seeCarDetails;
    private SeeCarDetail header;

    public CarInfoAdapter(Context context) {
        this.context = context;
        this.seeCarDetails = new ArrayList<>();
    }

    public void setSeeCarDetails(SeeCarDetail header,List<SeeCarDetail.CarSimpleVOList> seeCarDetails) {
        this.seeCarDetails = seeCarDetails;
        this.header = header;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return HEADER;
        }
        return CONTENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == HEADER){
            return new MyHeaderHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.see_car_info_header,parent,false));
        }
        return new MyHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.see_car_detail_item,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(position == 0){
            MyHeaderHolder myHeaderHolder = (MyHeaderHolder) holder;
            SeeCarInfoHeaderBinding binding = myHeaderHolder.binding;
            binding.warehouse.setText(header.warehouseName);
            binding.phone.setText(header.receiverPhone);
            binding.time.setText(header.appointmentDate);
        }else{
            MyHolder myHolder = (MyHolder) holder;
            SeeCarDetailItemBinding binding = myHolder.binding;
            SeeCarDetail.CarSimpleVOList seeCarDetail = seeCarDetails.get(position);
            binding.space.setText(seeCarDetail.simpleName);
            binding.innerOuter.setText(seeCarDetail.outerInner);
            binding.number.setText("车辆信息"+position);
            binding.carCount.setText(seeCarDetail.subtotalCars+"台");
            binding.carInfoArea.removeAllViews();
            for(SeeCarDetail.CarInfoVOList carInfoVOList:seeCarDetail.carInfoVOList){
                CarUniqueLayoutBinding binding1 = getUniqueView();
                binding1.carUnique.setText(carInfoVOList.carUnique);
                if(TextUtils.isEmpty(carInfoVOList.position)){
                    binding1.position.setText("-");
                }else{
                    binding1.position.setText(carInfoVOList.position);
                }
                binding.carInfoArea.addView(binding1.getRoot());
            }
        }


    }

    public CarUniqueLayoutBinding getUniqueView(){
        return DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.car_unique_layout,null,false);
    }

    @Override
    public int getItemCount() {
        return seeCarDetails.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        public SeeCarDetailItemBinding binding;

        public MyHolder(SeeCarDetailItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class MyHeaderHolder extends RecyclerView.ViewHolder{

        public SeeCarInfoHeaderBinding binding;

        public MyHeaderHolder(SeeCarInfoHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
