package com.maihaoche.commonbiz.service.image;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by dashu on 2016/9/10.
 * 正方形,圆角bitmap,centerCrop
 */
class GlideRoundTransform extends BitmapTransformation {
    private float radius = 0f;
    private int width = -1;
    private int height = -1;

    public GlideRoundTransform(Context context) {
        this(context, 2);
    }

    public GlideRoundTransform(Context context, int dp) {
        super(context);
        this.radius = Resources.getSystem().getDisplayMetrics().density * dp + 0.5f;
    }

    public GlideRoundTransform(Context context, int dp, int width, int height) {
        super(context);
        this.radius = Resources.getSystem().getDisplayMetrics().density * dp + 0.5f;
        this.width = width;
        this.height = height;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {

        return roundCrop(pool, toTransform);
    }

    private Bitmap roundCrop(BitmapPool pool, Bitmap toTransform) {
        if (toTransform == null) return null;
        if (width < 0 || height < 0) {
            width = height = Math.min(toTransform.getWidth(), toTransform.getHeight());
        }

        Bitmap squared = Bitmap.createScaledBitmap(toTransform, width, height, true);

        Bitmap result = pool.get(width, height, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, width, height);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        return result;
    }

    @Override
    public String getId() {
        return getClass().getName() + Math.round(radius);
    }
}
