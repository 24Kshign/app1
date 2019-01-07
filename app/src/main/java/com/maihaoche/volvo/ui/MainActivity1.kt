package com.maihaoche.volvo.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import com.baidu.location.BDLocation
import com.maihaoche.commonbiz.module.ui.AlertToast
import com.maihaoche.commonbiz.module.ui.BaseFragmentActivity
import com.maihaoche.commonbiz.service.utils.DeviceUtil
import com.maihaoche.commonbiz.service.utils.HintUtil
import com.maihaoche.commonbiz.service.utils.SharePreferenceHandler
import com.maihaoche.scanlib.ScanApplication
import com.maihaoche.scanlib.rfid.ExceptionForToast
import com.maihaoche.volvo.AppApplication
import com.maihaoche.volvo.BuildConfig
import com.maihaoche.volvo.R
import com.maihaoche.volvo.dao.Enums
import com.maihaoche.volvo.dao.po.GaragePO
import com.maihaoche.volvo.dao.po.WarehouseVO
import com.maihaoche.volvo.ui.avchat.AVChatCache
import com.maihaoche.volvo.ui.avchat.log.LogUtil
import com.maihaoche.volvo.ui.avchat.permission.MPermission
import com.maihaoche.volvo.ui.avchat.permission.annotation.OnMPermissionDenied
import com.maihaoche.volvo.ui.avchat.permission.annotation.OnMPermissionGranted
import com.maihaoche.volvo.ui.avchat.permission.annotation.OnMPermissionNeverAskAgain
import com.maihaoche.volvo.ui.avchat.utils.LocationManager
import com.maihaoche.volvo.ui.car.activity.SearchCarActivity
import com.maihaoche.volvo.ui.common.activity.InitDataActivity
import com.maihaoche.volvo.ui.instorage.activity.GarageListActivity
import com.maihaoche.volvo.ui.inwarehouse.record.ActivityStocktake
import com.maihaoche.volvo.ui.login.LoginActivity
import com.maihaoche.volvo.ui.login.LoginBiz
import com.maihaoche.volvo.ui.setting.SettingActivity
import com.maihaoche.volvo.ui.webview.WebViewActivity
import com.maihaoche.volvo.util.CookieUtil
import com.maihaoche.volvo.util.GpsUtil
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.auth.AuthServiceObserver
import io.reactivex.functions.Action
import kotlinx.android.synthetic.main.activity_main2.*

/**
 * Created by manji
 * Date：2018/8/22 下午4:11
 * Desc：
 */
class MainActivity1 : BaseFragmentActivity() {

    companion object {

        private const val LAST_CHOOSEN_WAREHOUDE_ID = "last_choosen_warehoude_id"

        private const val IS_MHC_STAFF = "is_mhc_staff"

        private const val BASIC_PERMISSION_REQUEST_CODE = 0x110

        private const val REQUEST_CHOOSE_GARAGE = 0//切换库

        private const val URL = BuildConfig.WEB_HOST + "appPage/home.htm?dd_nav_bgcolor=FF6C94F7"

        @JvmStatic
        fun start(context: Context, isMhcStaff: Boolean) {
            context.startActivity(Intent(context, MainActivity1::class.java).putExtra(IS_MHC_STAFF, isMhcStaff))
        }
    }

    private var mLocationManager: LocationManager? = null

    private var isMhcStaff: Boolean = false

    override fun bindLayoutRes(): Int {
        return R.layout.activity_main2
    }

    override fun initView() {
        super.initView()
        if (!SharePreferenceHandler.getPrefBooleanValue(LoginBiz.SP_IS_LOGIN, false)) {
            SharePreferenceHandler.commitBooleanPref(LoginBiz.SP_IS_LOGIN, true)
        }
        isMhcStaff = intent.getBooleanExtra(IS_MHC_STAFF, false)

        setStaffStyle()
        CookieUtil.setCookie(this)
        load()

        HintUtil.getInstance().init()
        if (DeviceUtil.isSENTER()) {
            try {
                ScanApplication.uhfInit()
            } catch (exceptionForToast: ExceptionForToast) {
                AlertToast.show("找车功能初始化失败，请重启设备")
            }
            setPdaXmlLayout()
        } else {
            setNotPdaXmlLayout()
        }
        //注册云信是否被踢
        NIMClient.getService(AuthServiceObserver::class.java).observeOnlineStatus({ statusCode ->
            if (statusCode.value == 7) {
                AlertToast.show("您的账号在其他设备登录")
                LoginBiz.logout(this@MainActivity1)
            }
        }, true)

        mLocationManager = LocationManager()
        checkPermission()

        initListener()
    }

    private fun checkPermission() {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                , Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO)

