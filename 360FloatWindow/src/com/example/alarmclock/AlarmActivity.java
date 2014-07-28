package com.example.alarmclock;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

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
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;  
//import android.app.Service;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;


public class AlarmActivity extends Activity {
	private Vibrator vibrator = null;
	private MediaPlayer alarmMusic = null;
	boolean hasVibrator = false;
	boolean set_ring;
	boolean set_vibrator;
	private Button btn_game_stop = null;
	private Button btn_game_start = null;
	private Button btn_game_ignore = null;
	private TextView textView_gameName=null;
	private ImageView imageView_gameIcon=null;
	int alarmID;
	
	public String appName;
	public String packageName;
	public static final String EXTRA_STRING_TARGET_PKG_NAME = "target_pkgname";
		
	//返回键的响应：这里设置为没有响应
	@Override
    public void onBackPressed() {
		//Toast.makeText(AlarmActivity.this, "onBackPressed()",
		//		Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_timeup);		
		//在窗口外点击事件：设置为没有响应
		this.setFinishOnTouchOutside(false);
		
		//从intent中获得传递过来的数据
		Bundle extras = getIntent().getExtras(); 
		set_ring = extras.getBoolean("set_ring");
		set_vibrator = extras.getBoolean("set_vibrator");
		alarmID=extras.getInt("alarmID");
		appName="";
		packageName=getIntent().getStringExtra(EXTRA_STRING_TARGET_PKG_NAME);
		if(packageName==null||packageName=="")
		{
			packageName="我的游戏";
		}
		appName=MyProgramPackage.getProgramNameByPackageName(this, packageName);
		
		//设置Activity显示内容		
		textView_gameName=(TextView)findViewById(R.id.textView_gameName);
		if(appName==""||appName==null)
		{
			appName="我的游戏";
		}
		textView_gameName.setText(appName);
		imageView_gameIcon=(ImageView)findViewById(R.id.imageView_gameIcon);
		imageView_gameIcon.setImageDrawable(MyProgramPackage.getProgramIconByPackageName(this, packageName));
		
		// 设置振动
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		hasVibrator = vibrator.hasVibrator();
		if (hasVibrator && set_vibrator) {
			vibrator.vibrate(new long[] { 1000, 2000, 1000, 2000, 1000, 2000 },
					-1);
		}

		// 设置响铃
		if (set_ring) {
			alarmMusic = MediaPlayer.create(this, R.raw.alarm);
			//alarmMusic.setLooping(true);
			alarmMusic.start();
		}

		//结束游戏
/*		btn_game_stop = (Button) findViewById(R.id.btn_game_stop);
		btn_game_stop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MyProgramPackage.CloseProgramByPackageName(AlarmActivity.this, packageName);
				CloseAlarm();
			}
		});*/
		
		//进入游戏
		btn_game_start = (Button) findViewById(R.id.btn_game_start);
		btn_game_start.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MyProgramPackage.StartProgramByPackageName(AlarmActivity.this, packageName);
				CloseAlarm();
			}
		});
		
		//忽略消息
		btn_game_ignore = (Button) findViewById(R.id.btn_game_ignore);
		btn_game_ignore.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CloseAlarm();
				
			}
		});
	/*	
		Toast.makeText(AlarmActivity.this, "vibrator="+set_vibrator+",ring="+set_ring,
				Toast.LENGTH_SHORT).show();
*/
	}
	
	//关闭闹钟并关闭闹钟显示界面
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
		try {
			UpdateAlarmDatasToSharedPreferences(packageName,alarmID);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		AlarmActivity.this.finish();//关闭Activity
	}
	
	//闹钟关闭后更新SharedPreferences数据
	public void UpdateAlarmDatasToSharedPreferences(String packageName,int alarmID)throws Throwable
	{
		ArrayList<MyData> list = new ArrayList<MyData>();
		SharedPreferences sharedPreferences = getSharedPreferences(
				"AlarmInfos", Activity.MODE_PRIVATE);
		String info = sharedPreferences.getString(packageName, "");
		if (info != "") {
			byte[] infoBytes = Base64.decode(info.getBytes(), Base64.DEFAULT);
			//byte[] infoBytes = info.getBytes();
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					infoBytes);
			ObjectInputStream objectInputStream = new ObjectInputStream(
					byteArrayInputStream);
			list = (ArrayList<MyData>) objectInputStream.readObject();
			objectInputStream.close();
			
			for(int i=0;i<list.size();i++)
			{				
				if(list.get(i).arrAlarmNumber==alarmID)
				{
					list.get(i).open=false;					
					break;
				}
				//用于显示闹钟信息
				//a+="  第"+list.get(i).arrAlarmNumber+"个"+list.get(i).open;
			}
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					byteArrayOutputStream);
			objectOutputStream.writeObject(list);
			objectOutputStream.flush();			
			
			Editor editor = sharedPreferences.edit();
			String info2 = new String(Base64.encode(
					byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
			editor.putString(packageName, info2);
			editor.commit();
			objectOutputStream.close();

		}
	}

}
