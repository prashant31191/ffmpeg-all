package com.lemda.videoconvert;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Admin on 8/11/2016.
 */

public class ActSplash extends Activity
{
    Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);

        try{
            System.out.println("=====1111=====");

            App.startAlarmServices(ActSplash.this);
            mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(ActSplash.this,ActAllList.class));
                }
            }, 2000);
          //  getActivityList();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }
    private void getActivityList() {
        try {
            System.out.println("=====2222=====");
            Intent ii = new Intent(Intent.ACTION_MAIN);
            ii.addCategory(Intent.CATEGORY_LAUNCHER);
            PackageManager pm = this.getPackageManager();

            PackageInfo info = pm.getPackageInfo("com.lemda.videoconvert", PackageManager.GET_ACTIVITIES);

            ApplicationInfo test = info.applicationInfo;
            ActivityInfo[] list = info.activities;

            if (list.length >= 0) {
                for (int i = 0; i < list.length; i++) {
                    Log.i("====", "===Activity toString=" + list[i].toString());
                    Log.i("====", "===Activity parentActivityName=" + list[i].parentActivityName);
                    Log.i("====", "===Activity permission=" + list[i].permission);
                    Log.i("====", "===Activity targetActivity=" + list[i].targetActivity);
                    Log.i("====", "===Activity Name=" + list[i].name);
                    System.out.println("=====2222=Activity toString====="+ list[i].toString());
                    System.out.println("==2222=Activity Name=====" + list[i].name);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("====222=Error=====");
            e.printStackTrace();
        }

    }
}
