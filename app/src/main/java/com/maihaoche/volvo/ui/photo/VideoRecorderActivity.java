/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.maihaoche.volvo.ui.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.maihaoche.base.log.LogUtil;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.ui.common.daomain.VideoEvent;
import com.maihaoche.volvo.view.videoView.CameraPreview;
import com.maihaoche.volvo.view.videoView.RecordedButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 由于Camera在SDK 21的版本被标为Deprecated,建议使用新的Camera2类来实现
 * 但是由于Camera2这个类要求minSDK大于21,所以依旧使用Camera这个类进行实现
 */
public class VideoRecorderActivity extends AppCompatActivity {
    private static final int FOCUS_AREA_SIZE = 500;
    private static final int MAX_TIME = 60_000;
    private static final int TIME_SLOT = 100;

    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mMediaRecorder;
    private String mUrlFile;

    private TextView mHintTv;

    private View mRecordLayout;
    private ImageView mCancelBtn;
    private RecordedButton mStartRBtn;

    private View mEditLayout;
    private ImageView mDeleteBtn;
    private ImageView mFinishBtn;

    private LinearLayout mPreviewArea;

    private boolean isRecording = false;
    private boolean isCameraPrepared = false;
    private long mStartTime;

    // 用于监听屏幕方向
    private OrientationEventListener mOrientationListener;
    private int mOrientation = 0;

