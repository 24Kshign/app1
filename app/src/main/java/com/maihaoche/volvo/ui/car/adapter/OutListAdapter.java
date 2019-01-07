package com.maihaoche.volvo.ui.car.adapter;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.OutstorageItemBinding;
import com.maihaoche.volvo.server.dto.request.CarIdRequest;
import com.maihaoche.volvo.ui.car.activity.OutStorageInfoActivity;
import com.maihaoche.volvo.ui.car.activity.OutStorageListActivity;
import com.maihaoche.volvo.ui.car.cardetail.ActivityCarInfo;
import com.maihaoche.volvo.ui.car.domain.OutStorageDetailRequest;
import com.maihaoche.volvo.ui.car.domain.OutStorageInfo;
import com.maihaoche.volvo.ui.car.fragment.OutStorageFragment;
import com.maihaoche.volvo.ui.common.daomain.KeyStatus;
import com.maihaoche.volvo.ui.photo.ImageViewerActivity;
import com.maihaoche.volvo.ui.common.daomain.KeyStatus;
import com.maihaoche.volvo.view.dialog.CarryManDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gujian
 * Time is 2017/7/28
 * Email is gujian@maihaoche.com
 */

public class OutListAdapter extends PullRecyclerAdapter<OutStorageInfo> {

    private KeyOptionListener listener;
    private String type;
    private OutStorageListActivity activity;

    public void setListener(KeyOptionListener listener) {
        this.listener = listener;
    }

