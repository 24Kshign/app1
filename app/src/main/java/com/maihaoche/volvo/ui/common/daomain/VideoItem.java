package com.maihaoche.volvo.ui.common.daomain;

import android.graphics.Bitmap;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2017/12/6
 * Email is gujian@maihaoche.com
 */

public class VideoItem {

    public String mOriPath = "";

    public String mServerUrl = "";

    public String compressUrl = "";

    public Bitmap bitmap;

    public boolean isUploading = false;

    public boolean isJustLook = false;

    public void clear() {
        mOriPath = "";
        mServerUrl = "";
        isUploading = false;
        isJustLook = false;
    }

    public boolean isFromServer() {
        return mOriPath.startsWith("http://") || mOriPath.startsWith("https://");
    }
}
