package com.maihaoche.commonbiz.service.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.RawRes;
import android.util.Log;

import java.io.IOException;

public final class Accompaniment implements MediaPlayer.OnCompletionListener {

    private static final String TAG = Accompaniment.class.getName();

    public MediaPlayer mMediaPlayer = null;
    Context ownersContext = null;
    private final int myResSoundId;
    private boolean isReleased = false;

    private Accompaniment(Context context, int resId) {
        if (context == null) throw new IllegalArgumentException("context cannot be null");
        ownersContext = context;
        myResSoundId = resId;
    }

    public static final Accompaniment newInstanceOfResource(Context contxt, @RawRes int id) {
        return new Accompaniment(contxt, id);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    public enum RingerMode {SILENT, VIBRATE, NORMAL}

    private AudioManager mAudioManager;

    public RingerMode ringerMode() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) ownersContext.getSystemService(Context.AUDIO_SERVICE);
        }
        return RingerMode.values()[mAudioManager.getRingerMode()];
    }

    public boolean start() {
        return start(false);
    }

    public boolean startForce() {
        return start(true);
    }

    private synchronized final boolean start(boolean forceInSilence) {
        if (isReleased) {
            return false;
        }
        if (mMediaPlayer == null) {
            try{
                mMediaPlayer = MediaPlayer.create(ownersContext, myResSoundId);
            }catch (Exception e){
                Log.e("TAG",e.getMessage());
            }

            if (mMediaPlayer == null) {
                return false;
            }
            //设置mediaplayer的输出类型为音乐
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(this);
        }

        if (forceInSilence == false && ringerMode() != RingerMode.NORMAL) {
            return false;
        }

        mMediaPlayer.start();
        return true;
    }

    public boolean stop() {
        if (isReleased) return false;

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            return true;
        }
        return false;
    }

    public void prepare() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public synchronized void release() {
        isReleased = true;
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        ownersContext = null;
    }
}
