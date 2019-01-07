package com.maihaoche.volvo.ui.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.maihaoche.volvo.R;
import com.maihaoche.volvo.util.DeviceUtil;
import com.maihaoche.volvo.view.videoView.SurfaceVideoViewCreator;


/**
 * 视频播放页
 */
public class VideoPlayerActivity extends AppCompatActivity {

    private static final String VIDEO_URL = "video_url";

    private SurfaceVideoViewCreator surfaceVideoViewCreator;
    private String url;

    public static void toActivity(Context context, String url) {

        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(VIDEO_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_activity_show_video);
        url = getIntent().getStringExtra(VIDEO_URL);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        surfaceVideoViewCreator =
                new SurfaceVideoViewCreator(this, (RelativeLayout) findViewById(R.id.activity_main)) {
                    @Override
                    protected Activity getActivity() {
                        // 当前的 Activity
                        return VideoPlayerActivity.this;
                    }

                    @Override
                    protected boolean setAutoPlay() {
                        // true 适合用于，已进入就自动播放的情况
                        return true;
                    }

                    @Override
                    protected int getSurfaceWidth() {
                        // Video 的显示区域宽度，0 就是适配手机宽度
                        return 0;
                    }

                    @Override
                    protected int getSurfaceHeight() {
                        //  Video 的显示区域高度，dp 为单位
                        return DeviceUtil.getDeviceHeight(VideoPlayerActivity.this);
                    }

                    @Override
                    protected void setThumbImage(ImageView thumbImageView) {
                        thumbImageView.setImageDrawable(new ColorDrawable(Color.parseColor("#252525")));
                    }

                    /**
                     * 这个是设置返回自己的缓存路径，应对这种情况：
                     * 录制的时候是在另外的目录，播放的时候默认是在下载的目录，所以可以在这个方法处理返回缓存
                     */
                    @Override
                    protected String getSecondVideoCachePath() {
                        return null;
                    }

                    @Override
                    protected String getVideoPath() {
                        return url;
                    }
                };

    }

    @Override
    protected void onPause() {
        super.onPause();
        surfaceVideoViewCreator.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceVideoViewCreator.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        surfaceVideoViewCreator.onDestroy();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 声音的大小调节
        surfaceVideoViewCreator.onKeyEvent(event);
        return super.dispatchKeyEvent(event);
    }
}
