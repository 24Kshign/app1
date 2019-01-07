package com.maihaoche.volvo.ui.common.daomain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gujian
 * Time is 2017/9/20
 * Email is gujian@maihaoche.com
 */

public class SearchResultInfo implements Serializable {


    @SerializedName("graphicX")
    @Expose
    public Integer row;//行

    @SerializedName("graphicY")
    @Expose
    public Integer col;//列

    public SearchResultInfo(int row,int col){

        this.row = row;
        this.col = col;
    }

}
