package com.maihaoche.volvo.ui.photo;

/**
 * Created by jiaoyu on 16/12/4.
 */

public class ImgInfo {
    private String imgUrl;

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    private String name="";
    public String remark="";

    public ImgInfo(String imgUrl, String name, String remark) {
        this.imgUrl = imgUrl;
        this.name = name;
        this.remark = remark;
    }
    public ImgInfo(String imgUrl, String name) {
        this.imgUrl = imgUrl;
        this.name = name;
    }

    public ImgInfo(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
