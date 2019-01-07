package com.kernal.smartvision.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

import com.kernal.smartvisionocr.model.ConfigParamsModel;
import com.kernal.smartvisionocr.utils.KernalLSCXMLInformation;

import java.util.List;

/**
 * 扫描页面盖上去的界面，包含中间一个框和外面覆盖的一层半透明浮层
 */
public final class ViewfinderView extends View {
    public static final String TAG = ViewfinderView.class.getSimpleName();

    /**
     * 刷新界面的时间
     */
    private static final long ANIMATION_DELAY = 25L;

    /**
     * 四周边框的宽度
     */
    private static final int FRAME_LINE_WIDTH = 4;
    private Rect frame;
    private int width;
    private int height;
    private Paint paint;
    public KernalLSCXMLInformation wlci;
    public static int fieldsPosition = 0;// 输出结果的先后顺序
    public List<ConfigParamsModel> configParamsModel;
    private Context context;
    private DisplayMetrics dm;

    public ViewfinderView(Context context, KernalLSCXMLInformation wlci,
                          String type, boolean bol) {
        super(context);
        this.wlci = wlci;
        paint = new Paint();
        if (type != null && !type.equals(""))
            configParamsModel = wlci.fieldType.get(type);
        this.context = context;
        // 获取当前屏幕
        dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
    }


    @Override
    public void onDraw(Canvas canvas) {
        width = canvas.getWidth();
        height = canvas.getHeight();

        if (configParamsModel != null) {
            String a = configParamsModel.get(fieldsPosition).nameTextColor.split("_")[0];
            String r = configParamsModel.get(fieldsPosition).nameTextColor.split("_")[1];
            String g = configParamsModel.get(fieldsPosition).nameTextColor.split("_")[2];
            String b = configParamsModel.get(fieldsPosition).nameTextColor.split("_")[3];
            paint.setColor(Color.argb(Integer.valueOf(a), Integer.valueOf(r), Integer.valueOf(g), Integer.valueOf(b)));
            paint.setTextSize(Float.valueOf(configParamsModel.get(fieldsPosition).nameTextSize));

            /**
             * 这个矩形就是中间显示的那个框框
             */
            frame = new Rect((int) (configParamsModel.get(fieldsPosition).leftPointX * width), (int) (height * configParamsModel.get(fieldsPosition).leftPointY), (int) ((configParamsModel.get(fieldsPosition).leftPointX + configParamsModel.get(fieldsPosition).width) * width), (int) (height * (configParamsModel.get(fieldsPosition).leftPointY + configParamsModel.get(fieldsPosition).height)));


            if (frame == null) {
                return;
            }
            // 画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
            // 扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
            paint.setColor(Color.argb(127, 0, 0, 0));
            canvas.drawRect(0, 0, width, frame.top, paint);//扫描框顶部阴影
            canvas.drawRect(0, frame.top, frame.left, frame.bottom,
                    paint);//扫描框左边阴影
            canvas.drawRect(frame.right, frame.top, width,
                    frame.bottom, paint);//扫描框右边阴影
            canvas.drawRect(0, frame.bottom, width, height, paint);//扫描框底部阴影
            a = configParamsModel.get(fieldsPosition).color.split("_")[0];
            r = configParamsModel.get(fieldsPosition).color.split("_")[1];
            g = configParamsModel.get(fieldsPosition).color.split("_")[2];
            b = configParamsModel.get(fieldsPosition).color.split("_")[3];

            // 绘制边宽FRAME_LINE_WIDTH的线框
            paint.setColor(Color.argb(Integer.valueOf(a), Integer.valueOf(r), Integer.valueOf(g), Integer.valueOf(b)));
            canvas.drawRect(frame.left + FRAME_LINE_WIDTH, frame.top,
                    frame.right - FRAME_LINE_WIDTH, frame.top + FRAME_LINE_WIDTH, paint);// 上边
            canvas.drawRect(frame.left, frame.top,
                    frame.left + FRAME_LINE_WIDTH, frame.bottom, paint);// 左边
            canvas.drawRect(frame.right - FRAME_LINE_WIDTH, frame.top,
                    frame.right, frame.bottom
                    , paint);// 右边
            canvas.drawRect(frame.left + FRAME_LINE_WIDTH,
                    frame.bottom - FRAME_LINE_WIDTH, frame.right - FRAME_LINE_WIDTH,
                    frame.bottom, paint);// 底边

        }
        fresh();

    }

    public void fresh() {
        /**
         * 当我们获得结果的时候，我们更新整个屏幕的内容
         */
        postInvalidateDelayed(ANIMATION_DELAY, 0, 0, (int) (width * 0.8),
                height);
    }

}
