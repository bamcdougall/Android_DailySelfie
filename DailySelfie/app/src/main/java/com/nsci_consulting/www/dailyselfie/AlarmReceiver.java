package com.nsci_consulting.www.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;
/**
 * Created by Brendan on 8/16/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmLoggerReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
//      create a log for when alarms are received
        Log.i(TAG, "Logging alarm at:" + DateFormat.getDateTimeInstance().format(new Date()));

//        Provide permission for alarm receiver to initiate a self-portrait
        Intent MainActivityIntent = new Intent(context, MainActivity.class);
        MainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, MainActivityIntent, 0);

//        Generate notification
        Notification.Builder mNotificationBuilder = new Notification.Builder(context)
                .setContentTitle(context.getText(R.string.notification_title))
                .setContentText(context.getText(R.string.notification_text))
                .setTicker(context.getText(R.string.notification_tickerText))
                .setSmallIcon(R.drawable.ic_camera_enhance_white_24dp)
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//        notification.flags |= Notification.FLAG_AUTO_CANCEL;


        notificationManager.notify(0, mNotificationBuilder.build());

    }
}
