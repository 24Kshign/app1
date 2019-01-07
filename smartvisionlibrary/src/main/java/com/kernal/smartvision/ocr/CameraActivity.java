package com.kernal.smartvision.ocr;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kernal.smartvision.R;
import com.kernal.smartvision.adapter.CameraDocTypeAdapter;
import com.kernal.smartvision.utils.CameraParametersUtils;
import com.kernal.smartvision.utils.CameraSetting;
import com.kernal.smartvision.utils.SharedPreferencesHelper;
import com.kernal.smartvision.utils.WriteToPCTask;
import com.kernal.smartvision.view.FinderView;
import com.kernal.smartvision.view.ViewfinderView;
import com.kernal.smartvisionocr.RecogService;
import com.kernal.smartvisionocr.model.ConfigParamsModel;
import com.kernal.smartvisionocr.model.RecogResultModel;
import com.kernal.smartvisionocr.utils.KernalLSCXMLInformation;
import com.kernal.smartvisionocr.utils.Utils;
import com.maihaoche.commonbiz.service.utils.HintUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.kernal.smartvision.view.ViewfinderView.fieldsPosition;

/**
 * VIN码识别的相机拍摄页面
 */
public class CameraActivity extends Activity implements SurfaceHolder.Callback,
        Camera.PreviewCallback {

    public static final String TAG = CameraActivity.class.getSimpleName();

    public static final String VIN_RESULT = "vin_result";
    public static final String PIC_PATH = "pic_path";

    private int srcWidth, srcHeight, screenWidth, screenHeight;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private List<Integer> srcList = new ArrayList<Integer>();// 拍照分辨率集合
    private DisplayMetrics dm = new DisplayMetrics();
    // private FocusManager mFocusManager;
    private Timer timeAuto;
    private TimerTask timer;
    private int selectedTemplateTypePosition = 0;
    private boolean isTouch = false;
    private Vibrator mVibrator;
    private boolean isToastShow = true;
    private int tempUiRot = 0;
    private boolean isFirstHandCheckField = false;// 是否跳转到点击左侧识别结果字段前 选中的字段位置
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getPhoneSizeAndRotation();

            RemoveView();
            isFirstProgram = true;
            if (msg.what == 5) {
                if (islandscape) {
                    LandscapeView();
                } else {
                    PortraitView();
                }
            } else {
                SetScreenOritation();
                changeData();

                if (camera != null) {
                    rotation = CameraSetting.getInstance(CameraActivity.this)
                            .setCameraDisplayOrientation(uiRot);
                    camera.setDisplayOrientation(rotation);
                }
            }
            AddView();
        }
    };
    private int rotation = 0; // 屏幕取景方向
    private CameraParametersUtils cameraParametersUtils;
    private RelativeLayout.LayoutParams layoutParams;
    public ViewfinderView myViewfinderView;
    private ImageView iv_camera_back, iv_camera_flash;
    private FinderView mFinderView;
    private boolean mIsDragging = false;
    private KernalLSCXMLInformation wlci_Landscape, wlci_Portrait, wlci;
    private RelativeLayout re;
    private CameraDocTypeAdapter adapter;
    private int[] regionPos = new int[4];// 敏感区域
    private boolean isFirstProgram = true;
    private Camera.Size size;
    private int[] nCharCount = new int[2];// 返回结果中的字符数
    Handler handler2 = new Handler();
    Runnable touchTimeOut = new Runnable() {
        @Override
        public void run() {
            isTouch = false;
        }
    };
    private byte[] data;
    private boolean isTakePic = false;
    private ImageButton imbtn_takepic;
    public static List<RecogResultModel> recogResultModelList = new ArrayList<RecogResultModel>();
    private RecogThread recogThread;
    private boolean isOnClickRecogFields = false;// 点击识别结果分项，
    private int tempPosition = -1;// -1是初始化的值；-2是识别完成后再点击识别不对的项，隐藏确定按钮，识别被选择的项
    private ArrayList<String> list_recogSult;
    private int returnResult = -1;// 测试返回值
    public RecogService.MyBinder recogBinder;
    private int iTH_InitSmartVisionSDK = -1;
    private String SavePicPath;// 图片路径
    private ArrayList<String> savePath;// 图片路径的集合
    private String Imagepath;//点击拍照按钮   保存的完整图像的路径
    private int position;// 记录当前识别字段的下标
    private boolean isClick = false;// 是否点击了识别结果按钮
    private boolean isFirstIn = true;
    private boolean islandscape = true;// 是否为横向
    private RecogResultModel recogResultModel;
    private Bitmap bitmap;
    public ServiceConnection recogConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            recogConn = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            recogBinder = (RecogService.MyBinder) service;
            iTH_InitSmartVisionSDK = recogBinder.getInitSmartVisionOcrSDK();
            if (iTH_InitSmartVisionSDK == 0) {
                recogBinder.AddTemplateFile();// 添加识别模板
                recogBinder
                        .SetCurrentTemplate(wlci.fieldType
                                .get(wlci.template
                                        .get(selectedTemplateTypePosition).templateType)
                                .get(fieldsPosition).ocrId);// 设置当前识别模板ID
            } else {
                Toast.makeText(CameraActivity.this,
                        "核心初始化失败，错误码：" + iTH_InitSmartVisionSDK,
                        Toast.LENGTH_LONG).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera_2);
        Utils.copyFile(this);// 写入本地文件
        // 已写入的情况下，根据version.txt中版本号判断是否需要更新，如不需要不执行写入操作
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        cameraParametersUtils = new CameraParametersUtils(this);
        uiRot = getWindowManager().getDefaultDisplay().getRotation();// 获取屏幕旋转的角度
        getPhoneSizeAndRotation();
        ParseXml();
        tempPosition = -1;
        findView();
        ClickEvent();
        initData();
        SetScreenOritation();
        AddView();

        if (recogBinder == null) {
            Intent authIntent = new Intent(CameraActivity.this,
                    RecogService.class);
            bindService(authIntent, recogConn, Service.BIND_AUTO_CREATE);
        }
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(CameraActivity.this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    // 获取设备分辨率 不受虚拟按键影响
    public void getPhoneSizeAndRotation() {

        cameraParametersUtils.setScreenSize(CameraActivity.this);
        srcWidth = cameraParametersUtils.srcWidth;
        srcHeight = cameraParametersUtils.srcHeight;
        getScreenSize();
    }

    // 解析横竖屏下 两个XML文件内容，赋予不同的对象
    private void ParseXml() {
        // 横屏模板解析
        wlci_Landscape = Utils.parseXmlFile(this,
                "appTemplateConfig.xml",
                true);
        if (wlci_Landscape.template == null
                || wlci_Landscape.template.size() == 0) {
            // 如果用户不选择任何模板类型，我们会强制选择第一个模板类型
            wlci_Landscape = Utils.parseXmlFile(this,
                    "appTemplateConfig.xml",
                    false);
            wlci_Landscape.template.get(0).isSelected = true;
            Utils.updateXml(this,wlci_Landscape, "appTemplateConfig.xml");
            wlci_Landscape = Utils.parseXmlFile(this,
                    "appTemplateConfig.xml",
                    true);
        }
        // 竖屏模板解析
        wlci_Portrait = Utils
                .parseXmlFile(this,
                        "appTemplatePortraitConfig.xml",
                        true);
        if (wlci_Portrait.template == null
                || wlci_Portrait.template.size() == 0) {
            // 如果用户不选择任何模板类型，我们会强制选择第一个模板类型
            wlci_Portrait = Utils
                    .parseXmlFile(this,
                            "appTemplatePortraitConfig.xml",
                            false);
            wlci_Portrait.template.get(0).isSelected = true;
            Utils.updateXml(this,wlci_Portrait, "appTemplatePortraitConfig.xml");
            wlci_Portrait = Utils
                    .parseXmlFile(this,
                            "appTemplatePortraitConfig.xml",
                            true);
        }
        if (uiRot == 0 || uiRot == 2) // 竖屏状态下
        {
            islandscape = false;
            wlci = wlci_Portrait;
        } else { // 横屏状态下
            islandscape = true;
            wlci = wlci_Landscape;
        }
    }

    // 设置横竖屏状态和变换数据对象
    private void SetScreenOritation() {

        if (uiRot == 0 || uiRot == 2) // 竖屏状态下
        {
            islandscape = false;
            wlci = wlci_Portrait;
            PortraitView();
        } else { // 横屏状态下
            islandscape = true;
            wlci = wlci_Landscape;
            LandscapeView();
        }

    }

    private void initData() {
        // 初始化数据
        if (islandscape) {
            adapter = new CameraDocTypeAdapter(this, wlci.template,
                    screenWidth, screenWidth);
        } else {
            adapter = new CameraDocTypeAdapter(this, wlci.template,
                    screenWidth, screenHeight);
        }
        adapter.selectedPosition = selectedTemplateTypePosition;
        recogResultModelList.clear();

        for (int i = 0; i < wlci.fieldType.get(
                wlci.template.get(selectedTemplateTypePosition).templateType)
                .size(); i++) {
            recogResultModel = new RecogResultModel();
            recogResultModel.resultValue = wlci.fieldType
                    .get(wlci.template.get(selectedTemplateTypePosition).templateType)
                    .get(i).name
                    + ":";
            recogResultModel.type = wlci.fieldType
                    .get(wlci.template.get(selectedTemplateTypePosition).templateType)
                    .get(i).type;

            recogResultModelList.add(recogResultModel);
        }
        savePath = new ArrayList<String>();
    }

    public void changeData() {
        if (islandscape) {
            adapter = new CameraDocTypeAdapter(this, wlci.template,
                    screenWidth, screenWidth);
        } else {
            adapter = new CameraDocTypeAdapter(this, wlci.template,
                    screenWidth, screenHeight);
        }
        adapter.selectedPosition = selectedTemplateTypePosition;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isTouch = false;
        list_recogSult = new ArrayList<String>();
        isTakePic = false;
        isFirstProgram = true;
    }

    /**
     * @return ${return_type} 返回类型
     * @throws
     * @Title: 相机界面
     * @Description: 动态布局相机界面的控件
     */

    private void findView() {
        re = (RelativeLayout) findViewById(R.id.container_rl);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview_camera);
        imbtn_takepic = (ImageButton) findViewById(R.id.imbtn_takepic);
        iv_camera_back = (ImageView) findViewById(R.id.back_iv);
        iv_camera_flash = (ImageView) findViewById(R.id.flash_iv);
        mFinderView = (FinderView) findViewById(R.id.focus_layout_fv);
        mFinderView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFinderView.setOnTouchListener(mOnTouchListener);
            }
        },2000);//延迟2s。等待硬件初始化
    }

    /**
     * 设置识别区可以拖动，来避开打开闪光灯时的光斑进行识别
     */
    private int lastX,lastY;
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(recogBinder==null || size==null){
                return true;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;

                    int left = v.getLeft() + dx;
                    int top = v.getTop() + dy;
                    int right = v.getRight() + dx;
                    int bottom = v.getBottom() + dy;

                    // 设置不能出界
                    if (left < 0) {
                        left = 0;
                        right = left + v.getWidth();
                    }

                    if (right > screenWidth) {
                        right = screenWidth;
                        left = right - v.getWidth();
                    }

                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }

                    if (bottom > screenHeight) {
                        bottom = screenHeight;
                        top = bottom - v.getHeight();
                    }
                    v.layout(left, top, right, bottom);

                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();

                    break;
                case MotionEvent.ACTION_UP:
                    int[] pos = new int[]{v.getLeft(),v.getTop(),v.getLeft()+v.getWidth(),v.getTop()+v.getHeight()};
                    recogBinder.SetROI(pos, size.width, size.height);
                    break;
            }
            return true;
        }
    };

    //
    // 获取屏幕尺寸 会受到虚拟按键影响而改变值
    public void getScreenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        Log.v(TAG, "屏幕高：" + screenHeight + " 宽： " + screenWidth);
    }

    public void LandscapeView() {
        // 重新编辑扫描框
        if (srcWidth == screenWidth) {

            layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, srcHeight);
            surfaceView.setLayoutParams(layoutParams);

        } else if (srcWidth > screenWidth) {
            // 如果将虚拟硬件弹出则执行如下布局代码，相机预览分辨率不变压缩屏幕的高度
            int surfaceViewHeight = (screenWidth * srcHeight) / srcWidth;
            layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.FILL_PARENT, surfaceViewHeight);
            layoutParams.topMargin = (srcHeight - surfaceViewHeight) / 2;
            surfaceView.setLayoutParams(layoutParams);
        }

    }

    public void PortraitView() {

        if (srcHeight == screenHeight) {
            layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.FILL_PARENT, srcHeight);
            surfaceView.setLayoutParams(layoutParams);

        } else if (srcHeight > screenHeight) {
            // 如果将虚拟硬件弹出则执行如下布局代码，相机预览分辨率不变压缩屏幕的宽度
            int surfaceViewWidth = (screenHeight * srcWidth) / srcHeight;
            layoutParams = new RelativeLayout.LayoutParams(surfaceViewWidth,
                    RelativeLayout.LayoutParams.FILL_PARENT);
            layoutParams.leftMargin = (srcWidth - surfaceViewWidth) / 2;
            surfaceView.setLayoutParams(layoutParams);
        }

    }

    public void ClickEvent() {

        iv_camera_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        iv_camera_flash.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (SharedPreferencesHelper.getBoolean(CameraActivity.this,
                        "isOpenFlash", false)) {
                    iv_camera_flash.setImageResource(getResources()
                            .getIdentifier("flash_on", "drawable",
                                    getPackageName()));
                    SharedPreferencesHelper.putBoolean(CameraActivity.this,
                            "isOpenFlash", false);
                    CameraSetting.getInstance(CameraActivity.this)
                            .closedCameraFlash(camera);
                } else {
                    SharedPreferencesHelper.putBoolean(CameraActivity.this,
                            "isOpenFlash", true);
                    iv_camera_flash.setImageResource(getResources()
                            .getIdentifier("flash_off", "drawable",
                                    getPackageName()));
                    CameraSetting.getInstance(CameraActivity.this)
                            .openCameraFlash(camera);
                    HintUtil.getInstance().playAudioOrVibrator(HintUtil.TYPE.HINT_FLASH_TURN_ON);
                }
            }
        });
        imbtn_takepic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                isTakePic = true;
                isFirstProgram = true;
            }
        });

    }

    public void RemoveView() {
//        if (myViewfinderView != null) {
//            myViewfinderView.destroyDrawingCache();
//            re.removeView(myViewfinderView);
//            myViewfinderView = null;
//        }
    }

    public void AddView() {
//        if (islandscape) {
//            myViewfinderView = new ViewfinderView(
//                    CameraActivity.this,
//                    wlci,
//                    wlci.template.get(selectedTemplateTypePosition).templateType,
//                    true);
//
//        } else {
//            myViewfinderView = new ViewfinderView(
//                    CameraActivity.this,
//                    wlci,
//                    wlci.template.get(selectedTemplateTypePosition).templateType,
//                    false);
//        }
//        re.addView(myViewfinderView,1);
        if (islandscape) {
            List<ConfigParamsModel> configParamsModel = wlci.fieldType.get(wlci.template.get(selectedTemplateTypePosition).templateType);
            Rect frame = new Rect((int) (configParamsModel.get(fieldsPosition).leftPointX * screenWidth), (int) (screenHeight * configParamsModel.get(fieldsPosition).leftPointY), (int) ((configParamsModel.get(fieldsPosition).leftPointX + configParamsModel.get(fieldsPosition).width) * screenWidth), (int) (screenHeight * (configParamsModel.get(fieldsPosition).leftPointY + configParamsModel.get(fieldsPosition).height)));

            int width = frame.width();
            int height = frame.height();
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            layoutParams.leftMargin = frame.left;
            layoutParams.topMargin = frame.top;
            mFinderView.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        if (camera != null) {
            if (islandscape) {
                CameraSetting.getInstance(CameraActivity.this)
                        .setCameraParameters(CameraActivity.this,
                                surfaceHolder, CameraActivity.this, camera,
                                (float) srcWidth / srcHeight, srcList, false,
                                rotation);

            } else {
                CameraSetting.getInstance(CameraActivity.this)
                        .setCameraParameters(CameraActivity.this,
                                surfaceHolder, CameraActivity.this, camera,
                                (float) srcHeight / srcWidth, srcList, false,
                                rotation);

            }

        }
        if (SharedPreferencesHelper.getBoolean(CameraActivity.this,
                "isOpenFlash", false)) {
            iv_camera_flash.setImageResource(getResources().getIdentifier(
                    "flash_off", "drawable", getPackageName()));
            CameraSetting.getInstance(this).openCameraFlash(camera);
        } else {
            iv_camera_flash.setImageResource(getResources().getIdentifier(
                    "flash_on", "drawable", getPackageName()));
            CameraSetting.getInstance(this).closedCameraFlash(camera);
        }
        recogThread = new RecogThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    // 小米PAD 解锁屏时执行surfaceChanged surfaceCreated，容易出现超时卡死现象，故在此处打开相机和设置参数
    @Override
    protected void onResume() {
        super.onResume();
        OpenCameraAndSetParameters();

    }

    int uiRot;

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        // 实时监听屏幕旋转角度

        uiRot = getWindowManager().getDefaultDisplay().getRotation();// 获取屏幕旋转的角度
        if (uiRot != tempUiRot) {
            Message mesg = new Message();
            mesg.what = uiRot;
            handler.sendMessage(mesg);
            tempUiRot = uiRot;

        }
        if (isTouch) {
            return;

        }
        data = bytes;
        if (iTH_InitSmartVisionSDK == 0) {
            synchronized (recogThread) {
                recogThread.run();
            }
        }
    }

    @Override
    protected void onDestroy() {
//        if (myViewfinderView != null) {
//            re.removeView(myViewfinderView);
//            myViewfinderView.destroyDrawingCache();
//            fieldsPosition = 0;
//
//            myViewfinderView = null;
//        }
        if (recogBinder != null) {
            unbindService(recogConn);
            recogBinder = null;
        }
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        if (handler2 != null)
            handler2.removeCallbacks(touchTimeOut);
        super.onDestroy();

    }

    @Override
    public void onPause() {
        super.onPause();
        CloseCameraAndStopTimer();
    }

    private class MyOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    isTouch = true;
                    break;
                case MotionEvent.ACTION_DOWN:
                    handler2.removeCallbacks(touchTimeOut);
                    isTouch = true;
                    break;
                case MotionEvent.ACTION_OUTSIDE:
                    isTouch = false;
                    break;
                case MotionEvent.ACTION_UP:
                    synchronized (event) {
                        handler2.postDelayed(touchTimeOut, 1500);

                    }
                    break;
            }
            return false;
        }
    }

    /**
     * @param
     * @return void 返回类型
     * leftPointX 扫描框左坐标系数      leftPointY  纵坐标系数
     * @Title: setRegion 计算识别区域
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public void setRegion() {
        if (islandscape) {
            regionPos[0] = (int) (wlci.fieldType
                    .get(wlci.template.get(selectedTemplateTypePosition).templateType)
                    .get(fieldsPosition).leftPointX * size.width);
            regionPos[1] = (int) (wlci.fieldType
                    .get(wlci.template.get(selectedTemplateTypePosition).templateType)
                    .get(fieldsPosition).leftPointY * size.height);
            regionPos[2] = (int) ((wlci.fieldType
                    .get(wlci.template.get(selectedTemplateTypePosition).templateType)
                    .get(fieldsPosition).leftPointX
                    + wlci.fieldType
                    .get(wlci.template.get(selectedTemplateTypePosition).templateType)
                    .get(fieldsPosition).width) * size.width);
            regionPos[3] = (int) ((wlci.fieldType
                    .get(wlci.template.get(selectedTemplateTypePosition).templateType)
                    .get(fieldsPosition).leftPointY
                    + wlci.fieldType
                    .get(wlci.template.get(selectedTemplateTypePosition).templateType)
                    .get(fieldsPosition).height) * size.height);
        } else {
            regionPos[0] = (int) (wlci.fieldType
                    .get(wlci.template.get(selectedTemplateTypePosition).templateType)
                    .get(fieldsPosition).leftPointX * size.height);
            regionPos[1] = (int) (wlci.fieldType
                    .get(wlci.template.get(selectedTemplateTypePosition).templateType)
                    .get(fieldsPosition).leftPointY * size.width);
            regionPos[2] = (int) ((wlci.fieldType
                    .get(wlci.template.get(selectedTemplateTypePosition).templateType)
                    .get(fieldsPosition).leftPointX
                    + wlci.fieldType
                    .get(wlci.template.get(selectedTemplateTypePosition).templateType)
                    .get(fieldsPosition).width) * size.height);
            regionPos[3] = (int) ((wlci.fieldType
                    .get(wlci.template.get(selectedTemplateTypePosition).templateType)
                    .get(fieldsPosition).leftPointY
                    + wlci.fieldType
                    .get(wlci.template.get(selectedTemplateTypePosition).templateType)
                    .get(fieldsPosition).height) * size.width);
        }


    }

    class RecogThread extends Thread {

        public long time;

        public RecogThread() {
            time = 0;
        }

        @Override
        public void run() {
            if (mIsDragging) return;

            time = System.currentTimeMillis();
            // 第一次加载布局 计算识别区域 也用于识别模板切换时调用
            if (isFirstProgram) {
                size = camera.getParameters().getPreviewSize();
                setRegion();
                recogBinder
                        .SetCurrentTemplate(wlci.fieldType
                                .get(wlci.template
                                        .get(selectedTemplateTypePosition).templateType)
                                .get(fieldsPosition).ocrId);
                position = fieldsPosition;
                Log.e(TAG, "setROI " + regionPos[0] + " " + regionPos[1] + " " + regionPos[2] + " " + regionPos[3] + " " + size.width + " " + size.height);
                recogBinder.SetROI(regionPos, size.width, size.height);
                isFirstProgram = false;
            }
            // 点击拍照按钮 保存图片，强制跳过未自动识别条目

            if (isTakePic) {
                Imagepath = savePicture(data);
                if (Imagepath != null && !"".equals(Imagepath)) {
                    // 根据图片路径 加载图片
                    recogBinder.LoadImageFile(Imagepath);
                    Imagepath = null;
                }
            } else {
//				 加载视频流数据源
                if (rotation == 90) {

                    recogBinder
                            .LoadStreamNV21(data, size.width, size.height, 1);
                } else if (rotation == 0) {
                    recogBinder
                            .LoadStreamNV21(data, size.width, size.height, 0);
                } else if (rotation == 180) {
                    recogBinder
                            .LoadStreamNV21(data, size.width, size.height, 2);
                } else {
                    recogBinder
                            .LoadStreamNV21(data, size.width, size.height, 3);
                }
            }

            // 开始识别
            returnResult = recogBinder
                    .Recognize(
                            Devcode.devcode,
                            wlci.fieldType
                                    .get(wlci.template
                                            .get(selectedTemplateTypePosition).templateType)
                                    .get(fieldsPosition).ocrId);

            if (returnResult == 0) {

                // 获取识别结果
                String recogResultString = recogBinder.GetResults(nCharCount);

                time = System.currentTimeMillis() - time;
                if ((recogResultString != null && !recogResultString.equals("") && nCharCount[0] > 0)
                        || isTakePic) {
                    if (fieldsPosition < wlci.fieldType
                            .get(wlci.template
                                    .get(selectedTemplateTypePosition).templateType)
                            .size()
                            && (CameraActivity.recogResultModelList
                            .get(fieldsPosition).resultValue
                            .split(":").length == 1 || fieldsPosition == 0)
                            || isOnClickRecogFields) {
                        if ((recogResultString != null && !recogResultString
                                .equals(""))) {
                            // 有识别结果时，保存识别核心裁切到的图像
                            SavePicPath = saveROIPicture(data, false);
                        } else {
                            //未识别到结果，此时保存识别区域ROI内的图像
                            recogResultString = " ";

                            SavePicPath = saveROIPicture(data, true);
                        }
                        if (SavePicPath != null && !"".equals(SavePicPath)) {
                            if (SharedPreferencesHelper.getBoolean(
                                    getApplicationContext(), "upload",
                                    false)) {
                                new WriteToPCTask(CameraActivity.this)
                                        .execute(SavePicPath);
                            }
                        }

                        // 将识别结果 赋值到识别结果集合中 以便完成识别后跳转传输
                        CameraActivity.recogResultModelList
                                .get(fieldsPosition).resultValue = wlci.fieldType
                                .get(wlci.template
                                        .get(selectedTemplateTypePosition).templateType)
                                .get(fieldsPosition).name
                                + ":" + recogResultString;
//                        // 识别完某字段后，跳转到下一字段识别
//                        if (myViewfinderView.configParamsModel.size() > (fieldsPosition + 1)) {
//                            if (isFirstHandCheckField) {
//                                if (tempPosition != -2 && tempPosition != -1) {
//                                    fieldsPosition = tempPosition;
//                                    isFirstHandCheckField = false;
//                                }
//
//                            } else {
//                                fieldsPosition = fieldsPosition + 1;
//                            }
//
//                            isFirstProgram = true;
//                            if (tempPosition == -1 && isOnClickRecogFields) {
//                                myViewfinderView.setVisibility(View.GONE);
//                                imbtn_takepic.setVisibility(View.GONE);
//
//                            } else if (tempPosition == -2
//                                    && isOnClickRecogFields) {
//                                myViewfinderView.setVisibility(View.GONE);
//                                imbtn_takepic.setVisibility(View.GONE);
//                            }
//                        } // 所有识别字段识别完成
//                        else if (myViewfinderView.configParamsModel.size() <= (fieldsPosition + 1)) {
//                            myViewfinderView.setVisibility(View.GONE);
//                            imbtn_takepic.setVisibility(View.GONE);
//                            tempPosition = -2;
//                        }
                        isTakePic = false;
                    }

                    if (isClick) {
                        savePath.remove(position);
                        isClick = false;
                    }

                    savePath.add(position, SavePicPath);

                    mVibrator = (Vibrator) getApplication().getSystemService(
                            Service.VIBRATOR_SERVICE);
                    mVibrator.vibrate(200);
                    // 如果识别条目只有一个 则直接跳转到结果显示界面 否则显示确定按钮 点击之后才进行跳转
                    if (recogResultModelList.size() == 1) {

                        CloseCameraAndStopTimer();
                        list_recogSult.clear();

                        list_recogSult
                                .add(recogResultModelList.get(0).resultValue);

                        Intent intent = new Intent(CameraActivity.this,
                                ShowResultActivity.class);
                        intent.putExtra(VIN_RESULT,
                                recogResultModelList.get(0).resultValue);
                        intent.putExtra(PIC_PATH, SavePicPath);


                        startActivity(intent);
                        overridePendingTransition(
                                getResources().getIdentifier("zoom_enter",
                                        "anim",
                                        getApplication().getPackageName()),
                                getResources().getIdentifier("push_down_out",
                                        "anim",
                                        getApplication().getPackageName()));
                        CameraActivity.this.finish();

                    }
                }

                // 测试 存储每一帧图像
                // savePicture(data, recogResultString);

            } else {

                if (isToastShow) {
                    Toast.makeText(getApplicationContext(),
                            "识别错误，错误码：" + returnResult, Toast.LENGTH_LONG)
                            .show();
                    isToastShow = false;

                }

            }

        }

    }

    public String saveROIPicture(byte[] data1, boolean bol) {
        // huangzhen测试
        String picPathString1 = "";
        if (!wlci.fieldType.get(
                wlci.template.get(selectedTemplateTypePosition).templateType)
                .get(fieldsPosition).ocrId
                .equals("SV_ID_BARCODE_QR")) {
            String PATH = Environment.getExternalStorageDirectory().toString()
                    + "/DCIM/VIN_OCR/";
            File file = new File(PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            String name = Utils.pictureName();
            picPathString1 = PATH + "mhc" + name;
            if (bol) {
                // 识别区域内图片
                recogBinder.svSaveImage(picPathString1);
            } else {
                // 识别成功剪切的图片
                recogBinder.svSaveImageResLine(picPathString1);
            }
        }
        return picPathString1;
    }

    public String savePicture(byte[] data1) {


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.inPurgeable = true;
        options.inInputShareable = true;
        YuvImage yuvimage = new YuvImage(data1, ImageFormat.NV21,
                size.width, size.height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, size.width, size.height), 100,
                baos);
        bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(),
                0, baos.size(), options);
        Matrix matrix = new Matrix();
        matrix.reset();
        if (rotation == 90) {
            matrix.setRotate(90);
        } else if (rotation == 180) {
            matrix.setRotate(180);
        } else if (rotation == 270) {
            matrix.setRotate(270);
        }
        bitmap = Bitmap
                .createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
        String picPathString2 = "";
        String PATH = Environment.getExternalStorageDirectory().toString()
                + "/DCIM/Camera/";
        String name = Utils.pictureName();
        picPathString2 = PATH + "smartVisitionComplete" + name + ".jpg";
        File file = new File(picPathString2);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return picPathString2;
    }

    public void autoFocus() {

        if (camera != null) {

            try {
                if (camera.getParameters().getSupportedFocusModes() != null
                        && camera.getParameters().getSupportedFocusModes()
                        .contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    camera.autoFocus(new Camera.AutoFocusCallback() {
                        public void onAutoFocus(boolean success, Camera camera) {
                            if (success) {

                            }

                        }
                    });
                } else {

                    Toast.makeText(
                            getBaseContext(),
                            getString(getResources().getIdentifier(
                                    "unsupport_auto_focus", "string",
                                    getPackageName())), Toast.LENGTH_LONG)
                            .show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                camera.stopPreview();
                camera.startPreview();
                Toast.makeText(
                        this,
                        getResources().getIdentifier("toast_autofocus_failure",
                                "string", getPackageName()), Toast.LENGTH_SHORT)
                        .show();

            }
        }
    }

    public void OpenCameraAndSetParameters() {
        try {
            if (null == camera) {
                camera = CameraSetting.getInstance(CameraActivity.this).open(0,
                        camera);
                rotation = CameraSetting.getInstance(CameraActivity.this)
                        .setCameraDisplayOrientation(uiRot);
                if (!isFirstIn) {
                    if (islandscape) {
                        CameraSetting.getInstance(CameraActivity.this)
                                .setCameraParameters(CameraActivity.this,
                                        surfaceHolder, CameraActivity.this,
                                        camera, (float) srcWidth / srcHeight,
                                        srcList, false, rotation);
                    } else {
                        CameraSetting.getInstance(CameraActivity.this)
                                .setCameraParameters(CameraActivity.this,
                                        surfaceHolder, CameraActivity.this,
                                        camera, (float) srcHeight / srcWidth,
                                        srcList, false, rotation);

                    }
                }
                isFirstIn = false;
                if (timer == null) {
                    timer = new TimerTask() {
                        public void run() {
                            if (camera != null) {
                                try {
                                    autoFocus();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    };
                }
                if (timeAuto == null) {
                    timeAuto = new Timer();
                }
                timeAuto.schedule(timer, 200, 2500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CloseCameraAndStopTimer() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timeAuto != null) {
            timeAuto.cancel();
            timeAuto = null;
        }
        if (camera != null) {
            camera = CameraSetting.getInstance(CameraActivity.this)
                    .closeCamera(camera);
        }
    }
}
