//说明：在alarm_main.xml文件中，ListView的设置要为：
//android:layout_width="match_parent"
//android:layout_height="match_parent"
//否则发生意想不的问题：如Switch联动，SharedPreferences自动改写等问题
package com.example.alarmclock;
import android.os.Bundle;
import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.ViewGroup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import com.example.floatwindow.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

public class AlarmMainActivity extends Activity {
    public static final String EXTRA_STRING_TARGET_PKG_NAME = "target_pkgname";
	static final int SET_ALARM = 0;
	static final int CALL_REQUEST = 1;
	private int alarm_nuber = 0;
	private ListView listview;
	public MyAdapter adapter;
	private Button btn_stop = null;
	private Button btn_alarm = null;
	private TextView titleText=null;
	private AlarmManager alarmManager = null;
	final int DIALOG_TIME = 0;
	boolean hasAlarm = false;
	String packageName;
	String appName;
	//String appName = "weibo";
	
	@Override
	protected void onResume() {
		super.onResume();
		packageName=getIntent().getStringExtra(EXTRA_STRING_TARGET_PKG_NAME);
		if(packageName==null||packageName=="")
		{
			packageName="我的游戏";
		}
		appName=MyProgramPackage.getProgramNameByPackageName(this, 
				packageName);
		if(appName==null||appName=="")
		{
			appName="我的游戏";
		}
		titleText.setText(appName+"闹钟列表");
		try {
			//获得SharedPreferences数据
			alarm_nuber=GetAlarmNumberFromSharedPreferences();
			Log.e("onResume", "alarm_nuber:"+alarm_nuber);
			adapter.arr.clear();
			ArrayList<MyData> list= GetAlarmDatasFromSharedPreferences(packageName);
			for(int i=0;i<list.size();i++)
			{
				MyData t=list.get(i);
				Log.e("uuuuuuuuuuuuuuu", ""+t.arrAlarmNumber+"  "+t.open);
				adapter.arr.add(t);
			}
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Toast.makeText(AlarmMainActivity.this,
					"GetAlarmDatasFromSharedPreferences()异常", Toast.LENGTH_LONG)
					.show();
		}
		Toast.makeText(AlarmMainActivity.this, "onResume()", Toast.LENGTH_LONG)
				.show();
		//更新界面
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_main);
		titleText = (TextView) findViewById(R.id.title_text);
		titleText.setText("闹钟列表");
		View titleClose = findViewById(R.id.title_close_btn);
		titleClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		listview = (ListView) findViewById(R.id.listView1);
		adapter = new MyAdapter(this);
		listview.setAdapter(adapter);
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		//更新界面：OnCreate会调用OnResume，这里不再获取数据
		//alarm_nuber=GetAlarmNumberFromSharedPreferences();
		//adapter.notifyDataSetChanged();
		btn_alarm = (Button) findViewById(R.id.btn_alarm);
		btn_alarm.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(AlarmMainActivity.this,
						SetAlarmActivity.class);
				intent.putExtra(SetAlarmActivity.EXTRA_STRING_TARGET_PKG_NAME, getIntent().getStringExtra(EXTRA_STRING_TARGET_PKG_NAME));
				startActivityForResult(intent, SET_ALARM);
				// startActivity(intent);
			}
		});		
		Toast.makeText(AlarmMainActivity.this, "onCreate()", Toast.LENGTH_LONG)
		.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intentData) {
		if (requestCode == SET_ALARM) {
			if (resultCode == RESULT_OK) {

				//设置闹钟
				Bundle bundle = intentData.getExtras();
				int hour = bundle.getInt("hour");
				int minute = bundle.getInt("minute");
				boolean set_vibrator = bundle.getBoolean("set_vibrator");
				boolean set_ring = bundle.getBoolean("set_ring");
				long timemillis = 60 * 1000 * (hour * 60 + minute);
				bundle.putInt("alarmID", alarm_nuber);
				Intent intent = new Intent(AlarmMainActivity.this,
						AlarmActivity.class);
				intent.putExtras(bundle);
				intent.putExtra(AlarmActivity.EXTRA_STRING_TARGET_PKG_NAME, getIntent().getStringExtra(EXTRA_STRING_TARGET_PKG_NAME));
				intent.setAction("com.alarm.action_alarm_on");
				
				PendingIntent pi = PendingIntent.getActivity(
						AlarmMainActivity.this, alarm_nuber, intent, 0);
				alarmManager.set(AlarmManager.RTC_WAKEUP,
						System.currentTimeMillis() + timemillis, pi);

				Toast.makeText(
						AlarmMainActivity.this,
						"onActivityResult()  alarm_nuber=" + alarm_nuber,
						Toast.LENGTH_SHORT).show();

				//更新数据
				MyData data = new MyData();
				data.hour = hour;
				data.minute = minute;
				data.vibrator = set_vibrator;
				data.ring = set_ring;
				data.open = true;
				data.arrAlarmNumber = alarm_nuber;
				adapter.arr.add(data);
				
				try {
					PutAlarmDatasToSharedPreferences(packageName, adapter.arr);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					Toast.makeText(AlarmMainActivity.this,
							"PutAlarmDatasToSharedPreferences()异常",

							Toast.LENGTH_LONG).show();
				}
				alarm_nuber++;
				PutAlarmNumberToSharedPreferences(alarm_nuber);
				
				//onActivityResult()之后调用OnResume(),会重新从SharedPreferences读取
				//数据并刷新ListView
				//adapter.notifyDataSetChanged();		

			} else if (resultCode == RESULT_CANCELED) {

			}
		} else if (requestCode == CALL_REQUEST) {

		}
	}

	private class MyAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater inflater;
		public ArrayList<MyData> arr;

		public MyAdapter(Context context) {
			super();
			this.context = context;
			inflater = LayoutInflater.from(context);
			arr = new ArrayList<MyData>();
			/*try {
				arr = GetAlarmDatasFromSharedPreferences(appName);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				Toast.makeText(AlarmMainActivity.this,
						"GetAlarmDatasFromSharedPreferences()异常",

						Toast.LENGTH_LONG).show();
			}*/

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arr.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			
			Log.e("getView()", ""+position);
			
			if (view == null) {
				view = inflater.inflate(R.layout.alarm_list, null);
			}

			Switch switch_open = null;
			TextView timeMsg = null;
			TextView openMsg = null;
			Button button = null;

			switch_open = (Switch) view.findViewById(R.id.array_switch);
			timeMsg = (TextView) view.findViewById(R.id.array_time);
			openMsg = (TextView) view.findViewById(R.id.array_open);
			button = (Button) view.findViewById(R.id.array_button);
			MyData data = arr.get(position);
			String msg = "定时";
			if (data.hour != 0) {
				msg = msg + data.hour + "小时";
			}
			if(data.minute!=0)
			{
				msg = msg + data.minute + "分钟";
			}			
			timeMsg.setText(msg);
			if (data.open) {
				openMsg.setText("已开启");

			} else {
				openMsg.setText("已关闭");
			}
			switch_open.setChecked(data.open);
			switch_open
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							
							for(int i=0;i<arr.size();i++)
							{
								MyData t=arr.get(i);
								Log.e("Switch更改前数据", ""+t.arrAlarmNumber+"  "+t.open);
								//adapter.arr.add(t);
							}							
							
							arr.get(position).open = isChecked;
							
							System.out.println(arr.get(position));
							try {
								PutAlarmDatasToSharedPreferences(packageName, arr);
								ArrayList<MyData> l=GetAlarmDatasFromSharedPreferences(packageName);
								for(int i=0;i<l.size();i++)
								{
									MyData t=l.get(i);
									Log.e("SharedPreferences", ""+t.arrAlarmNumber+"  "+t.open);
									//adapter.arr.add(t);
								}
								
							} catch (Throwable e) {
								// TODO Auto-generated catch block
								// e.printStackTrace();
								Toast.makeText(AlarmMainActivity.this,
										"PutAlarmDatasToSharedPreferences()异常",

										Toast.LENGTH_LONG).show();
							}
							
							//加上下面这句话会发生联动效果
							adapter.notifyDataSetChanged();						
							
							if (isChecked) {
								OpenAlarm(arr.get(position));
							} else {
								CloseAlarm(arr.get(position));
							}
							
							for(int i=0;i<arr.size();i++)
							{
								MyData t=arr.get(i);
								Log.e("Switch更改后数据", ""+t.arrAlarmNumber+"  "+t.open);
								//adapter.arr.add(t);
							}
						}
					});

			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//
					System.out.println(position);
					System.out.println(arr);
					CloseAlarm(arr.get(position));
					arr.remove(position);
					try {
						PutAlarmDatasToSharedPreferences(packageName, arr);
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
						Toast.makeText(AlarmMainActivity.this,
								"PutAlarmDatasToSharedPreferences()异常",

								Toast.LENGTH_LONG).show();
					}
					
					System.out.println(arr);
					adapter.notifyDataSetChanged();

				}
			});

			// final EditText edit = (EditText) view.findViewById(R.id.edit);

			return view;
		}
	}

	//
	public void CloseAlarm(MyData data) {
		Bundle bundle = new Bundle();
		bundle.putBoolean("set_vibrator", data.vibrator);
		bundle.putBoolean("set_ring", data.ring);
		bundle.putInt("hour", data.hour);
		bundle.putInt("minute", data.minute);
		bundle.putInt("alarmID", data.arrAlarmNumber);
		Intent intent = new Intent(AlarmMainActivity.this, AlarmActivity.class);
		intent.setAction("com.alarm.action_alarm_on");
		intent.putExtras(bundle);
		intent.putExtra(AlarmActivity.EXTRA_STRING_TARGET_PKG_NAME, getIntent().getStringExtra(EXTRA_STRING_TARGET_PKG_NAME));
		
		PendingIntent pi = PendingIntent.getActivity(AlarmMainActivity.this,
				data.arrAlarmNumber, intent, 0);
		alarmManager.cancel(pi);
		Toast.makeText(AlarmMainActivity.this, "Alarm Stop", Toast.LENGTH_SHORT)
				.show();
	}

	//
	public void OpenAlarm(MyData data) {

		Bundle bundle = new Bundle();
		bundle.putBoolean("set_vibrator", data.vibrator);
		bundle.putBoolean("set_ring", data.ring);
		bundle.putInt("hour", data.hour);
		bundle.putInt("minute", data.minute);
		bundle.putInt("alarmID", data.arrAlarmNumber);
		Intent intent = new Intent(AlarmMainActivity.this, AlarmActivity.class);
		intent.setAction("com.alarm.action_alarm_on");
		intent.putExtras(bundle);
		intent.putExtra(AlarmActivity.EXTRA_STRING_TARGET_PKG_NAME, getIntent().getStringExtra(EXTRA_STRING_TARGET_PKG_NAME));
		long timemillis = 60 * 1000 * (data.hour * 60 + data.minute);
		PendingIntent pi = PendingIntent.getActivity(AlarmMainActivity.this,
				data.arrAlarmNumber, intent, 0);

		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ timemillis, pi);

		Toast.makeText(
				AlarmMainActivity.this,
				"alarm_nuber=" + alarm_nuber + ",vibrator=" + data.vibrator
						+ ",ring=" + data.ring, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<MyData> GetAlarmDatasFromSharedPreferences(String packageName)
			throws Throwable {
		ArrayList<MyData> list = new ArrayList<MyData>();
		SharedPreferences sharedPreferences = getSharedPreferences(
				"AlarmInfos", Activity.MODE_PRIVATE);
		String info = sharedPreferences.getString(packageName, "");
		if (info != "") {
			byte[] infoBytes = Base64.decode(info.getBytes(), Base64.DEFAULT);
			// byte[] infoBytes = info.getBytes();
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					infoBytes);
			ObjectInputStream objectInputStream = new ObjectInputStream(
					byteArrayInputStream);
			list = (ArrayList<MyData>) objectInputStream.readObject();
			objectInputStream.close();

		}
		return list;

	}

	public void PutAlarmDatasToSharedPreferences(String packageName,
			ArrayList<MyData> list) throws Throwable {

		/*for (int i = 0; i < list.size(); i++) {
			Log.i("PutAlarmDatasToSharedPreferences()","hour:"+list.get(i).hour);
			Log.i("PutAlarmDatasToSharedPreferences()","minute"+list.get(i).minute);
		}*/

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteArrayOutputStream);
		objectOutputStream.writeObject(list);
		objectOutputStream.flush();

		SharedPreferences sharedPreferences = getSharedPreferences(
				"AlarmInfos", Activity.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		String info = new String(Base64.encode(
				byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
		editor.putString(packageName, info);
		editor.commit();
		objectOutputStream.close();

	}

public int GetAlarmNumberFromSharedPreferences()
{
	SharedPreferences sharedPreferences = getSharedPreferences(
			"AlarmInfos", Activity.MODE_PRIVATE);
	return sharedPreferences.getInt("AlarmNumber", 0);
}
public void PutAlarmNumberToSharedPreferences(int id){
	SharedPreferences sharedPreferences = getSharedPreferences(
			"AlarmInfos", Activity.MODE_PRIVATE);
	Editor editor = sharedPreferences.edit();	
	editor.putInt("AlarmNumber", id);
	editor.commit();
}
}
