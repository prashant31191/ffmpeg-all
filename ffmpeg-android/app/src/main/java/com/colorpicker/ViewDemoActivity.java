package com.colorpicker;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lemda.videoconvert.R;
import com.services.SampleOverlayService;
import com.rarepebble.colorpicker.ColorPickerView;

public class ViewDemoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_demo);
		ComponentName cn = getComponentName();

		final ColorPickerView picker = (ColorPickerView)findViewById(R.id.colorPicker);
		picker.setColor(0xffff0000);

		Button btnDone = (Button)findViewById(R.id.btnDone);
		btnDone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String hexColor = String.format("#%08X", (0xFFFFFFFF & picker.getColor()));
			//	Toast.makeText(getApplicationContext(),"Color code  = "+hexColor,Toast.LENGTH_SHORT).show();



				char  code_first_char = hexColor.charAt(1);
				if(isNumeric(""+code_first_char))
				{
					SampleOverlayService.stop();
					stopService(new Intent(ViewDemoActivity.this, SampleOverlayService.class));
					Toast.makeText(getApplicationContext(),"Color code =  '"+hexColor+"'  Applied.",Toast.LENGTH_SHORT).show();
				//SampleOverlayService sampleOverlayService = new SampleOverlayService();
					Intent intentService = new Intent(ViewDemoActivity.this, SampleOverlayService.class);
					intentService.putExtra("colorCode",hexColor);
					startService(intentService);
				}
				else
				{
					Toast.makeText(getApplicationContext(),"Color code =  '"+hexColor+"'  not applied, Please reduce transparency..",Toast.LENGTH_SHORT).show();
				}
			}
		});


		Button btnStop = (Button) findViewById(R.id.btnStop);
		btnStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				SampleOverlayService.stop();
				stopService(new Intent(ViewDemoActivity.this, SampleOverlayService.class));
				Toast.makeText(getApplicationContext(),"Stop overlay.",Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static boolean isNumeric(String str)
	{
		try
		{
			double d = Double.parseDouble(str);
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}
}
