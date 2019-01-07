package com.maihaoche.volvo.ui.photo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sanji on 16/3/2.
 */
public class PictureInfo implements Serializable {
    @SerializedName("pictureType")
    @Expose
    public Integer pictureType;
    //    1采购合同照片 2订单合同照片3支付定金凭证
//    4代付证明 5保证金凭证
//    @SerializedName("pictureUrls")
//    @Expose
//    public String pictureUrls;
    @SerializedName("pictureStatus")
    @Expose
    public Integer pictureStatus;
    @SerializedName("checkTime")
    @Expose
    public String checkTime;
    @SerializedName("reason")
    @Expose
    public String reason;
    @SerializedName("examplePicture")
    @Expose
    public String examplePicture;
    @SerializedName("exampleDesc")
    @Expose
    public String exampleText;

    @SerializedName("createTime")
    @Expose
    public String uploadTime;

    @SerializedName("imgUrls")
    @Expose
    public ArrayList<String> imgUrls;
}
