package com.services;

/**
 * Created by Admin on 10/3/2016.
 */


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class OverlayService extends Service {

    protected boolean foreground = false;
    protected boolean cancelNotification = false;
    protected int id = 0;
    protected  String colorCode = "";

    protected Notification foregroundNotification(int notificationId) {
        return null;
    }

    public void moveToForeground(int id, boolean cancelNotification) {
        moveToForeground(id, foregroundNotification(id), cancelNotification);
    }

    public void moveToForeground(int id, Notification notification, boolean cancelNotification) {
        if (! this.foreground && notification != null) {
            this.foreground = true;
            this.id = id;
            this.cancelNotification = cancelNotification;

            super.startForeground(id, notification);
        } else if (this.id != id && id > 0 && notification != null) {
            this.id = id;
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(id, notification);
        }
    }


    public void moveToBackground(int id, boolean cancelNotification) {
        foreground = false;
        id = 0;
        super.stopForeground(cancelNotification);
    }

    public void moveToBackground(int id) {
        moveToBackground(id, cancelNotification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras() != null && intent.getExtras().getString("colorCode") != null) {
            colorCode = intent.getExtras().getString("colorCode");

            Log.i("=Code==","==000==Color code="+colorCode);
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String getColorCode ()
    {
     return  colorCode;
    }

}
