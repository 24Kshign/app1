package com.maihaoche.volvo.ui.webview.jshandler

import com.maihaoche.base.jsbridge.CallBackFunction
import com.maihaoche.base.jsbridge.JsHandler
import com.maihaoche.volvo.ui.webview.callback.JsBridgeCallBack
import com.maihaoche.volvo.util.JsBridgeResponseUtil
import org.json.JSONObject

/**
 * Created by manji
 * Date：2018/8/10 下午3:52
 * Desc：
 */
class SetTitleJsHandler : JsHandler {

    private var mCallBack: JsBridgeCallBack

    constructor(callback: JsBridgeCallBack) {
        mCallBack = callback
    }

    override fun OnHandler(handlerName: String?, responseData: String?, function: CallBackFunction?) {
        responseData?.let {
            try {
                val json = JSONObject(it)
                mCallBack.setTitleContent(json.getString("title"))
                function?.onCallBack(JsBridgeResponseUtil.getJsBridgeResponse(true, "", ""))
            } catch (e: Exception) {
                function?.onCallBack(JsBridgeResponseUtil.getJsBridgeResponse(false, e.message.toString(), ""))
                e.printStackTrace()
            }
        }
    }

}