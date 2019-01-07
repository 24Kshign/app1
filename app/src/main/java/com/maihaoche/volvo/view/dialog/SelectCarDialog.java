package com.maihaoche.volvo.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.DialogSelectCarBinding;
import com.maihaoche.volvo.databinding.ItemSelectCarBinding;
import com.maihaoche.volvo.ui.car.domain.OutStorageInfo;
import com.maihaoche.volvo.ui.common.daomain.Customer;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/15
 * Email is gujian@maihaoche.com
 */

public class SelectCarDialog extends Dialog {

    private List<OutStorageInfo> list;

    private DialogSelectCarBinding binding;

    private SelectCarAdapter adapter;

    public SelectCarDialog(@NonNull Context context,List<OutStorageInfo> list) {
        super(context);
        this.list = list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_select_car,null,false);
        setContentView(binding.getRoot());
        setDialogWindowAttr();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.list.setLayoutManager(manager);
        adapter = new SelectCarAdapter(getContext());
        binding.list.setAdapter(adapter);
        if(list == null || list.size() ==0){
            showEmpty();
        }
        binding.sure.setOnClickListener(v->dismiss());

    }

    private class SelectCarAdapter extends RecyclerView.Adapter<SelectCarAdapter.MyViewHolder>{

        private LayoutInflater inflater;
        private com.maihaoche.volvo.ui.instorage.adapter.ContactListAdapter.OnItemClickListener listener;

        public void setListener(com.maihaoche.volvo.ui.instorage.adapter.ContactListAdapter.OnItemClickListener listener) {
            this.listener = listener;
        }

        public SelectCarAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(DataBindingUtil.inflate(inflater,R.layout.item_select_car,parent,false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.binding.frameCode.setText(list.get(position).carUnique);
            holder.binding.delete.setOnClickListener(v->{
                list.get(position).isSelect = false;
                RxBus.getDefault().post(list.remove(position));
                notifyDataSetChanged();
                if(list.size() == 0){
                    showEmpty();
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            public ItemSelectCarBinding binding;
            public MyViewHolder(ItemSelectCarBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

    }

    private void showEmpty(){
        binding.list.setVisibility(View.GONE);
        binding.empty.setVisibility(View.VISIBLE);
    }

    private void showContent(){
        binding.list.setVisibility(View.VISIBLE);
        binding.empty.setVisibility(View.GONE);
    }

    public void setDialogWindowAttr(){
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = SizeUtil.dip2px(300);//宽高可设置具体大小
        getWindow().setAttributes(lp);
    }

}
