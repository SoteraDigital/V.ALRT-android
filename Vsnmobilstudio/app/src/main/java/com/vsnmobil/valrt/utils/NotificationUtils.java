package com.vsnmobil.valrt.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.vsnmobil.valrt.R;
import com.vsnmobil.valrt.VALRTApplication;
import com.vsnmobil.valrt.activities.AlertProgressActivity;
import com.vsnmobil.valrt.activities.FallDetectActivity;
import com.vsnmobil.valrt.activities.HomeActivity;

/**
 * NotificationUtils.java
 */
public class NotificationUtils {
    /** The m notification manager. */
    static NotificationManager mNotificationManager = null;
    /** The other notify builder. */
    static NotificationCompat.Builder otherNotifyBuilder = null;
    /** The notify builder. */
    static NotificationCompat.Builder notifyBuilder = null;
    /** The notification. */
    static Notification notification = null;
    /** The other notification. */
    static Notification otherNotification = null;
    /**
     * To create the other notification.
     *
     * @param context the context
     * @param message the message
     * @param notifyId the notify id
     */
    public static void postNotification(Context context, String message, int notifyId) {
        Intent resultIntent = null;
        /* Invoking the default notification service */
        otherNotifyBuilder = new NotificationCompat.Builder(context);
        otherNotifyBuilder.setContentTitle(context.getString(R.string.app_name)).setSmallIcon(
                R.drawable.ic_launcher);
        otherNotifyBuilder.setContentText(message);
        // 32 is the maximum length of character in single line of notification.
        if(message.length()>42)
            otherNotifyBuilder .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        otherNotifyBuilder.setAutoCancel(true);
        /* Creates an explicit intent for an Activity in your app */
        if (notifyId == VALRTApplication.BLUETOOTH_NOTIFY_ID) {
            resultIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        } else if (notifyId == VALRTApplication.LOCATION_NOTIFY_ID) {
            resultIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        } else if (notifyId == VALRTApplication.DATA_CONNECTION_NOTIFY_ID) {
            resultIntent = new Intent(Settings.ACTION_SETTINGS);
        } else if (notifyId == VALRTApplication.ALERTINPROGRESS_NOTIFY_ID) {
            resultIntent = new Intent(context, AlertProgressActivity.class);
        } else if (notifyId == VALRTApplication.FALL_DETECT_NOTIFY_ID) {
            resultIntent = new Intent(context, FallDetectActivity.class);
        } else{
            resultIntent = new Intent(context, HomeActivity.class);
        }
        resultIntent.putExtra(VALRTApplication.NOTIFICATION_ID, notifyId);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int notificationCode = (int) System.currentTimeMillis();
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, notificationCode, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if(VALRTApplication.getPrefBoolean(context,VALRTApplication.CONGRATULATION) == true){
            otherNotifyBuilder.setContentIntent(resultPendingIntent);
        }
        otherNotification = otherNotifyBuilder.build();
        otherNotification.defaults = 0;
        if (!VALRTApplication.getPrefBoolean(context, VALRTApplication.PHONESILENTCBX)) {
            otherNotification.defaults = Notification.DEFAULT_SOUND;
        }
        otherNotification.flags |= Notification.FLAG_SHOW_LIGHTS;

        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        /* Update the existing notification using same notification ID */
        mNotificationManager.notify(notifyId, otherNotification);
    }
    /**
     * To create persistence notification status.
     *
     * @param context of the class
     * @param message to show in notification
     * @return the notification
     */
    public static Notification getNotification(Context context, String message) {
        /* Invoking the default notification service */
        notifyBuilder =new NotificationCompat.Builder(context);

        notifyBuilder.setContentTitle(context.getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_launcher);
        notifyBuilder.setContentText(message);
        notifyBuilder.setAutoCancel(false);
        notifyBuilder.setPriority(Notification.PRIORITY_MIN);
        // 32 is the maximum length of character in single line of notification.
        if(message.length()>42)
            notifyBuilder .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        /* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(context, HomeActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int notificationCode = (int) System.currentTimeMillis();
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, notificationCode, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (VALRTApplication.getPrefBoolean(context, VALRTApplication.CONGRATULATION) == true) {
            notifyBuilder.setContentIntent(resultPendingIntent);
        }
        notification = notifyBuilder.build();
        return notification;
    }
}
