package com.maihaoche.volvo.ui.instorage.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ContactItemBinding;
import com.maihaoche.volvo.ui.common.daomain.Customer;
import com.maihaoche.volvo.ui.photo.ChoosePicAdapter;

import java.util.List;

/**
 * Created by gujian
 * Time is 2017/8/2
 * Email is gujian@maihaoche.com
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder>{

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

    public ContactListAdapter(Context context, List<Customer> contacts) {
        this.contacts = contacts;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(DataBindingUtil.inflate(inflater,R.layout.contact_item,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        holder.binding.contact.setText(contacts.get(position).name+"  "+contacts.get(position).phoneName);


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
