package com.example.alarmclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.floatwindow.R;
public class SetAlarmActivity extends Activity {
	private NumberPicker numberPicker1=null;
	private NumberPicker numberPicker2=null;
	private TimePicker tp=null;
	private Button btn_setalarm=null;
	private AlarmManager alarmManager = null;
	private Switch switch_vibrate=null;
	private Switch switch_ring=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_setting);
		
		TextView title = (TextView) findViewById(R.id.title_text);
		title.setText("设置闹钟");
		View closeBtn = findViewById(R.id.title_close_btn);
		closeBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
		
		//NumberPicker
		numberPicker1=(NumberPicker)findViewById(R.id.numberPicker1);
		numberPicker1.setMaxValue(23);
		numberPicker1.setMinValue(0);
		numberPicker1.setValue(0);
		numberPicker2=(NumberPicker)findViewById(R.id.numberPicker2);
		numberPicker2.setMaxValue(59);
		numberPicker2.setMinValue(0);
		numberPicker2.setValue(0);

		//Switch
		switch_vibrate=(Switch)findViewById(R.id.switch_vibrate);
		switch_vibrate.setChecked(true);		
		switch_ring=(Switch)findViewById(R.id.switch_ring);
		switch_ring.setChecked(true);
		
		
		btn_setalarm=(Button)findViewById(R.id.btn_setalarm);
		btn_setalarm.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {		
				if(numberPicker1.getValue()+numberPicker2.getValue()!=0)
				{
				Bundle bundle = new  Bundle();
	        	bundle.putInt("hour",numberPicker1.getValue());
	        	bundle.putInt("minute",numberPicker2.getValue());
	        	bundle.putBoolean("set_vibrator",switch_vibrate.isChecked());
	        	bundle.putBoolean("set_ring",switch_ring.isChecked());
	        	SetAlarmActivity.this.setResult(RESULT_OK, SetAlarmActivity.this.getIntent().putExtras(bundle));
				SetAlarmActivity.this.finish();
				}
				else
				{
					Toast.makeText(SetAlarmActivity.this,"闹钟不能设置为当前时间！",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		  
	}


}
