package com.maihaoche.volvo.ui.avchat.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.maihaoche.volvo.R;
import com.maihaoche.volvo.ui.avchat.AVChatCache;
import com.maihaoche.volvo.ui.avchat.ui.AVChatActivity;

/**
 * 音视频聊天通知栏
 */
public class AVChatNotification {

    private Context context;

    private NotificationManager notificationManager;
    private Notification callingNotification;
    private Notification missCallNotification;
    private String account;
    private String displayName;
    private static final int CALLING_NOTIFY_ID = 111;
    private static final int MISS_CALL_NOTIFY_ID = 112;

    public AVChatNotification(Context context) {
        this.context = context;
    }

    public void init(String account) {
        this.account = account;
        this.displayName = account;

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        AVChatNotificationChannelCompat26.createNIMMessageNotificationChannel(context);
    }

    private void buildCallingNotification() {
        if (callingNotification == null) {
            Intent localIntent = new Intent();
            localIntent.setClass(context, AVChatActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            String tickerText = String.format(context.getString(R.string.avchat_notification), displayName);
            int iconId = R.drawable.volvo_logo;

            PendingIntent pendingIntent = PendingIntent.getActivity(context, CALLING_NOTIFY_ID, localIntent, PendingIntent
                    .FLAG_UPDATE_CURRENT);
            callingNotification = makeNotification(pendingIntent, context.getString(R.string.avchat_call), tickerText, tickerText,
                    iconId, false, false);
        }
    }

    private void buildMissCallNotification() {
        if (missCallNotification == null) {
            String title = context.getString(R.string.avchat_no_pickup_call);
            String tickerText = "总部抽检电话";
            int iconId = R.drawable.avchat_no_pickup;

            missCallNotification = makeNotification(null, title, tickerText, tickerText, iconId, true, true);
        }
    }

    private Notification makeNotification(PendingIntent pendingIntent, String title, String content, String tickerText,
                                          int iconId, boolean ring, boolean vibrate) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(tickerText)
                .setSmallIcon(iconId);
        int defaults = Notification.DEFAULT_LIGHTS;
        if (vibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (ring) {
            defaults |= Notification.DEFAULT_SOUND;
        }
        builder.setDefaults(defaults);

        return builder.build();
    }

    public void activeCallingNotification(boolean active) {
        if (notificationManager != null) {
            if (active) {
                buildCallingNotification();
                notificationManager.notify(CALLING_NOTIFY_ID, callingNotification);
                AVChatCache.getNotifications().put(CALLING_NOTIFY_ID, callingNotification);
            } else {
                notificationManager.cancel(CALLING_NOTIFY_ID);
                AVChatCache.getNotifications().remove(CALLING_NOTIFY_ID);
            }
        }
    }

    public void activeMissCallNotification(boolean active) {
        if (notificationManager != null) {
            if (active) {
                buildMissCallNotification();
                notificationManager.notify(MISS_CALL_NOTIFY_ID, missCallNotification);
                AVChatCache.getNotifications().put(MISS_CALL_NOTIFY_ID, callingNotification);
            } else {
                notificationManager.cancel(MISS_CALL_NOTIFY_ID);
                AVChatCache.getNotifications().remove(MISS_CALL_NOTIFY_ID);
            }
        }
    }
}
