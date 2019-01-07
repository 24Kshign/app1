package com.maihaoche.volvo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.maihaoche.volvo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dashu on 16/4/14.
 * 列表的首字母导航界面
 */
public class IndexView extends View {

    private static final float DIS = 1.6f;

    private float mIndexTextSize;
    private int mIndexTextColor;
    private int mIndexSelectTextColor;
    private int mIndexTouchedColor;
    private int mIndexTouchedSelectColor;
    private List<Map.Entry<String, Integer>> mIndexList = new ArrayList<>();

    private float mWidth;

    private boolean isTouched = false;

    private Paint mPaint;

    private int mSelectPosition = -1;

    private OnIndexTouchListener mListener;

    public void setOnIndexTouchListener(OnIndexTouchListener listener) {
        mListener = listener;
    }

    public void setIndexList(Set<Map.Entry<String, Integer>> data) {
        mIndexList.clear();
        mIndexList.addAll(data);
        requestLayout();
        invalidate();
    }

    public void setSelectPosition(long c) {
        for (int i = 0; i < mIndexList.size(); i++) {
            Map.Entry<String, Integer> entry = mIndexList.get(i);
            if (c == entry.getKey().charAt(0)) {
                mSelectPosition = i;
                break;
            }
        }
        invalidate();
    }

    public IndexView(Context context) {
        this(context, null);
    }

    public IndexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IndexView);

        mIndexTextSize = a.getDimension(R.styleable.IndexView_indexTextSize, 24);
        mIndexTextColor = a.getColor(R.styleable.IndexView_indexTextColor,
                ActivityCompat.getColor(context, R.color.grey_FCFCFC));
        mIndexSelectTextColor = a.getColor(R.styleable.IndexView_indexSelectTextColor,
                ActivityCompat.getColor(context, R.color.orange_88FF8903));
        mIndexTouchedColor = a.getColor(R.styleable.IndexView_indexTouchedColor,
                ActivityCompat.getColor(context, R.color.black_373737));
        mIndexTouchedSelectColor = a.getColor(R.styleable.IndexView_indexTouchedSelectColor,
                ActivityCompat.getColor(context, R.color.orange_FF9933));

        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mIndexTextSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setFakeBoldText(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = (int) (getPaddingLeft() + getPaddingRight() + mIndexTextSize * DIS);
        }


        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) (getPaddingTop() + getPaddingBottom() + mIndexTextSize * mIndexList.size() * DIS);
        }

        mWidth = width;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(isTouched ? mIndexTouchedColor : mIndexTextColor);
        for (int i = 0; i < mIndexList.size(); i++) {
            if (mSelectPosition == i) {
                mPaint.setColor(isTouched ? mIndexTouchedSelectColor : mIndexSelectTextColor);
                drawText(canvas, mIndexList.get(i).getKey(), getPaddingTop() + mIndexTextSize * DIS * i);
                mPaint.setColor(isTouched ? mIndexTouchedColor : mIndexTextColor);
            } else {
                drawText(canvas, mIndexList.get(i).getKey(), getPaddingTop() + mIndexTextSize * DIS * i);
            }
        }
    }

    private void drawText(Canvas canvas, String s, float y) {
        Rect bounds = new Rect();
        mPaint.getTextBounds(s, 0, s.length(), bounds);
        canvas.drawText(s, mWidth / 2, y + bounds.height(), mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int pos;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouched = true;
                pos = (int) ((event.getY() - getPaddingTop() - getPaddingBottom()) / (mIndexTextSize * DIS));
                if (pos > -1 && pos < mIndexList.size() && pos != mSelectPosition && mListener != null) {
                    mSelectPosition = pos;
                    mListener.onIndexTouch(mIndexList.get(pos).getKey(),mIndexList.get(pos).getValue());
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isTouched = true;
                pos = (int) ((event.getY() - getPaddingTop() - getPaddingBottom()) / (mIndexTextSize * DIS));
                if (pos > -1 && pos < mIndexList.size() && pos != mSelectPosition && mListener != null) {
                    mSelectPosition = pos;
                    mListener.onIndexTouch(mIndexList.get(pos).getKey(),mIndexList.get(pos).getValue());
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                isTouched = false;
                invalidate();
                break;
            default:
                isTouched = false;
                invalidate();
        }
        return true;
    }

    public interface OnIndexTouchListener {
        void onIndexTouch(String key, int value);
    }

}