    public OutListAdapter(Context context, String type) {
        super(context);
        this.type = type;
        this.activity = (OutStorageListActivity) context;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new RukuViewHolder(DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.outstorage_item,parent,false));
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, int position) {
        super.OnBindViewHolder(holder, position);
        RukuViewHolder holder1 = (RukuViewHolder) holder;
        OutstorageItemBinding binding = holder1.binding;
        OutStorageInfo info = getItem(position);
        KeyStatus keyStatus;
        if(info.keyStatus == null){
            keyStatus = KeyStatus.NULL;
        }else{
            keyStatus = KeyStatus.getStatus(info.keyStatus);
        }
        if(type.equals(OutStorageFragment.TYPE_OUT_STORAGE)){
            binding.buttonArea.setVisibility(View.VISIBLE);
            binding.location.setVisibility(View.VISIBLE);
            binding.pickletterArea.setVisibility(View.VISIBLE);
            binding.outStorageNoArea.setVisibility(View.VISIBLE);
            holder1.binding.applyKey.setVisibility(View.VISIBLE);
            holder1.binding.statusArea.setVisibility(View.VISIBLE);
            //钥匙柜相关
            if(keyStatus.hasDesc()){
                holder1.binding.statusDesc.setText(keyStatus.getDesc());
                holder1.binding.statusArea.setVisibility(View.VISIBLE);
            }else{
                holder1.binding.statusArea.setVisibility(View.GONE);
            }

            if(keyStatus.hasBtn() && keyStatus.getCode()!= KeyStatus.WAITE_BIND.getCode() ){
                holder1.binding.applyKey.setVisibility(View.VISIBLE);
                holder1.binding.applyKey.setText(keyStatus.getBtnText());
            }else{
                holder1.binding.applyKey.setVisibility(View.GONE);
            }

        }else{
            binding.buttonArea.setVisibility(View.GONE);
            binding.locationArea.setVisibility(View.GONE);
            binding.pickletterArea.setVisibility(View.GONE);
            binding.outStorageNoArea.setVisibility(View.GONE);
            holder1.binding.applyKey.setVisibility(View.GONE);
        }

        int index = position+1;
        binding.number.setText("NO."+index);

        holder1.binding.applyKey.setOnClickListener(v->{
            if(listener!=null){
                listener.optinoKey(info);
            }
        });

        holder1.itemView.setOnClickListener(v->{

            CarIdRequest request = new CarIdRequest();
            request.carId = getItem(position).carId;
            request.carStoreType = getItem(position).carStoreType;
            getContext().startActivity(ActivityCarInfo.createIntent(getContext(),request));
        });
        if(info.imgPickLetter == null || "".equals(info.imgPickLetter)){
            binding.pickletterArea.setVisibility(View.GONE);
        }else{
            binding.pickletterArea.setVisibility(View.VISIBLE);
        }

        binding.out.setOnClickListener(v->{
            OutStorageDetailRequest request = new OutStorageDetailRequest();
            request.wmsCarIdList = new ArrayList<>();
            request.wmsCarIdList.add(getItem(position).carId);
            AppApplication.getServerAPI()
                    .queryIsCanOut(request)
                    .setOnDataGet(response -> {
                        activity.cancelLoading();
                        if(response.result.canDepart()){
                            getContext().startActivity(OutStorageInfoActivity.create(getContext(),OutStorageInfoActivity.TYPE_SINGLE,getItem(position)));
                        }else{
                            AlertToast.show(response.result.message);
                        }
                    })
                    .setOnDataError(msg->{
                        AlertToast.show(msg);
                        activity.cancelLoading();
                    })
                    .setDoOnSubscribe(disposable -> {
                        activity.showLoading("出库校验中",null);
                    })
                    .call(activity);

        });

        if(StringUtil.isNotEmpty(info.pickPersonJpgUrl)){
            binding.tmsPersonArea.setVisibility(View.VISIBLE);
            binding.tmsPerson.setOnClickListener(v->{
                showImage(info.pickPersonJpgUrl);
            });
        }else{
            binding.tmsPersonArea.setVisibility(View.GONE);
        }

        if(StringUtil.isNotEmpty(info.paperPickPersonJpgUrl)){
            binding.paperCapacitorArea.setVisibility(View.VISIBLE);
            binding.paperCapacitor.setOnClickListener(v->{
                showImage(info.paperPickPersonJpgUrl);
            });
        }else{
            binding.paperCapacitorArea.setVisibility(View.GONE);
        }

        if(StringUtil.isNotEmpty(info.imgPickLetter)){
            binding.pickletterArea.setVisibility(View.VISIBLE);
            binding.image.setOnClickListener(v->{
                showImage(info.imgPickLetter);
            });
        }else{
            binding.pickletterArea.setVisibility(View.GONE);
        }



    }

    private void showImage(String url){
        //查看图片
        Intent intent = new Intent(getContext(), ImageViewerActivity.class);
        intent.putExtra(ImageViewerActivity.EXTRA_URLS_STR,
                url);
        getContext().startActivity(intent);
    }

    public interface KeyOptionListener{
        void optinoKey(OutStorageInfo info);
    }

    private static class RukuViewHolder extends BaseViewHolder<OutStorageInfo>{

        public OutstorageItemBinding binding;


        public RukuViewHolder(OutstorageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        @Override
        public void setData(OutStorageInfo data) {
            super.setData(data);
            binding.frameCode.setText(data.carUnique);
            binding.carAttribute.setText(data.carAttribute);
            binding.carOld.setText(data.inWarehouseDay+"天");
            binding.outStorageNo.setText(data.outWarehouseNo);
            binding.key.setText(data.keyNumber+"把");
            binding.location.setText(data.areaPositionName);

        }
    }

    public interface ItemClickListener{
        void click(String data);
    }

    @Override
    public void remove(OutStorageInfo info){
        List<OutStorageInfo> list = getAllData();
        for(int i=0;i<list.size();i++){
            if(info.carId.longValue() == list.get(i).carId.longValue()){
                remove(i);
                notifyDataSetChanged();
            }
        }
    }

    public void remove(List<OutStorageInfo> infos){
        List<OutStorageInfo> list = getAllData();
        for(int i=0;i<infos.size();i++){
            for(int j=0;j<list.size();j++){
                if(infos.get(i).carId.longValue() == list.get(j).carId.longValue()){
                    list.remove(j);
                    continue;
                }
            }
        }
        clear();
        addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void add(OutStorageInfo object) {
        List<OutStorageInfo> list = getAllData();
        list.add(0,object);
        clear();
        addAll(list);
    }

    public void add(List<OutStorageInfo> infos) {
        List<OutStorageInfo> list = getAllData();
        for(OutStorageInfo info:infos){
            list.add(0,info);
        }
        clear();
        addAll(list);
    }
}
