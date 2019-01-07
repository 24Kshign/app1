package com.maihaoche.volvo.ui.car.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.OutstorageInfoItemBinding;
import com.maihaoche.volvo.ui.car.domain.DepartingCarDetail;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/3
 * Email is gujian@maihaoche.com
 */

public class OutStorageInfoAdapter extends RecyclerView.Adapter<OutStorageInfoAdapter.MyHolder>{

    private List<DepartingCarDetail> lists;
    private LayoutInflater inflater;

    public OutStorageInfoAdapter(Context context,List<DepartingCarDetail> lists) {
        this.lists = lists;
        inflater = LayoutInflater.from(context);
    }

    public void setLists(List<DepartingCarDetail> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(DataBindingUtil.inflate(inflater, R.layout.outstorage_info_item,parent,false));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        DepartingCarDetail info = lists.get(position);
        String title = "车辆信息 NO."+(position+1);
        holder.binding.title.setText(title);
        holder.binding.frameCode.setText(info.carUnique);
        holder.binding.carAttribute.setText(info.carAttribute);
        holder.binding.storageArea.removeAllViews();
        if(!info.isPayed()){
            holder.binding.feeArea.setVisibility(View.GONE);
            holder.binding.feeDetail.setVisibility(View.VISIBLE);
            for(DepartingCarDetail.DepartCarSettlement depa: info.paymentWarehouseList){
                LinearLayout view = getWareHouseView(holder.binding.storageArea,depa);
                holder.binding.storageArea.addView(view);
            }
            String total = "共计"+info.paymentWarehouseList.size()+"仓库 小计: ￥"+info.totalPaymentWarehouse;
            holder.binding.totleFee.setText(total);
        }else{
            holder.binding.feeDetail.setVisibility(View.GONE);
            holder.binding.feeArea.setVisibility(View.VISIBLE);
        }
    }

    private LinearLayout getWareHouseView(ViewGroup viewRoot,DepartingCarDetail.DepartCarSettlement depa) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.warehouse_cost_item,viewRoot,false);
        TextView warehostName = view.findViewById(R.id.warehouse_name);
        TextView feeDay = view.findViewById(R.id.fee_day);
        warehostName.setText(depa.warehouseName);
        feeDay.setText(depa.storageDays+"天");
        int index = 0;
        for(DepartingCarDetail.FeeItem item : depa.fees){
            LinearLayout itemView = (LinearLayout) inflater.inflate(R.layout.cost_item,null,false);
            TextView tvType = itemView.findViewById(R.id.typeName);
            TextView calcFee = itemView.findViewById(R.id.calc_fee);
            TextView adjustFee = itemView.findViewById(R.id.adjust_fee);
            TextView realFee = itemView.findViewById(R.id.real_fee);
            tvType.setText(item.feeTypeName);
            calcFee.setText(item.calcFeeValue);
            adjustFee.setText(item.adjustFeeValue);
            realFee.setText(item.feeValue);
            view.addView(itemView);
            index++;
        }
        return view;
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        public OutstorageInfoItemBinding binding;
        public MyHolder(OutstorageInfoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
