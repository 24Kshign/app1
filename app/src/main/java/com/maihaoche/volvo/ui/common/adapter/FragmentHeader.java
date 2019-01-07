package com.maihaoche.volvo.ui.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.maihaoche.volvo.R;

/**
 * Created by gujian
 * Time is 2017/8/4
 * Email is gujian@maihaoche.com
 */

public class FragmentHeader implements RecyclerArrayAdapter.ItemView {

    private Context context;
    private String text;
    private View view;

    public FragmentHeader(Context context, String text) {
        this.context = context;
        this.text = text;
    }

    @Override
    public View onCreateView(ViewGroup parent) {
        view = LayoutInflater.from(context).inflate(R.layout.fragment_header,parent,false);
        return view;
    }

    @Override
    public void onBindView(View headerView) {
        TextView textView = (TextView) headerView;
        textView.setText(text);
    }

    public void setText(String text) {
        this.text = text;
        if(view!=null){
            onBindView(view);
        }
    }
}
