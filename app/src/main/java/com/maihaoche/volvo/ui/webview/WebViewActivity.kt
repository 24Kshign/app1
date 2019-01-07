package com.maihaoche.volvo.ui.webview

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.maihaoche.base.jsbridge.BridgeWebView
import com.maihaoche.base.jsbridge.BridgeWebViewClient
import com.maihaoche.base.jsbridge.CallBackFunction
import com.maihaoche.base.jsbridge.JsHandler
import com.maihaoche.commonbiz.module.ui.BaseFragmentActivity
import com.maihaoche.commonbiz.service.permision.PermissionHandler
import com.maihaoche.commonbiz.service.utils.DeviceUtil
import com.maihaoche.commonbiz.service.utils.FileUtil
import com.maihaoche.commonbiz.service.utils.HintUtil
import com.maihaoche.commonbiz.service.utils.StringUtil
import com.maihaoche.scanlib.BarCodeHelper
import com.maihaoche.volvo.R
import com.maihaoche.volvo.ui.avchat.log.LogUtil
import com.maihaoche.volvo.ui.car.activity.QrScanCodeAty
import com.maihaoche.volvo.ui.photo.ImageViewerActivity
import com.maihaoche.volvo.ui.photo.PhotoWallActivity
import com.maihaoche.volvo.ui.webview.callback.JsBridgeCallBack
import com.maihaoche.volvo.ui.webview.jshandler.*
import com.maihaoche.volvo.util.CompressUtil
import com.maihaoche.volvo.util.JsBridgeResponseUtil
import com.maihaoche.volvo.util.QiNiuUtil1
import com.maihaoche.volvo.view.ChoosePicImage.QINIU_IMG_HEADER
import com.qiniu.android.storage.UploadManager
import com.qiniu.android.storage.UploadOptions
import com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_webview.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by manji
 * Date：2018/8/10 上午10:36
 * Desc：
 */
class WebViewActivity : BaseFragmentActivity(), JsBridgeCallBack {

    companion object {
        private const val HANDLER_PROGRESS = 0
        private const val HANDLER_FINISH = 1
        private const val WEB_URL = "web_url"
        private const val REQUEST_PIC_CODE = 100
        private const val REQUEST_TAKE_PIC = 0

        private val APP_NAME_UA = "maihaoche_wms" + JsBridgeResponseUtil.getAppVersion()

        @JvmStatic
        fun start(context: Context, url: String?) {
            val bundle = Bundle()
            bundle.putString(WEB_URL, url)
            val intent = Intent(context, WebViewActivity::class.java);
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun bindLayoutRes(): Int {
        return R.layout.activity_webview
    }

    private var mUrl: String? = ""
    private var mRecentUrl: String? = ""
    private var mRecentTitle: String? = ""
    internal var mIsFailed = false
    private var mIsUploading = false
    private var isCancel = false
    private var mIsCompress = false
    private var mPhotoCount = 0
    private var mPhotoFlag = 0
    private lateinit var mUploadPhotoList: ArrayList<String>
    private var mUploadPhotoSuccess = true

    private var mFunction: CallBackFunction? = null

    override fun initView() {
        super.initView()
        mUrl = intent.getStringExtra(WEB_URL)
        initWebView()
        initClickListener()
    }


    private fun initClickListener() {
        mIvBack.setOnClickListener {
            if (!isCanGoBack()) {
                finish()
            }
        }
        mTvRight.setOnClickListener { it ->
            if (!TextUtils.isEmpty(mTvRight.text)) {
                mWebView.callHandler(getString(R.string.js_handler_set_right_listener), JsBridgeResponseUtil.getJsBridgeResponse(true, "", "")) {

                }
            }
        }
    }

    private fun initWebView() {

        initHandler()

        mWebView.webChromeClient = object : WebChromeClient() {
            /**
             * 设置响应 js 的 Alert() 函数
             */
            override fun onJsAlert(webView: WebView?, url: String?, message: String?, jsResult: JsResult?): Boolean {
                val b = AlertDialog.Builder(thisActivity())
                b.setMessage(message)
                b.setPositiveButton(android.R.string.ok) { dialog, which -> jsResult!!.confirm() }
                b.setCancelable(false)
                b.create().show()
                return true
            }

            /**
             * 设置响应 js 的 Confirm() 函数
             */
            override fun onJsConfirm(webView: WebView?, url: String?, message: String?, jsResult: JsResult?): Boolean {
                val b = AlertDialog.Builder(thisActivity())
                b.setMessage(message)
                b.setPositiveButton(android.R.string.ok) { dialog, which -> jsResult!!.confirm() }
                b.setNegativeButton(android.R.string.cancel) { dialog, which -> jsResult!!.cancel() }
                b.create().show()
                return true
            }

            override fun onReceivedTitle(webView: WebView?, s: String?) {
                super.onReceivedTitle(webView, s)
                mRecentTitle = webView?.title
                if (StringUtil.isNotEmpty(mRecentTitle)) {
                    mTvTitle.text = mRecentTitle
                }
            }

            /**腾讯X5webView封装的,用于拦截web打开文件的请求。
             * @param webView               webView
             * @param valueCallback         用于回调接口。将本地找到的uir通过该接口传给web
             * @param fileChooserParams     请求的参数。
             * @return true:该请求已被处理。false:该请求未被处理。
             */
            override fun onShowFileChooser(webView: WebView?,
                                           valueCallback: ValueCallback<Array<Uri>>?,
                                           fileChooserParams: WebChromeClient.FileChooserParams?): Boolean {
                //TODO 有需要的时候
                return true
            }
        }

        mWebView.webViewClient = object : BridgeWebViewClient(mWebView as BridgeWebView) {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (TextUtils.isEmpty(url)) {
                    return true
                }
                if (url?.trim { it <= ' ' }?.startsWith("tel:") == true) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url.trim { it <= ' ' }))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    return true
                }
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onPageFinished(webView: WebView?, url: String?) {
                super.onPageFinished(webView, url)
                handler.sendEmptyMessage(HANDLER_FINISH)
                if (!mIsFailed) {
                    mWebView.visibility = View.VISIBLE
                    mTvResultError.visibility = View.GONE
                    mRecentUrl = url
                } else {
                    mRecentUrl = null
                    mRecentTitle = null
                }
            }

            override fun onReceivedError(webView: WebView?, i: Int, s: String?, s1: String?) {
                super.onReceivedError(webView, i, s, s1)
                mIsFailed = true
                mWebView.visibility = View.GONE
                mTvResultError.visibility = View.VISIBLE
            }
        }

