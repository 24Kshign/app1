package com.maihaoche.volvo.ui.webview.jshandler

import com.maihaoche.base.jsbridge.CallBackFunction
import com.maihaoche.base.jsbridge.JsHandler
import com.maihaoche.volvo.ui.webview.callback.JsBridgeCallBack

/**
 * Created by manji
 * Date：2018/8/10 下午3:52
 * Desc：
 */
class ScanCodeJsHandler : JsHandler {

    private var mCallBack: JsBridgeCallBack

    constructor(callback: JsBridgeCallBack) {
        mCallBack = callback
    }

    override fun OnHandler(handlerName: String?, responseData: String?, function: CallBackFunction?) {
        mCallBack.scanCode(function)
    }

}