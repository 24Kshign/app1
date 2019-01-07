package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created with Android Studio
 * Auth gujian
 * Time is 2018/4/3
 * Email is gujian@maihaoche.com
 */

public class CheckCarResponse implements Serializable{

    @SerializedName("checkPhotos")
    @Expose
    public List<PhotoConfig> checkPhotos;//照片配置

    @SerializedName("upVideoStatus")
    @Expose
    public Integer upVideoStatus;//是否需要上传绕车视频，1-订单宝商品，2-已有同款车辆视频3-非1，2类型 开启上传视频模块

    public static class PhotoConfig{
        @SerializedName("name")
        @Expose
        public String name;//名称

        @SerializedName("no")
        @Expose
        public int no;//

        @SerializedName("imgUrl")
        @Expose
        public String imgUrl;//名称

        public PhotoConfig(int no, String imgUrl) {
            this.no = no;
            this.imgUrl = imgUrl;
        }
    }
}
