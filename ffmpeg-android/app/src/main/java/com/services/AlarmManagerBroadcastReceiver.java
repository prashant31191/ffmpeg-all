package com.services;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.lemda.videoconvert.App;
import com.lemda.videoconvert.Home;
import com.lemda.videoconvert.R;
import com.utils.DatabaseUtils;
import com.utils.DownloadFileFromURL;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";
    Context mContext;


    String TAG = "===---ALARM--Receiver--===";

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        if(mContext == null)
        {
            mContext = App.mContext;
        }

        //App.sysOut("==onReceive==");
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();
        //You can do the processing here.
        Bundle extras = intent.getExtras();
        StringBuilder msgStr = new StringBuilder();

        if (extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)) {
            //Make sure this intent has been sent by the one-time timer button.
            msgStr.append("One time Timer : ");
        }

        //Format formatter = new SimpleDateFormat("hh:mm:ss a");

        Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

        msgStr.append(formatter.format(new Date()));

        String strCurrTime = msgStr.toString();

        App.showLog2(TAG+"==onReceive Time ==" + strCurrTime);



        App.showLog2(TAG+"==--1111--==strCurrTime=" + strCurrTime+"==App.strPrevTime=" + App.strPrevTime);

        if (strCurrTime.equalsIgnoreCase(App.strPrevTime)) {
            App.showLog2(TAG+"==Same date Time == xxxx ==No need to call===");
            App.showLog2(TAG+"==-- xxxxxxx 22222--==strCurrTime=" + strCurrTime+"==App.strPrevTime=" + App.strPrevTime);
        } else {
            App.strPrevTime = strCurrTime;
            App.showLog2(TAG+"==Same date Time = yyyyy ===need to call===");

            App.showLog2(TAG+"==-- yyyyy 22222--==strCurrTime=" + strCurrTime+"==App.strPrevTime=" + App.strPrevTime);


            String strCode = App.sharePrefrences.getStringPref(App.PF_CODE);
            if (strCode != null && strCode.length() > 0) {
                checkDatabseDataForNotify(strCurrTime);
             //   Toast.makeText(mContext,"-T-"+strCurrTime,Toast.LENGTH_SHORT).show();
            } else {
                App.showLog(TAG+"==onReceive Time -No login----Stop to call alarm---==");

//                App.showLog(TAG+"==Path==="+App.constructScreenshotImage(mContext));
              //111  App.stopUpdateLocation(mContext);
            }
        }


        //Release the lock
        wl.release();
    }

    public void SetAlarm(Context context) {
        App.sysOut("==SetAlarm==");
        mContext = context;
      //  strPrevTime = "";
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 20, alarmIntent); //5 -> 60 (1minute)




       /* AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        60 * 1000, alarmIntent);*/

        //After after 5 seconds
        //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5 , pi);
        //After after 0.90 minute

    }

    public void CancelAlarm(Context context) {
        App.sysOut("==CancelAlarm==");
        //App.strPrevTime = "";
        mContext = context;
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void setOnetimeTimer(Context context) {
        App.sysOut("==setOnetimeTimer==");
        mContext = context;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }


    private void setLocalNotification(Context context, String date, String lat, String time) {
        try {
            mContext = context;
            App.sysOut("==setLocalNotification==");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //private String fileName, title, message, imageUrl, time;
    private Bitmap result;


    private void generateNotificationOneTime(String title, String message, String imageUrl, String time, String type, String reg_day, String itllst) {

        Intent intent2 = new Intent("message-id");
        intent2.putExtra("someExtraMessage", "Some Message :)");
        //   mContext = this.getApplicationContext();
        /*LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent2);
*/
        Intent intent = new Intent(mContext, Home.class);

        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("imageUrl", imageUrl);
        intent.putExtra("time", time);

        intent.putExtra("type", type);
        intent.putExtra("reg_day", reg_day);
        intent.putExtra("itllst", itllst);


        if (result != null) {

        } else {
            result = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
        }

        int notifitionIdTime = (int) System.currentTimeMillis();

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, notifitionIdTime  /*Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      /*  NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                //.setSmallIcon(R.mipmap.ic_launcher)
                //.setSmallIcon(R.drawable.noti_icon1)
                //.setLargeIcon(result)

                .setContentTitle(Html.fromHtml(title))
                // .setContentText(this.message)
                .setSubText(Html.fromHtml(message))
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(result))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);*/

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.noti_icon1);
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }*/

      /*  NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifitionIdTime  *//*ID of notification*//*, notificationBuilder.build());*/
    }


    DatabaseUtils db;

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @SuppressLint("LongLogTag")
    private void checkDatabseDataForNotify(String currentDateTime) {
        try {
            db = new DatabaseUtils(mContext);
            Cursor cursor = db.getNotificationList(DatabaseUtils.TABLE_NOTIFICATION);
            try {
                while (cursor.moveToNext()) {

                    String strKEY_nid = cursor.getString(cursor.getColumnIndex(App.KEY_nid));
                    String strKEY_fid = cursor.getString(cursor.getColumnIndex(App.KEY_fid));
                    String strKEY_ttl = cursor.getString(cursor.getColumnIndex(App.KEY_ttl));
                    String strKEY_img = cursor.getString(cursor.getColumnIndex(App.KEY_img));
                    String strKEY_desc = cursor.getString(cursor.getColumnIndex(App.KEY_desc));
                    String strKEY_itllst = cursor.getString(cursor.getColumnIndex(App.KEY_itllst));
                    String strKEY_date = cursor.getString(cursor.getColumnIndex(App.KEY_date));
                    String strKEY_time = cursor.getString(cursor.getColumnIndex(App.KEY_time));
                    String strKEY_type = cursor.getString(cursor.getColumnIndex(App.KEY_type));
                    String strKEY_sts = cursor.getString(cursor.getColumnIndex(App.KEY_sts));
                    String strKEY_isDaily = cursor.getString(cursor.getColumnIndex(App.KEY_isDaily));
                    String strKEY_reg_day = cursor.getString(cursor.getColumnIndex(App.KEY_reg_day));
                    String strKEY_isNotify = cursor.getString(cursor.getColumnIndex(App.KEY_isNotify));
                    String strKEY_isDownload = cursor.getString(cursor.getColumnIndex(App.KEY_isDownload));
                    String strKEY_timeNotifty = cursor.getString(cursor.getColumnIndex(App.KEY_timeNotifty));

                    App.showLog(TAG+"=====currentDateTime==" + currentDateTime + "==T-" + strKEY_type);
                    if (strKEY_type != null && strKEY_type.equalsIgnoreCase("166666")) {
                        String strTagType16 = "=166666--Wrong--No entery----==Notificaiton type (16) == strKEY_type==" + strKEY_type + "===";

                        //App.showLog(TAG+"===TYPE 16==currentDateTime==" + currentDateTime + "==T-" + strKEY_type);

                        boolean blnShowAlert16 = false;
                        if (App.checkDatabaseDates(strKEY_timeNotifty, currentDateTime) == false) {
                            App.showLog(TAG+strKEY_timeNotifty + "= <<1 db ====Notify DateTime Not match==== crnt 2>> =" + currentDateTime + "==T-" + strKEY_type);
                        } else {
                            if (strKEY_isNotify != null && strKEY_isNotify.equalsIgnoreCase("0")) {
                                App.showLog(TAG+"===16 notify updated=====");
                                blnShowAlert16 = true;
                                db.updateFieldsNotificationTable(DatabaseUtils.TABLE_NOTIFICATION, App.KEY_isNotify, "1", App.KEY_nid, strKEY_nid, "---KEY_timeNotifty Update 1 Sucess notify--");
                            }
                        }

                        App.showLog(TAG+strKEY_timeNotifty + "= <<1 db ==T-16==Notify DateTime Not match==16== crnt 2>> =" + currentDateTime + "==T-" + strKEY_type);

                        if (blnShowAlert16 == false) {


                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            //String formattedDate = df.format(calendar.getTime());
                            String formattedDate = df.format(new Date());
                            Date date1 = df.parse(formattedDate);

                            String startdate = App.sharePrefrences.getStringPref(App.PF_FS_DATE);
                            Date date2 = df.parse(startdate);

                            String enddate = App.sharePrefrences.getStringPref(App.PF_FE_DATE);
                            Date date3 = df.parse(enddate);


                            /*111 App.showLog(TAG+strTagType16 + "date1=> " + date1.toString());
                            App.showLog(TAG+strTagType16 + "date2=> " + date2.toString());
                            App.showLog(TAG+strTagType16 + "date3=> " + date3.toString());*/

                          /*  SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                            Date starttime = sdf.parse(strKEY_time);
                            System.out.println(sdf.format(starttime));*/


                            if (strKEY_time != null && strKEY_time.length() > 0) {
                                try {
                                    App.showLog(TAG+"=Every day notify at =" + strKEY_time);
                                    String[] strTime = strKEY_time.split(",");
                                    String time1 = "", time2 = "", time3 = "";
                                    if (strTime[0] != null) {
                                        time1 = strTime[0];
                                    }
                                    if (strTime[1] != null) {
                                        time2 = strTime[1];
                                    }
                                    if (strTime[2] != null) {
                                        time3 = strTime[2];
                                    }

                                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

                                    Date starttime_first = sdf.parse(time1);
                                    Date starttime_second = sdf.parse(time2);
                                    Date starttime_third = sdf.parse(time3);


                                    String formatedcurrenttime = sdf.format(new Date());
                                    Date updatedcurrenttime = sdf.parse(formatedcurrenttime);

                                   /* 1111 App.showLog(TAG+strTagType16 + "11111--C--" + sdf.format(starttime_first));
                                    App.showLog(TAG+strTagType16 + "22222--S--" + sdf.format(starttime_second));
                                    App.showLog(TAG+strTagType16 + "33333--E--" + sdf.format(starttime_third));

                                    App.showLog(TAG+strTagType16 + "time1=>" + time1.toString() + "<==");
                                    App.showLog(TAG+strTagType16 + "time2=>" + time2.toString() + "<==");
                                    App.showLog(TAG+strTagType16 + "time3=>" + time3.toString() + "<==");*/

                                    //App.showLog(TAG+strTagType16 + "date1--starttime_first=> " + starttime_first.toString());
                                    //App.showLog(TAG+strTagType16 + "date2--starttime_second=> " + starttime_second.toString());
                                    //App.showLog(TAG+strTagType16 + "date3--starttime_third=> " + starttime_third.toString());


                                    App.showLog(TAG+strTagType16 + "=Ctime=formatedcurrenttime=111=>" + formatedcurrenttime.toString());

                                   /* App.showLog(TAG+"C_date1=> " + date1.toString());
                                    App.showLog(TAG+"S_date2=> " + date2.toString());
                                    App.showLog(TAG+"E_date3=> " + date3.toString());*/


                                    if (date1.compareTo(date2) > 0 && date1.compareTo(date3) < 0) {
                                        try {
                                            App.showLog(TAG+"==--------------Current date NOT match start or end date--------------------------=");
                                            if (starttime_first.compareTo(updatedcurrenttime) == 0) {
                                                //App.showLog(TAG+"=Fire Notification---f1---A-11" + starttime_first.toString());
                                                blnShowAlert16 = true;
                                            } else {
                                                if (starttime_second.compareTo(updatedcurrenttime) == 0) {
                                                    //App.showLog(TAG+"=Fire Notification---s2---A---22--" + starttime_second.toString());
                                                    blnShowAlert16 = true;
                                                } else {
                                                    if (starttime_third.compareTo(updatedcurrenttime) == 0) {
                                                        //App.showLog(TAG+"Fire Notification---t3---A---33--" + starttime_third.toString());
                                                        blnShowAlert16 = true;
                                                    } else {
                                                        App.showLog(TAG+"=Notime=No Match time=A-00=");
                                                        blnShowAlert16 = false;
                                                    }
                                                }
                                            }
                                        } catch (Exception e2) {
                                            e2.printStackTrace();
                                        }

                                    } else if (date1.compareTo(date2) == 0 || date1.compareTo(date3) == 0) {
                                        try {
                                            App.showLog(TAG+"==--------------Current date match start or end date--------------------------=");
                                            if (starttime_first.compareTo(updatedcurrenttime) == 0) {
                                                //App.showLog(TAG+"=F1time=starttime=B-111=" + starttime_first.toString());
                                                blnShowAlert16 = true;
                                            } else {
                                                if (starttime_second.compareTo(updatedcurrenttime) == 0) {
                                                    //App.showLog(TAG+"=S2time=starttime_second=B-222=" + starttime_second.toString());
                                                    blnShowAlert16 = true;
                                                } else {
                                                    if (starttime_third.compareTo(updatedcurrenttime) == 0) {
                                                        //App.showLog(TAG+"=T3time=starttime_third=B-333=" + starttime_third.toString());
                                                        blnShowAlert16 = true;
                                                    } else {
                                                        App.showLog(TAG+"=Notime=No Match time=000=");
                                                        blnShowAlert16 = false;
                                                    }
                                                }
                                            }
                                        } catch (Exception ex2) {
                                            ex2.printStackTrace();
                                        }
                                    } else {
                                        blnShowAlert16 = false;
                                    }
                                } catch (Exception ex2) {
                                    ex2.printStackTrace();
                                }

                            }
                        }


                        if (blnShowAlert16 == true) {

                           // App.showLog(TAG+blnShowAlert16 + "==blnShowAlert16===YES==Notify - App.KEY_nid====T-16====" + App.KEY_nid);
                            App.showLog(TAG+"====Notify - App.KEY_nid====T-16====" + App.KEY_nid);

                            String fileName, title, message, imageUrl, time, type, reg_day, itllst;

                            title = strKEY_ttl;
                            message = strKEY_desc;
                            imageUrl = strKEY_img;
                            time = strKEY_timeNotifty;
                            fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

                            type = strKEY_type;
                            reg_day = strKEY_reg_day;
                            itllst = strKEY_itllst;


                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            Bitmap bitmap = BitmapFactory.decodeFile(App.strFolderNamePath + File.separator + fileName, options);
                            if (bitmap != null) {
                                result = bitmap;
                                generateNotificationOneTime(title, message, imageUrl, time, type, reg_day, itllst);
                            } else {
                                if (App.isInternetAvail(mContext) == true) {
                                    //new DownloadFileFromURL2((Activity) mContext, title, message, imageUrl, time, type, reg_day, itllst).execute(strKEY_img);
                                    new DownloadFileFromURL2(title, message, imageUrl, time, type, reg_day, itllst).execute(strKEY_img);
                                } else {
                                    generateNotificationOneTime(title, message, imageUrl, time, type, reg_day, itllst);
                                }
                            }

                        } else {
                            App.showLog(TAG+blnShowAlert16 + "==blnShowAlert16==NOT==Notify - App.KEY_nid====T-16====" + App.KEY_nid);
                        }
                    }

                    else if (strKEY_type != null && strKEY_type.equalsIgnoreCase("16")) {


                        String strTagType16 = "===Notificaiton type (16) == strKEY_type==" + strKEY_type + "===";
                        // App.showLog(TAG+strTagType16 + "==Attractions=TYPE 17==currentDateTime==" + currentDateTime + "==T-" + strKEY_type);

                        boolean blnShowAlert16 = false;

                        if (App.checkDatabaseDates(strKEY_timeNotifty, currentDateTime) == false) {
                            App.showLog(TAG+strTagType16 + strKEY_timeNotifty + "= <<1 db ====Notify DateTime Not match==== crnt 2>> =" + currentDateTime + "==T-" + strKEY_type);
                        } else {
                            if (strKEY_isNotify != null && strKEY_isNotify.equalsIgnoreCase("0")) {
                                App.showLog(TAG+strTagType16 + "===17 notify updated=====");
                                blnShowAlert16 = true;
                                db.updateFieldsNotificationTable(DatabaseUtils.TABLE_NOTIFICATION, App.KEY_isNotify, "1", App.KEY_nid, strKEY_nid, "---KEY_timeNotifty Update 1 Sucess notify--");
                            }
                        }

                        if (blnShowAlert16 == false) {
                            try {

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                String formattedDate = df.format(new Date());
                                Date date1 = df.parse(formattedDate);

                                String startdate = App.sharePrefrences.getStringPref(App.PF_FS_DATE);
                                Date date2 = df.parse(startdate);

                                String enddate = App.sharePrefrences.getStringPref(App.PF_FE_DATE);
                                Date date3 = df.parse(enddate);


                               /* 111 App.showLog(TAG+strTagType16 + "date1=> " + date1.toString());
                                App.showLog(TAG+strTagType16 + "date2=> " + date2.toString());
                                App.showLog(TAG+strTagType16 + "date3=> " + date3.toString());*/

                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                                Date starttime = sdf.parse(strKEY_time);
                                App.showLog(TAG+strTagType16 + "11111--C--" + sdf.format(starttime));

                                String formatedcurrenttime = sdf.format(new Date());
                                Date updatedcurrenttime = sdf.parse(formatedcurrenttime);

                                if (date1.compareTo(date2) > 0 && date1.compareTo(date3) < 0) {
                                    try {
                                        if (starttime.compareTo(updatedcurrenttime) == 0) {
                                            Log.e(strTagType16 + "Fire Notification---", "Time --" + starttime.toString());
                                            blnShowAlert16 = true;
                                        } else {
                                            blnShowAlert16 = false;
                                        }
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }


                                } else if (date1.compareTo(date2) == 0 || date1.compareTo(date3) == 0) {
                                    try {
                                        if (starttime.compareTo(updatedcurrenttime) == 0) {
                                            Log.e(strTagType16 + "Fire Notification---", "Time --" + starttime.toString());
                                            blnShowAlert16 = true;
                                        } else {
                                            blnShowAlert16 = false;
                                        }
                                    } catch (Exception ex2) {
                                        ex2.printStackTrace();
                                    }
                                } else {
                                    //System.out.println("How to get here?");
                                    blnShowAlert16 = false;
                                }
                            } catch (Exception ex2) {
                                ex2.printStackTrace();
                            }
                        }


                        if (blnShowAlert16 == true) {

                            //App.showLog(TAG+strTagType16 + blnShowAlert16 + "==blnShowAlert16====Notify - App.KEY_nid====T-17====" + App.KEY_nid);
                            App.showLog(TAG+strTagType16 + "====Notify - App.KEY_nid====T-16====" + App.KEY_nid);
                            //tmp_poeple.put(App.KEY_isNotify, "1");
                            String fileName, title, message, imageUrl, time, type, reg_day, itllst;

                            title = strKEY_ttl;
                            message = strKEY_desc;
                            imageUrl = strKEY_img;
                            time = strKEY_timeNotifty;
                            fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

                            type = strKEY_type;
                            reg_day = strKEY_reg_day;
                            itllst = strKEY_itllst;


                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            Bitmap bitmap = BitmapFactory.decodeFile(App.strFolderNamePath + File.separator + fileName, options);
                            if (bitmap != null) {
                                result = bitmap;
                                //generateNotificationOneTime();
                                generateNotificationOneTime(title, message, imageUrl, time, type, reg_day, itllst);
                            } else {
                                if (App.isInternetAvail(mContext) == true) {
                                    //new DownloadFileFromURL2((Activity) mContext, title, message, imageUrl, time, type, reg_day, itllst).execute(strKEY_img);
                                    new DownloadFileFromURL2(title, message, imageUrl, time, type, reg_day, itllst).execute(strKEY_img);
                                } else {
                                    generateNotificationOneTime(title, message, imageUrl, time, type, reg_day, itllst);
                                }
                            }



                        }
                    }


                    else if (strKEY_type != null && strKEY_type.equalsIgnoreCase("17")) {


                        String strTagType17 = "===Notificaiton type (17) == strKEY_type==" + strKEY_type + "===";
                       // App.showLog(TAG+strTagType17 + "==Attractions=TYPE 17==currentDateTime==" + currentDateTime + "==T-" + strKEY_type);

                        boolean blnShowAlert17 = false;

                        if (App.checkDatabaseDates(strKEY_timeNotifty, currentDateTime) == false) {
                            App.showLog(TAG+strTagType17 + strKEY_timeNotifty + "= <<1 db ====Notify DateTime Not match==== crnt 2>> =" + currentDateTime + "==T-" + strKEY_type);
                        } else {
                            if (strKEY_isNotify != null && strKEY_isNotify.equalsIgnoreCase("0")) {
                                App.showLog(TAG+strTagType17 + "===17 notify updated=====");
                                blnShowAlert17 = true;
                                db.updateFieldsNotificationTable(DatabaseUtils.TABLE_NOTIFICATION, App.KEY_isNotify, "1", App.KEY_nid, strKEY_nid, "---KEY_timeNotifty Update 1 Sucess notify--");
                            }
                        }

                        if (blnShowAlert17 == false) {
                            try {

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                String formattedDate = df.format(new Date());
                                Date date1 = df.parse(formattedDate);

                                String startdate = App.sharePrefrences.getStringPref(App.PF_FS_DATE);
                                Date date2 = df.parse(startdate);

                                String enddate = App.sharePrefrences.getStringPref(App.PF_FE_DATE);
                                Date date3 = df.parse(enddate);


                               /* 111 App.showLog(TAG+strTagType17 + "date1=> " + date1.toString());
                                App.showLog(TAG+strTagType17 + "date2=> " + date2.toString());
                                App.showLog(TAG+strTagType17 + "date3=> " + date3.toString());*/

                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                                Date starttime = sdf.parse(strKEY_time);
                                App.showLog(TAG+strTagType17 + "11111--C--" + sdf.format(starttime));

                                String formatedcurrenttime = sdf.format(new Date());
                                Date updatedcurrenttime = sdf.parse(formatedcurrenttime);

                                if (date1.compareTo(date2) > 0 && date1.compareTo(date3) < 0) {
                                    try {
                                        if (starttime.compareTo(updatedcurrenttime) == 0) {
                                            Log.e(strTagType17 + "Fire Notification---", "Time --" + starttime.toString());
                                            blnShowAlert17 = true;
                                        } else {
                                            blnShowAlert17 = false;
                                        }
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }


                                } else if (date1.compareTo(date2) == 0 || date1.compareTo(date3) == 0) {
                                    try {
                                        if (starttime.compareTo(updatedcurrenttime) == 0) {
                                            Log.e(strTagType17 + "Fire Notification---", "Time --" + starttime.toString());
                                            blnShowAlert17 = true;
                                        } else {
                                            blnShowAlert17 = false;
                                        }
                                    } catch (Exception ex2) {
                                        ex2.printStackTrace();
                                    }
                                } else {
                                    //System.out.println("How to get here?");
                                    blnShowAlert17 = false;
                                }
                            } catch (Exception ex2) {
                                ex2.printStackTrace();
                            }
                        }


                        if (blnShowAlert17 == true) {

                            //App.showLog(TAG+strTagType17 + blnShowAlert17 + "==blnShowAlert17====Notify - App.KEY_nid====T-17====" + App.KEY_nid);
                            App.showLog(TAG+strTagType17 + "====Notify - App.KEY_nid====T-16====" + App.KEY_nid);
                            //tmp_poeple.put(App.KEY_isNotify, "1");
                            String fileName, title, message, imageUrl, time, type, reg_day, itllst;

                            title = strKEY_ttl;
                            message = strKEY_desc;
                            imageUrl = strKEY_img;
                            time = strKEY_timeNotifty;
                            fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

                            type = strKEY_type;
                            reg_day = strKEY_reg_day;
                            itllst = strKEY_itllst;


                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            Bitmap bitmap = BitmapFactory.decodeFile(App.strFolderNamePath + File.separator + fileName, options);
                            if (bitmap != null) {
                                result = bitmap;
                                //generateNotificationOneTime();
                                generateNotificationOneTime(title, message, imageUrl, time, type, reg_day, itllst);
                            } else {
                                if (App.isInternetAvail(mContext) == true) {
                                    //new DownloadFileFromURL2((Activity) mContext, title, message, imageUrl, time, type, reg_day, itllst).execute(strKEY_img);
                                    new DownloadFileFromURL2(title, message, imageUrl, time, type, reg_day, itllst).execute(strKEY_img);
                                } else {
                                    generateNotificationOneTime(title, message, imageUrl, time, type, reg_day, itllst);
                                }
                            }



                        }
                    } else {

                        if (App.checkDatabaseDates(strKEY_timeNotifty, currentDateTime) == false) {
                            App.showLog(TAG+strKEY_timeNotifty + "= <<1 db ====Notify DateTime Not match==== crnt 2>> =" + currentDateTime + "==T-" + strKEY_type);
                        } else {
                            if (strKEY_isNotify != null && strKEY_isNotify.equalsIgnoreCase("0")) {
                                if (strKEY_isDaily != null && strKEY_isDaily.equalsIgnoreCase("1")) {

                                } else {
                                    db.updateFieldsNotificationTable(DatabaseUtils.TABLE_NOTIFICATION, App.KEY_isNotify, "1", App.KEY_nid, strKEY_nid, "---KEY_timeNotifty Update 1 Sucess notify--");
                                }
                                App.showLog(TAG+"====Notify - App.KEY_nid========" + App.KEY_nid);
                                //tmp_poeple.put(App.KEY_isNotify, "1");
                                String fileName, title, message, imageUrl, time, type, reg_day, itllst;

                                title = strKEY_ttl;
                                message = strKEY_desc;
                                imageUrl = strKEY_img;
                                time = strKEY_timeNotifty;
                                fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

                                type = strKEY_type;
                                reg_day = strKEY_reg_day;
                                itllst = strKEY_itllst;


                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                Bitmap bitmap = BitmapFactory.decodeFile(App.strFolderNamePath + File.separator + fileName, options);
                                if (bitmap != null) {
                                    result = bitmap;
                                    //generateNotificationOneTime();
                                    generateNotificationOneTime(title, message, imageUrl, time, type, reg_day, itllst);
                                } else {
                                    if (App.isInternetAvail(mContext) == true) {
                                        //new DownloadFileFromURL2((Activity) mContext, title, message, imageUrl, time, type, reg_day, itllst).execute(strKEY_img);
                                        new DownloadFileFromURL2(title, message, imageUrl, time, type, reg_day, itllst).execute(strKEY_img);
                                    } else {
                                        generateNotificationOneTime(title, message, imageUrl, time, type, reg_day, itllst);
                                    }
                                }

                            } else {
                                App.showLog(TAG+"===strKEY_isNotify null =======" + App.KEY_nid);
                            }
                        }
                    }
                }
                cursor.close();
            } finally {
                // cursor.close();
            }
        } catch (Exception e) {
            App.showLog(TAG+"====Notify Exception Alarm service checkDatabseDataForNotify ========");
            e.printStackTrace();
        }
    }


    public void startAt10(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60 * 20;

        /* Set the alarm to start at 10:30 AM */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 30);


        // AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        //  alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime() +60 * 1000, alarmIntent);
        /* Repeating on every 20 minutes interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, alarmIntent);
    }


    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public class DownloadFileFromURL2 extends AsyncTask<String, String, String> {


        String fileName = "usa_app.jpg";
        String imageUrl1 = "http://www.usa_app.com/upload/usa_app.jpg";
        //Activity mActivity;
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */

        String title, message, imageUrl, time, type, reg_day, itllst;

        public DownloadFileFromURL2(String title1, String message1, String imageUrl1, String time1, String type1, String reg_day1, String itllst1) {
           // mActivity = activity;
            title = title1;
            message = message1;
            imageUrl = imageUrl1;
            time = time1;
            type = type1;
            reg_day = reg_day1;
            itllst = itllst1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         */
        @SuppressLint("SdCardPath")
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {

                App.showLog(TAG+"==userImageUrl start downloading==");

                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1);
                imageUrl1 = f_url[0];
                App.showLog(TAG+"==file name is==" + fileName);


                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                File dir = new File(App.strFolderNamePath);
                try {
                    if (dir.mkdirs()) {
                        App.showLog(TAG+"Directory created");
                    } /*111 else {
                        App.showLog(TAG+"Directory is already created");
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }

                OutputStream output = new FileOutputStream(App.strFolderNamePath + File.separator + fileName);
                App.showLog(TAG+"==save image path is ==>>" + App.strFolderNamePath + File.separator + fileName);

                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    // writing data to file
                    output.write(data, 0, count);
                }
                // flushing output
                output.flush();
                // closing streams
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            App.showLog(TAG+"=On post=");
            App.showLog(TAG+"==Save path is ==" + App.strFolderNamePath + File.separator + fileName);


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(App.strFolderNamePath + File.separator + fileName, options);
            if (bitmap != null) {
                result = bitmap;
            }
            generateNotificationOneTime(title, message, imageUrl, time, type, reg_day, itllst);
        }
    }


}
