package com.overlay;

import android.app.Activity;
import android.os.Bundle;

import com.services.SampleOverlayService;

/**
 * Created by Admin on 10/3/2016.
 */

public class SampleOverlayHideActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SampleOverlayService.stop();

        finish();

    }

}
