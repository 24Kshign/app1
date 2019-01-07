package com.maihaoche.volvo.ui.avchat.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.module.ui.NormalDialog;
import com.maihaoche.commonbiz.service.utils.SharePreferenceHandler;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.server.dto.request.NotifyLiveClosedRequest;
import com.maihaoche.volvo.server.dto.request.NotifyLiveConnectedRequest;
import com.maihaoche.volvo.server.dto.request.StartCheckRequest;
import com.maihaoche.volvo.ui.avchat.AVChatProfile;
import com.maihaoche.volvo.ui.avchat.AVChatTimeoutObserver;
import com.maihaoche.volvo.ui.avchat.log.LogUtil;
import com.maihaoche.volvo.ui.avchat.notification.AVChatNotification;
import com.maihaoche.volvo.ui.avchat.receiver.PhoneCallStateObserver;
import com.maihaoche.volvo.ui.avchat.sound.AVChatSoundPlayer;
import com.maihaoche.volvo.ui.avchat.utils.AnimationUtils;
import com.maihaoche.volvo.ui.avchat.utils.LocationManager;
import com.maihaoche.volvo.ui.login.LoginBiz;
import com.maihaoche.volvo.util.GpsUtil;
import com.maihaoche.volvo.util.permission.GoToPermissionSetting;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.ClientType;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserverLite;
import com.netease.nimlib.sdk.avchat.constant.AVChatEventType;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoCropRatio;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoQuality;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatAudioFrame;
import com.netease.nimlib.sdk.avchat.model.AVChatCameraCapturer;
import com.netease.nimlib.sdk.avchat.model.AVChatCommonEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatImageFormat;
import com.netease.nimlib.sdk.avchat.model.AVChatNetworkStats;
import com.netease.nimlib.sdk.avchat.model.AVChatOnlineAckEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatParameters;
import com.netease.nimlib.sdk.avchat.model.AVChatSessionStats;
import com.netease.nimlib.sdk.avchat.model.AVChatSurfaceViewRenderer;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoCapturerFactory;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoFrame;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.maihaoche.volvo.util.permission.GoToPermissionSetting.REQUEST_CODE_PERMISSION_SETTING;

public class AVChatActivity extends BaseActivity implements AVChatStateObserverLite {
    public static final String TAG = AVChatActivity.class.getSimpleName();

    private static final String KEY_SOURCE = "source";
    private static final String KEY_CALL_CONFIG = "KEY_CALL_CONFIG";

    private static final int BASIC_PERMISSION_REQUEST_CODE = 0x010;
    private static final int REQUEST_GPS_CODE = 0x121;

    /**
     * 来自广播
     */
    public static final int FROM_BROADCASTRECEIVER = 0;

    private View vIncomingCall;
    private TextView tvCustomService;
    private TextView tvVinNo;
    private TextView tvVehicleAttr;
    private TextView tvRepositoryInfo;
    private TextView tvKeyInfo;
    private AVChatSurfaceViewRenderer renderer;
    private View vChattingAttrs;
    private View vChattingAttrsClosing;

    private ObjectAnimator animator;

    private AVChatData avChatData; // config for connect video server
    private AVChatCameraCapturer mVideoCapturer;
    private AVChatNotification notifier; // notification

    private String channelId;
    private Long recordId;

    // state
    private boolean isUserFinish = false;
    private boolean hasOnPause = false; // 是否暂停音视频
    private boolean isCallEstablished = false; // 电话是否接通

    private boolean incomingViewClick = false;
    private boolean closeRtc = true;
    private boolean hangUp = false;
    private boolean finish = false;
    private boolean isUploadLocation = false;
    private boolean notifyLiveClosed = false;

    private long checkId;
    private String checkerId;

    private String mLatitude, mLongitude;

    private long chatId;

    private StartCheckRequest mStartCheckRequest;

    private LocationManager mLocationManager;

    private Dialog mGpsDialog;

    private Dialog mLocationDialog;