        MPermission.with(this@MainActivity1)
                .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(*permissions)
                .request()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    @OnMPermissionNeverAskAgain(BASIC_PERMISSION_REQUEST_CODE)
    fun onBasicPermissionFailed() {
        try {
            AlertToast.show("未全部授权，部分功能可能无法正常运行！请到系统中设置授权")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    fun onBasicPermissionSuccess() {
        LogUtil.i("TAG", "权限检查通过...")
        mLocationManager?.startLocation()
    }

    private fun setStaffStyle() {
        amIvNotMhcStaff.visibility = if (isMhcStaff) View.GONE else View.VISIBLE
        amLlTop.visibility = if (isMhcStaff) View.VISIBLE else View.GONE
        amTvTitle.visibility = if (isMhcStaff) View.VISIBLE else View.GONE
        amLlBottom.visibility = if (isMhcStaff) View.VISIBLE else View.GONE
        amTvDescription.visibility = if (isMhcStaff) View.GONE else View.VISIBLE
        if (!isMhcStaff) {
            val content = "总部\n" +
                    "当前暂无抽检任务\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "感谢配合!"
            var ss = SpannableString(content)
            ss.setSpan(AbsoluteSizeSpan(22, true), content.indexOf('当'), content.indexOf('当') + 8, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            ss.setSpan(AbsoluteSizeSpan(18, true), content.indexOf('感'), content.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(thisActivity(), R.color.red_FD7A71)), content.indexOf('感'), content.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            amTvDescription.text = ss
        }
    }

    private fun setPdaXmlLayout() {
        amTvAllFunction.text = "盘库"
        amTvLibrary.text = "找车"
        setDrawable(true)
    }

    private fun setNotPdaXmlLayout() {
        amTvAllFunction.text = "WMS仓储管理"
        amTvLibrary.text = "盘库"
        setDrawable(false)
    }

    private fun setDrawable(isPda: Boolean) {
        val drawableTop: Drawable? = ContextCompat.getDrawable(thisActivity(), if (isPda) R.drawable.icon_library else R.drawable.icon_all_function)
        amTvAllFunction.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null)

        val drawableTopLibrary = ContextCompat.getDrawable(thisActivity(), if (isPda) R.drawable.icon_search_car else R.drawable.icon_library)
        amTvLibrary.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopLibrary, null, null)
    }

    private fun initListener() {
        amFlLibrary.setOnClickListener {
            if (!DeviceUtil.isSENTER()) {
                AlertToast.show("盘库功能只支持PDA设备")
                return@setOnClickListener
            }
            startActivity(Intent(thisActivity(), SearchCarActivity::class.java))

        }
        amFlAllFunction.setOnClickListener {
            if (DeviceUtil.isSENTER()) {
                if (AppApplication.getGaragePO().usePda == Enums.YesOrNoEnum.YES) {
                    startActivity(ActivityStocktake.createIntent(this))
                } else {
                    AlertToast.show("仓库为开通PDA盘点，请联系总部运营开通或使用钉钉盘点")
                }
            } else {
                WebViewActivity.start(thisActivity(), URL)
            }
        }
        amBindCode.setOnClickListener { startActivity(Intent(this, InitDataActivity::class.java)) }
        amTvWarehouse.setOnClickListener { toGarageChoose() }
        amTvSetting.setOnClickListener { startActivity(Intent(this, SettingActivity::class.java)) }


        mLocationManager?.setOnLocationListener(object : LocationManager.OnLocationListener {
            override fun onSuccess(bdLocation: BDLocation) {
                if (bdLocation.longitude == 0.0 || bdLocation.latitude == 0.0) {
                    return
                }
                SharePreferenceHandler.commitStringPref(GpsUtil.LOCATION_LONGITUDE, bdLocation.longitude.toString())
                SharePreferenceHandler.commitStringPref(GpsUtil.LOCATION_LATITUDE, bdLocation.latitude.toString())
                LogUtil.e("mLatitude--->${bdLocation.latitude}\nmLongitude--->${bdLocation.longitude}")
            }

            override fun onFailure(errorCode: Int, errorInfo: String) {
                AlertToast.show("定位失败，请确保手机Gps定位已打开并且定位权限授权")
            }
        })
    }

    private fun toGarageChoose() {
        startActivityForResult(GarageListActivity.createIntent(this, AppApplication.getGaragePO()
                .wmsGarageId, AppApplication.getGaragePO().garageName), REQUEST_CHOOSE_GARAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null != data && requestCode == requestCode && resultCode == Activity.RESULT_OK) {
            if (null != data.getSerializableExtra(GarageListActivity.CHOSEN_WAREHOUSE)) {
                val warehouseVO = data.getSerializableExtra(GarageListActivity.CHOSEN_WAREHOUSE) as WarehouseVO
                onGarageGet(warehouseVO.toPO())
                SharePreferenceHandler.commitLongPref(LAST_CHOOSEN_WAREHOUDE_ID + AppApplication.getUserName(), warehouseVO.warehouseId)
            }
        }
    }

    private fun load() {
        try {
            val noLastChoose = Action {
                //没有找到这个用户对应的仓库。从仓库列表中选择第一个仓库显示
                AppApplication.getDaoApi().getDefaultWmsGarage(AppApplication.getUserName())
                        .setOnResultGet { garagePO -> onGarageGet(garagePO) }
                        .call(this@MainActivity1)
            }
            val lastWarehouseId = SharePreferenceHandler.getPrefLongValue(LAST_CHOOSEN_WAREHOUDE_ID + AppApplication.getUserName(), 0)
            if (lastWarehouseId >= 0) {
                AppApplication.getDaoApi().getWmsGarage(lastWarehouseId)
                        .setOnResultGet { garagePO -> onGarageGet(garagePO) }
                        .setOnDataError { emsg -> noLastChoose.run() }
                        .call(this@MainActivity1)
            } else {
                noLastChoose.run()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onGarageGet(garagePO: GaragePO) {
        amTvWarehouse.text = garagePO.garageName
        AppApplication.setGaragePO(garagePO)
    }

    @SuppressLint("MissingSuperCall")
    override fun onResume() {
        super.onResume()
        AVChatCache.setMainTaskLaunching(false)
        if (isMhcStaff && (AppApplication.getUserPO() == null || TextUtils.isEmpty(AppApplication.getUserPO().userId))) {
            val intent = Intent(this@MainActivity1, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != mLocationManager) {
            mLocationManager?.onDestroy()
            mLocationManager = null
        }
    }
}