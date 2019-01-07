package com.maihaoche.volvo.ui.photo;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.CheckCarPhotoItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/4/3
 * Email is gujian@maihaoche.com
 */

public class CheckCarPhotoAdapter extends RecyclerView.Adapter<CheckCarPhotoAdapter.MyHolder> {

    private List<CheckCarPhotoItem> mData;
    private Context context;
    private ClickItem clickItem;

    public CheckCarPhotoAdapter(Context context){
        this.context = context;
        mData = new ArrayList<>();
    }

    public void setClickItem(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    @Override
    public CheckCarPhotoAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.check_car_photo_item,parent,false));
    }

    @Override
    public void onBindViewHolder(CheckCarPhotoAdapter.MyHolder holder, int position) {
        CheckCarPhotoItem item = mData.get(position);
        holder.binding.imgName.setText(item.imgName);
        if(item.isShowDefault){
            holder.binding.choosePicItemDelete1.setVisibility(View.GONE);
            holder.binding.choosePicItem1.showAdd();
        }else{
            holder.binding.choosePicItemDelete1.setVisibility(View.VISIBLE);
            holder.binding.choosePicItem1.setUrl(context,item);
        }

        holder.binding.choosePicItemDelete1.setOnClickListener(v->{
            holder.binding.choosePicItem1.showAdd();
            item.clear();
            holder.binding.choosePicItemDelete1.setVisibility(View.GONE);
            item.mServerUrl = null;
        });
        holder.binding.getRoot().setOnClickListener(v->{
            if(clickItem!=null){
                clickItem.click(item,position);
            }
        });
    }

    public void setUrl(String url,int position){
        mData.get(position).mOriPath = url;
        mData.get(position).isShowDefault = false;
        notifyDataSetChanged();
    }

    public List<CheckCarPhotoItem> getmData() {
        return mData;
    }

    public void setmData(List<CheckCarPhotoItem> mData) {
        this.mData = mData;
        notifyItemRangeChanged(0,mData.size()-1);
    }



    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        public CheckCarPhotoItemBinding binding;

        public MyHolder(CheckCarPhotoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.choosePicItem1.setCompress(true);
        }
    }

    public interface ClickItem{
        void click(CheckCarPhotoItem item,int position);
    }
}
