package com.maihaoche.volvo.ui.instorage.activity;


import android.os.Bundle;

import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivityVideoIntroduceBinding;
import com.maihaoche.volvo.ui.photo.VideoPlayerActivity;
import com.maihaoche.volvo.util.permission.PermissionHandler;
import com.maihaoche.volvo.view.dialog.CommonDialog;

public class VideoIntroduceActivity extends HeaderProviderActivity<ActivityVideoIntroduceBinding> {

    private static final String SAMPLE_VIDEO = "https://static.youxianche.com/fanli.mp4";

    private ActivityVideoIntroduceBinding binding;

    @Override
    public int getContentResId() {
        return R.layout.activity_video_introduce;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initHeader("拍摄说明");
        binding = getContentBinding();
        binding.videoEx.setOnClickListener(v-> {
                    PermissionHandler.checkVideo(this, granted -> {
                        if (granted) {
                            VideoPlayerActivity.toActivity(this, SAMPLE_VIDEO);
                        } else {
                            CommonDialog dialog = new CommonDialog(this, "", "当前应用缺少视频相关多媒体权限\n请点击\"设置\"-\"权限\"-打开所需权限",
                                    () -> PermissionHandler.toSettings(this),
                                    null);
                            dialog.show();
                        }

                    });
                });
    }
}
