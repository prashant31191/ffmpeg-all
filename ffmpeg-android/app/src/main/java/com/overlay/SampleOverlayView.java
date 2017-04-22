package com.overlay;

/**
 * Created by Admin on 10/3/2016.
 */


import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mmin18.widget.RealtimeBlurView;
import com.lemda.videoconvert.R;
import com.services.OverlayService;


public class SampleOverlayView extends OverlayView {

    private SeekBar seekBar;
    private RealtimeBlurView realtimeBlurView;
    private LinearLayout llBgLayer;
    private TextView info;
    private  String colorCode = "#00000000";

    int progress = 0;

    public SampleOverlayView(OverlayService service) {
        super(service, R.layout.overlay, 1);
    }
    public SampleOverlayView(OverlayService service,String colorCode) {
        super(service, R.layout.overlay, 1);
        this.colorCode = colorCode;
        setData();
    }


    public int getGravity() {
        return Gravity.TOP + Gravity.RIGHT;
    }

    @Override
    protected void onInflateView() {

        info = (TextView) this.findViewById(R.id.textview_info);
        llBgLayer = (LinearLayout) this.findViewById(R.id.llBgLayer);

        seekBar= (SeekBar) this.findViewById(R.id.seekBar);
        realtimeBlurView = (RealtimeBlurView) this.findViewById(R.id.realtimeBlurView);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int mProgress, boolean fromUser) {

                progress = mProgress;
                realtimeBlurView.setBlurRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setData();
    }

    private void setData()
    {

        Log.i("=Code==","set final==Color code="+colorCode);
        if(colorCode !=null && colorCode.length() >= 8) {
            llBgLayer.setBackgroundColor(Color.parseColor(colorCode));
        }
    }

/*

    @Override
    protected void refreshViews() {
        info.setText("WAITING\nWAITING");
    }

    @Override
    protected void onTouchEvent_Up(MotionEvent event) {
        info.setText("UP\nPOINTERS: " + event.getPointerCount());
    }

    @Override
    protected void onTouchEvent_Move(MotionEvent event) {
        info.setText("MOVE\nPOINTERS: " + event.getPointerCount());
    }

    @Override
    protected void onTouchEvent_Press(MotionEvent event) {
        info.setText("DOWN\nPOINTERS: " + event.getPointerCount());
    }

    @Override
    public boolean onTouchEvent_LongPress() {
        info.setText("LONG\nPRESS");

        return true;
    }
*/


}