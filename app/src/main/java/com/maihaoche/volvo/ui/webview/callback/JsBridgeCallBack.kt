package com.maihaoche.volvo.ui.webview.callback

import com.maihaoche.base.jsbridge.CallBackFunction

/**
 * Created by manji
 * Date：2018/8/10 下午2:55
 * Desc：
 */
interface JsBridgeCallBack {

    //设置WebView标题内容
    fun setTitleContent(title: String)

    //设置标题右侧内容
    fun setRightContent(content: String)

    //扫码
    fun scanCode(function: CallBackFunction?)

    //拍照 或 相册 或 拍照/相册
    fun takePhotoAndGallery(code: Int, maxPhoto: Int, isCompress: Boolean, function: CallBackFunction?)

    //浏览图片
    fun scanPhoto(imageUrl: ArrayList<String>, index: Int)

    fun close();

}