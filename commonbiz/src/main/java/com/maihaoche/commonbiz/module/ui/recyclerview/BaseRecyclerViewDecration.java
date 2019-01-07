package com.maihaoche.commonbiz.module.ui.recyclerview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.maihaoche.commonbiz.R;
import com.maihaoche.commonbiz.service.utils.ResourceUtils;
import com.maihaoche.commonbiz.service.utils.SizeUtil;

/**
 * 作者：yang
 * 时间：17/6/12
 * 邮箱：yangyang@maihaoche.com
 */
public class BaseRecyclerViewDecration extends RecyclerView.ItemDecoration {

    /**
     * 垂直方向
     */
    public static final int VERTICAL = LinearLayoutManager.VERTICAL;
    private static final int DEFAULT_SIZE = 1;
    private static final String DEFAULT_COLOR = "#F3F4F5";

    // 画笔
    private Paint paint;

    private int leftMargin;
    // 分割线颜色
    private int color;
    //背景颜色
    private int bgColor;
    // 分割线尺寸
    private int size;


    public static BaseRecyclerViewDecration createLineDecration() {
        BaseRecyclerViewDecration decration = new BaseRecyclerViewDecration(0, 1);
        decration.setBgColor(ResourceUtils.getColor(R.color.gray_E7E7E7));
        decration.setColor(ResourceUtils.getColor(R.color.gray_E7E7E7));
        return decration;
    }

    public static BaseRecyclerViewDecration createBlockDecration(int heightDP) {
        BaseRecyclerViewDecration decration = new BaseRecyclerViewDecration(0, SizeUtil.dip2px(heightDP));
        decration.setBgColor(ResourceUtils.getColor(R.color.bg_grey));
        decration.setColor(ResourceUtils.getColor(R.color.bg_grey));
        return decration;
    }


    public BaseRecyclerViewDecration() {
        this(0, 0);
    }


    public BaseRecyclerViewDecration(int leftMarginDp) {
        this(leftMarginDp, 0);
    }

    public BaseRecyclerViewDecration(int leftMarginDp, int verticalHeightPX) {
        paint = new Paint();
        color = Color.parseColor(DEFAULT_COLOR);
        bgColor = Color.WHITE;
        this.leftMargin = SizeUtil.dip2px(leftMarginDp);
        if (verticalHeightPX <= 0) {
            verticalHeightPX = DEFAULT_SIZE;
        }
        size = verticalHeightPX;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawVertical(c, parent);
    }

    public void setLeftMargin(int margin) {
        this.leftMargin = margin;
    }

    /**
     * 设置分割线颜色
     */
    public void setColor(int color) {
        this.color = color;
    }

    public void setBgColor(int color) {
        this.bgColor = color;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, size);
    }

    /**
     * 设置分割线尺寸
     */
    public void setHeight(int size) {
        this.size = size;
    }

    /**
     * 绘制垂直分割线
     */
    protected void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + size;
            paint.setColor(bgColor);
            c.drawRect(left, top, left + leftMargin, bottom, paint);
            paint.setColor(color);
            c.drawRect(left + leftMargin, top, right, bottom, paint);
        }
    }
}
