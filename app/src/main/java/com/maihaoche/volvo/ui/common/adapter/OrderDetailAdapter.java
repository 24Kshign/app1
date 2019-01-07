package com.maihaoche.volvo.ui.common.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.OrderDetailItemBinding;
import com.maihaoche.volvo.ui.common.daomain.InstorageInfo;
import com.maihaoche.volvo.ui.common.daomain.OrderCarList;
import com.maihaoche.volvo.ui.instorage.activity.InStorageActivity;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/3
 * Email is gujian@maihaoche.com
 */

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.MyHolder> {

    public static final int TYPE_IN = 1;
    public static final int TYPE_HAVE_IN = 2;

    private List<OrderCarList.SimpleCar> lists;
    private LayoutInflater inflater;
    private Context context;
    private int type;

    public void setLists(List<OrderCarList.SimpleCar> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    public OrderDetailAdapter(Context context, int type) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.type = type;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(DataBindingUtil.inflate(inflater, R.layout.order_detail_item,parent,false));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        if(type == TYPE_IN){
            holder.binding.instorage.setVisibility(View.VISIBLE);
            holder.binding.ined.setVisibility(View.GONE);
        }else{
            holder.binding.instorage.setVisibility(View.GONE);
            holder.binding.ined.setVisibility(View.VISIBLE);
        }
        int index = position+1;
        holder.binding.number.setText("NO."+index);
        holder.binding.instorage.setOnClickListener(v->{
            InstorageInfo info = new InstorageInfo();
            info.carAttribute = lists.get(position).carAttribute;
            info.carUnique = lists.get(position).carUnique;
            info.carId = lists.get(position).carId;
            info.showRefuseButton = lists.get(position).showRefuseButton;
            info.bindingKeyBoxFlag = lists.get(position).bindingKeyBoxFlag;
            info.carStoreType = lists.get(position).carStoreType;
                context.startActivity(InStorageActivity.createIntent(context,info));
        });
        holder.binding.frameCode.setText(lists.get(position).carUnique);
        holder.binding.carAttribute.setText(lists.get(position).carAttribute);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        public OrderDetailItemBinding binding;

        public MyHolder(OrderDetailItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
