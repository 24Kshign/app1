package com.maihaoche.commonbiz.service.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.maihaoche.commonbiz.service.utils.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sanji on 14/11/27.
 * 图片处理类
 */
public class ImageUtil {

    /**
     * 七牛图片处理，支持处理的原图片格式有psd、jpeg、png、gif、webp、tiff、bmp。
     */
    //图片基本处理
    private static final String IMAGE2 = "?imageView2/";
    /**
     * http://developer.qiniu.com/code/v6/api/kodo-api/image/imageview2.html
     */

    //图片高级处理
    private static final String IMOGR2 = "?imageMogr2/";
    //模式
    public static final String CROP = "1";

    public static final String WIDTH = "/w/";
    public static final String HEIGHT = "/h/";
    public static final String QUALITY = "/q/";

    public static final String SPLITE_REGIX = ";";

    public static final String WATER_WITH_IMG = "?watermark/1/image/";
    public static final String WATER_WITH_TEXT = "?watermark/2/text/";

    private static final String JPG = ".jpg";
    private static final String PNG = ".png";

    /**
     * 获取格式化后的url地址 高度自适应
     *
     * @param url   图片url
     * @param width 宽度
     * @return 处理过的url
     */
    public static String getMixedSizeUrl(String url, int width) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(url);
        sb.append(IMAGE2);
        sb.append(CROP);
        sb.append(WIDTH).append(width).append(QUALITY).append(100);
        return sb.toString();
    }

    /**
     * 获取定宽定高的图片url
     *
     * @param url    图片url
     * @param width  宽度
     * @param height 高度
     * @return 处理过的url
     */
    public static String getMixedSizeUrl(String url, int width, int height) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(url);
        sb.append(IMAGE2);
        sb.append(CROP);
        sb.append(WIDTH).append(width).append(HEIGHT).append(height).append(QUALITY).append(100);
        return sb.toString();
    }

    public static String getMp4FromMovUrl(String url) {
        if (url.toUpperCase().endsWith("MOV")) {
            return url.substring(0, url.length() - 3) + "mp4";
        } else {
            return url;
        }
    }

    /**
     * 获取缩放30%的缩略图
     */
    public static String getThumbUrl(String url) {
        return url + "?imageMogr2/thumbnail/!30p";
    }

    /**
     * 获取大图用的渐进效果
     *
     * @param url 图片url，使用jpg
     * @return 处理过的url
     */
    public static String getBigUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        StringBuilder sb = new StringBuilder(url);
        sb.append(IMAGE2);
        sb.append(0);
        sb.append("/interlace/1").append(QUALITY).append(80);
        return sb.toString();
    }


    /**
     * 由于camera返回的图片data是翻转90度的，导致直接存储正常图片需要做图片矩阵变化，再转存，非常消耗资源
     * 这里的方案是设置翻转角度，然后直接存储翻转前的数据，最后获取出来根据@getImageFileDegree方法获得角度
     * 然后把bitmap进行矩阵变化
     * <p>
     * 设置存储图片的翻转角度为90度
     */
    public static void setImageFileDegree(String filePath, int orientation) {

        int type = ExifInterface.ORIENTATION_ROTATE_90;
        switch (orientation) {
            case 0:
                type = ExifInterface.ORIENTATION_ROTATE_90;
                break;
            case 90:
                type = ExifInterface.ORIENTATION_ROTATE_180;
                break;
            case 180:
                type = ExifInterface.ORIENTATION_ROTATE_270;
                break;
            case -90:
                return;
        }

        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(type));
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取存储图片的翻转角度
     */
    public static int getImageFileDegree(String filePath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 获取翻转图片
     *
     * @param degree    翻转角度
     * @param resBitmap 原图，会被回收
     * @return 处理后的图片
     */
    public static Bitmap getFixedDegreeBitmapByDegree(int degree, Bitmap resBitmap) {
        return getFixedDegreeBitmapByDegree(degree, resBitmap, Bitmap.Config.ARGB_8888);
    }


    /**
     * 获取根据等比显示图片的高度
     *
     * @param path      图片路径
     * @param viewWidth 宽度
     * @return 高度
     */
    public static int getBitmapFixedHeight(String path, int viewWidth) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream fis = new FileInputStream(new File(path));
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            int degree = getImageFileDegree(path);
            int width, height;
            if (degree % 180 == 0) {
                width = o.outWidth;
                height = o.outHeight;
            } else {
                width = o.outHeight;
                height = o.outWidth;
            }
            float scale = ((float) viewWidth) / width;
            return (int) (height * scale);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * * 将bitmap根据设置角度翻转
     * 注意！！;传入的原图会被回收！请勿使用原图
     *
     * @param degree    翻转角度
     * @param resBitmap 原图，会被回收
     * @param config    设置
     * @return 处理后的图片
     */
    public static Bitmap getFixedDegreeBitmapByDegree(int degree, Bitmap resBitmap, Bitmap.Config config) {
        //生成bitmap的宽高
        int width, height;
        if (degree % 180 == 0) { // 画面が横向きなら
            width = resBitmap.getWidth();
            height = resBitmap.getHeight();
        } else {
            width = resBitmap.getHeight();
            height = resBitmap.getWidth();
        }
        if (degree % 360 == 0) {
            return resBitmap;
        } else {
            Bitmap bitmap = Bitmap.createBitmap(width, height, config);
            Canvas canvas = new Canvas(bitmap);
            canvas.rotate(degree, width / 2, height / 2);

            int offset = (degree % 180 == 0) ? 0 : (degree == 90)
                    ? (width - height) / 2
                    : (height - width) / 2;

            canvas.translate(offset, -offset);
            canvas.drawBitmap(resBitmap, 0, 0, null);
            resBitmap.recycle();
            return bitmap;
        }
    }

    /**
     * 获得一张无损的bitmap
     *
     * @param filePath 文件路径
     * @return 图片
     */
    public static Bitmap decodeFile(String filePath) {
        FileInputStream fis = null;
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = false;
            fis = new FileInputStream(new File(filePath));
            return BitmapFactory.decodeStream(fis, null, o);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据宽度获得等比例的图片
     *
     * @param f     文件
     * @param width 宽度
     * @return 图片
     */
    public static Bitmap decodeFile(File f, int width) {
        Bitmap b = null;
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            o.inJustDecodeBounds = false;

//            int scale = 1;
//            if (o.outHeight > 640 || o.outWidth > 480) {
//                scale = Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
//            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = o.outWidth / width;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (Exception e) {
        }
        return b;
    }


    public static File getOutputMediaFile() {
        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!picDir.exists()) {
            picDir.mkdir();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(picDir.getPath() + File.separator + "IMAGE_" + timeStamp + ".jpg");
    }

    public static File getOutputVideoFile() {
        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!picDir.exists()) {
            picDir.mkdir();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(picDir.getPath() + File.separator + "IMAGE_" + timeStamp + ".mp4");
    }


    public static Bitmap bytes2Bitmap(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }


    public static Drawable getViewSideDrawable(Context context, int resId) {
        Drawable drawable = ActivityCompat.getDrawable(context, resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        return drawable;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 85, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 检查图片是否超过屏幕,超过进行缩放
     */
    public static Bitmap resizeByWindow(Context context, Bitmap bitmap) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int windowHeight = dm.heightPixels;
        int windowWidth = dm.widthPixels;
        return resizeByHW(bitmap, windowHeight, windowWidth);
    }


    /**
     * 根据给定的宽高进行缩放
     *
     * @param bitmap 原图
     * @param height 高度
     * @param width  宽度
     * @return 缩放后的图片
     */
    public static Bitmap resizeByHW(Bitmap bitmap, int height, int width) {
        byte[] buffer = readBitmap(bitmap);

        // 获取原图宽度
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        options.inInputShareable = true;
        Bitmap bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length,
                options);

        // 缩放
        options.inJustDecodeBounds = false;

        // 计算缩放比例
        double shrink = options.outHeight * options.outWidth / width / height;
        options.inSampleSize = (int) Math.sqrt(shrink);
        if (bm != null && !bm.isRecycled()) {
            bm.recycle();
        }
        bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);

        if (bm == null) {
            return null;
        }
        return bm;
    }


    /**
     * 将图片转换成字节流
     *
     * @param bmp 原图
     * @return 字节数组
     */
    private static byte[] readBitmap(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    /**
     * 把图片的背景颜色设为白色
     *
     * @param bitmap 原图片
     * @return Bitmap 转换后的图片
     */
    public static Bitmap changeBitmapColor(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] colorArray = new int[w * h];
        int n = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int color = getMixtureWhite(bitmap.getPixel(j, i));
                colorArray[n++] = color;
            }
        }
        return Bitmap.createBitmap(colorArray, w, h, Bitmap.Config.ARGB_8888);
    }

    /**
     * 获取和白色混合颜色
     *
     * @return 混合色的色值
     */
    private static int getMixtureWhite(int color) {
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.rgb(
                getSingleMixtureWhite(red, alpha),
                getSingleMixtureWhite(green, alpha),
                getSingleMixtureWhite(blue, alpha));
    }

    /**
     * 获取单色的混合值
     *
     * @param color 颜色
     * @param alpha 透明度
     * @return 处理后带透明度的颜色
     */
    private static int getSingleMixtureWhite(int color, int alpha) {
        int newColor = color * alpha / 255 + 255 - alpha;
        return newColor > 255 ? 255 : newColor;
    }

    /**
     * 基于质量的压缩算法， 此方法未 解决压缩后图像失真问题
     * 可先调用比例压缩适当压缩图片后，再调用此方法可解决上述问题
     *
     * @param maxBytes 压缩后的图像最大大小 单位为byte
     * @return 压缩后的图像
     */
    public static Bitmap compressBitmap(Bitmap bitmap, int maxBytes) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
            int options = 90;
            while (baos.toByteArray().length > maxBytes) {
                baos.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                options -= 10;
            }
            byte[] bts = baos.toByteArray();
            Bitmap bmp = BitmapFactory.decodeByteArray(bts, 0, bts.length);
            baos.close();
            return bmp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getCompressBytes(Bitmap bitmap, int maxBytes) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
            int options = 90;
            while (baos.toByteArray().length > maxBytes && options >= 0) {
                baos.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                options -= 10;
            }
            byte[] bts = baos.toByteArray();
            baos.close();
            return bts;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取下载图片URL的的文件保存路径
     */
    public static String getImageSavePath(Context context, String url) {
        File file = context.getCacheDir();
        if (file != null && file.exists()) {
        } else {
            file = context.getFilesDir();
        }
        return file.getAbsolutePath() + File.separator + getImageFileNameFromURL(url);
    }

    /**
     * 获取下载图片URL的文件名
     */
    private static String getImageFileNameFromURL(String url) {
        if (url.endsWith(PNG)) {
            return StringUtil.md5(url) + PNG;
        } else {
            return StringUtil.md5(url) + JPG;
        }
    }
}
