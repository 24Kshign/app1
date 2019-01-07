package com.maihaoche.volvo.ui.photo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.maihaoche.commonbiz.module.ui.recyclerview.BaseListAdapter;
import com.maihaoche.volvo.R;
import com.maihaoche.commonbiz.service.image.ImageLoader;

import java.io.File;
import java.util.List;


/**
 * PhotoWall中GridView的适配器
 *
 * @author hanj
 */

public class PhotoWallAdapter extends BaseListAdapter<String> {


    //记录是否被选择
    private List<String> selectionMap;

    public PhotoWallAdapter(Context context, List<String> map) {
        super(context);
        selectionMap = map;
    }

    public void addUpSelectionMap(List<String> selectionList) {
        this.selectionMap = selectionList;
        notifyDataSetChanged();
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_phone_wall, null);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.checkView.setVisibility(View.GONE);
//            holder.checkBox.setVisibility(View.GONE);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.imageView.setImageResource(R.drawable.photo_wall_take);
        } else {
            String filePath = getItem(position);
            Boolean isSelected = selectionMap.indexOf(filePath) >= 0;
            if (isSelected) {
                holder.checkView.setVisibility(View.VISIBLE);

//                holder.checkBox.setVisibility(View.VISIBLE);
//                holder.imageView.setColorFilter(Color.parseColor("#66000000"));
            } else {
                holder.checkView.setVisibility(View.GONE);

//                holder.checkBox.setVisibility(View.GONE);
                holder.imageView.setColorFilter(null);
            }

            ImageLoader.with(mContext, new File(filePath), holder.imageView);

        }
        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;

        ImageView checkBox;

        View checkView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.photo_wall_item_photo);
            checkBox = (ImageView) view.findViewById(R.id.photo_wall_item_cb);
            checkView = view.findViewById(R.id.photo_wall_check);

        }
    }


}
