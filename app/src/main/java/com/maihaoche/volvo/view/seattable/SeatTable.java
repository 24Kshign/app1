package com.maihaoche.volvo.view.seattable;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import android.widget.Toast;

import com.maihaoche.commonbiz.service.utils.DeviceUtil;
import com.maihaoche.commonbiz.service.utils.StringUtil;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.ui.common.chart.PipChartFormatter;
import com.maihaoche.volvo.ui.common.daomain.SearchResultInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.maihaoche.volvo.view.seattable.CarportStatus.EMPTY;
import static com.maihaoche.volvo.view.seattable.CarportStatus.OCCUPY;
import static com.maihaoche.volvo.view.seattable.CarportStatus.OCCUPY_SELECT;
import static com.maihaoche.volvo.view.seattable.CarportStatus.SELECTED;
import static com.maihaoche.volvo.view.seattable.CarportStatus.UNAVAILABLE;
import static com.maihaoche.volvo.view.seattable.CarportStatus.USED;
import static com.maihaoche.volvo.view.seattable.CarportStatus.USED_SELECT;


/**
 * Created by brantyu on 17/8/8.
 */
public class SeatTable extends View {
    private final boolean DBG = true;

    Paint paint = new Paint();
    Paint overviewPaint = new Paint();
    Paint lineNumberPaint;
    float lineNumberTxtHeight;

    /**
     * 设置行号 默认显示 1,2,3....数字
     *
     * @param lineNumbers
     */
    public void setLineNumbers(ArrayList<String> lineNumbers) {
        this.lineNumbers = lineNumbers;
        invalidate();
    }

    /**
     * 用来保存所有行号
     */
    ArrayList<String> lineNumbers = new ArrayList<>();

    Paint.FontMetrics lineNumberPaintFontMetrics;
    Matrix matrix = new Matrix();

    /**
     * 座位水平间距
     */
    int spacing;

    /**
     * 座位垂直间距
     */
    int verSpacing;

    /**
     * 行号宽度
     */
    int numberWidth;

    /**
     * 行数
     */
    int row;

    /**
     * 列数
     */
    int column;

    /**
     * 可选时座位的图片
     */
    Bitmap seatBitmap;

    /**
     * 搜索座位的图片
     */
    Bitmap searchBitmap;

    /**
     * 占位的图片
     */
    Bitmap occupyBitmap;
    /**
     * 占位选中的图片
     */
    Bitmap occupySelectBitmap;

    /**
     * 搜索选中座位的图片
     */
    Bitmap searcSelecthBitmap;

    /**
     * 选中时座位的图片
     */
    Bitmap checkedSeatBitmap;

    Bitmap doorBitmap;

    /**
     * 座位已经售出时的图片
     */
    Bitmap seatSoldBitmap;

    Bitmap overviewBitmap;

    Bitmap empytStatus;
    Bitmap occupyStatus;
    Bitmap useStatus;

    int lastX;
    int lastY;

    /**
     * 整个座位图的宽度
     */
    int seatBitmapWidth;

    /**
     * 整个座位图的高度
     */
    int seatBitmapHeight;

    /**
     * 标识是否需要绘制座位图
     */
    boolean isNeedDrawSeatBitmap = true;

    /**
     * 概览图白色方块高度
     */
    float rectHeight;

    /**
     * 概览图白色方块的宽度
     */
    float rectWidth;

    /**
     * 概览图上方块的水平间距
     */
    float overviewSpacing;

    /**
     * 概览图上方块的垂直间距
     */
    float overviewVerSpacing;

    /**
     * 概览图的比例
     */
    float overviewScale = 4.8f;

    /**
     * 荧幕高度
     */
    float screenHeight;

    /**
     * 荧幕默认宽度与座位图的比例
     */
    float screenWidthScale = 0.5f;

    /**
     * 荧幕最小宽度
     */
    int defaultScreenWidth;

    /**
     * 标识是否正在缩放
     */
    boolean isScaling;
    float scaleX, scaleY;

    /**
     * 是否是第一次缩放
     */
    boolean firstScale = true;

    /**
     * 最多可以选择的座位数量
     */
    int maxSelected = Integer.MAX_VALUE;

    private CarportChecker seatChecker;

    /**
     * 荧幕名称
     */
    private String screenName = "";

    /**
     * 整个概览图的宽度
     */
    float rectW;

    /**
     * 整个概览图的高度
     */
    float rectH;

    Paint headPaint;
    Bitmap headBitmap;

    /**
     * 是否第一次执行onDraw
     */
    boolean isFirstDraw = true;

    /**
     * 标识是否需要绘制概览图
     */
    boolean isDrawOverview = false;

    /**
     * 标识是否需要更新概览图
     */
    boolean isDrawOverviewBitmap = true;

    int overview_checked;
    int overview_sold;
    int txt_color;
    int seatCheckedResID;
    int seatSoldResID;
    int seatAvailableResID;

    boolean isOnClick;

    private int downX, downY;
    private boolean pointer;
    private boolean mIsFling;

    /**
     * 顶部高度,可选,已选,已售区域的高度
     */
    float headHeight;

    Paint pathPaint;
    RectF rectF;

    /**
     * 头部下面横线的高度
     */
    int borderHeight = 1;
    Paint redBorderPaint;

    /**
     * 默认的座位图宽度,如果使用的自己的座位图片比这个尺寸大或者小,会缩放到这个大小
     */
    private float defaultImgW = 30;

    /**
     * 默认的座位图高度
     */
    private float defaultImgH = 30;

    /**
     * 座位图片的宽度
     */
    private int seatWidth;

    /**
     * 座位图片的高度
     */
    private int seatHeight;

    private Scroller mScroller;

    private ValueAnimator valueAnimator;

    private boolean isFirst = true;
    //是否需要开启动画，如果座位图小于屏幕就不用开启
    private boolean shouldAni = false;
    //座位起始偏移量
    private int dx;
    private int dy;

    //是否进入搜索模式
    private boolean isSearchMode = false;
    //搜索结果
    private List<SearchResultInfo> searchResult;

    private boolean isMoveMode = false;

    private float doorX;