        // 各种设置
        if (mWebView.x5WebViewExtension != null) {
            mWebView.x5WebViewExtension.setScrollBarFadingEnabled(false)
            mWebView.x5WebViewExtension.webViewClientExtension = object : ProxyWebViewClientExtension() {
                override fun onMiscCallBack(method: String, bundle: Bundle): Any? {
                    return null
                }
            }
            //视频播放设置
            val data = Bundle()
            data.putBoolean("standardFullScreen", false)//true表示标准全屏，false表示X5全屏；不设置默认false，
            data.putBoolean("supportLiteWnd", true)//false：关闭小窗；true：开启小窗；不设置默认true，
            data.putInt("DefaultVideoScreen", 2)//1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
            mWebView.x5WebViewExtension.invokeMiscMethod("setVideoParams", data)
        }
        mWebView.setUserAgent(APP_NAME_UA)
        mWebView.isVerticalScrollBarEnabled = false
        mWebView.isHorizontalScrollBarEnabled = false

        loadUrl(mUrl)
    }


    private fun initHandler() {
        registerHandler(getString(R.string.js_handler_set_title), SetTitleJsHandler(this))
        registerHandler(getString(R.string.js_handler_set_right), SetRightJsHandler(this))
        registerHandler(getString(R.string.js_handler_scan_code), ScanCodeJsHandler(this))
        registerHandler(getString(R.string.js_handler_take_photo_and_gallery), TakePhotoAndGalleryJsHandler(this))
        registerHandler(getString(R.string.js_handler_scan_photo), ScanPhotoJsHandler(this))
        registerHandler(getString(R.string.js_handler_close_webview), CloseWebViewJsBridge(this))
    }

    private fun registerHandler(handlerName: String, handler: JsHandler?) {
        mWebView.registerHandler(handlerName) { data, function ->
            handler?.OnHandler(handlerName, data, function)
        }
    }

    override fun handleMessage(msg: Message?) {
        if (msg?.what == HANDLER_FINISH || msg?.what == HANDLER_PROGRESS) {
            var progress = mProgressBar.progress
            if (progress < 100) {
                mProgressBar.visibility = View.VISIBLE
                when (msg.what) {
                    HANDLER_PROGRESS -> {
                        if (progress < 60) {
                            progress += 2
                        } else if (progress < 95) {
                            progress += 1
                        }
                        mProgressBar.progress = progress
                        handler.sendEmptyMessageDelayed(HANDLER_PROGRESS, 200)
                    }
                    HANDLER_FINISH -> {
                        progress += if (progress > 70) 100 - progress else 30
                        mProgressBar.progress = progress
                        handler.sendEmptyMessageDelayed(HANDLER_FINISH, 200)
                    }
                }
            } else {
                mProgressBar.visibility = View.GONE
            }
        }
    }

    private fun loadUrl(url: String?) {
        val newUrl = checkHead(url)
        if (isEqualUrl(newUrl, mRecentUrl)) {
            return
        }
        mProgressBar.progress = 1
        mWebView.loadUrl(newUrl)
        handler.sendEmptyMessage(HANDLER_PROGRESS)
    }

    private fun checkHead(url: String?): String {
        return url?.let {
            return if (it.isEmpty()) {
                ""
            } else if (it.startsWith("http://") || it.startsWith("https://") || it.startsWith("file:///")) {
                url
            } else {
                "https://$url"
            }
        } ?: ""
    }

    /**
     * 判断是否属于同一链接
     */
    private fun isEqualUrl(url: String?, compare: String?): Boolean {
        if (url?.isEmpty() == true || compare?.isEmpty() == true) {
            return false
        }
        var newUrl = ""
        url?.let {
            if (it.startsWith("http://") || it.startsWith("https://")) {
                newUrl = it.replace("https://", "")
                newUrl = it.replace("http://", "")
            }
        }
        var newCompare = ""
        compare?.let {
            if (it.startsWith("http://") || it.startsWith("https://")) {
                newCompare = it.replace("https://", "")
                newCompare = it.replace("http://", "")
            }
        }
        return newUrl == newCompare
    }

    ////////////////////////////////////////JSBridge回调////////////////////////////////////////////

    override fun setTitleContent(title: String) {
        //设置标题
        mTvTitle.text = title
    }

    override fun setRightContent(content: String) {
        //设置右侧文字
        mTvRight.text = content
    }

    override fun scanCode(function: CallBackFunction?) {
        //扫码
        mFunction = function
        if (DeviceUtil.isSENTER()) {
            HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_BARCODE_SCAN)
            BarCodeHelper.scan { result ->
                if (!TextUtils.isEmpty(result)) {
                    HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.BARCODE_SCAN_SUCCEED_WITH_VIBRATOR)
                    function?.onCallBack(JsBridgeResponseUtil.getJsBridgeResponse(true, "", result))
                }
            }
        } else {
            PermissionHandler.checkCamera(this) { granted ->
                if (granted) {
                    val intent = Intent(this, QrScanCodeAty::class.java)
                    startActivityForResult(intent, QrScanCodeAty.REQUEST_CODE_RFID)
                }
            }
        }
    }

    private fun isCanGoBack(): Boolean {
        val isCanGoBack = mWebView.canGoBack()
        if (mWebView.canGoBack()) {
            mWebView.goBack()
        }
        return isCanGoBack
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (isCanGoBack()) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun takePhotoAndGallery(code: Int, maxPhoto: Int, isCompress: Boolean, function: CallBackFunction?) {
        mFunction = function
        mIsCompress = isCompress
        mPhotoFlag = 0
        mUploadPhotoList = ArrayList()
        mUploadPhotoSuccess = true
        //拍照/相册/相册 拍照
        when (code) {
            0 -> {
                startActivityForResult(getPhotoIntent(true, 1), REQUEST_TAKE_PIC)
            }
            else -> {
                startActivityForResult(getPhotoIntent(false, maxPhoto), REQUEST_PIC_CODE)
            }
        }
    }

    private fun getPhotoIntent(isOnlyFromCamera: Boolean, maxPhoto: Int): Intent {
        val intent = Intent(this, PhotoWallActivity::class.java)
        intent.putExtra(PhotoWallActivity.EXTRA_MEDIA_TYPE, PhotoWallActivity.TYPE_PICTURE)
        intent.putExtra(PhotoWallActivity.EXTRA_IS_MUTIPLE, true)
        intent.putExtra(PhotoWallActivity.EXTRA_MAX_PHOTO, maxPhoto)
        intent.putExtra(PhotoWallActivity.EXTRA_IS_LIMIT_MAX, true)
        intent.putExtra(PhotoWallActivity.IS_ONLY_FROM_CAMERA, isOnlyFromCamera)
        return intent
    }

    override fun scanPhoto(imageUrl: ArrayList<String>, index: Int) {
        //浏览图片
        val intent = Intent(thisActivity(), ImageViewerActivity::class.java)
        intent.putExtra(ImageViewerActivity.EXTRA_URLS_LIST, imageUrl)
        intent.putExtra(ImageViewerActivity.EXTRA_CUR_INDEX, index)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && null != data) {
            when (requestCode) {
                REQUEST_TAKE_PIC -> {
                    getTokenAndUpload(data.getStringArrayListExtra("path")[0])
                    mPhotoCount = 1
                }
                REQUEST_PIC_CODE -> {
                    val photoList = data.getStringArrayListExtra("path")
                    if (null != photoList) {
                        mPhotoCount = photoList.size
                        uploadPicToServer(photoList)
                    }
                }
                QrScanCodeAty.REQUEST_CODE -> {
                    val result = data.getStringExtra(CodeUtils.RESULT_STRING)
                    mFunction?.onCallBack(JsBridgeResponseUtil.getJsBridgeResponse(true, "", result))
                }
                QrScanCodeAty.REQUEST_CODE_RFID -> {
                    val result = data.getStringExtra(CodeUtils.RESULT_STRING)
                    mFunction?.onCallBack(JsBridgeResponseUtil.getJsBridgeResponse(true, "", result))
                }
            }
        }
    }

    private fun uploadPicToServer(photoList: ArrayList<String>) {
        for (string in photoList) {
            if (!mUploadPhotoSuccess) {
                break
            }
            getTokenAndUpload(string)
        }
    }

    private fun getTokenAndUpload(path: String) {
        QiNiuUtil1.getToken(this, { token ->
            if (mIsCompress) {
                uploadImageWithCompress(path, token)
            } else {
                uploadImage(path, token)
            }
        }, { string ->
            LogUtil.e("失败---->$string")
            mFunction?.onCallBack(JsBridgeResponseUtil.getJsBridgeResponse(false, string, ""))
        })
    }

    private fun uploadImage(path: String, token: String?) {
        if (token == null) {
            return
        }
        val key = UUID.randomUUID().toString() + ".jpg"
        val uploadManager = UploadManager()
        uploadManager.put(
                path, //文件地址
                key,
                token,
                { key1, info, response ->
                    mIsUploading = false
                    if (!isCancel) {
                        if (info.isOK) {
                            val uploadUrl = QINIU_IMG_HEADER + key1
                            mUploadPhotoList.add(uploadUrl)
                            if (mUploadPhotoList.size == mPhotoCount) {
                                mFunction?.onCallBack(JsBridgeResponseUtil.getJsBridgeResponse(true, "", mUploadPhotoList))
                            }
                        } else {
                            mUploadPhotoSuccess = false
                            mFunction?.onCallBack(JsBridgeResponseUtil.getJsBridgeResponse(false, "上传图片失败", ""))
                        }
                    }
                },
                UploadOptions(null, null, false, { key1, percent ->
                }, {
                    if (isCancel) {
                        Log.i("wsr", "取消上传")
                    }
                    mUploadPhotoSuccess = false
                    mFunction?.onCallBack(JsBridgeResponseUtil.getJsBridgeResponse(false, "用户取消上传", ""))
                    return@UploadOptions isCancel
                }))
    }

    private fun uploadImageWithCompress(path: String, token: String?) {
        if (token == null) {
            return
        }
        val key = UUID.randomUUID().toString() + ".jpg"
        val uploadManager = UploadManager()
        val bytes = CompressUtil.compressWHSrcGetByte(path)
        var exifInterface: ExifInterface
        var orValue = ""
        try {
            exifInterface = ExifInterface(path)
            orValue = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION)
        } catch (e: IOException) {
            e.printStackTrace()
            orValue = "6"
        }

        FileUtil.save(thisActivity(), bytes, key)
        val file = File(FileUtil.getExternalFilePath(thisActivity(), key))
        var filePath = ""
        filePath = file.absolutePath
        try {
            exifInterface = ExifInterface(filePath)
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, orValue)
            exifInterface.saveAttributes()
        } catch (e: IOException) {
            FileUtil.deleteFile(filePath)
            mFunction?.onCallBack(JsBridgeResponseUtil.getJsBridgeResponse(false, "上传图片失败", ""))
            e.printStackTrace()
        }

        if (file.exists()) {
            uploadManager.put(
                    file, //文件地址
                    key,
                    token,
                    { key1, info, response ->
                        FileUtil.deleteFile(filePath)
                        mIsUploading = false
                        if (!isCancel) {
                            if (info.isOK) {
                                val uploadUrl = QINIU_IMG_HEADER + key1
                                mUploadPhotoList.add(uploadUrl)
                                if (mUploadPhotoList.size == mPhotoCount) {
                                    mFunction?.onCallBack(JsBridgeResponseUtil.getJsBridgeResponse(true, "", mUploadPhotoList))
                                }
                            } else {
                                mUploadPhotoSuccess = false
                                mFunction?.onCallBack(JsBridgeResponseUtil.getJsBridgeResponse(false, "上传图片失败", ""))
                            }
                        }
                    },
                    UploadOptions(null, null, false, { key1, percent ->
                    }, {
                        if (isCancel) {
                            Log.i("wsr", "取消上传")
                        }
                        FileUtil.deleteFile(filePath)
                        mUploadPhotoSuccess = false
                        mFunction?.onCallBack(JsBridgeResponseUtil.getJsBridgeResponse(false, "用户取消上传", ""))
                        return@UploadOptions isCancel
                    }))
        }
    }

    override fun close() {
        finish()
    }
}