    private View.OnClickListener vIncomingCallClickListener = view -> {
        if (incomingViewClick) {
            AlertToast.showAsync("通话连接中, 请稍等...");
            return;
        }

        LogUtil.i("接听按钮被点击了...");
        incomingViewClick = true;

        mStartCheckRequest = new StartCheckRequest();
        mStartCheckRequest.checkId = checkId;

        mStartCheckRequest.checkerId = (checkerId.equals("null") || checkerId == null) ? "" : String.valueOf(checkerId);
        String loginInUserName = SharePreferenceHandler.getPrefStringValue(LoginBiz.SP_LOGIN_USER_NAME, "");
        if (!TextUtils.isEmpty(loginInUserName)) {
            AppApplication.getDaoApi().getUser(loginInUserName).setOnResultGet(userPo -> {
                if (null != userPo) {
                    mStartCheckRequest.userType = userPo.isMhcStaff() ? 0 : 1;
                }
            }).call(this);
        }

        if (Double.valueOf(mLatitude) == 0 || Double.valueOf(mLongitude) == 0) {
            if (null != mLocationManager) {
                mLocationManager.startLocation();
            }
        } else {
            uploadLongitudeAndLatitude();
        }
    };

    private void uploadLongitudeAndLatitude() {
        mStartCheckRequest.latitude = mLatitude;
        mStartCheckRequest.longitude = mLongitude;
        LogUtil.i(TAG, "开始请求服务器建立视频连接, request->" + new Gson().toJson(mStartCheckRequest));
        AppApplication.getServerAPI().avChatStartCheck(mStartCheckRequest)
                .setOnDataGet(response -> {
                    LogUtil.i(TAG, "请求视频建立连接成功->" + new Gson().toJson(response));
                    recordId = response.result.recordId;
                    // 设置通话界面相关数据
                    setChattingInfo(
                            response.result.checkerName,
                            response.result.carUnique,
                            response.result.carInfo,
                            response.result.postionInfo,
                            response.result.keyId
                    );

                    // 开启本地视频，准备与Web实时音视频连接
                    inComingCalling(avChatData);
                })
                .setOnDataError(emsg -> {
                    LogUtil.e(TAG, "请求视频建立连接失败->" + emsg);
                    AlertToast.showAsync("接听失败, 请求连接失败:" + emsg);
                    finish();
                })
                .call(AVChatActivity.this);
    }

    private View.OnClickListener vChattingAttrsOpenningListener = view -> {
        LogUtil.i(TAG, "收起按钮被点击了...");
        TranslateAnimation moveToViewBottomAnimation = AnimationUtils.buildMoveToViewBottomAnimation(600);
        vChattingAttrs.setAnimation(moveToViewBottomAnimation);
        vChattingAttrs.setVisibility(View.INVISIBLE);

        TranslateAnimation moveToViewLocationAnimation = AnimationUtils.buildMoveToViewLocationAnimation(300);
        moveToViewLocationAnimation.setStartOffset(600);
        vChattingAttrsClosing.setAnimation(moveToViewLocationAnimation);
        vChattingAttrsClosing.setVisibility(View.VISIBLE);
    };

    private View.OnClickListener vSwitchCameraClickListener = view -> {
        LogUtil.i("切换镜头按钮被点击了...");
        if (mVideoCapturer != null) {
            mVideoCapturer.switchCamera();
        }
    };

    private View.OnClickListener vChattingAttrsClosingListener = view -> {
        TranslateAnimation moveToViewBottomAnimation = AnimationUtils.buildMoveToViewBottomAnimation(300);
        vChattingAttrsClosing.setAnimation(moveToViewBottomAnimation);
        vChattingAttrsClosing.setVisibility(View.INVISIBLE);

        TranslateAnimation moveToViewLocationAnimation = AnimationUtils.buildMoveToViewLocationAnimation(600);
        moveToViewLocationAnimation.setStartOffset(300);
        vChattingAttrs.setAnimation(moveToViewLocationAnimation);
        vChattingAttrs.setVisibility(View.VISIBLE);
    };

    private AVChatCallback accept2AVChatCallback = new AVChatCallback<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            LogUtil.i(TAG, "accept2 accept success");

            animator.cancel();
            AVChatSoundPlayer.instance().stop();

            // 改变UI界面显示
            changeAVChat();
            renderer.setVisibility(View.VISIBLE);
            AVChatManager.getInstance().setupLocalVideoRender(renderer, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);

