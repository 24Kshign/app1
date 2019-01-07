package com.maihaoche.volvo.ui.instorage.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maihaoche.volvo.R;
import com.maihaoche.volvo.ui.photo.ImageViewerActivity;
import com.maihaoche.volvo.ui.photo.ImgInfo;
import com.maihaoche.commonbiz.service.image.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */

public class CarPhotoAdapter extends RecyclerView.Adapter<CarPhotoAdapter.CarPhotoViewHoler> {
    private boolean mIsUpload;
    private List<String> imgUrl;
    private List<ImgInfo> mDatas;
    protected LayoutInflater mInflater;
    protected Context mContext;
    public CarPhotoAdapter(Context context, boolean isUpload) {
        imgUrl = new ArrayList<>();
        mIsUpload = isUpload;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mDatas = new ArrayList<>();
    }

    public void addData(List<ImgInfo> list){
        if(mDatas == null){
            mDatas = new ArrayList<>();
        }
        mDatas.addAll(list);
        for(ImgInfo s:list){
            imgUrl.add(s.getImgUrl());
        }
        notifyDataSetChanged();
    }

    @Override
    public CarPhotoViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CarPhotoViewHoler(LayoutInflater.from(mContext).inflate(R.layout.item_car_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(CarPhotoViewHoler holder, int position) {
        ImgInfo item = mDatas.get(position);
        ImageLoader.withSimple(mContext, item.getImgUrl(), holder.ivCarPhoto);
        if (mIsUpload){ //判断是上传图片还是查看图片
            if (TextUtils.isEmpty(item.getName())){  //name是否为空，为空是异常照片，否则是常规照片
                //异常-->隐藏tvName
                holder.tvName.setVisibility(View.GONE);
                //显示备注按钮
                holder.tvInfo.setVisibility(View.VISIBLE);
                holder.tvInfo.setText(TextUtils.isEmpty(item.remark) ? "" : item.remark); //设置配置文本
                //根据remark是否为空判断是否显示tvInfo
//                holder.tvInfo.setVisibility(TextUtils.isEmpty(item.remark) ? View.GONE : View.VISIBLE);

            }else {
                //如果是常规照片，显示tvName，隐藏tvInfo和备注按钮
                holder.tvName.setVisibility(View.VISIBLE);
                holder.tvInfo.setVisibility(View.GONE);
//                holder.btnRemark.setVisibility(View.GONE);
                holder.tvName.setText(item.getName());
            }
        }else {
            //如果是查看图片
            if (TextUtils.isEmpty(item.getName())) {  //name是否为空，为空是异常照片，否则是常规照片
                //异常照片，显示tvInfo，隐藏其他
                holder.tvInfo.setVisibility(View.VISIBLE);
//                holder.btnRemark.setVisibility(View.GONE);
                holder.tvName.setVisibility(View.GONE);
                holder.tvInfo.setText(item.remark);
            }else {
                //常规照片，显示tvName，隐藏其他
                holder.tvInfo.setVisibility(View.GONE);
//                holder.btnRemark.setVisibility(View.GONE);
                holder.tvName.setVisibility(View.VISIBLE);
                holder.tvName.setText(item.getName()); //设置文本
            }
        }
        holder.ivCarPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ImageViewerActivity.class);
            intent.putExtra(ImageViewerActivity.EXTRA_URLS_LIST, (ArrayList<String>) imgUrl);
            intent.putExtra(ImageViewerActivity.EXTRA_CUR_INDEX, position);
            intent.putExtra(ImageViewerActivity.EXTRA_TITLE, item.getName());
            List<ImgInfo> data = mDatas;
            List<String> remarks = new ArrayList<>() ;
            for (ImgInfo info : data ){
                remarks.add(info.remark);
            }
            intent.putExtra(ImageViewerActivity.EXTRA_REMARK,(Serializable)remarks);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void clear() {
        imgUrl.clear();
    }

    public static class CarPhotoViewHoler extends RecyclerView.ViewHolder {
        ImageView ivCarPhoto;
        TextView tvName;
        TextView tvInfo;
        public CarPhotoViewHoler(View itemView) {
            super(itemView);
            ivCarPhoto = (ImageView) itemView.findViewById(R.id.iv_car);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvInfo = (TextView) itemView.findViewById(R.id.tv_info);
        }
    }
}
