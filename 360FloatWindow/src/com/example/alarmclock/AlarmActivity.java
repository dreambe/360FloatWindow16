package com.example.alarmclock;


import com.example.floatwindow.R;

import android.app.Activity;
import android.app.Service;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.DialogInterface.OnClickListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.content.SharedPreferences;  
//import android.app.Service;


public class AlarmActivity extends Activity {
	private Vibrator vibrator = null;
	private MediaPlayer alarmMusic = null;
	boolean hasVibrator = false;
	boolean set_ring;
	boolean set_vibrator;
	private Button btn_game_stop = null;
	private Button btn_game_start = null;
	private Button btn_game_ignore = null;
	
	@Override
    public void onBackPressed() {
		Toast.makeText(AlarmActivity.this, "onBackPressed()",
				Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_timeup);
		this.setFinishOnTouchOutside(false);
		Bundle extras = getIntent().getExtras(); 
		set_ring = extras.getBoolean("set_ring");
		set_vibrator = extras.getBoolean("set_vibrator");
				
		// 设置振动
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		hasVibrator = vibrator.hasVibrator();
		if (hasVibrator && set_vibrator) {
			vibrator.vibrate(new long[] { 1000, 2000, 1000, 2000, 1000, 2000 },
					-1);
		}

		// 设置铃声
		if (set_ring) {
			alarmMusic = MediaPlayer.create(this, R.raw.alarm);
			alarmMusic.setLooping(true);
			alarmMusic.start();
		}

		
		btn_game_stop = (Button) findViewById(R.id.btn_game_stop);
		btn_game_stop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CloseAlarm();
			}
		});
		
		btn_game_start = (Button) findViewById(R.id.btn_game_start);
		btn_game_start.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CloseAlarm();
			}
		});
		
		btn_game_ignore = (Button) findViewById(R.id.btn_game_ignore);
		btn_game_ignore.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CloseAlarm();
				
			}
		});
		
		Toast.makeText(AlarmActivity.this, "vibrator="+set_vibrator+",ring="+set_ring,
				Toast.LENGTH_SHORT).show();

		// 显示对话框
		/*new AlertDialog.Builder(AlarmActivity.this).setTitle("闹钟").// 设置标题
				setMessage("时间到了！\tvibrator="+set_vibrator+",ring="+set_ring).// 设置内容
				setPositiveButton("知道了", new OnClickListener() {// 设置按钮
							public void onClick(DialogInterface dialog,
									int which) {
								if (hasVibrator && set_vibrator) {
									vibrator.cancel();
									set_vibrator=false;
								}
								if (set_ring)
								{
									alarmMusic.stop();
									set_ring=false;
								}
								AlarmActivity.this.finish();// 关闭Activity
							}
						}).create().show();*/

	}
	public void CloseAlarm()
	{
		if (hasVibrator && set_vibrator)
		{
			vibrator.cancel();
		}
		if (set_ring)
		{
			alarmMusic.stop();
		}
		AlarmActivity.this.finish();// 关闭Activity
	}

}
