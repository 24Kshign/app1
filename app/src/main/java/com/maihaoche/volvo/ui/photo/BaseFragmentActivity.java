package com.maihaoche.volvo.ui.photo;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.maihaoche.base.log.LogUtil;
import com.maihaoche.commonbiz.service.utils.SizeUtil;
import com.maihaoche.commonbiz.service.permision.PermissionHandler;

import org.reactivestreams.Subscription;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by wangshengru on 16/1/12.
 * Activity基类
 */
//SwipeBackActivity
@SuppressLint("Registered")
public abstract class BaseFragmentActivity extends AppCompatActivity {

    public static final String INTENT_KEY_TO_ACTIVITY = "TO_ACTIVITY";

    public boolean mIsHidden;

    protected Subscription mSubscription;

//    protected final CompositeSubscription mPendingSubscriptions = new CompositeSubscription();

    private final HashSet<BroadcastReceiver> mLocalBR = new HashSet<>();

    @SuppressWarnings("unchecked")
    public <T extends View> T $$(int id) {
        return (T) getLayoutInflater().inflate(id, null);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T $(int id) {
        return (T) super.findViewById(id);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T $(View view, int id) {
        return (T) view.findViewById(id);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("-------------------》" + this.getClass().getSimpleName());
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        // 这里有一篇http://www.jianshu.com/p/662c46cd3b5f文章,可以从Fragment的角度解决重启Activity导致的Fragment重叠问题
        if (savedInstanceState != null) {
            FragmentManager fragmentManager = getFragmentManager();
            try {
                Class<?> clazz = null;
                clazz = Class.forName("android.app.FragmentManagerImpl");
                Field activeField = null;
                activeField = clazz.getDeclaredField("mActive");
                activeField.setAccessible(true);
                Field addField = clazz.getDeclaredField("mAdded");
                addField.setAccessible(true);

                ArrayList<Fragment> mActive = (ArrayList<Fragment>) activeField.get(fragmentManager);
                ArrayList<Fragment> mAdded = (ArrayList<Fragment>) addField.get(fragmentManager);
                if (mActive != null && mActive.size() > 0) {
                    for (int i = 0, s = mActive.size(); i < s; i++) {
                        Fragment fragment = mActive.get(i);
                        fragmentManager.beginTransaction().remove(fragment).commit();
                    }
                }
                if (mAdded != null && mAdded.size() > 0) {
                    for (int i = 0, s = mAdded.size(); i < s; i++) {
                        Fragment fragment = mAdded.get(i);
                        fragmentManager.beginTransaction().remove(fragment).commit();
                    }
                }
                fragmentManager.executePendingTransactions();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsHidden = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
//            mSubscription.unsubscribe();
//        }
//        mPendingSubscriptions.clear();
//
//        if (ActivityUtil.getActivityList().contains(this)) {
//            ActivityUtil.getActivityList().remove(this);
//        }
        synchronized (mLocalBR) {
            Iterator iterator = mLocalBR.iterator();
            while (iterator.hasNext()) {
                BroadcastReceiver broadcastReceiver = (BroadcastReceiver) iterator.next();
                unregisterLocalBR(broadcastReceiver);
            }
        }
    }

    /**
     * 埋点相关
     */
//    protected void updatePageProperty(Map<String, String> map) {
//        if (map == null) return;
//        MANServiceProvider.getService().getMANPageHitHelper().updatePageProperties(map);
//    }
    protected void makeBuriedMap(Map<String, String> map) {

    }

    @Override
    protected void onResume() {
//        if (!BuildConfig.DEBUG) {
//            StatService.onResume(this);
//            MANServiceProvider.getService().getMANPageHitHelper().pageAppear(this);
//            if (UserInfoHelper.getUserInfo() != null) {
//                Map<String, String> map = new HashMap<>();
//                makeBuriedMap(map);
//                map.put(BuriedHelper.PHONE_NUMBER, String.valueOf(UserInfoHelper.getUserInfo().mMobile));
//                MANServiceProvider.getService().getMANPageHitHelper().updatePageProperties(map);
//            }
//        }
        super.onResume();
        mIsHidden = false;
    }


    /**
     * 点击外部隐藏键盘必要的点击判断
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {

        if (v != null && (v instanceof EditText)) {
            int[] point = {0, 0};
            v.getLocationOnScreen(point);
            int left = point[0], top = point[1],
                    bottom = top + v.getHeight(), right = left + v.getWidth();
            //不判断右边界可以解决点击删除按钮宽度导致键盘隐藏，但会导致短的editText点击右边区域不会隐藏软键盘（可以接受）
            return !(event.getRawX() > left
                    && event.getRawY() > top
                    && event.getRawY() < right + SizeUtil.dip2px(15f)
                    && event.getRawY() < bottom);
        }
        //如果焦点不是EditText则忽略
        return false;
    }


    @Override
    public void onBackPressed() {
//        if (PushHelper.isNeedGoMain(getIntent())) {
//            try {
//                RouterUtil.startActivity(this,RouterUtil.getRouter(PathValue.PLATFORM_MAIN));
//            } catch (ModuleNotAssembledException e) {
//                e.printStackTrace();
//                AlertToast.show(e.getModuleNoExistStr());
//            }
//        }
        super.onBackPressed();
    }
//
//    protected void makeCustomBuriedWithId(String buriedName, long id) {
//        MANHitBuilders.MANCustomHitBuilder manCustomHitBuilder = new MANHitBuilders.MANCustomHitBuilder(buriedName);
//        manCustomHitBuilder.setProperty(BuriedHelper.CUSTOMER_PARAMS_KEY_ID, String.valueOf(id));
//        BuriedHelper.sendCustomBuriedEvent(this, manCustomHitBuilder);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * 注册本地广播
     */
    protected final void registerLocalBR(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        if (broadcastReceiver != null) {
            synchronized (mLocalBR) {
                mLocalBR.add(broadcastReceiver);
                LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
            }
        }
    }

    /**
     * 注销本地广播
     */
    protected final void unregisterLocalBR(BroadcastReceiver broadcastReceiver) {
        if (broadcastReceiver != null) {
            synchronized (mLocalBR) {
                mLocalBR.remove(broadcastReceiver);
                LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
            }
        }
    }


}
