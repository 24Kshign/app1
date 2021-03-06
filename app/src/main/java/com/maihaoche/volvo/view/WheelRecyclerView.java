package com.maihaoche.volvo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 滚轮控件的RecyclerView版本
 * Created by CrazyPumPkin on 2016/12/3.
 */

public class WheelRecyclerView extends RecyclerView {

    //默认参数
    private final int DEFAULT_WIDTH = SizeUtil.dip2px(160);

    private final int DEFAULT_ITEM_HEIGHT = SizeUtil.dip2px(30);

    private final int DEFAULT_SELECT_TEXT_COLOR = Color.parseColor("#3396FF");

    private final int DEFAULT_UNSELECT_TEXT_COLOR = ContextCompat.getColor(getContext(),R.color.text_black);

    private final int DEFAULT_SELECT_TEXT_SIZE = SizeUtil.dip2px(14);

    private final int DEFAULT_UNSELECT_TEXT_SIZE = SizeUtil.dip2px(14);

    private final int DEFAULT_OFFSET = 2;

    private final int DEFAULT_DIVIDER_WIDTH = ViewGroup.LayoutParams.MATCH_PARENT;

    private final int DEFAULT_DIVIDER_HEIGHT = SizeUtil.dip2px(1);

    private final int DEFAULT_DIVIVER_COLOR = Color.parseColor("#E4F1FF");

    private final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#00000000");


    private WheelAdapter mAdapter;

    private LinearLayoutManager mLayoutManager;

    private List<String> mDatas; //数据

    private int mItemHeight; //选项高度

    private int mOffset; //处于中间的item为选中，在头尾需补充 offset个空白view，可显示的item数量=2*offset+1

    private int mSelectTextColor; //选中item的文本颜色

    private int mUnselectTextColor; //非选中item的文本颜色

    private float mSelectTextSize; //选中item的文本大小

    private float mUnselectTextSize;//非选中item的文本大小

    private float mDividerWidth;//分割线的宽度

    private float mDividerHeight;//分割线高度

    private int mDividerColor;//分割线颜色

    private int mBackgroundColor;//背景颜色

    private String mSelectItemSuffix;//选中item的后缀

    private Paint mPaint;//绘制分割线的paint

    private OnSelectListener mOnSelectListener;

    private int mSelected;

    private String mSelectText;

