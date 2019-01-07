package com.maihaoche.volvo.ui.photo;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ItemChoosePicBinding;
import com.maihaoche.commonbiz.service.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangshengru on 16/3/9.
 * 选择图片的适配器
 */
public class ChoosePicAdapter extends RecyclerView.Adapter<ChoosePicAdapter.Holder> {

    public int mMaxCount = 9;//最大张数
    private boolean mLimitMax = true;//是否限制最大张数
    private boolean mJustLook = false;//只是查看图片
    private boolean mIsWithWater = false;//是否带水印

    private LayoutInflater inflater;
    private Context context;
    private List<ChooseImageItem> mData;
    private OnItemClickListener mOnItemClickListener;
    private RemarkClick remarkClick;


    public void setRemarkClick(RemarkClick remarkClick) {
        this.remarkClick = remarkClick;
    }

    public ChoosePicAdapter(Context context, boolean limitMax, int max, boolean isJustLook) {
        mLimitMax = limitMax;
        if (limitMax && max > 0) {
            mMaxCount = max;
        }
        mJustLook = isJustLook;
        inflater = LayoutInflater.from(context);
        this.context = context;
        mData = new ArrayList<>();
    }

    public void setIsShowWater(boolean isWithWater) {
        mIsWithWater = isWithWater;
        notifyDataSetChanged();
    }

    public void addData(List<ChooseImageItem> list){
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public List<ChooseImageItem> getData(){
        return mData;
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener deleteListener) {
        onDeleteListener = deleteListener;
    }

    public List<String> getUploadImgUrl() {
        List<String> list = new ArrayList<>();
        for (ChooseImageItem chooseImageItem : mData) {
            if (StringUtil.isNotEmpty(chooseImageItem.mServerUrl)) {
                list.add(chooseImageItem.mServerUrl);
            }
        }
        return list;
    }

    public boolean hasUploadAll() {
        for (ChooseImageItem chooseImageItem : mData) {
            if (TextUtils.isEmpty(chooseImageItem.mServerUrl)) {
                return false;
            }
        }
        return true;
    }

    public List<String> getShowUrl() {
        List<String> list = new ArrayList<>();
        for (ChooseImageItem chooseImageItem : mData) {
            list.add(chooseImageItem.mOriPath);
        }
        return list;
    }

    public List<String> getRemark(){
        List<String> list = new ArrayList<>();
        for (ChooseImageItem chooseImageItem : mData) {
            if(StringUtil.isEmpty(chooseImageItem.remark)){
                list.add("");
            }else{
                list.add(chooseImageItem.remark);
            }


        }
        return list;
    }

    public boolean updateComplete() {
        return getUploadImgUrl().size() == getShowUrl().size();
    }

    public void addOriPath(List<String> list) {
        ArrayList<ChooseImageItem> imgList = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                ChooseImageItem chooseImageItem = new ChooseImageItem();
                chooseImageItem.mOriPath = list.get(i);
                imgList.add(chooseImageItem);
            }
            addData(imgList);
        }
    }

    @Override
    public int getItemCount() {
        if ((mLimitMax && mData.size() >= mMaxCount) || (mJustLook)) {
            return mData == null?0:mData.size();
        } else {
            return mData == null?0:mData.size() + 1;
        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemChoosePicBinding inflate = DataBindingUtil.inflate(inflater, R.layout.item_choose_pic, parent, false);
        return new Holder(inflate.getRoot(), inflate);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.setIsRecyclable(false);
        holder.binding.choosePicItem.setIsWithWater(mIsWithWater);

        if (position == mData.size()) {//显示添加图片按钮
            if ((mLimitMax && position >= mMaxCount)) {
                holder.itemView.setVisibility(View.INVISIBLE);
            } else {

                holder.itemView.setVisibility(View.VISIBLE);
                holder.binding.choosePicItem.showAdd();
                holder.binding.choosePicItemDelete.setVisibility(View.GONE);
            }
        } else {
            ChooseImageItem chooseImageItem = mData.get(position);
            holder.itemView.setVisibility(View.VISIBLE);
            holder.binding.tvInfo.setText(chooseImageItem.remark);
            holder.binding.btnRemark.setVisibility(View.VISIBLE);

            if(StringUtil.isEmpty(chooseImageItem.remark)){
                holder.binding.tvInfo.setVisibility(View.GONE);
            }else{
                holder.binding.tvInfo.setVisibility(View.VISIBLE);
            }

            if (mJustLook) {
                holder.binding.choosePicItemDelete.setVisibility(View.GONE);
            } else {

                holder.binding.choosePicItemDelete.setVisibility(View.VISIBLE);
            }
            holder.binding.choosePicItem.setUrl(context, chooseImageItem);
        }
    }

    public class Holder extends RecyclerView.ViewHolder {
        //        ChoosePicImage mImgItem;
        ItemChoosePicBinding binding;

        public Holder(View itemView, ItemChoosePicBinding binding) {
            super(itemView);
            this.binding = binding;
            binding.choosePicItemDelete.setOnClickListener(v -> delete());
            binding.choosePicItem.setCompress(true);
            binding.choosePicItem.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(binding.getRoot(),null,getLayoutPosition());
                }
            });
            binding.btnRemark.setOnClickListener(v->{
                if(remarkClick!=null){
                    remarkClick.click(getLayoutPosition(),mData.get(getLayoutPosition()));
                }
            });
        }

        public void delete() {
            mData.remove(getLayoutPosition());
            notifyItemRemoved(getLayoutPosition());
            if (mLimitMax && mData.size() == mMaxCount - 1) {//上传最大张数点击删除,最后的加号会没有掉,这里recycleView只做局部刷新,所以这里手动刷一下
                notifyItemChanged(mMaxCount - 1);
            }

            if (onDeleteListener != null) {
                onDeleteListener.onDelete(getLayoutPosition());
            }
        }
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ChooseImageItem data,int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public int getSize(){
        return mData == null?0:mData.size();
    }

    public interface RemarkClick{
        void click(int position,ChooseImageItem chooseImageItem);
    }
}