            resumeVideo();
        }

        @Override
        public void onFailed(int code) {
            LogUtil.e(TAG, "接听失败->" + code);

            AlertToast.showAsync("接听失败, code->" + code);
            finish();
        }

        @Override
        public void onException(Throwable exception) {
            LogUtil.e(TAG, "接听异常->" + new Gson().toJson(exception));

            AlertToast.showAsync("接听失败, e->" + new Gson().toJson(exception));
            finish();
        }
    };

    /**
     * incoming call
     *
     * @param context
     */
    public static void launch(Context context, AVChatData config, int source) {
        Intent intent = new Intent();
        intent.setClass(context, AVChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_CALL_CONFIG, config);
        intent.putExtra(KEY_SOURCE, source);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_avchat;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        super.afterCreate(savedInstanceState);
        LogUtil.i(TAG, "afterCreate");

        // 锁屏唤醒
        dismissKeyguard();

        // 启动成功，取消timeout处理
        AVChatProfile.getInstance().activityLaunched();

        // init view
        initView();

        initDialog();

        checkPermission();

        parseIncomingIntent();

        registerNetCallObserver(true);

        String account = avChatData.getAccount() != null ? avChatData.getAccount() : "NULL";
        notifier = new AVChatNotification(this);
        notifier.init(account);

        isCallEstablished = false;

        AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.RING);
    }

    private void initDialog() {
        mLocationDialog = new NormalDialog(this)
                .setTilte("定位权限")
                .setContent("定位权限未授权，请到系统设置页面手动授权")
                .setSingleConfirmStr("去授权")
                .setCanceledOutside(false)
                .setIsCancelable(false)
                .setOnOKClickListener(() -> {
                    GoToPermissionSetting.GoToSetting(this);
                });
        mGpsDialog = new NormalDialog(this)
                .setTilte("Gps定位")
                .setContent("检测到您没有开启Gps定位，请去设置页面手动开启")
                .setSingleConfirmStr("开启")
                .setCanceledOutside(false)
                .setIsCancelable(false)
                .setOnOKClickListener(() -> {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                });
    }

    @Override
    protected void onResume() {
        LogUtil.i(TAG, "onResume");
        super.onResume();
        cancelCallingNotifier();
        if (hasOnPause) {
            resumeVideo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BASIC_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//用户同意权限,执行我们的操作
                if (null != mLocationDialog && mLocationDialog.isShowing()) {
                    mLocationDialog.dismiss();
                }
                showGpsDialog();
            } else {//用户拒绝之后，直接跳转到系统设置页面
                if (null != mLocationDialog && !mLocationDialog.isShowing()) {
                    mLocationDialog.show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GPS_CODE) {
            showGpsDialog();
        } else if (requestCode == REQUEST_CODE_PERMISSION_SETTING) {
            checkPermission();
        }
    }

    private void showGpsDialog() {
        if (!GpsUtil.isGpsOpen(this)) {
            if (null != mGpsDialog && !mGpsDialog.isShowing()) {
                mGpsDialog.show();
            }
        } else {
            if (null != mGpsDialog && mGpsDialog.isShowing()) {
                mGpsDialog.dismiss();
            }
        }
    }

    @Override
    protected boolean isPermissionHandler() {
        return false;
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, BASIC_PERMISSION_REQUEST_CODE);
        } else {
            if (!GpsUtil.isGpsOpen(this)) {
                if (null != mGpsDialog && !mGpsDialog.isShowing()) {
                    mGpsDialog.show();
                }
            } else {
                if (null != mGpsDialog && mGpsDialog.isShowing()) {
                    mGpsDialog.dismiss();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        LogUtil.i(TAG, "onPause");
        super.onPause();
        pauseVideo(); // 暂停视频聊天（用于在视频聊天过程中，APP退到后台时必须调用）
    }

    @Override
    protected void onStop() {
        LogUtil.i(TAG, "onStop");
        super.onStop();
        activeCallingNotifier();
    }

    @Override
    public void finish() {
        if (finish) return;

        LogUtil.i(TAG, "finish");
        isUserFinish = true;
        finish = true;
        super.finish();
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        super.onDestroy();
        if (null != mLocationManager) {
            mLocationManager.onDestroy();
            mLocationManager = null;
        }
        incomingViewClick = false;
        notifyLiveClosed();
        hangUp();
        closeRtc();
        cancelCallingNotifier();
        AVChatProfile.getInstance().setAVChatting(false);
        registerNetCallObserver(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LogUtil.i(TAG, "返回键被点击了, 禁用...");
            AlertToast.showAsync("视频通话中, 请勿点击该按钮");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {

        LogUtil.i(TAG, "初始化界面UI...");
        // find view
        vIncomingCall = findViewById(R.id.avchat_iv_incoming_call);
        View vSwitchCamera = findViewById(R.id.avchat_iv_switch_camera);
        tvCustomService = (TextView) findViewById(R.id.avchat_tv_custom_service);
        tvVinNo = (TextView) findViewById(R.id.avchat_tv_vin_no);
        tvVehicleAttr = (TextView) findViewById(R.id.avchat_tv_vehicle_attr);
        tvRepositoryInfo = (TextView) findViewById(R.id.avchat_tv_repository_info);
        tvKeyInfo = (TextView) findViewById(R.id.avchat_tv_key_info);
        renderer = (AVChatSurfaceViewRenderer) findViewById(R.id.avchat_renderer_preview);
        vChattingAttrs = findViewById(R.id.avchat_ll_attrs);
        View vChattingAttrsOpenning = findViewById(R.id.avchat_bt_open_control);
        vChattingAttrsClosing = findViewById(R.id.avchat_bt_close_control);

        mLocationManager = new LocationManager();

        // set click listener
        vIncomingCall.setOnClickListener(vIncomingCallClickListener);
        vSwitchCamera.setOnClickListener(vSwitchCameraClickListener);
        vChattingAttrsOpenning.setOnClickListener(vChattingAttrsOpenningListener);
        vChattingAttrsClosing.setOnClickListener(vChattingAttrsClosingListener);

        // 设置动画
        animator = AnimationUtils.nope(vIncomingCall, 4f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();

        mLocationManager.setOnLocationListener(new LocationManager.OnLocationListener() {
            @Override
            public void onSuccess(BDLocation bdLocation) {
                LogUtil.e("mLatitude--->${bdLocation.latitude}\nmLongitude--->${bdLocation.longitude}");
                if (bdLocation.getLongitude() != 0 && bdLocation.getLatitude() != 0) {
                    mLongitude = String.valueOf(bdLocation.getLongitude());
                    mLatitude = String.valueOf(bdLocation.getLatitude());
                }
                uploadLongitudeAndLatitude();
            }

            @Override
            public void onFailure(int errorCode, String errorInfo) {
                AlertToast.show("定位失败，请确保手机Gps定位已打开并且定位权限授权");
            }
        });
    }

    // 设置窗口flag，亮屏并且解锁/覆盖在锁屏界面上
    private void dismissKeyguard() {
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
    }

    /**
     * 来电参数解析
     */
    private void parseIncomingIntent() {
        avChatData = (AVChatData) getIntent().getSerializableExtra(KEY_CALL_CONFIG);
        if (avChatData == null) return;
        setNotNullText(tvCustomService, avChatData.getAccount());
        chatId = avChatData.getChatId();
        try {
            String extra = avChatData.getExtra();
            JSONObject extraJO = new JSONObject(extra);
            checkId = Long.valueOf(extraJO.optString("checkId"));
            checkerId = extraJO.optString("checkerId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mLongitude = SharePreferenceHandler.getPrefStringValue(GpsUtil.LOCATION_LONGITUDE, "0");
        mLatitude = SharePreferenceHandler.getPrefStringValue(GpsUtil.LOCATION_LATITUDE, "0");
        LogUtil.e("mLongitude--->" + mLongitude + "\nmLatitude--->" + mLatitude);
        LogUtil.i(TAG, "parseIncomingIntent->" + new Gson().toJson(avChatData) + " " + checkId + " " + checkerId);
    }

    private void resumeVideo() {
        LogUtil.i(TAG, "恢复视频...");

        AVChatManager.getInstance().muteLocalVideo(false);
        AVChatManager.getInstance().muteLocalAudio(false);

        hasOnPause = false;
    }

    private void pauseVideo() {
        LogUtil.i(TAG, "暂停视频...");

        AVChatManager.getInstance().muteLocalVideo(true);
        AVChatManager.getInstance().muteLocalAudio(true);

        hasOnPause = true;
    }

    private void inComingCalling(AVChatData avChatData) {
        LogUtil.i(TAG, "初始化本地音视频, 接听网络电话...");
        AVChatManager.getInstance().enableRtc();

        pauseVideo();

        if (mVideoCapturer == null) {
            mVideoCapturer = AVChatVideoCapturerFactory.createCameraCapturer();
            if (mVideoCapturer != null) {
                AVChatManager.getInstance().setupVideoCapturer(mVideoCapturer);
            } else {
                AlertToast.show("手机版本太低，无法启动视频播放模块");
            }
        }

        AVChatManager.getInstance().setParameters(buildAVChatParameters());

        AVChatManager.getInstance().enableVideo();
        AVChatManager.getInstance().startVideoPreview();

        AVChatManager.getInstance().accept2(avChatData.getChatId(), accept2AVChatCallback);

        closeRtc = false;
    }

    private void closeRtc() {
        if (closeRtc) {
            return;
        }
        LogUtil.i(TAG, "closeRtc");

        // closeRtc
        AVChatManager.getInstance().stopVideoPreview();
        AVChatManager.getInstance().disableVideo();
        AVChatManager.getInstance().disableRtc();

        closeRtc = true;
    }

    private void notifyLiveClosed() {
        if (notifyLiveClosed) {
            return;
        }

        NotifyLiveClosedRequest request = new NotifyLiveClosedRequest();
        request.checkId = checkId;
        request.channelId = channelId == null ? "" : channelId;
        LogUtil.i(TAG, "开始向服务器发送连接断开,  request:" + new Gson().toJson(request));
        AppApplication.getServerAPI().avChatNotifyLiveClosed(request)
                .setOnDataGet(response -> {
                    LogUtil.v(TAG, "连接断开上传成功->" + new Gson().toJson(response));
                })
                .setOnDataError(emsg -> {
                    LogUtil.v(TAG, "连接断开上传失败->" + emsg);
                })
                .call();

        notifyLiveClosed = true;
    }

    /**
     * 注册监听
     *
     * @param register
     */
    private void registerNetCallObserver(boolean register) {
        LogUtil.i(TAG, "注册监听者->" + register);
        AVChatManager.getInstance().observeAVChatState(this, register);
        AVChatManager.getInstance().observeHangUpNotification(callHangupObserver, register);
        AVChatManager.getInstance().observeOnlineAckNotification(onlineAckObserver, register);
        AVChatTimeoutObserver.getInstance().observeTimeoutNotification(timeoutObserver, register, true);
        PhoneCallStateObserver.getInstance().observeAutoHangUpForLocalPhone(autoHangUpForLocalPhoneObserver, register);
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, register);
    }

    private void setChattingInfo(String customService,
                                 String vinNo,
                                 String vehicleAttr,
                                 String repositoryInfo,
                                 String keyId) {
        setNotNullText(tvCustomService, customService);
        setNotNullText(tvVinNo, vinNo);
        setNotNullText(tvVehicleAttr, vehicleAttr);
        setNotNullText(tvRepositoryInfo, repositoryInfo);
        setNotNullText(tvKeyInfo, keyId);
    }

    private void setNotNullText(TextView tv, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        tv.setText(text);
    }

    /**
     * 注册/注销网络通话对方挂断的通知
     */
    Observer<AVChatCommonEvent> callHangupObserver = avChatHangUpInfo -> {
        LogUtil.e(TAG, "网络通话对方挂断监听通知->" + new Gson().toJson(avChatHangUpInfo));

        closeRtc();

        // 如果是incoming call主叫方挂断，那么通知栏有通知
        if (!isCallEstablished) {
            cancelCallingNotifier();
            activeMissCallNotifier();
            AlertToast.show("对方已挂断");
            finish();
            return;
        }

        uploadLocation();
    };

    Observer<Integer> timeoutObserver = integer -> {
        LogUtil.e(TAG, "超时监听通知->" + integer);

        activeMissCallNotifier();
        AVChatSoundPlayer.instance().stop();
        AlertToast.show("超时未接听");
        finish();
    };

    Observer<Integer> autoHangUpForLocalPhoneObserver = integer -> {
        LogUtil.e(TAG, "本地来电挂断通知->" + integer);

        if (!isCallEstablished) {
            activeMissCallNotifier();
        } else {
            AlertToast.show("本地来电话了, 视频通话断开");
        }
        AVChatSoundPlayer.instance().stop();
        finish();
    };

    Observer<StatusCode> userStatusObserver = code -> {
        LogUtil.e(TAG, "用户状态监听通知->" + code);

        if (code.wontAutoLogin()) {
            AlertToast.show("登录状态已断开");
            AVChatSoundPlayer.instance().stop();
            finish();
        }
    };

    /**
     * 注册/注销同时在线的其他端对主叫方的响应
     */
    private Observer<AVChatOnlineAckEvent> onlineAckObserver = ackInfo -> {
        LogUtil.e(AVChatActivity.TAG, "其他端监听触发->" + new Gson().toJson(ackInfo));

        if (avChatData.getChatId() == ackInfo.getChatId()) {
            AVChatSoundPlayer.instance().stop();

            String client = null;
            switch (ackInfo.getClientType()) {
                case ClientType.Web:
                    client = "Web";
                    break;
                case ClientType.Windows:
                    client = "Windows";
                    break;
                case ClientType.Android:
                    client = "Android";
                    break;
                case ClientType.iOS:
                    client = "iOS";
                    break;
                case ClientType.MAC:
                    client = "Mac";
                    break;
                default:
                    break;
            }
            if (client != null) {
                String option = ackInfo.getEvent() == AVChatEventType.CALLEE_ONLINE_CLIENT_ACK_AGREE ? "接听！" : "拒绝！";
                AlertToast.show("通话已在" + client + "端被" + option);
                finish();
            }
        }
    };

    private void uploadLocation() {
        if (isUploadLocation) {
            return;
        }

        NotifyLiveClosedRequest request = new NotifyLiveClosedRequest();
        request.recordId = recordId;
        LogUtil.i(TAG, "开始向服务器发送位置信息,  request:" + new Gson().toJson(request));
        AppApplication.getServerAPI().closeApply(request)
                .setOnDataGet(response -> {
                    LogUtil.v(TAG, "位置数据上传成功->" + new Gson().toJson(response));

                    String remark = TextUtils.isEmpty(response.result.remark) ? "无" : response.result.remark;
                    switch (response.result.resultCode) {
                        case 0:
                            AlertToast.show("抽检 失败, 原因：未抽检. 备注：" + remark);
                            break;
                        case 1:
                            AlertToast.show("抽检 失败, 原因：抽检中. 备注：" + remark);
                            break;
                        case 2:
                            AlertToast.show("当前车辆抽检完成，谢谢配合");
                            break;
                        case 3:
                            AlertToast.show("当前车辆抽检完成，谢谢配合");
                            break;
                        case 101:
                            AlertToast.show("抽检 失败, 原因：连接已断开. 备注：" + remark);
                            break;
                    }

                    finish();
                })
                .setOnDataError(emsg -> {
                    LogUtil.e(TAG, "位置数据上传失败->" + emsg);
                    AlertToast.show("抽检 失败");
                    finish();
                })
                .call();

        isUploadLocation = true;
    }

    /**
     * 处理连接服务器的返回值
     *
     * @param auth_result
     */
    protected void handleWithConnectServerResult(int auth_result) {
        LogUtil.i(TAG, "服务器连接处理->" + auth_result);
        if (auth_result != 200) {
            AlertToast.show("服务器连接异常");
            finish();
        }
    }

    private AVChatParameters buildAVChatParameters() {
        AVChatParameters avChatParameters = new AVChatParameters();

        avChatParameters.setBoolean(AVChatParameters.KEY_AUDIO_CALL_PROXIMITY, true);
        avChatParameters.setInteger(AVChatParameters.KEY_VIDEO_FIXED_CROP_RATIO, AVChatVideoCropRatio.CROP_RATIO_16_9);
        avChatParameters.setBoolean(AVChatParameters.KEY_SERVER_AUDIO_RECORD, true);
        avChatParameters.setBoolean(AVChatParameters.KEY_SERVER_VIDEO_RECORD, true);
        avChatParameters.setBoolean(AVChatParameters.KEY_VIDEO_DEFAULT_FRONT_CAMERA, false);
        avChatParameters.setInteger(AVChatParameters.KEY_VIDEO_QUALITY, AVChatVideoQuality.QUALITY_HIGH);
        avChatParameters.setBoolean(AVChatParameters.KEY_AUDIO_HIGH_QUALITY, true);
        avChatParameters.setBoolean(AVChatParameters.KEY_AUDIO_DTX_ENABLE, true);
        //采用I420图像格式
        avChatParameters.setInteger(AVChatParameters.KEY_VIDEO_FRAME_FILTER_FORMAT, AVChatImageFormat.I420);

        return avChatParameters;
    }

    private void changeAVChat() {
        // 接听按钮设置为不可点击
        vIncomingCall.setClickable(false);

        View layoutIncomingCall = findViewById(R.id.avchat_layout_incoming_call);
        AlphaAnimation invisibleAnimation = AnimationUtils.buildAlphaAnimation(1.0f, 0.0f, 800);
        layoutIncomingCall.setAnimation(invisibleAnimation);
        invisibleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutIncomingCall.setVisibility(View.GONE);

                View layoutChatting = findViewById(R.id.avchat_layout_chatting);
                AlphaAnimation visibleAnimation = AnimationUtils.buildAlphaAnimation(0.0f, 1.0f, 500);
                layoutChatting.setAnimation(visibleAnimation);
                layoutChatting.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        layoutIncomingCall.startAnimation(invisibleAnimation);
    }

    /**
     * 拒绝来电
     */
    private void hangUp() {
        if (hangUp) {
            return;
        }

        LogUtil.i(TAG, "开始挂断电话...");
        /**
         * 接收方拒绝通话
         * AVChatCallback 回调函数
         */
        AVChatManager.getInstance().hangUp2(avChatData.getChatId(), new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                LogUtil.i(TAG, "挂断远程通话成功");
            }

            @Override
            public void onFailed(int code) {
                LogUtil.i(TAG, "挂断远程通话 onFailed->" + code);
            }

            @Override
            public void onException(Throwable exception) {
                LogUtil.i(TAG, "挂断远程通话 onException" + exception.toString());
            }
        });
        closeRtc();
        AVChatSoundPlayer.instance().stop();

        hangUp = true;
    }

    /**
     * 通知栏
     */
    private void activeCallingNotifier() {
        if (notifier != null && !isUserFinish) {
            LogUtil.i(TAG, "创建通话中notifiter...");
            notifier.activeCallingNotification(true);
        }
    }

    private void cancelCallingNotifier() {
        if (notifier != null) {
            LogUtil.i(TAG, "取消通话中notifiter...");
            notifier.activeCallingNotification(false);
        }
    }

    private void activeMissCallNotifier() {
        if (notifier != null) {
            LogUtil.i(TAG, "创建通话未接听notifiter...");
            notifier.activeMissCallNotification(true);
        }
    }

    @Override
    public void onJoinedChannel(int code, String audioFile, String videoFile, int elapsed) {
        LogUtil.i(TAG, "服务器连接回调->加入服务器结果:" + code + " 指向语音文件:" + audioFile + " 指向视频文件:" + videoFile + " 成功加入房间的耗时:" + elapsed);
        handleWithConnectServerResult(code);
    }

    @Override
    public void onUserJoined(String account) {
        LogUtil.i(TAG, "用户加入频道->" + account);

        if (chatId == 0) {
            channelId = String.valueOf(AVChatManager.getInstance().getCurrentChatId());
        } else {
            channelId = String.valueOf(chatId);
        }

        NotifyLiveConnectedRequest request = new NotifyLiveConnectedRequest();
        request.channelId = channelId == null ? "" : channelId;
        request.checkId = checkId;
        request.checkerId = checkerId;
        request.channelId = channelId == null ? "" : channelId;
        request.recordId = recordId;
        LogUtil.i(TAG, "通话连接建立成功数据上传, request->" + new Gson().toJson(request));

        AppApplication.getServerAPI().avChatNotifyLiveConnected(request)
                .setOnDataGet(response -> {
                    LogUtil.v(TAG, "通话连接建立成功数据上传成功->" + new Gson().toJson(response));
                })
                .setOnDataError(emsg -> {
                    LogUtil.e(TAG, "通话连接建立成功数据上传失败->" + emsg);
                    AlertToast.showAsync("接听失败, 发送连接数据失败:" + emsg);

                    finish();
                })
                .call(AVChatActivity.this);
    }

    @Override
    public void onUserLeave(String account, int event) {
        LogUtil.i(TAG, "用户离开频道->用户ID:" + account + " 退出类型:" + event);
        AlertToast.show("用户离开频道");
        if (isCallEstablished) {
            uploadLocation();
        }
        finish();
    }

    @Override
    public void onLeaveChannel() {
        LogUtil.i(TAG, "退出频道");
        AlertToast.show("退出频道");
        finish();
    }

    @Override
    public void onProtocolIncompatible(int status) {
        LogUtil.e(TAG, "双方协议版本不兼容->" + status);
    }

    @Override
    public void onDisconnectServer(int code) {
        LogUtil.e(TAG, "从服务器断开连接, 当自己断网后超时:" + code);
    }

    @Override
    public void onNetworkQuality(String user, int quality, AVChatNetworkStats stats) {
//        LogUtil.i(TAG,"网络状态发生变化->用户账号:" + user + " 网络状态等级:" + quality + " 网络状态信息:" + new Gson().toJson(stats));
    }

    @Override
    public void onCallEstablished() {
        LogUtil.i(TAG, "会话成功建立");//移除超时监听
        isCallEstablished = true;
        AVChatTimeoutObserver.getInstance().observeTimeoutNotification(timeoutObserver, false, true);
    }

    @Override
    public void onDeviceEvent(int code, String desc) {
//        LogUtil.i(TAG,"语音采集设备和视频采集设备事件通知->事件ID:" + code + " 事件描述:" + desc);
    }

    @Override
    public void onConnectionTypeChanged(int netType) {
        LogUtil.i(TAG, "客户端网络类型发生了变化->当前的网络类型:" + netType);
    }

    @Override
    public void onFirstVideoFrameAvailable(String account) {
        LogUtil.i(TAG, "用户第一帧视频数据绘制前通知->用户账号:" + account);
    }

    @Override
    public void onFirstVideoFrameRendered(String user) {
        LogUtil.i(TAG, "第一帧绘制通知->用户账号:" + user);
    }

    @Override
    public void onVideoFrameResolutionChanged(String user, int width, int height, int rotate) {
//        LogUtil.i(TAG,"用户画面尺寸改变通知->用户账号:" + user + " 视频宽:" + width + " 视频高:" + height + " 视频角度:" + rotate);
    }

    @Override
    public void onVideoFpsReported(String account, int fps) {
//        LogUtil.i(TAG,"用户视频画面fps更新->用户账号:" + account + " 当前绘制帧率:" + fps);
    }

    @Override
    public boolean onVideoFrameFilter(AVChatVideoFrame frame, boolean maybeDualInput) {
        return true;
    }

    @Override
    public boolean onAudioFrameFilter(AVChatAudioFrame frame) {
        return true;
    }

    @Override
    public void onAudioDeviceChanged(int device) {
//        LogUtil.i(TAG,"音频设备变化->" + device);
    }

    @Override
    public void onReportSpeaker(Map<String, Integer> speakers, int mixedEnergy) {
        LogUtil.i(TAG, "汇报正在说话的用户->" + mixedEnergy);
    }

    @Override
    public void onSessionStats(AVChatSessionStats sessionStats) {
//        LogUtil.i(TAG,"实时统计信息->" + new Gson().toJson(sessionStats));
    }

    @Override
    public void onLiveEvent(int event) {
        LogUtil.i(TAG, "互动直播相关事件通知->" + event);
    }
}
