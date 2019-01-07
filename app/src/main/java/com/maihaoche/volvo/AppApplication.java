package com.maihaoche.volvo;

import android.content.Context;

import com.maihaoche.base.http.RetrofitProvider;
import com.maihaoche.scanlib.ScanApplication;
import com.maihaoche.volvo.dao.AppDaoAPI;
import com.maihaoche.volvo.dao.AppDaoHandler;
import com.maihaoche.volvo.dao.po.GaragePO;
import com.maihaoche.volvo.dao.po.UserPO;
import com.maihaoche.volvo.server.AppDataLoader;
import com.maihaoche.volvo.server.AppServerAPI;
import com.maihaoche.volvo.ui.avchat.AVChatLibManager;
import com.maihaoche.volvo.ui.setting.DefauleValue;
import com.maihaoche.volvo.util.SPUtil;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者：yang
 * 时间：17/6/7
 * 邮箱：yangyang@maihaoche.com
 */

public class AppApplication extends ScanApplication {

    private static AppDaoAPI sDaoApi;
    private static AppServerAPI sServerAPI;
    private static UserPO sUserPO = new UserPO();//存放在内存中的用户数据。
    private static volatile GaragePO sGaragePO = new GaragePO();//存放在内存中的仓库数据。

    private RefWatcher refWatcher;
    private static RetrofitProvider.RetrofitCompnent mRetrofitCompnent;

    private static int isInitCount = 0;


    @Override
    protected void initInMain() {
        super.initInMain();
        sDaoApi = new AppDaoHandler(this);
        mRetrofitCompnent = AppDataLoader.DEFAULT;
        sServerAPI = AppDataLoader.load(AppServerAPI.class);
        if (!SPUtil.getBoolean(SPUtil.HAVESET)) {
            SPUtil.saveToPrefs(new DefauleValue());
        }

        ZXingLibrary.initDisplayOpinion(this);

        CrashReport.initCrashReport(getApplicationContext(), "12a516d184", false);
//        LeakCanary.install(this);
//        refWatcher = LeakCanary.install(this);

        initTbs();

        AVChatLibManager.getInstance().initSDK(this);
        AVChatLibManager.getInstance().enableAVChat(this);
        AVChatLibManager.getInstance().registerOnlineStatusObserver(this);

    }

    private void initTbs() {
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean b) {

            }
        });

        Map<String,Object> map = new HashMap<>();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        QbSdk.initTbsSettings(map);
        if (QbSdk.isTbsCoreInited()) {
            QbSdk.preInit(getApplicationContext(), null);
        }
    }

    public static boolean isIsInit() {
        if (isInitCount < 2) {
            return false;
        }
        return true;
    }

    public static void setIsInit(boolean isInit) {
        if (isInit) {
            isInitCount = 0;
        } else {
            isInitCount++;
        }

    }

    public static RefWatcher getRefWatcher(Context context) {
        AppApplication application = (AppApplication) context.getApplicationContext();
        return application.refWatcher;
    }


    @Override
    protected void initAsync() {
        super.initAsync();
    }

    /**
     * 获取服务器访问的API
     */
    public static AppServerAPI getServerAPI() {
        return sServerAPI;
    }

    public static RetrofitProvider.RetrofitCompnent getmRetrofitCompnent() {
        return mRetrofitCompnent;
    }

    public static void setmRetrofitCompnent(RetrofitProvider.RetrofitCompnent retrofitCompnent) {
        mRetrofitCompnent = retrofitCompnent;
        sServerAPI = AppDataLoader.load(AppServerAPI.class, mRetrofitCompnent);
    }

    /**
     * 获取数据库访问的API
     */
    public static AppDaoAPI getDaoApi() {
        return sDaoApi;
    }

    public static UserPO getUserPO() {
        return sUserPO;
    }

    public synchronized static GaragePO getGaragePO() {
        if (sGaragePO == null) {
            throw new NullPointerException("sGaragePO为空，不能调用getGaragePO方法");
        }
        return sGaragePO;
    }

    public synchronized static void setGaragePO(GaragePO garagePO) {
        sGaragePO = garagePO;
    }

    public static String getUserName() {
        return sUserPO.getUserName();
    }

    public static String getRealName() {
        return sUserPO.getRealName();
    }

    public static void setUserPO(UserPO userPO) {
        sUserPO = userPO;
    }


}
