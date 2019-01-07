package com.maihaoche.volvo.ui.instorage.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ContactItemBinding;
import com.maihaoche.volvo.ui.common.daomain.Customer;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/2
 * Email is gujian@maihaoche.com
 */

public class ClientListAdapter extends RecyclerView.Adapter<ClientListAdapter.MyViewHolder>{

    private List<Customer> contacts;
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setContacts(List<Customer> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    public ClientListAdapter(Context context, List<Customer> contacts) {
        this.contacts = contacts;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(inflater,R.layout.contact_item,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if(position == 0){
            SpannableString sp = new SpannableString("没有该客户？选择为其他客户");
            sp.setSpan(new ForegroundColorSpan(Color.parseColor("#6C94F7")),9,sp.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.binding.contact.setText(sp);
        }else{
            holder.binding.contact.setText(contacts.get(position).name);
        }


        holder.binding.getRoot().setOnClickListener(v->{
            if(listener!=null){
                listener.click(contacts.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        public ContactItemBinding binding;
        public MyViewHolder(ContactItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener{
        void click(Customer customer);
    }
}