    public WheelRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WheelRecyclerView);

        mItemHeight = (int) ta.getDimension(R.styleable.WheelRecyclerView_itemHeight, DEFAULT_ITEM_HEIGHT);
        mSelectTextColor = ta.getColor(R.styleable.WheelRecyclerView_selectTextColor, DEFAULT_SELECT_TEXT_COLOR);
        mUnselectTextColor = ta.getColor(R.styleable.WheelRecyclerView_unselectTextColor, DEFAULT_UNSELECT_TEXT_COLOR);
        mSelectTextSize = ta.getDimension(R.styleable.WheelRecyclerView_selectTextSize, DEFAULT_SELECT_TEXT_SIZE);
        mUnselectTextSize = ta.getDimension(R.styleable.WheelRecyclerView_unselectTextSize, DEFAULT_UNSELECT_TEXT_SIZE);
        mOffset = ta.getInteger(R.styleable.WheelRecyclerView_wheelOffset, DEFAULT_OFFSET);
        mDividerWidth = ta.getDimension(R.styleable.WheelRecyclerView_dividerWidth, DEFAULT_DIVIDER_WIDTH);
        mDividerHeight = ta.getDimension(R.styleable.WheelRecyclerView_dividerHeight, DEFAULT_DIVIDER_HEIGHT);
        mDividerColor = ta.getColor(R.styleable.WheelRecyclerView_dividerColor, DEFAULT_DIVIVER_COLOR);
        mBackgroundColor = ta.getColor(R.styleable.WheelRecyclerView_backgroundColor, DEFAULT_BACKGROUND_COLOR);
        mSelectItemSuffix = ta.getString(R.styleable.WheelRecyclerView_selectSuffix);

        ta.recycle();

        mDatas = new ArrayList<>();
        mPaint = new Paint();
        mPaint.setColor(mDividerColor);
        mPaint.setStrokeWidth(mDividerHeight);

        init();
    }

    private void init() {
        mLayoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(mLayoutManager);
        if (mDividerColor != Color.TRANSPARENT && mDividerHeight != 0 && mDividerWidth != 0) {
            addItemDecoration(new DividerItemDecoration());
        }
        mAdapter = new WheelAdapter();
        setAdapter(mAdapter);
        addOnScrollListener(new OnWheelScrollListener());
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int width;
        int height;
        int heightSpecSize = MeasureSpec.getSize(heightSpec);
        int heightSpecMode = MeasureSpec.getMode(heightSpec);
        //当指定了控件高度时，每个item平分整个高度，控件无指定高度时，默认高度为可视item的累加高度
        switch (heightSpecMode) {
            case MeasureSpec.EXACTLY:
                height = heightSpecSize;
                mItemHeight = height / (mOffset * 2 + 1);
                break;
            default:
                height = (mOffset * 2 + 1) * mItemHeight;
                break;
        }
        width = getDefaultSize(DEFAULT_WIDTH, widthSpec);
        if (mDividerWidth == DEFAULT_DIVIDER_WIDTH) {
            mDividerWidth = width;
        }
        setMeasuredDimension(width, height);
    }

    public void setData(List<String> datas) {
        if (datas == null) {
            return;
        }
        mDatas.clear();
        mDatas.addAll(datas);
        mAdapter.notifyDataSetChanged();
        setSelect(0);
        scrollTo(0,0);
    }

    public void notifyData(){
        mAdapter.notifyDataSetChanged();
    }

    public void setOnSelectListener(OnSelectListener listener) {
        mOnSelectListener = listener;
    }

    public void setSelect(int position) {
        mSelected = position;
        mLayoutManager.scrollToPosition(mSelected);
    }

    public int getSelected() {
        return mSelected;
    }

    private class WheelAdapter extends Adapter<WheelAdapter.WheelHolder> {

        @Override
        public WheelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            WheelHolder holder = new WheelHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_wheel, parent, false));
            holder.name.getLayoutParams().height = mItemHeight;
            return holder;
        }

        @Override
        public int getItemCount() {
            return mDatas.size() == 0 ? 0 : mDatas.size() + mOffset * 2;
        }

        @Override
        public void onBindViewHolder(WheelHolder holder, int position) {
            //头尾填充offset个空白view以使数据能处于中间选中状态
            if (position < mOffset || position > mDatas.size() + mOffset - 1) {
                holder.name.setText("");
            } else {
                holder.name.setText(mDatas.get(position - mOffset));
            }
        }

        class WheelHolder extends ViewHolder {

            TextView name;

            public WheelHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.tv_name);
            }
        }
    }

    private class OnWheelScrollListener extends OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                //当控件停止滚动时，获取可视范围第一个item的位置，滚动调整控件以使选中的item刚好处于正中间
                int firstVisiblePos = mLayoutManager.findFirstVisibleItemPosition();
                if (firstVisiblePos == RecyclerView.NO_POSITION) {
                    return;
                }
                Rect rect = new Rect();
                mLayoutManager.findViewByPosition(firstVisiblePos).getHitRect(rect);
                if (Math.abs(rect.top) > mItemHeight / 2) {
                    smoothScrollBy(0, rect.bottom);
                    mSelected = firstVisiblePos + 1;

                } else {
                    smoothScrollBy(0, rect.top);
                    mSelected = firstVisiblePos;
                }
                if (mOnSelectListener != null) {
                    if(mSelected <mDatas.size()){
                        mOnSelectListener.onSelect(mSelected, mDatas.get(mSelected));
                    }else{
                        mOnSelectListener.onSelect(mSelected, mDatas.get(mDatas.size()-1));
                    }

                }

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            setSelectedItem();
        }
    }

    private void setSelectedItem(){
        //获取可视范围的第一个控件的位置
        int firstVisiblePos = mLayoutManager.findFirstVisibleItemPosition();
        if (firstVisiblePos == RecyclerView.NO_POSITION) {
            return;
        }
        Rect rect = new Rect();
        mLayoutManager.findViewByPosition(firstVisiblePos).getHitRect(rect);
        //被选中item是否已经滑动超出中间区域
        boolean overScroll = Math.abs(rect.top) > mItemHeight / 2 ? true : false;
        //更新可视范围内所有item的样式
        for (int i = 0; i < 1 + mOffset * 2; i++) {
            TextView item;
            if (overScroll) {
                item = (TextView) mLayoutManager.findViewByPosition(firstVisiblePos + i + 1);
            } else {
                item = (TextView) mLayoutManager.findViewByPosition(firstVisiblePos + i);
            }

            if (i == mOffset) {
                item.setTextColor(mSelectTextColor);
                item.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSelectTextSize);

            } else {
                item.setTextColor(mUnselectTextColor);
                item.setTextSize(TypedValue.COMPLEX_UNIT_PX, mUnselectTextSize);
            }
        }
    }

    private class DividerItemDecoration extends ItemDecoration {
        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, State state) {
            //绘制分割线
            float startX = getMeasuredWidth() / 2 - mDividerWidth / 2;
            float topY = mItemHeight * mOffset;
            float endX = getMeasuredWidth() / 2 + mDividerWidth / 2;
            float bottomY = mItemHeight * (mOffset + 1);

            c.drawLine(startX, topY, endX, topY, mPaint);
            c.drawLine(startX, bottomY, endX, bottomY, mPaint);
            int color = mPaint.getColor();
            float width = mPaint.getStrokeWidth();
            mPaint.setColor(mBackgroundColor);
            RectF rect = new RectF(0,topY, SizeUtil.getScreenWidth(getContext()),bottomY);
            c.drawRect(rect,mPaint);

            int height = (int) ((topY+bottomY)/2);
            mPaint.setTextSize(SizeUtil.dip2px(16));
            mPaint.setColor(Color.BLACK);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setStrokeWidth(4);

            Rect bounds = new Rect();
            mPaint.getTextBounds(mSelectItemSuffix, 0, mSelectItemSuffix.length(), bounds);
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            int baseline = (height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;

            c.drawText(mSelectItemSuffix,getMeasuredWidth() / 2+SizeUtil.dip2px(30),height+ SizeUtil.dip2px(6),mPaint);
            mPaint.setColor(color);
            mPaint.setStrokeWidth(width);
        }
    }

    public interface OnSelectListener {
        void onSelect(int position, String data);
    }

}