    private Handler mRecorderHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == -1) {
                stop();
            } else if (msg.what == MAX_TIME) {
                this.sendEmptyMessage(-1);
                Toast.makeText(VideoRecorderActivity.this, "已达录制时间上限", Toast.LENGTH_SHORT).show();
            } else {
                this.sendEmptyMessageDelayed(msg.what + TIME_SLOT, TIME_SLOT);
                mStartRBtn.setProgress(msg.what);
                if (msg.what < 10_000) {
                    mHintTv.setText(String.format("00:0%s", String.valueOf(msg.what / 1000)));
                } else {
                    mHintTv.setText(String.format("00:%s", String.valueOf(msg.what / 1000)));
                }
            }
        }
    };

    public static void startActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, VideoRecorderActivity.class);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_activity_video);
        bindView();
        initialize();
    }

    private void bindView() {
        mPreviewArea = (LinearLayout) findViewById(R.id.camera_preview);
        mHintTv = (TextView) findViewById(R.id.tv_hint);
        mRecordLayout = findViewById(R.id.layout_record);
        mCancelBtn = (ImageView) findViewById(R.id.btn_cancel);
        mStartRBtn = (RecordedButton) findViewById(R.id.rb_start);
        mEditLayout = findViewById(R.id.layout_edit);
        mDeleteBtn = (ImageView) findViewById(R.id.btn_delete);
        mFinishBtn = (ImageView) findViewById(R.id.btn_finish);
        mStartRBtn.setMax(MAX_TIME);

        mStartRBtn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (isCameraPrepared) {
                        mStartTime = System.currentTimeMillis();
                        start();
                        mRecorderHandler.sendEmptyMessageDelayed(0, TIME_SLOT);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mRecorderHandler.removeCallbacksAndMessages(null);
                    if (isRecording) {
                        long time = System.currentTimeMillis() - mStartTime;
                        if (time < 1000) {
                            mRecorderHandler.sendEmptyMessageDelayed(-1, 1000 - time);
                        } else {
                            mRecorderHandler.sendEmptyMessage(-1);
                        }
                    }
                    break;
                default:
            }
            return true;
        });
        mFinishBtn.setOnClickListener(v -> {
            VideoEvent event = new VideoEvent(mUrlFile, getVideoThumb(mUrlFile));
            RxBus.getDefault().post(event);
            finish();
        });
        mDeleteBtn.setOnClickListener(v -> {
            mStartRBtn.setProgress(0);
            mRecorderHandler.removeCallbacksAndMessages(null);
            mEditLayout.setVisibility(View.GONE);
            mRecordLayout.setVisibility(View.VISIBLE);
            mHintTv.setText("长按摄像");
            prepareCamera();
        });
        mCancelBtn.setOnClickListener(v -> finish());
    }

    /**
     * 点击对焦
     */
    public void initialize() {
        mPreview = new CameraPreview(VideoRecorderActivity.this, mCamera);
        mPreviewArea.addView(mPreview);
        mPreviewArea.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    focusOnTouch(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        });

        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                mOrientation = orientation;
            }
        };
        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareCamera();
    }

    private void prepareCamera() {
        if (!hasCamera(getApplicationContext())) {
            //这台设备没有发现摄像头
            Toast.makeText(getApplicationContext(), "摄像头不可用", Toast.LENGTH_SHORT).show();
            releaseMediaRecorder();
            releaseCamera();
            finish();
        }
        if (mCamera == null) {
            int cameraId = findBackFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "找不到摄像头", Toast.LENGTH_SHORT).show();
            } else {
                mCamera = Camera.open(cameraId);
                mPreview.refreshCamera(mCamera);
            }
        }
        isCameraPrepared = true;
    }

    /**
     * 检查设备是否有摄像头
     */
    private boolean hasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * 找后置摄像头,没有则返回-1
     *
     * @return cameraId
     */
    private int findBackFacingCamera() {
        int cameraId = -1;
        //获取摄像头个数
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void start() {
        if (isCameraPrepared) {
            mStartRBtn.openButton();
            isRecording = true;
            //准备开始录制视频
            if (!prepareMediaRecorder()) {
                Toast.makeText(VideoRecorderActivity.this, "录制准备失败", Toast.LENGTH_SHORT).show();
                releaseMediaRecorder();
                releaseCamera();
                finish();
            }
            //开始录制视频
            runOnUiThread(() -> {
                // If there are stories, add them to the table
                try {
                    mMediaRecorder.start();
                } catch (final Exception ex) {
                    Log.i("---", "Exception in thread");
                    releaseMediaRecorder();
                    releaseCamera();
                    finish();
                }
            });
        }
    }

    private boolean prepareMediaRecorder() {
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        if (mOrientation < 315 && mOrientation >= 225) {
            mMediaRecorder.setOrientationHint(0);
        } else if (mOrientation < 45 || mOrientation >= 315) {
            mMediaRecorder.setOrientationHint(90);
        } else if (mOrientation < 135 && mOrientation >= 45) {
            mMediaRecorder.setOrientationHint(180);
        } else if (mOrientation < 225 && mOrientation >= 135) {
            mMediaRecorder.setOrientationHint(270);
        }
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
        // 720P的视频设置码率3500k也就够了
        mMediaRecorder.setVideoEncodingBitRate(3_500_000);
        File file = new File(Environment.getExternalStorageDirectory(), "maihaoche");
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (mkdirs) {
                LogUtil.e("file: ");
            }
        }
        File tempFile = new File(file, "mhc_temp.mp4");
        if (tempFile.exists()) {
            boolean delete = tempFile.delete();
            if (delete) {
                LogUtil.e("file: " + tempFile.getAbsolutePath() + " delete!");
            }
        }
        mUrlFile = tempFile.getAbsolutePath();
        mMediaRecorder.setOutputFile(mUrlFile);
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    private void stop() {
        if (isRecording) {
            mStartRBtn.closeButton();
            isRecording = false;
            //如果正在录制点击这个按钮表示录制完成
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setOnInfoListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop(); //停止
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mEditLayout.setVisibility(View.VISIBLE);
            mRecordLayout.setVisibility(View.GONE);
            releaseMediaRecorder();
            releaseCamera();
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            isCameraPrepared = false;
        }
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            if (mCamera != null) {
                mCamera.lock();
            }
        }
    }

    private void focusOnTouch(MotionEvent event) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getMaxNumMeteringAreas() > 0) {
                Rect rect = calculateFocusArea(event.getX(), event.getY());
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                List<Camera.Area> meteringAreas = new ArrayList<>();
                meteringAreas.add(new Camera.Area(rect, 800));
                parameters.setFocusAreas(meteringAreas);
                mCamera.setParameters(parameters);
            }
            mCamera.autoFocus((success, camera) -> {
                if (success) {
                    // do something...
                    Log.i("tap_to_focus", "success!");
                } else {
                    // do something...
                    Log.i("tap_to_focus", "fail!");
                }
            });
        }
    }

    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / mPreview.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / mPreview.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
            if (touchCoordinateInCameraReper > 0) {
                result = 1000 - focusAreaSize / 2;
            } else {
                result = -1000 + focusAreaSize / 2;
            }
        } else {
            result = touchCoordinateInCameraReper - focusAreaSize / 2;
        }
        return result;
    }

    /**
     * 获取视频文件截图
     *
     * @param path 视频文件的路径
     * @return Bitmap 返回获取的Bitmap
     */
    public Bitmap getVideoThumb(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return media.getFrameAtTime();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecorderHandler.removeCallbacksAndMessages(null);
        mOrientationListener.disable();
    }
}