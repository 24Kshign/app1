package com.maihaoche.commonbiz.module.ui.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 自己封装的简易adapter
 * 作者：yang
 * 时间：17/6/9
 * 邮箱：yangyang@maihaoche.com
 */
public abstract class SimpleAdapter<DATA, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected LayoutInflater mInflater;
    protected Context mContext;
    private OnItemClickListener<DATA> mOnItemClickListener = null;
    private final ArrayList<OnItemClickListener<DATA>> mOnItemClickList = new ArrayList<>();

    protected AdapterDataHandler<DATA> mAdapterDataRelated = new AdapterDataHandler<DATA>() {
        private final ArrayList<DATA> mDATAs = new ArrayList<>();

        @Override
        public void addData(DATA data) {
            if (data != null) {
                mDATAs.add(data);
            }
        }

        @Override
        public void clear() {
            mDATAs.clear();
        }


        @Override
        public void addAll(List<DATA> datas) {
            if (datas != null && datas.size() > 0) {
                mDATAs.addAll(datas);
            }
        }

        @Override
        public ArrayList<DATA> getData() {
            return mDATAs;
        }
    };

    public SimpleAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    /**
     * 重新定义该adapter处理数据的类。
     */
    public void setAdapterDataRelated(AdapterDataHandler<DATA> adapterDataRelated) {
        mAdapterDataRelated = adapterDataRelated;
    }

    public void addData(DATA data) {
        addData(data, true);
    }

    public void addData(DATA data, boolean notify) {
        mAdapterDataRelated.addData(data);
        if (notify) {
            this.notifyDataSetChanged();
        }
    }


    public void clear() {
        clear(true);
    }

    /**
     * 清除数据
     */
    public void clear(boolean notify) {
        mAdapterDataRelated.clear();
        if (notify) {
            notifyDataSetChanged();
        }
    }

    /**
     * 默认add后会notify
     */
    public void addAll(List<DATA> datas) {
        addAll(datas, true);
    }

    /**
     * 添加数据
     */
    public void addAll(List<DATA> datas, boolean notify) {
        mAdapterDataRelated.addAll(datas);
        if (notify) {
            this.notifyDataSetChanged();
        }
    }

    public ArrayList<DATA> getData() {
        return mAdapterDataRelated.getData();
    }

    /**
     * 用数据绘图
     */
    protected abstract void bindViewHolder(@Nullable DATA data, VH holder);

    @Override
    final public void onBindViewHolder(VH holder, int position) {
        this.onBindViewHolder(holder, position, null);
    }


    @Override
    final public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        DATA data;
        if (payloads != null && payloads.size() > 0) {
            data = (DATA) payloads.remove(0);
            mAdapterDataRelated.getData().add(position, data);
        } else {
            data = getData(position);
        }
        //先清空点击事件
        holder.itemView.setOnClickListener(null);
        bindViewHolder(data, holder);
        if (data == null) {
            return;
        }
        //再赋予点击事件。
        if (mOnItemClickListener != null || mOnItemClickList.size() > 0) {
            if (holder.itemView.hasOnClickListeners()) {
                throw new IllegalArgumentException(
                        "点击事件冲突." +
                                SimpleAdapter.this.getClass().getSimpleName() + "中，viewHolder的itemView设置了点击事件，" +
                                "同时又设置了item点击事件");
            } else {
                holder.itemView.setOnClickListener(v -> {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, data);
                    }
                    if (mOnItemClickList.size() > 0) {
                        for (OnItemClickListener listener : mOnItemClickList) {
                            listener.onItemClick(v, data);
                        }
                    }
                });
            }
        }

    }

    /**
     * 可以被子类重写。
     */
    protected DATA getData(int position) {
        if (mAdapterDataRelated.getData().size() <= position) {
            return null;
        }
        return mAdapterDataRelated.getData().get(position);
    }

    /**
     * item的点击事件
     * 如果viewHolder.itemview本身的有点击事件(在bindViewHolder中添加)，会抛出异常。
     */
    public void setOnItemClickListener(OnItemClickListener<DATA> onItemClickListener) {
        clearOnItemClick();
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mAdapterDataRelated.getData().size();
    }

    /**
     * 添加点击事件
     */
    public void addOnItemClickListener(OnItemClickListener<DATA> onItemClickListener) {
        mOnItemClickList.add(onItemClickListener);
    }

    public void clearOnItemClick() {
        mOnItemClickListener = null;
        mOnItemClickList.clear();
    }

    public OnItemClickListener<DATA> getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public interface OnItemClickListener<DATA> {
        void onItemClick(View view, @NonNull DATA data);
    }

}
