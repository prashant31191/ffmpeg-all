package com.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.lemda.videoconvert.App;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Admin on 9/2/2016.
 */

public class DownloadFileFromURL extends AsyncTask<String, String, String> {


    String fileName = "usa_app.jpg";
    String imageUrl = "http://www.usa_app.com/upload/usa_app.jpg";
    Activity mActivity;
    DatabaseUtils db;

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */

    public DownloadFileFromURL (Activity activity){
        mActivity = activity;
        db = new DatabaseUtils(mActivity);
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

            System.out.println("==userImageUrl start downloading==");

            fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1);
            imageUrl =  f_url[0];
            System.out.println("==file name is==" + fileName);


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
                    System.out.println("Directory created");
                } else {
                    System.out.println("Directory is already created");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            OutputStream output = new FileOutputStream(App.strFolderNamePath + File.separator + fileName);
            System.out.println("==save image path is ==>>" + App.strFolderNamePath + File.separator + fileName);

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
            db.updateFieldsNotificationTable(DatabaseUtils.TABLE_NOTIFICATION, App.KEY_isDownload,"1",App.KEY_img,imageUrl,"---isDownload Update 0/1 Sucess download--");
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            db.updateFieldsNotificationTable(DatabaseUtils.TABLE_NOTIFICATION, App.KEY_isDownload,"0",App.KEY_img,imageUrl,"---isDownload Update 0/1 Fail download--");
        }
        return null;
    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     **/
    @Override
    protected void onPostExecute(String file_url) {
        System.out.println("=On post=");
        System.out.println("==Save path is ==" + App.strFolderNamePath + File.separator + fileName);
        //String table_name,String setColumnName,String setColumnValues,String whereColName,String whereColValues, String UpdateTag
        db.close();
    }
}