    private boolean moveDialogMode = false;//是否是在库列表移位
    private Point movePoint = null;//移位的车辆


    public void setSearchSeat(List<SearchResultInfo> searchResult) {
        this.searchResult = searchResult;
        cleanSelects();
        addChooseSeat(searchResult.get(0).row, searchResult.get(0).col);
    }

    public void setMoveDialogMode(boolean moveDialogMode){
        this.moveDialogMode = moveDialogMode;
    }

    public void setMovePoint(Point movePoint){
        this.movePoint = movePoint;
        this.moveDialogMode = true;
    }

    public void setSearchMode(boolean isSearchMode) {
        this.isSearchMode = isSearchMode;
        isDrawOverview = true;
        isDrawOverviewBitmap = true;

        if (!isSearchMode && searchResult!=null && searchResult.size() > 0) {
            for (SearchResultInfo info : searchResult) {
                int id = getID(info.row, info.col);
                int index = isHave(id);
                if (index >= 0) {
                    remove(index);
                }
            }

        }

        invalidate();
    }

    public void setMoveMode(boolean isMoveMode) {
        this.isMoveMode = isMoveMode;
    }

    public boolean getMoveMode() {
        return isMoveMode;
    }

    public void setDoorX(float x){
        doorX = x;
    }

    public SeatTable(Context context) {
        super(context);

    }

    public SeatTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mScroller = new Scroller(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeatTableView);
        overview_checked = typedArray.getColor(R.styleable.SeatTableView_overview_checked, Color.parseColor("#0000ff"));
        overview_sold = typedArray.getColor(R.styleable.SeatTableView_overview_sold, Color.GRAY);
        txt_color = typedArray.getColor(R.styleable.SeatTableView_txt_color, Color.BLACK);
        seatCheckedResID = typedArray.getResourceId(R.styleable.SeatTableView_seat_checked, R.drawable.select_seat);
        seatSoldResID = typedArray.getResourceId(R.styleable.SeatTableView_overview_sold, R.drawable.fill_seat);
        seatAvailableResID = typedArray.getResourceId(R.styleable.SeatTableView_seat_available, R.drawable.empty_seat);
        typedArray.recycle();
    }

    public SeatTable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    float xScale1 = 1;
    float yScale1 = 1;

    private void init() {
        spacing = (int) dip2Px(5);
        verSpacing = (int) dip2Px(5);
        defaultScreenWidth = (int) dip2Px(80);

        searchBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.search_seat);
        searcSelecthBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.search_select_seat);
        occupyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.occupy_seat);
        occupySelectBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.occupy_select_seat);
        seatBitmap = BitmapFactory.decodeResource(getResources(), seatAvailableResID);
        empytStatus = BitmapFactory.decodeResource(getResources(), R.drawable.empyt_status);
        occupyStatus = BitmapFactory.decodeResource(getResources(), R.drawable.occupy_status);
        useStatus = BitmapFactory.decodeResource(getResources(), R.drawable.use_status);
        doorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_door);

        float scaleX = defaultImgW / seatBitmap.getWidth();
        float scaleY = defaultImgH / seatBitmap.getHeight();
        xScale1 = scaleX;
        yScale1 = scaleY;

        seatHeight = (int) (seatBitmap.getHeight() * yScale1);
        seatWidth = (int) (seatBitmap.getWidth() * xScale1);

        checkedSeatBitmap = BitmapFactory.decodeResource(getResources(), seatCheckedResID);
        seatSoldBitmap = BitmapFactory.decodeResource(getResources(), seatSoldResID);

        seatBitmapWidth = (int) (column * seatBitmap.getWidth() * xScale1 + (column - 1) * spacing);
        seatBitmapHeight = (int) (row * seatBitmap.getHeight() * yScale1 + (row - 1) * verSpacing+doorBitmap.getHeight()*yScale1+verSpacing);
        paint.setColor(Color.RED);
        numberWidth = (int) dip2Px(20);

        screenHeight = dip2Px(10);
        headHeight = dip2Px(30);

        headPaint = new Paint();
        headPaint.setStyle(Paint.Style.FILL);
        headPaint.setTextSize(dip2Px(13));
        headPaint.setColor(Color.WHITE);
        headPaint.setAntiAlias(true);

        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(Color.parseColor("#e2e2e2"));

        redBorderPaint = new Paint();
        redBorderPaint.setAntiAlias(true);
        redBorderPaint.setColor(Color.RED);
        redBorderPaint.setStyle(Paint.Style.STROKE);
        redBorderPaint.setStrokeWidth(getResources().getDisplayMetrics().density * 1);

        rectF = new RectF();

        rectHeight = seatHeight / overviewScale;
        rectWidth = seatWidth / overviewScale;
        overviewSpacing = spacing / overviewScale;
        overviewVerSpacing = verSpacing / overviewScale;

        rectW = column * rectWidth + (column - 1) * overviewSpacing + overviewSpacing * 2;
        rectH = (row+1) * rectHeight + (row) * overviewVerSpacing + overviewVerSpacing * 2;
        overviewBitmap = Bitmap.createBitmap((int) rectW, (int) rectH, Bitmap.Config.ARGB_4444);

        lineNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lineNumberPaint.setColor(bacColor);
        lineNumberPaint.setTextSize(getResources().getDisplayMetrics().density * 16);
        lineNumberTxtHeight = lineNumberPaint.measureText("4");
        lineNumberPaintFontMetrics = lineNumberPaint.getFontMetrics();
        lineNumberPaint.setTextAlign(Paint.Align.CENTER);

        if (lineNumbers == null) {
            lineNumbers = new ArrayList<>();
        } else if (lineNumbers.size() <= 0) {
            for (int i = 0; i < row; i++) {
                lineNumbers.add((i + 1) + "");
            }
        }


        dx = spacing;
        dy = (int) (headHeight + screenHeight + borderHeight + verSpacing);

        matrix.postTranslate(dx, dy);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        final float scalX = (float) (getWidth() * 1.0 / seatBitmapWidth);
        final float scalY = (float) (getHeight() * 1.0 / seatBitmapHeight);
        if (getWidth() < seatBitmapWidth) {
            shouldAni = true;
        }
        if (isFirst && shouldAni) {
            matrix.setScale(scalX, scalY);
            matrix.postTranslate(dx, dy);
        }

        if (row <= 0 || column == 0) {
            return;
        }

        drawSeat(canvas);
