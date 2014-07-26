package com.example.alarmclock;

import com.example.floatwindow.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.NumberPicker;
import android.widget.Toast;
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
		
		//NumberPicker 控件设置
		numberPicker1=(NumberPicker)findViewById(R.id.numberPicker1);
		numberPicker1.setMaxValue(23);
		numberPicker1.setMinValue(0);
		numberPicker1.setValue(0);
		numberPicker2=(NumberPicker)findViewById(R.id.numberPicker2);
		numberPicker2.setMaxValue(59);
		numberPicker2.setMinValue(0);
		numberPicker2.setValue(0);

		//Switch控件设置
		switch_vibrate=(Switch)findViewById(R.id.switch_vibrate);
		switch_vibrate.setChecked(true);		
		switch_ring=(Switch)findViewById(R.id.switch_ring);
		switch_ring.setChecked(true);
		
		
		btn_setalarm=(Button)findViewById(R.id.btn_setalarm);
		btn_setalarm.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {		
					        	
	        /*
				int hour=numberPicker1.getValue();
				int minute=numberPicker2.getValue();
				long timemillis=60*1000*(hour*60+minute);
				alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);			
				
				Intent intent = new Intent(SetAlarmActivity.this,AlarmActivity.class);
				intent.setAction("com.alarm.action_alarm_on");
				PendingIntent pi=PendingIntent.getActivity(SetAlarmActivity.this, 0, intent, 0);				
				
				alarmManager.set(AlarmManager.RTC_WAKEUP,
						System.currentTimeMillis()+timemillis, pi);
				Toast.makeText(SetAlarmActivity.this,"定时提醒设置成功！",
						Toast.LENGTH_LONG).show();*/
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
					Toast.makeText(SetAlarmActivity.this,"无效的输入，不能设置闹钟为当前时间！",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		  
	}


}
