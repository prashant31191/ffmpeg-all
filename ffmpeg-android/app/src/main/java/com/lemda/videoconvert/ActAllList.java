package com.lemda.videoconvert;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.utils.ScreentShotUtil;

import java.util.ArrayList;

/**
 * Created by Admin on 8/11/2016.
 */

public class ActAllList extends Activity
{
    ImageView ivBgImage;
    ListView lvActivities;
    ArrayList<ActivityModel> arrListActivityModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);

        try{
            System.out.println("=====1111=====");
            initViews();

            getActivityList();
           App.constructScreenshotImage(ActAllList.this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    private void initViews() {
        lvActivities = (ListView)findViewById(R.id.lvActivities);
        ivBgImage = (ImageView) findViewById(R.id.ivBgImage);



        lvActivities.setFastScrollEnabled(true);
        lvActivities.setVisibility(View.VISIBLE);
        ivBgImage.setVisibility(View.GONE);

        arrListActivityModel = new ArrayList<>();

    }

    private void getActivityList() {
        try {
            System.out.println("=====2222=====");
         //   Intent ii = new Intent(Intent.ACTION_MAIN);
           // ii.addCategory(Intent.CATEGORY_LAUNCHER);
            PackageManager pm = this.getPackageManager();
//com.lemda.videoconvert


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

                    ActivityModel activityModel = new ActivityModel();

                    activityModel.activityName = list[i].name;
                    activityModel.className = Class.forName(list[i].name);

                    arrListActivityModel.add(activityModel);
                }
                if(arrListActivityModel !=null && arrListActivityModel.size() >0)
                {
                    AdapterActivtyList adapterActivtyList = new AdapterActivtyList(ActAllList.this,arrListActivityModel);
                    lvActivities.setAdapter(adapterActivtyList);
                }


            }
        }
        catch (Exception e)
        {
            System.out.println("====222=Error=====");
            e.printStackTrace();
        }
    }


    public class AdapterActivtyList extends BaseAdapter {
        private LayoutInflater inflater;
        Context mContext;
        ArrayList<ActivityModel> mArrListActivityModel;

        public AdapterActivtyList(Context context, ArrayList<ActivityModel> arrListActivityModel) {
            mContext = context;
            mArrListActivityModel = arrListActivityModel;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mArrListActivityModel.size();
        }

        @Override
        public Object getItem(int position) {
            return mArrListActivityModel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mArrListActivityModel.size();
        }


        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.raw_activity_list, null);
                holder = new ViewHolder();
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            String activityName = mArrListActivityModel.get(position).activityName;

            if( activityName != null && activityName.length() > 0)
            {
                holder.tvName.setText(activityName);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Class classData = mArrListActivityModel.get(position).className;
                    Intent intent = new Intent(mContext,classData);
                    startActivity(intent);
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView tvName;

        }
    }



    private class ActivityModel
    {
        String activityName;
        Class className;
    }
}
