/**
 *
 */
package com.kernal.smartvision.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kernal.smartvision.R;

import java.util.HashMap;
import java.util.List;

/**
 * VIN码解析的列表适配器
 */

public class VinParseResultAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    public List<HashMap<String, String>> mList;

    public VinParseResultAdapter(Context context, List<HashMap<String, String>> recogResult) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mList = recogResult;
    }

    @Override
    public int getCount() {
        return mList.size();

    }


    @Override
    public Object getItem(int position) {
        return mList.get(position);

    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public void SetData(List<HashMap<String, String>> recogResult) {
        this.mList = recogResult;
    }

    public static class ViewHolder {
        public TextView nameTv = null;
        public TextView valueTv = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.vinparse_list_item, null);

            holder.nameTv = (TextView) convertView.findViewById(R.id.item_name_tv);
            holder.valueTv = (TextView) convertView.findViewById(R.id.item_value_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        String keyName = mList.get(position).keySet().iterator().next();
        holder.nameTv.setText(keyName);
        holder.valueTv.setText(mList.get(position).get(keyName));
        return convertView;
    }

}
