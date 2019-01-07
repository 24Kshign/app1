package com.maihaoche.volvo.ui.car.cardetail;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.maihaoche.commonbiz.module.ui.recyclerview.SimpleAdapter;
import com.maihaoche.commonbiz.service.image.ImageLoader;
import com.maihaoche.commonbiz.service.utils.ResourceUtils;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ItemCarAttrBinding;
import com.maihaoche.volvo.databinding.ItemCarattrPicBinding;
import com.maihaoche.volvo.ui.photo.ImageViewerActivity;

/**
 * 类简介：车辆属性展示的adapter
 * 作者：  yang
 * 时间：  2017/8/16
 * 邮箱：  yangyang@maihaoche.com
 */
public class CarAttrAdapter extends SimpleAdapter<CarAttr, RecyclerView.ViewHolder> {

    private static final int CAR_ATRR_PIC = 1;
    private static final int CAR_ATRR_TEXT = 2;

    private CarAttrVH.CarAttrVHFactory mCarAttrVHFactory;
    private Context mContext;

    public CarAttrAdapter(Context context, CarAttrVH.CarAttrVHFactory carAttrVHFactory) {
        super(context);
        mCarAttrVHFactory = carAttrVHFactory;
        mContext = context;
    }

    @Override
    protected void bindViewHolder(@Nullable CarAttr carAttr,  RecyclerView.ViewHolder holder) {

        if (holder instanceof HolderPic){
            ImageLoader.withCrop(mContext,
                    carAttr.mAttrValue,
                    R.color.grey_F3F4F5,
                    ((HolderPic) holder).binding.rfidPic);

            ((HolderPic) holder).binding.rfidPic.setOnClickListener(v->{
                //查看图片
                Intent intent = new Intent(mContext, ImageViewerActivity.class);
                intent.putExtra(ImageViewerActivity.EXTRA_URLS_STR,
                        carAttr.mAttrValue);
                mContext.startActivity(intent);
            });


        }else{
            CarAttrVH holder1= (CarAttrVH) holder;
            ItemCarAttrBinding binding = holder1.getBinding();
            if(carAttr==null || TextUtils.isEmpty(carAttr.mAttrValue)){
                binding.getRoot().setVisibility(View.GONE);
                return;
            }else {
                binding.getRoot().setVisibility(View.VISIBLE);
            }
            binding.attrName.setText(carAttr.mAttrName);
            binding.attrValue.setText(carAttr.mAttrValue);
            if (carAttr.mOnCarAttrClick != null) {
                binding.attrValue.setTextColor(ResourceUtils.getColor(R.color.blue_6C94F7));
                binding.attrValue.setOnClickListener(v -> carAttr.mOnCarAttrClick.onAttrClick(carAttr));
            } else {
                binding.attrValue.setTextColor(ResourceUtils.getColor(R.color.black_373737));
                binding.attrValue.setOnClickListener(null);
            }
        }


    }

    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == CAR_ATRR_PIC){
            return new HolderPic(DataBindingUtil.inflate(LayoutInflater.from(mContext),R.layout.item_carattr_pic,parent,false));
        }else{
            return mCarAttrVHFactory.get(parent);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(position == 3){
            return CAR_ATRR_PIC;
        }else{
            return CAR_ATRR_TEXT;
        }
    }

    static class HolderPic extends RecyclerView.ViewHolder{

        public ItemCarattrPicBinding binding;

        public HolderPic(ItemCarattrPicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
