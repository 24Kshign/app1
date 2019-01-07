package com.maihaoche.commonbiz.service.permision;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * 权限工具类具体的实现,不对外开放。对外使用PermissionHandler.
 * 使用观察者模式。
 * 作者：王洋洋 on 2016/1/25 14:51
 * QQ： 564033203
 */
class PermissionUtils {

    public final static Observable permissionObservable = new Observable() {
        /**
         * 该函数决定了 Observable 发起notifyObserver的时候，各个Observer是否调用update函数。
         * 如果返回false(既不发生变化)，则不会调用update。这里强制返回true，既每次notifyObserver的时候，必然调用update。
         * @return
         */
        @Override
        public boolean hasChanged() {
            return true;
        }
    };
    //多个请求同时发生时，使用观察者模式，用来通知 其他 的观察者。
    private static HashMap<Integer, PermissionHandler.PermissionRequestInterface> mPermissionRequests = new HashMap<>();//存储request code与权限请求回调的map
    private static int mIncreasingRequestCode = 0;//自增长的requestcode
    private static int mRquestCodeInProcess = -1;//当前正在处理的requestCode，专门用于线程间的同步;

    /**
     * -----------------------------------------对外的API-----------------------------------
     */
    /**
     * 封装好的检查权限的函数检查权限
     *
     * @param activity         当前的activity
     * @param hintContent      申请该权限时的提示文案
     * @param permission       具体的权限名，数组形式，支持同时申请多个权限
     * @param requestInterface 监听申请权限结果的回调
     */
    static void checkPermission(
            final Activity activity
            , String hintContent
            , final String[] permission
            , final PermissionHandler.PermissionRequestInterface requestInterface
    ) {
        checkPermission(activity, hintContent, permission, -1, requestInterface);
    }

    /**
     * 在activity中申请权限后的回调处理
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean isAllPermissionGranted = true;
        if (permissions != null
                || grantResults != null
                || permissions.length == 0
                || grantResults.length == 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (i < grantResults.length && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isAllPermissionGranted = false;
                }
            }
        } else {
            isAllPermissionGranted = false;
        }
        if (mPermissionRequests.get(requestCode) != null) {
            mPermissionRequests.get(requestCode).onPermissionGranted(isAllPermissionGranted);
            removePermissionRequest(requestCode);
        }
        permissionObservable.notifyObservers();//通知，
    }

    static void removeAllPermissionRequest() {
        for (int i = 0; i < mIncreasingRequestCode; i++) {
            mPermissionRequests.remove(i);
        }
        mIncreasingRequestCode = 0;
        mRquestCodeInProcess = -1;
    }

    /**-----------------------------------------私有的函数-----------------------------------*/

    /**
     * 封装好的检查权限的函数检查权限
     *
     * @param activity         当前的activity
     * @param hintContent      申请该权限时的提示文案
     * @param permission       具体的权限名，数组形式，支持同时申请多个权限
     * @param requestCode      申请权限时，需要的requestCode.注意:该requestCode是由PermissionUtils类负责处理,自增长,最大不超过256,超过后重置为0,再自增长。
     *                         若传入一个大于0的requestCode,说明该权限是排在队列中的,又一次调用checkPermission函数。
     * @param requestInterface 监听申请权限结果的回调
     */
    private static boolean checkPermission(
            final Activity activity
            , String hintContent
            , final String[] permission
            , int requestCode
            , final PermissionHandler.PermissionRequestInterface requestInterface
    ) {


        if (activity == null || permission == null || permission.length == 0 || requestInterface == null) {
            return false;
        }
        synchronized (mPermissionRequests) {
            final String hint = !TextUtils.isEmpty(hintContent) ? hintContent : "需要相应的权限，是否授权？";
            //判断是否是新的request
            boolean newRequest = false;
            if (!mPermissionRequests.containsKey(requestCode)) {
                if (requestCode < 0) {
                    requestCode = mIncreasingRequestCode % 256;
                    mIncreasingRequestCode++;
                }
                mPermissionRequests.put(requestCode, requestInterface);
                newRequest = true;
            }
            //当前有个权限正在被申请---竞争
            if (mRquestCodeInProcess >= 0) {
                if (newRequest) {
                    //如果是新的请求，要注册到观察者中
                    permissionObservable.addObserver(new PermissionObserver(activity, hint, permission, requestCode, requestInterface));
                }
            }
            //当前没有权限正在被申请
            else {
                //如果是新的请求，要判断是否需要弹窗申请。
                if (newRequest) {
                    boolean isAllPermissionGranted = true;
                    for (int i = 0; i < permission.length; i++) {
                        //有某个权限未被授权
                        if (ActivityCompat.checkSelfPermission(activity, permission[i]) != PackageManager.PERMISSION_GRANTED) {
                            isAllPermissionGranted = false;
                        }
                    }
                    if (isAllPermissionGranted) {
                        requestInterface.onPermissionGranted(true);
                        removePermissionRequest(requestCode);
                        permissionObservable.notifyObservers();
                    } else {
                        requstPermissionWithHint(activity, hint, permission, requestCode);
                    }
                }
                //如果是旧的请求，则直接竞争。
                else {
                    requstPermissionWithHint(activity, hint, permission, requestCode);
                }
                return true;
            }
            return false;
        }
    }

