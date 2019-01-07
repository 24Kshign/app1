package com.maihaoche.volvo.ui.inwarehouse.record;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.maihaoche.commonbiz.module.ui.recyclerview.PullRecyclerAdapter;
import com.maihaoche.commonbiz.service.utils.ResourceUtils;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ItemRecordlistBinding;
import com.maihaoche.volvo.server.dto.StocktakeRecordVO;

/**
 * 盘库记录列表的adapter
 * 作者：yang
 * 时间：17/6/9
 * 邮箱：yangyang@maihaoche.com
 */
public class RecordListAdapter extends PullRecyclerAdapter<StocktakeRecordVO> {

    public RecordListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecordListVH(parent);
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, int position) {
        super.OnBindViewHolder(holder, position);
        if (!(holder instanceof RecordListVH)) {
            return;
        }
        StocktakeRecordVO recordVO = getItem(position);
        ItemRecordlistBinding binding = ((RecordListVH) holder).getBinding();
        binding.itemRecordTime.setText(recordVO.stocktakePeriod);

        String inNumStr = recordVO.stocktakenNum + "";
        String totalNumStr = "/" + recordVO.totalNum;
        boolean completed = recordVO.stocktakenNum >= recordVO.totalNum;
        SpannableString inNumSS =
                !completed ?
                        StringUtil.creatColorString(inNumStr, R.color.red_FD7A71, 0, inNumStr.length())
                        : StringUtil.creatColorString(inNumStr, R.color.black_626262, 0, (inNumStr).length());
        SpannableString totolNumSS = StringUtil.creatColorString(totalNumStr, R.color.gray_AFAFAF, 0, (totalNumStr).length());
        binding.itemRecordNum.setText("");
        binding.itemRecordNum.append(inNumSS);
        binding.itemRecordNum.append(totolNumSS);

        if (completed) {
            binding.itemRecordResultIcon.setImageDrawable(ResourceUtils.getDrawable(R.drawable.vector_tick));
            binding.itemRecordResult.setText("正常");
            binding.itemRecordResult.setTextColor(ResourceUtils.getColor(R.color.green_4BD1AF));
        } else {
            binding.itemRecordResultIcon.setImageDrawable(ResourceUtils.getDrawable(R.drawable.vector_cross));
            binding.itemRecordResult.setText("异常");
            binding.itemRecordResult.setTextColor(ResourceUtils.getColor(R.color.red_FD7A71));
        }
    }

    public static class RecordListVH extends BaseViewHolder<StocktakeRecordVO> {
        private ItemRecordlistBinding mBinding;

        public RecordListVH(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recordlist, parent, false));
            mBinding = DataBindingUtil.bind(itemView);
        }

        public ItemRecordlistBinding getBinding() {
            return mBinding;
        }
    }
}
