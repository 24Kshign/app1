package com.maihaoche.volvo.ui.photo;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/4/3
 * Email is gujian@maihaoche.com
 */

public class CheckCarPhotoItem extends ChooseImageItem {

    public int imgId;
    public String imgName;

    public CheckCarPhotoItem(String mOriPath, boolean isJustLook, int imgId, String imgName) {
        super(mOriPath, isJustLook);
        this.imgId = imgId;
        this.imgName = imgName;
        this.isShowDefault = false;
    }

    public CheckCarPhotoItem(int imgId, String imgName) {
        this.imgId = imgId;
        this.imgName = imgName;
        isShowDefault = true;
    }
}