    /**
     * 申请权限前显示带提示的弹窗。点击确定后请求某个权限.
     * 只有在某个权限被禁用过后，再次申请的时候才需要弹窗。
     *
     * @param context     上下文环境
     * @param hint        snack提示的文案
     * @param permission  数组形式，支持多个权限同时申请
     * @param requestCode 请求编码
     *                    <p>
     *                    修改后不弹窗，直接申请。
     */
    private static void showRequestDailog(final Activity context
            , String hint
            , final String[] permission
            , final int requestCode) {
        requestPermission(context, permission, requestCode);
        //        if (mRequestAlertDialog != null && mRequestAlertDialog.isShowing()) {
        //            mRequestAlertDialog.dismiss();
        //        }
        //        mRequestAlertDialog = DialogUtil.showUnCancelOKDialog(
        //                context
        //                , "需要授权"
        //                , hint
        //                , "确定"
        //                , (dialog, which) -> {
        //                    requestPermission(context, permission, requestCode);
        //                }
        //        );
        //        mRequestAlertDialog.show();
    }

    /**
     * 请求某个权限，并检查是否需要弹框提醒
     *
     * @param context     上下文环境
     * @param hint        snack提示的文案
     * @param permission  在snackbar上点击确定后发起请求的权限名。数组形式，支持多个权限同时申请
     * @param requestCode 请求编码
     */
    private static void requstPermissionWithHint(final Activity context
            , final String hint
            , final String[] permission
            , final int requestCode) {
        mRquestCodeInProcess = requestCode;
        boolean shouldShowHint = false;
        for (int i = 0; i < permission.length; i++) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission[i])) {
                shouldShowHint = true;
            }
        }
        //权限之前被禁用过。需要弹窗提示。
        if (shouldShowHint) {
            showRequestDailog(context, hint, permission, requestCode);
        }
        //权限未被禁止,还未授权。直接申请
        else {
            requestPermission(context, permission, requestCode);
        }
    }


    /**
     * 申请某个权限
     *
     * @param context     上下文环境
     * @param permission  发起请求的权限名。数组形式，支持多个权限同时申请
     * @param requestCode 请求编码
     */
    private static void requestPermission(Activity context, String[] permission, int requestCode) {
        if (permission == null || permission.length == 0) {
            return;
        }
        ActivityCompat.requestPermissions(context,
                permission,
                requestCode);
    }

    /**
     * 权限请求的观察者。
     */
    private static class PermissionObserver implements Observer {
        private Activity mActivity;
        private String mHintContent;
        private String[] mPermission;
        private PermissionHandler.PermissionRequestInterface mRequestInterface;
        private int mRequestCode = 0;

        /**
         * 权限请求的观察者，如果有其他权限正在请求，那么当前权限请求添加观察者。
         * 当其他权限请求完毕后，会通知该观察者再次发起请求
         *
         * @param activity         上下文环境
         * @param hintContent      snack提示的文案
         * @param permission       发起请求的权限名。数组形式，支持多个权限同时申请
         * @param requestCode      请求码
         * @param requestInterface 申请权限的回调接口
         */
        public PermissionObserver(
                Activity activity
                , String hintContent
                , String[] permission
                , int requestCode
                , PermissionHandler.PermissionRequestInterface requestInterface) {
            mActivity = activity;
            mHintContent = hintContent;
            mPermission = permission;
            mRequestCode = requestCode;
            mRequestInterface = requestInterface;
        }

        //data传入 requestCode
        @Override
        public void update(Observable observable, Object data) {
            if (checkPermission(mActivity, mHintContent, mPermission, mRequestCode, mRequestInterface)) {
                permissionObservable.deleteObserver(this);
            }
        }
    }


    /**
     * 移除某个requestCode所对应的回调接口
     *
     * @param requestCode 请求编码
     */
    private static void removePermissionRequest(int requestCode) {
        mPermissionRequests.remove(requestCode);
        if (mRquestCodeInProcess == requestCode) {
            mRquestCodeInProcess = -1;
        }
    }
}
