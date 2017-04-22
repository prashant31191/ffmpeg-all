package com.lemda.videoconvert;

/**
 * Created by Admin on 8/8/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.services.BackRecorderService;
import com.services.RecorderService;

public class CameraRecorder extends Activity implements SurfaceHolder.Callback {
    private static final String TAG = "Recorder";
    public static SurfaceView mSurfaceView;
    public static SurfaceHolder mSurfaceHolder;
    public static Camera mCamera ;
    public static boolean mPreviewRunning;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_record_camera);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Button btnStart = (Button) findViewById(R.id.StartService);
        btnStart.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(CameraRecorder.this, RecorderService.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);
               // finish();
            }
        });

        Button btnStop = (Button) findViewById(R.id.StopService);
        btnStop.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                stopService(new Intent(CameraRecorder.this, RecorderService.class));
            }
        });

        Button btnView = (Button) findViewById(R.id.btnView);
        btnView.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.i("==","--view--");
                Intent intent = new Intent(CameraRecorder.this, ActVideoCapture.class);
                startActivity(intent);
              //  finish();
            }
        });





        Button btnBackStart = (Button) findViewById(R.id.BackStartService);
        btnBackStart.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(CameraRecorder.this, BackRecorderService.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);
                finish();
            }
        });

        Button btnBackStop = (Button) findViewById(R.id.BackStopService);
        btnBackStop.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                stopService(new Intent(CameraRecorder.this, BackRecorderService.class));
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }
}
