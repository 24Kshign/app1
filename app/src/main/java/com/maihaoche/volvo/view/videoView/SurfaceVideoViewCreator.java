package com.maihaoche.volvo.view.videoView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.maihaoche.volvo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>
 * author: LinGuanHong,lzq is my dear wife.
 * <p>
 * My GitHub : https://github.com/af913337456/
 * <p>
 * My Blog   : http://www.cnblogs.com/linguanh/
 * <p>
 * on 2017/4/26.
 */


public abstract class SurfaceVideoViewCreator
        implements View.OnClickListener,
        SurfaceVideoView.OnPlayStateListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private final static String TAG = "zzzzz";

    private SurfaceVideoView surfaceVideoView;
    private LoadingCircleView progressBar;
    private Button statusButton;
    private ImageView surface_video_screenshot;

    private File videoFile = null;
    private boolean isUseCache = false;
    private boolean mNeedResume;

    private boolean isFinishDownload = false;
    public boolean debugModel = false;

    public int deviceWidth;
    public int deviceHeight;

    protected abstract Activity getActivity();

    protected abstract boolean setAutoPlay();

    protected abstract int getSurfaceWidth();

    protected abstract int getSurfaceHeight();

    protected abstract void setThumbImage(ImageView thumbImageView);

    protected abstract String getSecondVideoCachePath();

    protected abstract String getVideoPath();

    public void setUseCache(boolean useCache) {
        this.isUseCache = useCache;
    }

    public SurfaceVideoViewCreator(Activity activity, RelativeLayout container) {


        View view = LayoutInflater
                .from(activity)
                .inflate(R.layout.auth_surface_video_view_layout, container, false);

        container.addView(view);

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;

        surfaceVideoView = view.findViewById(R.id.surface_video_view);
        progressBar = view.findViewById(R.id.surface_video_progress);
        statusButton = view.findViewById(R.id.surface_video_button);
        surface_video_screenshot = view.findViewById(R.id.surface_video_screenshot);
        setThumbImage(surface_video_screenshot);


        int width = getSurfaceWidth();
        if (width != 0) {
            // 默认就是手机宽度
            surfaceVideoView.getLayoutParams().width = width;
        }
        view.findViewById(R.id.surface_video_container).getLayoutParams().height =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getSurfaceHeight(), container.getContext().getResources().getDisplayMetrics());
        view.findViewById(R.id.surface_video_container).requestLayout();

        surfaceVideoView.setOnPreparedListener(this);
        surfaceVideoView.setOnPlayStateListener(this);
        surfaceVideoView.setOnErrorListener(this);
        surfaceVideoView.setOnInfoListener(this);
        surfaceVideoView.setOnCompletionListener(this);

        surfaceVideoView.setOnClickListener(this);

        surfaceVideoView.setOnVideoSizeChangedListener((mp, width1, height1) -> {
            int lpWidth, lpHeight;
            if (deviceWidth * 1.0f / deviceHeight < width1 * 1.0f / height1) {
                lpWidth = deviceWidth;
                lpHeight = (int) (height1 * 1.0f / width1 * deviceWidth);
            } else {
                lpHeight = deviceHeight;
                lpWidth = (int) (width1 * 1.0f / height1 * deviceHeight);
            }
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(lpWidth, lpHeight);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            surfaceVideoView.setLayoutParams(lp);
        });

        if (setAutoPlay()) {
            prepareStart(getVideoPath());
        } else {
            statusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击即加载
                    // 这里进行本地是否存在判断
                    prepareStart(getVideoPath());
                }
            });
        }
    }

    private void prepareStart(String videoPath) {
        try {
            //本地视频
            File fileTemp = new File(videoPath);
            if (fileTemp.exists()) {
                play(videoPath);
                return;
            }


            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "myvideos/";
            File file = new File(rootPath);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    throw new NullPointerException("请开启文件读写权限");
                }
            }

            String[] temp = videoPath.split("/");
            videoFile =
                    new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                            + File.separator + "myvideos/" + temp[temp.length - 1]);

            if (debugModel) {
                // 测试模式
                if (isUseCache) {
                    Log.e(TAG, "使用缓存播放");
                    play(videoFile.getAbsolutePath());
                } else {
                    if (videoFile.exists()) {
                        videoFile.delete();
                        videoFile.createNewFile();
                    }
                    new MyAsyncTask().execute(getVideoPath());
                }
                return;
            }
            // 实际情况
            if (videoFile.exists()) {     // 存在缓存
                play(videoFile.getAbsolutePath());
            } else {
                String secondCacheFilePath = getSecondVideoCachePath(); // 第二缓存目录，应对此种情况，例如，本地上传是一个目录，那么就可能要到这个目录找一下
                if (secondCacheFilePath != null) {
                    play(secondCacheFilePath);
                    return;
                }
                videoFile.createNewFile();
                new MyAsyncTask().execute(getVideoPath());         // 下载再播放
            }

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public void onKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {// 跟随系统音量走
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (!getActivity().isFinishing())
                    surfaceVideoView.dispatchKeyEvent(getActivity(), event);
                break;
        }
    }

    public void onDestroy() {
        progressBar = null;
        statusButton = null;
        interceptFlag = true;
        if (!isFinishDownload) {
            // 如果还没下载完，清空缓存文件
            if (videoFile != null) {
                Log.d(TAG, "还没下载完，清空缓存文件");
                videoFile.delete();
            }
        }
        isFinishDownload = false;
        if (surfaceVideoView != null) {
            surfaceVideoView.release();
            surfaceVideoView = null;
        }
    }

    public void onResume() {
        if (surfaceVideoView != null && mNeedResume) {
            mNeedResume = false;
            interceptFlag = false;
            if (surfaceVideoView.isRelease())
                surfaceVideoView.reOpen();
            else
                surfaceVideoView.start();
        }
    }

    public void onPause() {
        if (surfaceVideoView != null) {
            if (surfaceVideoView.isPlaying()) {
                mNeedResume = true;
                surfaceVideoView.pause();
            }
        }
    }

    private void play(String path) {
        if (!surfaceVideoView.isPlaying()) {
            progressBar.setVisibility(View.GONE);
            statusButton.setVisibility(View.GONE);
            surfaceVideoView.setVideoPath(path);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (!getActivity().isFinishing())
            surfaceVideoView.reOpen();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "播放失败 onError " + what);
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                // 音频和视频数据不正确
                Log.d(TAG, "音频和视频数据不正确 ");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                // 缓冲开始
                if (!getActivity().isFinishing()) {
                    surfaceVideoView.pause();
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                // 缓冲结束
                if (!getActivity().isFinishing())
                    surfaceVideoView.start();
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                // 渲染开始 rendering
                surfaceVideoView.setBackground(null);
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "播放开始 onPrepared ");
        surfaceVideoView.setVolume(SurfaceVideoView.getSystemVolumn(getActivity()));
        surfaceVideoView.start();
        surface_video_screenshot.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        getActivity().finish();
    }

    @Override
    public void onStateChanged(boolean isPlaying) {
        statusButton.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
    }


    /**
     * 内部下载类，微信的机制是下载好再播放的，也可以直接边下载边播放
     */
    private boolean interceptFlag = false;

    private class MyAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String[] params) {
            try {
                String urlPath = params[0];
                URL url = new URL(urlPath);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

//                videoFile.delete();
//                videoFile.createNewFile();

                FileOutputStream fos = new FileOutputStream(videoFile);
                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numRead = is.read(buf);
                    count += numRead;
                    int progress = (int) (((float) count / length) * 100);
                    //更新进度
                    Log.d(TAG, "更新进度 " + progress);
                    if (numRead <= 0) {
                        publishProgress(100);
                        break;
                    } else {
                        publishProgress(progress);
                    }
                    fos.write(buf, 0, numRead);
                } while (!interceptFlag);//点击取消就停止下载.
                Log.d(TAG, "下载结束 ");
                isFinishDownload = true;
                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                Log.d(TAG, e.toString());
            } catch (IOException e) {
                // Toast.makeText(App.context,"安卓6.0+ 请动态申请文件读取权限",Toast.LENGTH_LONG).show();
                Log.d(TAG, e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer[] values) {
            if (progressBar == null)
                return;
            int progress = values[0];
            progressBar.setProgerss(progress, true);
            if (progress >= 100) {
                Log.d(TAG, "开始播放 ");
                play(videoFile.getAbsolutePath());
            }
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
        }
    }
}
