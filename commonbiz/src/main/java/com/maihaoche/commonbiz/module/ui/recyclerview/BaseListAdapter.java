package com.maihaoche.commonbiz.module.ui.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanji on 14/12/4.
 */
abstract public class BaseListAdapter<T> extends BaseAdapter implements View.OnClickListener {

    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mList = new ArrayList<>();

//    protected ImageLoader mImageLoader = ImageLoader.getInstance();
//    protected DisplayImageOptions baseOption;

    public BaseListAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
//        baseOption = ImageLoaderUtil.getOption();
//        mFadeImage = new AlphaAnimation(0, 1);
//        mFadeImage.setDuration(200);
//        mFadeImage.setInterpolator(new DecelerateInterpolator());
    }

    public Context getContext(){
        return mContext;
    }


    public List<T> getData() {
        return mList;
    }

    public void addData(List<T> list) {
        if (list != null) {
            if (mList == null) {
                mList = new ArrayList<>();
            }
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void addData(T info) {
        if (info != null) {
            if (mList == null) {
                mList = new ArrayList<>();
            }
            mList.add(info);
            notifyDataSetChanged();
        }
    }

    public void remove(int position) {
        if (mList != null && mList.size() > position) {
            mList.remove(position);
            notifyDataSetChanged();

        }
    }

    public void addDataAt(T data, int position) {
        mList.add(position, data);
    }

    public void addDataAt(List<T> dataList, int position) {
        mList.addAll(position, dataList);
    }


    public void clear() {
        if (mList != null) {
            mList.clear();
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    abstract public View getView(int position, View convertView, ViewGroup parent);

    @Override
    public void onClick(View v) {

    }


}
