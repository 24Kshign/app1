package com.maihaoche.volvo.ui.photo;

import android.text.TextUtils;

import com.maihaoche.commonbiz.service.utils.StringUtil;

import java.io.Serializable;

/**
 * Created by wangshengru on 16/3/8.
 * 上传图片类
 */
public class ChooseImageItem implements Serializable{

    public String mOriPath = "";

    public String mServerUrl = "";

    public Boolean isShowDefault;

    public String remark;

    public boolean mIsUploading = false;

    public boolean mIsJustLook = false;

    public boolean isAdd = false;

    //是否用crop模式
    public boolean useCrop = true;

    public void clear() {
        mOriPath = "";
        mServerUrl = "";
        mIsUploading = false;
        mIsJustLook = false;
    }

    public boolean isFromServer() {
        return mOriPath.startsWith("http://") || mOriPath.startsWith("https://");
    }
    public ChooseImageItem(String mOriPath, boolean isJustLook){
        if(!TextUtils.isEmpty(mOriPath)){
            this.mOriPath = mOriPath;
        }
        this.mIsJustLook = isJustLook;
    }
    public ChooseImageItem(){
    }

    public boolean haveServerUrl(){
        if(StringUtil.isEmpty(mServerUrl)){
            return false;
        }
        return mServerUrl.startsWith("http://") || mServerUrl.startsWith("https://");
    }


}
