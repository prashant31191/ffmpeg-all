package com.services;

/**
 * Created by Admin on 10/3/2016.
 */


import android.app.Notification;
import android.app.PendingIntent;

import android.content.Intent;
import android.util.Log;

import com.lemda.videoconvert.R;
import com.overlay.SampleOverlayHideActivity;
import com.overlay.SampleOverlayView;

public class SampleOverlayService extends OverlayService {

    public static SampleOverlayService instance;

    private static SampleOverlayView overlayView;
    private String colorCode = "";

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        colorCode = getColorCode();

        Log.i("=Code==","==111Color code="+colorCode);

        if (colorCode != null && colorCode.length() >= 8) {
            overlayView = new SampleOverlayView(this, colorCode);
        } else {
            overlayView = new SampleOverlayView(this);
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras() != null && intent.getExtras().getString("colorCode") != null) {
            colorCode = intent.getExtras().getString("colorCode");

            Log.i("=Code==","==555===Color code="+colorCode);
        }


        if (colorCode != null && colorCode.length() >= 8) {
            overlayView = new SampleOverlayView(this, colorCode);
        } else {
            overlayView = new SampleOverlayView(this);
        }

        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        if (overlayView != null) {
            overlayView.destory();
        }

    }

    static public void stop() {
        if (instance != null) {
            instance.stopSelf();
        }
    }



   /* @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (intent.getExtras() != null && intent.getExtras().getString("colorCode") != null) {
            colorCode = intent.getExtras().getString("colorCode");

            Log.i("=Code==","==111Color code="+colorCode);
        }
    }
*/
    @Override
    protected Notification foregroundNotification(int notificationId) {
        Notification notification;

        notification = new Notification(R.drawable.ic_launcher, getString(R.string.title_notification), System.currentTimeMillis());

        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_ONLY_ALERT_ONCE;

        notification.setLatestEventInfo(this, getString(R.string.title_notification), getString(R.string.message_notification), notificationIntent());

        return notification;
    }


    private PendingIntent notificationIntent() {
        Intent intent = new Intent(this, SampleOverlayHideActivity.class);

        PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pending;
    }

}