//        drawNumber(canvas);

        if (headBitmap == null) {
            headBitmap = drawHeadInfo();
        }
        canvas.drawBitmap(headBitmap, 0, 0, null);

//        drawScreen(canvas);

        if (isDrawOverview) {
            if (isDrawOverviewBitmap) {
                drawOverview();
            }
            canvas.drawBitmap(overviewBitmap, 0, 0, null);
            drawOverview(canvas);
        }



        if (isFirst && shouldAni) {
            valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(2000);
            valueAnimator.addUpdateListener(animation -> {
                float xx = (float) animation.getAnimatedValue();
                float scaX = 0;
                float scaY = 0;
                if (xx < 1) {
                    scaX = scalX + (1 - scalX) * xx;
                    scaY = scalY + (1 - scalY) * xx;
                }
                if (scaX <= 0) {
                    scaX = 1;
                    scaY = 1;
                }
                matrix.setScale(scaX, scaY);
                matrix.postTranslate(dx, dy);
                invalidate();

            });
            valueAnimator.start();
            isFirst = !isFirst;
        }
    }

    private void drawBorder(Canvas canvas) {

        zoom = getMatrixScaleX();
        float translateX = getTranslateX();
        float translateY = getTranslateY();
        float scaleX = zoom;
        float scaleY = zoom;

        float x1 = translateX*scaleX;
        float y1 = (seatBitmapHeight+translateY)*scaleY+dip2Px(3);
        float x2 = (seatBitmapWidth+translateX)*scaleX;
        float y2 = (seatBitmapHeight+translateY)*scaleY+dip2Px(3);

        float x3 = (seatBitmapWidth+translateX)*scaleX+dip2Px(3);
        float y3 = (headHeight+translateY)*scaleY;
        float x4 = (seatBitmapWidth+translateX)*scaleX+dip2Px(3);
        float y4 = (seatBitmapHeight+translateY)*scaleY+dip2Px(3);
        int color = paint.getColor();
        paint.setColor(Color.GRAY);
        canvas.drawLine(x1,y1,x2,y2,paint);
        canvas.drawLine(x3,y3,x4,y4,paint);
        paint.setColor(color);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        int x = (int) event.getX();
//        super.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        int pointerCount = event.getPointerCount();
        if (pointerCount > 1) {
            pointer = true;
        }

        //禁止parent拦截事件
        this.getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pointer = false;
                mIsFling = false;
                downX = x;
                downY = y;
                isDrawOverview = true;
                isDrawOverviewBitmap = true;
                handler.removeCallbacks(hideOverviewRunnable);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isScaling && !isOnClick) {
                    int downDX = Math.abs(x - downX);
                    int downDY = Math.abs(y - downY);
                    if ((downDX > 10 || downDY > 10) && !pointer) {
                        int dx = x - lastX;
                        int dy = y - lastY;
                        matrix.postTranslate(dx, dy);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                handler.postDelayed(hideOverviewRunnable, 1500);
                autoScale();
                int downDX = Math.abs(x - downX);
                int downDY = Math.abs(y - downY);
                if ((downDX > 10 || downDY > 10) && !pointer && !mIsFling) {
                    autoScroll();
                }

                break;
            default:
        }
        isOnClick = false;
        lastY = y;
        lastX = x;

        return true;
    }

    private Runnable hideOverviewRunnable = new Runnable() {
        @Override
        public void run() {
            isDrawOverview = false;
            invalidate();
        }
    };

    Bitmap drawHeadInfo() {
        String txt = "已售";
        float txtY = getBaseLine(headPaint, 0, headHeight);
        int txtWidth = (int) headPaint.measureText(txt);
        float spacing = dip2Px(8);
        float spacing1 = dip2Px(5);
        float statusHeight = empytStatus.getHeight();
        float statusWidth = empytStatus.getWidth();

        float width = empytStatus.getWidth() + spacing1 + txtWidth + spacing + occupyStatus.getWidth() + txtWidth + spacing1 + spacing + useStatus.getHeight() + spacing1 + txtWidth;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), (int) headHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        //绘制背景
        canvas.drawRect(0, 0, getWidth(), headHeight, headPaint);
        headPaint.setColor(Color.GRAY);
        headPaint.setTextSize(statusWidth/4*3);

        float startX = (getWidth() - width)-spacing1;
        float diffScalX = 0.2F;
        float diffScalY = 0.2F;
        tempMatrix.setScale(xScale1 + dip2Px(diffScalX), yScale1 + dip2Px(diffScalY));
        //图片没有居中显示，2dp做微调
        tempMatrix.postTranslate(startX, (headHeight - (statusHeight-dip2Px(2))) / 2);
        canvas.drawBitmap(empytStatus, tempMatrix, headPaint);
        canvas.drawText("可选", startX + statusWidth + spacing1, txtY, headPaint);

        float soldSeatBitmapY = startX + empytStatus.getWidth() + spacing1 + txtWidth + spacing;
        tempMatrix.setScale(xScale1 + dip2Px(diffScalX), yScale1 + dip2Px(diffScalY));
        tempMatrix.postTranslate(soldSeatBitmapY, (headHeight - (statusHeight-dip2Px(2))) / 2);
        canvas.drawBitmap(occupyStatus, tempMatrix, headPaint);
        canvas.drawText("占用", soldSeatBitmapY + statusWidth + spacing1, txtY, headPaint);

        float checkedSeatBitmapX = soldSeatBitmapY + empytStatus.getWidth() + spacing1 + txtWidth + spacing;
        tempMatrix.setScale(xScale1 + dip2Px(diffScalX), yScale1 + dip2Px(diffScalY));
        tempMatrix.postTranslate(checkedSeatBitmapX, (headHeight - (statusHeight-dip2Px(2))) / 2);
        canvas.drawBitmap(useStatus, tempMatrix, headPaint);
        canvas.drawText("已停", checkedSeatBitmapX + spacing1 + statusWidth, txtY, headPaint);

        //绘制分割线
        headPaint.setStrokeWidth(dip2Px(0.5f));
        headPaint.setColor(Color.GRAY);
        canvas.drawLine(0, headHeight, getWidth(), headHeight, headPaint);
        return bitmap;

    }

    /**
     * 绘制中间屏幕
     */
    void drawScreen(Canvas canvas) {
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(Color.parseColor("#e2e2e2"));
        float startY = headHeight + borderHeight;

        float centerX = seatBitmapWidth * getMatrixScaleX() / 2 + getTranslateX();
        float screenWidth = seatBitmapWidth * screenWidthScale * getMatrixScaleX();
        if (screenWidth < defaultScreenWidth) {
            screenWidth = defaultScreenWidth;
        }

        Path path = new Path();
        path.moveTo(centerX, startY);
        path.lineTo(centerX - screenWidth / 2, startY);
        path.lineTo(centerX - screenWidth / 2 + 20, screenHeight * getMatrixScaleY() + startY);
        path.lineTo(centerX + screenWidth / 2 - 20, screenHeight * getMatrixScaleY() + startY);
        path.lineTo(centerX + screenWidth / 2, startY);

        canvas.drawPath(path, pathPaint);

        pathPaint.setColor(Color.BLACK);
        pathPaint.setTextSize(20 * getMatrixScaleX());

        canvas.drawText(screenName, centerX - pathPaint.measureText(screenName) / 2, getBaseLine(pathPaint, startY, startY + screenHeight * getMatrixScaleY()), pathPaint);
    }

    Matrix tempMatrix = new Matrix();

    void drawSeat(Canvas canvas) {
        zoom = getMatrixScaleX();
        float translateX = getTranslateX();
        float translateY = getTranslateY();
        float scaleX = zoom;
        float scaleY = zoom;
        blockNames.clear();

        for (int i = 0; i < row; i++) {
            float top = i * seatBitmap.getHeight() * yScale1 * scaleY + i * verSpacing * scaleY + translateY;

            float bottom = top + seatBitmap.getHeight() * yScale1 * scaleY;
            if (bottom < 0 || top > getHeight()) {
                continue;
            }

            for (int j = 0; j < column; j++) {
                float left = j * seatBitmap.getWidth() * xScale1 * scaleX + j * spacing * scaleX + translateX;

                float right = (left + seatBitmap.getWidth() * xScale1 * scaleY);
                if (right < 0 || left > getWidth()) {
                    continue;
                }

                CarportStatus seatType = getSeatType(i, j);
                tempMatrix.setTranslate(left, top);
                tempMatrix.postScale(xScale1, yScale1, left, top);
                tempMatrix.postScale(scaleX, scaleY, left, top);

                if (seatType != UNAVAILABLE) {
                    drawBlockText(canvas, i, j, top, left);
                }
                switch (seatType) {
                    case EMPTY:
                        RectF rectF = new RectF(left,top,right,bottom);
                        int oldColor = paint.getColor();
                        paint.setColor(android.graphics.Color.GRAY);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(1);
                        canvas.drawRect(rectF,paint);
                        paint.setColor(oldColor);
//                        canvas.drawBitmap(seatBitmap, tempMatrix, paint);
//                        drawText(canvas, i, j, bottom, left);
                        break;
                    case UNAVAILABLE:
                        break;
                    case SELECTED:
                        canvas.drawBitmap(checkedSeatBitmap, tempMatrix, paint);
                        drawText(canvas, i, j, top, left);
                        break;
                    case USED:
                        canvas.drawBitmap(seatSoldBitmap, tempMatrix, paint);
//                        drawText(canvas, i, j, top, left);
                        break;
                    case USED_SELECT:
                        canvas.drawBitmap(searcSelecthBitmap, tempMatrix, paint);
                        break;
                    case OCCUPY:
                        canvas.drawBitmap(occupyBitmap, tempMatrix, paint);
//                        drawText(canvas, i, j, top, left);
                        break;
                    case OCCUPY_SELECT:
                        canvas.drawBitmap(occupySelectBitmap, tempMatrix, paint);
//                        drawText(canvas, i, j, top, left);
                        break;
                }

            }
        }

        if(moveDialogMode && movePoint!=null){
            int id = getID(movePoint.x, movePoint.y);
            int index = isHave(id);
            float top = movePoint.x * seatBitmap.getHeight() * yScale1 * scaleY + movePoint.x * verSpacing * scaleY + translateY;
            float bottom = top + seatBitmap.getHeight() * yScale1 * scaleY;
            float left = movePoint.y * seatBitmap.getWidth() * xScale1 * scaleX + movePoint.y * spacing * scaleX + translateX;
            float right = (left + seatBitmap.getWidth() * xScale1 * scaleY);
            tempMatrix.setTranslate(left, top);
            tempMatrix.postScale(xScale1, yScale1, left, top);
            tempMatrix.postScale(scaleX, scaleY, left, top);

            canvas.drawBitmap(searcSelecthBitmap, tempMatrix, paint);
        }

        //画大门
        paint.setAntiAlias(true);
        float doorTop = row * seatBitmap.getHeight() * yScale1 * scaleY + row * verSpacing * scaleY + translateY;
        float doorLeft = doorX * seatBitmap.getWidth() * xScale1 * scaleX + doorX * spacing * scaleX + translateX;
        tempMatrix.setTranslate(doorLeft, doorTop);
        tempMatrix.postScale(xScale1, yScale1, doorLeft, doorTop);
        tempMatrix.postScale(scaleX, scaleY, doorLeft, doorTop);
        canvas.drawBitmap(doorBitmap, tempMatrix, paint);

        //画搜索出的车辆
        if (isSearchMode && searchResult != null && searchResult.size() > 0) {
            for (SearchResultInfo p : searchResult) {

                int id = getID(p.row, p.col);
                int index = isHave(id);
                if (index >= 0) {
                    continue;
                }

                float top = p.row * seatBitmap.getHeight() * yScale1 * scaleY + p.row * verSpacing * scaleY + translateY;
                float bottom = top + seatBitmap.getHeight() * yScale1 * scaleY;
                float left = p.col * seatBitmap.getWidth() * xScale1 * scaleX + p.col * spacing * scaleX + translateX;
                float right = (left + seatBitmap.getWidth() * xScale1 * scaleY);
                tempMatrix.setTranslate(left, top);
                tempMatrix.postScale(xScale1, yScale1, left, top);
                tempMatrix.postScale(scaleX, scaleY, left, top);

                canvas.drawBitmap(searchBitmap, tempMatrix, paint);

            }

        }

    }

    private Set<String> blockNames = new HashSet<>();

    private void drawBlockText(Canvas canvas, int row, int column, float top, float left) {

        String txt = null;

        if (seatChecker != null) {
            String string = seatChecker.checkedSeatArea(row, column);
            if (StringUtil.isNotEmpty(string)) {
                txt = string + "区";
            }
        }
        if (txt == null) {
            return;
        }
        if (blockNames.contains(txt)) {
            return;
        }

        TextPaint txtPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(txt_color);
        txtPaint.setTypeface(Typeface.DEFAULT_BOLD);
        float seatHeight = this.seatHeight * getMatrixScaleX();
        float seatWidth = this.seatWidth * getMatrixScaleX();
        float verSpace = this.verSpacing * getMatrixScaleX();
        txtPaint.setTextSize(seatHeight / 2);

        //获取中间线
        float center = seatHeight / 2;
        float txtWidth = txtPaint.measureText(txt);
        float startX = left + seatWidth / 2 - txtWidth / 2;

        //只绘制一行文字
        canvas.drawText(txt, startX, getBaseLine(txtPaint, top - verSpace * 2 - seatHeight, top + seatHeight) - dip2Px(5), txtPaint);

        blockNames.add(txt);

        if (DBG) {
//            Log.d("drawTest:", "top:" + top);
        }
    }

    private CarportStatus getSeatType(int row, int column) {

        if (seatChecker != null && row>=0 && column>=0) {
            if (!seatChecker.isValid(row, column)) {
                return UNAVAILABLE;
            } else if (seatChecker.isUsed(row, column) && isHave(getID(row, column)) >= 0) {
                return USED_SELECT;
            } else if (seatChecker.isOccupy(row, column) && isHave(getID(row, column)) >= 0) {
                return OCCUPY_SELECT;
            } else if (seatChecker.isUsed(row, column)) {
                return USED;
            } else if (seatChecker.isOccupy(row, column)) {
                return OCCUPY;
            }
        }

        if (isHave(getID(row, column)) >= 0) {
            return SELECTED;
        }

        return EMPTY;
    }

    private int getID(int row, int column) {
        return row * this.column + (column + 1);
    }

    /**
     * 绘制选中座位的行号列号
     *
     * @param row
     * @param column
     */
    private void drawText(Canvas canvas, int row, int column, float top, float left) {

        String txt = (row + 1) + "排";
        String txt1 = (column + 1) + "列";

        if (seatChecker != null) {
            String[] strings = seatChecker.checkedSeatTxt(row, column);
            if (strings != null && strings.length > 0) {
                if (strings.length >= 2) {
                    txt = strings[0];
                    txt1 = strings[1];
                } else {
                    txt = strings[0];
                    txt1 = null;
                }
            }
        }

        TextPaint txtPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(Color.WHITE);
        txtPaint.setTypeface(Typeface.DEFAULT_BOLD);
        float seatHeight = this.seatHeight * getMatrixScaleX();
        float seatWidth = this.seatWidth * getMatrixScaleX();
        float textSize = seatHeight / 3;
        txtPaint.setTextSize(textSize);

        //获取中间线
        float txtWidth1 = txtPaint.measureText(txt);
        float txtWidth2 = txtPaint.measureText(txt1);
        float startX1 = left + seatWidth / 2 - txtWidth1 / 2;
        float startX2 = left + seatWidth / 2 - txtWidth2 / 2;

        //只绘制一行文字
        if (txt1 == null) {
            canvas.drawText(txt, startX1, top + textSize, txtPaint);
        } else {
            canvas.drawText(txt, startX1, top + textSize, txtPaint);
            canvas.drawText(txt1, startX2, top + seatHeight - 5, txtPaint);
        }

//        if (DBG) {
//            Log.d("drawTest:", "top:" + top);
//        }
    }

    int bacColor = Color.parseColor("#7e000000");

    /**
     * 绘制行号
     */
    void drawNumber(Canvas canvas) {
        long startTime = System.currentTimeMillis();
        lineNumberPaint.setColor(bacColor);
        int translateY = (int) getTranslateY();
        float scaleY = getMatrixScaleY();

        rectF.top = translateY - lineNumberTxtHeight / 2;
        rectF.bottom = translateY + (seatBitmapHeight * scaleY) + lineNumberTxtHeight / 2;
        rectF.left = 0;
        rectF.right = numberWidth;
        canvas.drawRoundRect(rectF, numberWidth / 2, numberWidth / 2, lineNumberPaint);

        lineNumberPaint.setColor(Color.WHITE);

        for (int i = 0; i < row; i++) {

            float top = (i * seatHeight + i * verSpacing) * scaleY + translateY;
            float bottom = (i * seatHeight + i * verSpacing + seatHeight) * scaleY + translateY;
            float baseline = (bottom + top - lineNumberPaintFontMetrics.bottom - lineNumberPaintFontMetrics.top) / 2;

            canvas.drawText(lineNumbers.get(i), numberWidth / 2, baseline, lineNumberPaint);
        }

        if (DBG) {
            long drawTime = System.currentTimeMillis() - startTime;
//            Log.d("drawTime", "drawNumberTime:" + drawTime);
        }
    }

    /**
     * 绘制概览图
     */
    void drawOverview(Canvas canvas) {

        //绘制红色框
        int left = (int) -getTranslateX();
        if (left < 0) {
            left = 0;
        }
        left /= overviewScale;
        left /= getMatrixScaleX();

        int currentWidth = (int) (getTranslateX() + (column * seatWidth + spacing * (column - 1)) * getMatrixScaleX());
        if (currentWidth > getWidth()) {
            currentWidth = currentWidth - getWidth();
        } else {
            currentWidth = 0;
        }
        int right = (int) (rectW - currentWidth / overviewScale / getMatrixScaleX());

        float top = -getTranslateY() + headHeight;
        if (top < 0) {
            top = 0;
        }
        top /= overviewScale;
        top /= getMatrixScaleY();
        if (top > 0) {
            top += overviewVerSpacing;
        }

        int currentHeight = (int) (getTranslateY() + (row * seatHeight + verSpacing * (row - 1)) * getMatrixScaleY());
        if (currentHeight > getHeight()) {
            currentHeight = currentHeight - getHeight();
        } else {
            currentHeight = 0;
        }
        int bottom = (int) (rectH - currentHeight / overviewScale / getMatrixScaleY());

        canvas.drawRect(left, top, right, bottom, redBorderPaint);
    }

    /**
     * 绘制概览图的底图
     *
     * @return
     */
    Bitmap drawOverview() {
        isDrawOverviewBitmap = false;

        int bac = Color.parseColor("#33000000");
        overviewPaint.setColor(bac);
        overviewPaint.setAntiAlias(true);
        overviewPaint.setStyle(Paint.Style.FILL);
        overviewBitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(overviewBitmap);
        //绘制透明灰色背景
        canvas.drawRect(0, 0, rectW, rectH, overviewPaint);

        overviewPaint.setColor(Color.WHITE);
        for (int i = 0; i < row; i++) {
            float top = i * rectHeight + i * overviewVerSpacing + overviewVerSpacing;
            for (int j = 0; j < column; j++) {

                CarportStatus seatType = getSeatType(i, j);
                switch (seatType) {
                    case EMPTY:
                        overviewPaint.setColor(Color.WHITE);
                        break;
                    case UNAVAILABLE:
                        continue;
                    case SELECTED:
                    case USED_SELECT:
                    case OCCUPY_SELECT:
                        overviewPaint.setColor(overview_checked);
                        break;
                    case USED:
                    case OCCUPY:
                        overviewPaint.setColor(overview_sold);
                        break;
                }

                float left;

                left = j * rectWidth + j * overviewSpacing + overviewSpacing;
                canvas.drawRect(left, top, left + rectWidth, top + rectHeight, overviewPaint);
            }
        }

        //画门
        overviewPaint.setColor(overview_sold);
        float doorTop = row * rectHeight + row * overviewVerSpacing + overviewVerSpacing;
        float doorLeft = doorX * rectWidth + doorX * overviewSpacing + overviewSpacing;
        canvas.drawRect(doorLeft, doorTop, doorLeft + rectWidth*3, doorTop + rectHeight, overviewPaint);

        if (isSearchMode && searchResult != null && searchResult.size() > 0) {
            overviewPaint.setColor(Color.BLUE);
            for (SearchResultInfo p : searchResult) {
                float top = p.row * rectHeight + p.row * overviewVerSpacing + overviewVerSpacing;
                float left = p.col * rectWidth + p.col * overviewSpacing + overviewSpacing;
                canvas.drawRect(left, top, left + rectWidth, top + rectHeight, overviewPaint);
            }
        }
        overviewPaint.setColor(overview_checked);
        if(moveDialogMode && movePoint!=null){
            float top = movePoint.x * rectHeight + movePoint.x * overviewVerSpacing + overviewVerSpacing;
            float left = movePoint.y * rectWidth + movePoint.y * overviewSpacing + overviewSpacing;
            canvas.drawRect(left, top, left + rectWidth, top + rectHeight, overviewPaint);
        }

        return overviewBitmap;
    }

    /**
     * 自动回弹
     * 整个大小不超过控件大小的时候:
     * 往左边滑动,自动回弹到行号右边
     * 往右边滑动,自动回弹到右边
     * 往上,下滑动,自动回弹到顶部
     * <p>
     * 整个大小超过控件大小的时候:
     * 往左侧滑动,回弹到最右边,往右侧滑回弹到最左边
     * 往上滑动,回弹到底部,往下滑动回弹到顶部
     */
    private void autoScroll() {
        float currentSeatBitmapWidth = seatBitmapWidth * getMatrixScaleX();
        float currentSeatBitmapHeight = seatBitmapHeight * getMatrixScaleY();
        float moveYLength = 0;
        float moveXLength = 0;


        float maxTransX = spacing * getMatrixScaleX();
        float minTransX = -(currentSeatBitmapWidth + spacing * getMatrixScaleX() - getWidth());
        //处理左右滑动的情况
        if (currentSeatBitmapWidth < getWidth()) {
            //图的宽度小于view的宽度
            if (getTranslateX() < 0 || getMatrixScaleX() < spacing) {
                //计算要移动的距离
                if (getTranslateX() < 0) {
                    moveXLength = (-getTranslateX()) + maxTransX;
                } else {
                    moveXLength = maxTransX - getTranslateX();
                }
            }
        } else {
            //图的宽度大于等于view的宽度
            if (getTranslateX() < 0 && getTranslateX() + currentSeatBitmapWidth > getWidth()) {

            } else {

                if (getTranslateX() < minTransX) {
                    //往左侧滑动
                    moveXLength = minTransX - getTranslateX();
                } else if (getTranslateX() > maxTransX) {
                    //往右侧滑动
                    moveXLength = maxTransX - getTranslateX();
                }

            }

        }

        float maxTransY = screenHeight * getMatrixScaleY() + verSpacing * getMatrixScaleY() + headHeight + borderHeight;
        float minTransY = -(currentSeatBitmapHeight + verSpacing * getMatrixScaleY() - getHeight());

        //处理上下滑动
        if (currentSeatBitmapHeight + headHeight < getHeight()) {

            if (getTranslateY() < maxTransY) {
                moveYLength = maxTransY - getTranslateY();
            } else {
                moveYLength = -(getTranslateY() - (maxTransY));
            }

        } else {

            if (getTranslateY() < 0 && getTranslateY() + currentSeatBitmapHeight > getHeight()) {

            } else {
                //往上滑动
                if (getTranslateY() < minTransY) {
                    moveYLength = minTransY - getTranslateY();
                } else if (getTranslateY() > maxTransY) {
                    moveYLength = maxTransY - getTranslateY();
                }
            }
        }

        Point start = new Point();
        start.x = (int) getTranslateX();
        start.y = (int) getTranslateY();

        Point end = new Point();
        end.x = (int) (start.x + moveXLength);
        end.y = (int) (start.y + moveYLength);

        moveAnimate(start, end);

    }

    private void autoScale() {

        if (getMatrixScaleX() > 2.2) {
            zoomAnimate(getMatrixScaleX(), 2.0f);
        } else if (getMatrixScaleX() < 0.98) {
            zoomAnimate(getMatrixScaleX(), 1.0f);
        }
    }

    Handler handler = new Handler();

    ArrayList<Integer> selects = new ArrayList<>();

    public ArrayList<String> getSelectedSeat() {
        ArrayList<String> results = new ArrayList<>();
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.column; j++) {
                if (isHave(getID(i, j)) >= 0) {
                    results.add(i + "," + j);
                }
            }
        }
        return results;
    }

    private int isHave(Integer seat) {
        return Collections.binarySearch(selects, seat);
    }

    private void remove(int index) {
        selects.remove(index);
    }

    public void cleanSelects() {
        selects.clear();
    }

    float[] m = new float[9];

    private float getTranslateX() {
        matrix.getValues(m);
        return m[Matrix.MTRANS_X];
    }

    private float getTranslateY() {
        matrix.getValues(m);
        return m[Matrix.MTRANS_Y];
    }

    private float getMatrixScaleY() {
        matrix.getValues(m);
        return m[Matrix.MSCALE_Y];
    }

    private float getMatrixScaleX() {
        matrix.getValues(m);
        return m[Matrix.MSCALE_X];
    }

    private float dip2Px(float value) {
        return getResources().getDisplayMetrics().density * value;
    }

    private float getBaseLine(Paint p, float top, float bottom) {
        Paint.FontMetrics fontMetrics = p.getFontMetrics();
        int baseline = (int) ((bottom + top - fontMetrics.bottom - fontMetrics.top) / 2);
        return baseline;
    }

    private void moveAnimate(Point start, Point end) {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new MoveEvaluator(), start, end);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        MoveAnimation moveAnimation = new MoveAnimation();
        valueAnimator.addUpdateListener(moveAnimation);
        valueAnimator.setDuration(400);
        valueAnimator.start();
    }

    private void zoomAnimate(float cur, float tar) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(cur, tar);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        ZoomAnimation zoomAnim = new ZoomAnimation();
        valueAnimator.addUpdateListener(zoomAnim);
        valueAnimator.addListener(zoomAnim);
        valueAnimator.setDuration(400);
        valueAnimator.start();
    }

    private float zoom;

    private void zoom(float zoom) {
        float z = zoom / getMatrixScaleX();
        matrix.postScale(z, z, scaleX, scaleY);
        invalidate();
    }

    private void move(Point p) {
        float x = p.x - getTranslateX();
        float y = p.y - getTranslateY();
        matrix.postTranslate(x, y);
        invalidate();

        Log.d("move", "transX = " + getTranslateX() + ", transY = " + getTranslateY());
    }

    class MoveAnimation implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            Point p = (Point) animation.getAnimatedValue();

            move(p);
        }
    }

    class MoveEvaluator implements TypeEvaluator {

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            Point startPoint = (Point) startValue;
            Point endPoint = (Point) endValue;
            int x = (int) (startPoint.x + fraction * (endPoint.x - startPoint.x));
            int y = (int) (startPoint.y + fraction * (endPoint.y - startPoint.y));
            return new Point(x, y);
        }
    }

    class ZoomAnimation implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            zoom = (Float) animation.getAnimatedValue();
            zoom(zoom);

            if (DBG) {
                Log.d("zoomTest", "zoom:" + zoom);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            autoScroll();
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

    }

    public void setData(int row, int column) {
        this.row = row;
        this.column = column;
        init();
        invalidate();
    }

    ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            isScaling = true;
            float scaleFactor = detector.getScaleFactor();
            if (getMatrixScaleY() * scaleFactor > 3) {
                scaleFactor = 3 / getMatrixScaleY();
            }
            if (firstScale) {
                scaleX = detector.getCurrentSpanX();
                scaleY = detector.getCurrentSpanY();
                firstScale = false;
            }

            if (getMatrixScaleY() * scaleFactor < 0.5) {
                scaleFactor = 0.5f / getMatrixScaleY();
            }
            matrix.postScale(scaleFactor, scaleFactor, scaleX, scaleY);
            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            isScaling = false;
            firstScale = true;
        }
    });


    GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            isOnClick = true;
            int x = (int) e.getX();
            int y = (int) e.getY();
            float curScaleY = getMatrixScaleY();
            Carport carport = getCarportFromMotionEvent(e);

            //bugly爆出一个bug，这里获取到的行列可能小于0.
            if (carport != null && carport.getRow()>=0 && carport.getColumn()>=0) {
                int i = carport.getRow();
                int j = carport.getColumn();
                int id = getID(i, j);
                int index = isHave(id);
                if (index >= 0) {
                    remove(index);
                    if (seatChecker != null) {
                        seatChecker.unCheck(i, j);
                    }
                    invalidate();
                    return super.onSingleTapConfirmed(e);
                }
                if (seatChecker != null && seatChecker.isValid(i, j) && !seatChecker.isUsed(i, j) && !seatChecker.isOccupy(i, j)) {
                    //点击空车位
                    cleanSelects();
                    addChooseSeat(i, j);
                    if (seatChecker != null) {
                        seatChecker.checked(i, j);
                    }

                    isNeedDrawSeatBitmap = true;
                    isDrawOverviewBitmap = true;

                    if (curScaleY < 1.7) {
                        scaleX = x;
                        scaleY = y;
                        zoomAnimate(curScaleY, 1.9f);
                    }


                } else if (seatChecker.isUsed(i, j)) {
                    //点击的是已经停放车辆的车位
                    if (!isMoveMode) {
                        cleanSelects();
                        addChooseSeat(i, j);
                    }
                    if (seatChecker != null) {
                        seatChecker.onCarClick(i, j);
                    }
                } else if (seatChecker.isOccupy(i, j)) {
                    if (!isMoveMode) {
                        cleanSelects();
                        addChooseSeat(i, j);
                    }
                    seatChecker.occupy(i, j);
                }
                invalidate();
            }

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            Carport carport = getCarportFromMotionEvent(e);
            if (carport != null && getSeatType(carport.getRow(), carport.getColumn()) == USED) {
                Toast.makeText(getContext(), "请选择空位进行车辆移位", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float curScaleY = getMatrixScaleY();
            scaleX = e.getX();
            scaleY = e.getY();
            if (curScaleY < 1.7) {
                zoomAnimate(curScaleY, 1.9f);
            } else {
                zoomAnimate(curScaleY, 1.0f);
            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e("fling", "e1 " + e1.getX() + " " + e1.getY() + ", e2 " + e2.getX() + " " + e2.getY() + " " + velocityX + " " + velocityY);
            mIsFling = true;
            float currentSeatBitmapWidth = seatBitmapWidth * getMatrixScaleX();
            float currentSeatBitmapHeight = seatBitmapHeight * getMatrixScaleY();
            float dx;
            float dy;


            float maxTransX =spacing * getMatrixScaleX();

            float minTransX = -(currentSeatBitmapWidth + spacing * getMatrixScaleX() - getWidth());
            float maxTransY = screenHeight * getMatrixScaleY() + verSpacing * getMatrixScaleY() + headHeight + borderHeight;
            float minTransY = -(currentSeatBitmapHeight + verSpacing * getMatrixScaleY() - getHeight());
            if (currentSeatBitmapWidth < getWidth()) {
                minTransX = maxTransX;
            }

            if (currentSeatBitmapHeight + headHeight < getHeight()) {
                minTransY = maxTransY;
            }

            Log.d("fling", "minTransX = " + minTransX + ", maxTransX = " + maxTransX + ", minTransY = " + minTransY + ", maxTransY = " + maxTransY);

            Point start = new Point();
            start.x = (int) getTranslateX();
            start.y = (int) getTranslateY();

            float tempX = velocityX / 6 + start.x;
            float tempY = velocityY / 6 + start.y;

            if (tempX > maxTransX) {
                dx = maxTransX;
            } else if (tempX < minTransX) {
                dx = minTransX;
            } else if (maxTransX == minTransX) {
                dx = minTransX;
            } else {
                dx = tempX;
            }

            if (tempY > maxTransY) {
                dy = maxTransY;
            } else if (tempY < minTransY) {
                dy = minTransY;
            } else if (minTransY == maxTransY) {
                dy = minTransY;
            } else {
                dy = tempY;
            }

            Point end = new Point();
            end.x = (int) dx;
            end.y = (int) dy;

            moveAnimate(start, end);

            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    });


    /**
     * 根据手指触碰位置得到车位
     *
     * @param motionEvent
     * @return
     */
    private Carport getCarportFromMotionEvent(MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        float curScaleX = getMatrixScaleX();
        float curScaleY = getMatrixScaleY();
        float translateX = getTranslateX();
        float translateY = getTranslateY();
        float tempX = (((x - translateX) / curScaleX) % (seatWidth + spacing));
        float tempY = (((y - translateY) / curScaleY) % (seatHeight + verSpacing));
        int column = (int) (((x - translateX) / curScaleX) / (seatWidth + spacing));
        int row = (int) (((y - translateY) / curScaleY) / (seatHeight + verSpacing));
        if (tempX > seatWidth || tempY > seatHeight) {
            return null;
        }
        Carport carport = new Carport(row, column);
        return carport;
    }

    public void addChooseSeat(int row, int column) {
        int id = getID(row, column);
        for (int i = 0; i < selects.size(); i++) {
            int item = selects.get(i);
            if (id < item) {
                selects.add(i, id);
                return;
            }
        }

        selects.add(id);
    }

    //清除搜索内容的时候，如果有选中不是搜索结果的车辆，下方车辆信息不能gong
    public boolean hasSelect() {
        return selects.size() > 0;
    }

    public void clearSelect(){
        selects.clear();
    }

    public interface CarportChecker {
        /**
         * 车位是否可用
         *
         * @param row
         * @param column
         * @return
         */
        boolean isValid(int row, int column);

        /**
         * 车位是否被占用
         *
         * @param row
         * @param column
         * @return
         */
        boolean isOccupy(int row, int column);

        /**
         * 车位是否已停车
         *
         * @param row
         * @param column
         * @return
         */
        boolean isUsed(int row, int column);

        void checked(int row, int column);

        void unCheck(int row, int column);

        void onCarClick(int row, int column);

        void occupy(int row, int colum);

        //获取选中的区
        String checkedSeatArea(int row, int column);

        /**
         * 获取选中后座位上显示的文字
         *
         * @param row
         * @param column
         * @return 返回2个元素的数组, 第一个元素是第一行的文字, 第二个元素是第二行文字, 如果只返回一个元素则会绘制到座位图的中间位置
         */
        String[] checkedSeatTxt(int row, int column);


    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setMaxSelected(int maxSelected) {
        this.maxSelected = maxSelected;
    }

    public void setSeatChecker(CarportChecker seatChecker) {
        this.seatChecker = seatChecker;
        invalidate();
    }

    private int getRowNumber(int row) {
        int result = row;
        if (seatChecker == null) {
            return -1;
        }

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (seatChecker.isValid(i, j)) {
                    break;
                }

                if (j == column - 1) {
                    if (i == row) {
                        return -1;
                    }
                    result--;
                }
            }
        }
        return result;
    }

    private int getColumnNumber(int row, int column) {
        int result = column;
        if (seatChecker == null) {
            return -1;
        }

        for (int i = row; i <= row; i++) {
            for (int j = 0; j < column; j++) {

                if (!seatChecker.isValid(i, j)) {
                    if (j == column) {
                        return -1;
                    }
                    result--;
                }
            }
        }
        return result;
    }
}
