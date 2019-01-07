package com.maihaoche.volvo.ui.car.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.CarInfoAdapterBinding;
import com.maihaoche.volvo.databinding.CarUniqueLayoutBinding;
import com.maihaoche.volvo.databinding.SeeCarDetailItemBinding;
import com.maihaoche.volvo.ui.car.domain.SeeCarDetail;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/5/29
 * Email is gujian@maihaoche.com
 */
public class CarInfoAdapter2 extends PullRecyclerAdapter<SeeCarDetail> {

    public CarInfoAdapter2(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new CarInfoAdapter2.MyHolder(DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.car_info_adapter,parent,false));
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        SeeCarDetail carDetail = getItem(position);
        myHolder.binding.infoHeader.warehouse.setText(carDetail.warehouseName);
        myHolder.binding.infoHeader.phone.setText(carDetail.receiverPhone);
        myHolder.binding.infoHeader.time.setText(carDetail.appointmentDate);
        int count = 1;
        for(SeeCarDetail.CarSimpleVOList simpleVOList : carDetail.carSimpleVOList){
            SeeCarDetailItemBinding detailItemBinding = getCarDetailBinding();
            detailItemBinding.carCount.setText(simpleVOList.subtotalCars+"");
            detailItemBinding.innerOuter.setText(simpleVOList.outerInner);
            detailItemBinding.number.setText("车辆信息"+count);
            detailItemBinding.space.setText(simpleVOList.simpleName);
            for(SeeCarDetail.CarInfoVOList infoVOList : simpleVOList.carInfoVOList){
                CarUniqueLayoutBinding uniqueLayoutBinding = getUniqueView();
                uniqueLayoutBinding.carUnique.setText(infoVOList.carUnique);
                uniqueLayoutBinding.position.setText(infoVOList.position);
                detailItemBinding.carInfoArea.addView(uniqueLayoutBinding.getRoot());
            }

            count++;
            myHolder.binding.simpleList.addView(detailItemBinding.getRoot());
        }
//        View view = new View(getContext());
//        view.setBackgroundColor(getContext().getResources().getColor(R.color.bg_grey,null));
//        myHolder.binding.simpleList.addView(view);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
//        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
//        params.height = SizeUtil.dip2px(10);
//        view.setLayoutParams(params);
    }

    private SeeCarDetailItemBinding getCarDetailBinding(){
        return DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.see_car_detail_item,null,false);
    }

    public CarUniqueLayoutBinding getUniqueView(){
        return DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.car_unique_layout,null,false);
    }


    private static class MyHolder extends BaseViewHolder<SeeCarDetail>{
        public CarInfoAdapterBinding binding;
        public MyHolder(CarInfoAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
