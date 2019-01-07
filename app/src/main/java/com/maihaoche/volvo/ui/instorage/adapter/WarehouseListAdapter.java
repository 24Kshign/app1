package com.maihaoche.volvo.ui.instorage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maihaoche.commonbiz.module.ui.recyclerview.DataBindingViewHolder;
import com.maihaoche.commonbiz.module.ui.recyclerview.SimpleAdapter;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.dao.po.WarehouseVO;
import com.maihaoche.volvo.databinding.ItemGarageListBinding;

/**
 * 仓库列表的adapter
 */
public class WarehouseListAdapter extends SimpleAdapter<WarehouseVO, DataBindingViewHolder> {

    private OnWarehouseItemClick mOnWarehouseItemClick;

    private long mChoosenWarehouseId = 0;

    public WarehouseListAdapter(Context context, OnWarehouseItemClick onWarehouseItemClick) {
        super(context);
        mOnWarehouseItemClick = onWarehouseItemClick;
    }

    public void setChoosenWarehouseId(long choosenWarehouseId) {
        mChoosenWarehouseId = choosenWarehouseId;
        notifyDataSetChanged();
    }

    @Override
    protected void bindViewHolder(WarehouseVO warehouseVO, DataBindingViewHolder holder) {
        ItemGarageListBinding binding = holder.getBinding();
        TextView garageNameTV = binding.itemGarageListName;
        garageNameTV.setText(warehouseVO.warehouseName);
        garageNameTV.setSelected(warehouseVO.warehouseId == mChoosenWarehouseId);
        garageNameTV.setOnClickListener(v -> {
            if (mOnWarehouseItemClick != null) {
                mOnWarehouseItemClick.onItemClick(warehouseVO);
            }
            mChoosenWarehouseId = warehouseVO.warehouseId;
            notifyDataSetChanged();
        });
    }

    @Override
    public DataBindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DataBindingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_garage_list, parent, false));
    }

    public interface OnWarehouseItemClick {
        void onItemClick(WarehouseVO garagePO);
    }

}
