package com.ppmessage.sdk.core.utils;

import android.media.MediaRecorder;
import android.os.Handler;

import com.ppmessage.sdk.core.L;

/**
 *
 * Copied from http://blog.csdn.net/greatpresident/article/details/38402147
 *
 * Created by ppmessage on 7/20/16.
 */
public class AudioRecordMicPhoneStatusUtil {

    private static final String VOLUME_DB_VALUE = "[AudioRecordMicPhoneStatusUtil] current micphone volume: %fdb";

    public interface OnMicPhoneVolumeChangedEvent {
        void onVolumeChanged(double currentDb, double ratio);
    }

    private static final int BASE = 1;
    private static final int SPACE = 100;// 间隔取样时间
    private static final double MAX_DB = 90.3; // 0db ~ 90.3db

    private MediaRecorder mediaRecorder;
    private Handler handler;
    private Runnable updateMicPhoneStatusTimer;
    private OnMicPhoneVolumeChangedEvent micPhoneVolumeChangedEvent;
    private boolean cancel;

    public AudioRecordMicPhoneStatusUtil(MediaRecorder mediaRecorder) {
        this.mediaRecorder = mediaRecorder;
        this.handler = new Handler();
        this.cancel = false;
        updateMicPhoneStatusTimer = new Runnable() {
            @Override
            public void run() {
                updateMicPhoneStatus();
            }
        };
    }

    public void setMicPhoneVolumeChangedEvent(OnMicPhoneVolumeChangedEvent micPhoneVolumeChangedEvent) {
        this.micPhoneVolumeChangedEvent = micPhoneVolumeChangedEvent;
    }

    public void start() {
        updateMicPhoneStatus();
    }

    public void stop() {
        cancel = true;
        if (handler != null) {
            handler.removeCallbacks(updateMicPhoneStatusTimer);
            handler = null;
        }
    }

    private void updateMicPhoneStatus() {
        if (!cancel && mediaRecorder != null) {
            double ratio = (double) mediaRecorder.getMaxAmplitude() /BASE;
            double db = 0;
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
            }
            L.d(VOLUME_DB_VALUE, db);
            // Make a event callback
            if (this.micPhoneVolumeChangedEvent != null) {
                this.micPhoneVolumeChangedEvent.onVolumeChanged(db, Math.min(1.0, db / MAX_DB));
            }
            handler.postDelayed(updateMicPhoneStatusTimer, SPACE);
        }
    }

}
