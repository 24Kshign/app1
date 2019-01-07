package com.maihaoche.volvo.ui.car.adapter;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.utils.DeviceUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.CheckCarItemBinding;
import com.maihaoche.volvo.server.dto.InWarehouseCarVO;
import com.maihaoche.volvo.server.dto.request.CarIdRequest;
import com.maihaoche.volvo.ui.car.cardetail.ActivityCarInfo;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/7/28
 * Email is gujian@maihaoche.com
 */

public class SearchCarAdapter extends PullRecyclerAdapter<InWarehouseCarVO> {

    private ItemClickListener listener;
    private FindCarClick click;

    public void setListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public void setClick(FindCarClick click) {
        this.click = click;
    }

    public SearchCarAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new RukuViewHolder(DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.check_car_item, parent, false));
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, int position) {
        super.OnBindViewHolder(holder, position);
        RukuViewHolder holder1 = (RukuViewHolder) holder;

        holder1.binding.firstCheckIcon.setVisibility(View.GONE);
        holder1.binding.checkCar.setVisibility(View.GONE);
        holder1.binding.quickSearch.setVisibility(View.VISIBLE);

        int index = position + 1;
        holder1.binding.number.setText("NO." + index);

        holder1.itemView.setOnClickListener(v -> {
            CarIdRequest request = new CarIdRequest();
            request.carId = getItem(position).carId;
            request.carStoreType = getItem(position).carStoreType;
            getContext().startActivity(ActivityCarInfo.createIntent(getContext(), request));
        });
        holder1.binding.quickSearch.setOnClickListener(v -> {
            if (!DeviceUtil.isSENTER()) {
                AlertToast.show("非指定设备，找车功能无法使用");
                return;
            }
            if (click != null) {
                click.click(getItem(position));
            }
        });
    }

    private static class RukuViewHolder extends BaseViewHolder<InWarehouseCarVO> {

        public CheckCarItemBinding binding;


        public RukuViewHolder(CheckCarItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        @Override
        public void setData(InWarehouseCarVO data) {
            super.setData(data);
            binding.frameCode.setText(data.carUnique);
            binding.carAttribute.setText(data.carAttribute);
            binding.location.setText(data.areaPositionName);
            if (TextUtils.isEmpty(data.carTagId) || data.carTagId.equals("null")) {
                binding.searchCarLabel.setVisibility(View.VISIBLE);
                binding.quickSearch.setEnabled(false);
            } else {
                binding.searchCarLabel.setVisibility(View.INVISIBLE);
                binding.quickSearch.setEnabled(true);
            }
        }
    }

    public interface ItemClickListener {
        void click(String data);
    }

    public interface FindCarClick {
        void click(InWarehouseCarVO data);
    }

    @Override
    public void remove(InWarehouseCarVO info) {
        List<InWarehouseCarVO> list = getAllData();
        for (int i = 0; i < list.size(); i++) {
            if (info.carId == list.get(i).carId) {
                remove(i);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void add(InWarehouseCarVO object) {
        List<InWarehouseCarVO> list = getAllData();
        list.add(0, object);
        clear();
        addAll(list);
    }
}
