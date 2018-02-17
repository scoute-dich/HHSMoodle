package de.baumann.hhsmoodle.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import de.baumann.hhsmoodle.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class RebootReceiver_not extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        PreferenceManager.setDefaultValues(context, R.xml.user_settings, false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")
                && sp.getBoolean("action_not", false)) {

            android.content.Intent iMain = new android.content.Intent();
            iMain.setAction("shortcutToDo");
            iMain.setClassName(context, "de.baumann.hhsmoodle.popup.Popup_todo_restart");
            PendingIntent piMain = PendingIntent.getActivity(context, 0, iMain, 0);

            NotificationCompat.Builder builder;

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String CHANNEL_ID = "hhs_not";// The id of the channel.
                CharSequence name = context.getString(R.string.app_name);// The user-visible name of the channel.
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
                mNotificationManager.createNotificationChannel(mChannel);
                builder = new NotificationCompat.Builder(context, CHANNEL_ID);
            } else {
                //noinspection deprecation
                builder = new NotificationCompat.Builder(context);
            }

            Notification notification  = builder
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.school)
                    .setContentTitle(context.getString(R.string.toast_restart_title))
                    .setContentText(context.getString(R.string.toast_restart_text))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.toast_restart_text)))
                    .setContentIntent(piMain)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }
    }
}