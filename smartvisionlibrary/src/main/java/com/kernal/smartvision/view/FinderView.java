package com.kernal.smartvision.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class FinderView extends SurfaceView {
    public FinderView(Context context) {
        super(context);
    }

    public FinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FinderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void draw(Canvas canvas) {
        Path path = new Path();
        //用矩形表示SurfaceView宽高
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        path.addRect(rect, Path.Direction.CCW);
        //裁剪画布，并设置其填充方式
        canvas.clipPath(path, Region.Op.REPLACE);
        super.draw(canvas);
    }

}
