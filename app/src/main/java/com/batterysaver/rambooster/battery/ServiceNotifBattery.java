package com.batterysaver.rambooster.battery;

/**
 * Created by ElMakkaoui on 08/11/2016.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.batterysaver.rambooster.R;

public class ServiceNotifBattery extends Service {

    private static final int NOTIFICATION_ID = 1;
    NotificationManager mNotificationManager;
    Intent intent;
    int level = -1;
    @Override public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);

        //Context context = this.getApplicationContext();
        Intent intentBattery  = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level   = intentBattery.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

        Intent resultIntent = new Intent(getApplicationContext(), ActivitySaveBattery.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext());

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        Notification notification;
        notification = mBuilder.setSmallIcon(R.drawable.battery_widget_icon).setTicker("FAST CHARGING").setWhen(0)
                .setAutoCancel(true)
                .setContentTitle("Battery Saver")
                .setContentIntent(resultPendingIntent)
                .setStyle(inboxStyle)
                .setSmallIcon(R.drawable.battery_widget_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_ram))
                .setContentText("Level "+level+"% - click to open fast charging mode")
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(152, notification);

        /*

                Intent resultIntent = new Intent(getApplicationContext(), ActivitySaveBattery.class);
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);

        //intentFilter
        Notification.Builder notificationBuilder = new Notification.Builder(
                ServiceNotifBattery.this);

        Intent i = new Intent(getApplicationContext(),ActivitySaveBattery.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 , i , 0);
        notificationBuilder.setContentTitle("FAST CHARGING");
        notificationBuilder.setContentText("Level " + level + " %");
        notificationBuilder.setWhen(System.currentTimeMillis());
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setOngoing(true);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.build();

        NotificationManager notifi = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification();


        //notification.flags = Notification.FLAG_ONGOING_EVENT;
        //PendingIntent penInt = PendingIntent.getActivity(getApplicationContext(), 0 , i , 0);
        //notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), " " +level+"%", penInt);

        notification = notificationBuilder.getNotification();
        notifi.notify(NOTIFICATION_ID, notification);
        //notificationManager.cancel(NOTIFICATION_ID);
        startForeground(NOTIFICATION_ID, notification);

        notifi.cancel(NOTIFICATION_ID);
        stopForeground(false);

        Log.d("Raj", "Battery level is " + level);
        */
    }


    public BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            Notification.Builder notificationBuilder = new Notification.Builder(
                    ServiceNotifBattery.this);

            Intent i = new Intent(getApplicationContext(), ActivitySaveBattery.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 , i , 0);
            notificationBuilder.setContentTitle("FAST CHARGING");
            notificationBuilder.setContentText("Level " + level + " %");
            notificationBuilder.setWhen(System.currentTimeMillis());
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher_boost);
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setOngoing(true);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.build();

            NotificationManager notifi = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification();


            //notification.flags = Notification.FLAG_ONGOING_EVENT;
            //PendingIntent penInt = PendingIntent.getActivity(getApplicationContext(), 0 , i , 0);
            //notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), " " +level+"%", penInt);

            notification = notificationBuilder.getNotification();
            notifi.notify(NOTIFICATION_ID, notification);
            //notificationManager.cancel(NOTIFICATION_ID);
            startForeground(NOTIFICATION_ID, notification);

            notifi.cancel(NOTIFICATION_ID);
            stopForeground(false);

            Log.d("Raj", "Battery level is " + level);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        //registerReceiver(batteryReceiver , new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(batteryReceiver);
    }

}

