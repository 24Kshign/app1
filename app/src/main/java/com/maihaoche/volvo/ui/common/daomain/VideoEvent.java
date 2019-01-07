package com.maihaoche.volvo.ui.common.daomain;

import android.graphics.Bitmap;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2017/12/6
 * Email is gujian@maihaoche.com
 */

public class VideoEvent {

    public String videoPath;
    public Bitmap bitmap;

    public VideoEvent(String videoPath, Bitmap bitmap) {
        this.videoPath = videoPath;
        this.bitmap = bitmap;
    }
}
