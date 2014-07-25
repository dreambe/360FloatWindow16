package com.example.alarmclock;

import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.ViewGroup;

import java.util.ArrayList;
import com.example.floatwindow.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmMainActivity extends Activity {
	static final int SET_ALARM = 0; 
	static final int CALL_REQUEST = 1;
	static private int alarm_nuber=0;
	private ListView listview;  
	public MyAdapter adapter;
	private Button btn_stop = null;
	private Button btn_alarm = null;

	private AlarmManager alarmManager = null;
	final int DIALOG_TIME = 0; // 设置对话框id
	boolean hasAlarm = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_main);
		
		listview = (ListView) findViewById(R.id.listView1);  
		adapter = new MyAdapter(this);  
		listview.setAdapter(adapter); 		
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		adapter.notifyDataSetChanged();
		//删除按钮，暂时无用，需要删除
		btn_stop = (Button) findViewById(R.id.btn_stop);
		btn_stop.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				
				Intent intent = new Intent(AlarmMainActivity.this,AlarmActivity.class);
				intent.setAction("com.alarm.action_alarm_on");
				PendingIntent pi=PendingIntent.getActivity(AlarmMainActivity.this, 0, intent, 0);
				
				alarmManager.cancel(pi);
				Toast.makeText(AlarmMainActivity.this, "Alarm Stop",
						Toast.LENGTH_SHORT).show();

			}
		});

		//设置闹钟
		btn_alarm = (Button) findViewById(R.id.btn_alarm);
		btn_alarm.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(AlarmMainActivity.this,
						SetAlarmActivity.class);
				startActivityForResult(intent,SET_ALARM);
				//startActivity(intent);
			}
		});

	}
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent intentData) { 
	    if (requestCode == SET_ALARM) { 
	        if (resultCode == RESULT_OK) {      	
	        	
	        	//设置闹钟
	        	Bundle bundle = intentData.getExtras(); 
	        	int hour=bundle.getInt("hour");
	        	int minute=bundle.getInt("minute");
	        	boolean set_vibrator=bundle.getBoolean("set_vibrator");
	        	boolean set_ring=bundle.getBoolean("set_ring");
	        	long timemillis=60*1000*(hour*60+minute);
	        	
	        	Intent intent = new Intent(AlarmMainActivity.this,AlarmActivity.class);
	        	intent.putExtras(bundle);
				intent.setAction("com.alarm.action_alarm_on");
				PendingIntent pi=PendingIntent.getActivity(AlarmMainActivity.this, alarm_nuber, intent, 0);
				
				alarmManager.set(AlarmManager.RTC_WAKEUP,
						System.currentTimeMillis()+timemillis, pi);	
				
				Toast.makeText(AlarmMainActivity.this, "alarm_nuber="+alarm_nuber+",vibrator="+set_vibrator+",ring="+set_ring,
						Toast.LENGTH_SHORT).show();
				
				//更新闹钟列表
				MyData data=new MyData();
				data.hour=hour;
				data.minute=minute;
				data.vibrator=set_vibrator;
				data.ring=set_ring;
				data.open=true;
				data.arrAlarmNumber=alarm_nuber;
				adapter.arr.add(data);  
				adapter.notifyDataSetChanged(); 
				
				alarm_nuber++;
				
	        }else if (resultCode == RESULT_CANCELED) { 
	            
	        } 
	    }else if (requestCode == CALL_REQUEST) { 
	        
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
        //arr2 = new ArrayList<String>(); 
        //for(int i=0;i<3;i++){    //listview初始化3个子项  
         //   arr.add("这个数是："+i*i); 
         //   arr2.add("这个数的编号是："+i); 
        //}  
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
        if(view == null){  
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
		MyData data=arr.get(position);
		String msg="定时";
		if(data.hour!=0)
		{
			msg=msg+data.hour+"时";
		}
		msg=msg+data.minute+"分";
		timeMsg.setText(msg);
		if(data.open)
		{
			openMsg.setText("已开启");
			
		}
		else
		{
			openMsg.setText("已关闭");
		}
		switch_open.setChecked(data.open);
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(AlarmMainActivity.this, "您点击的第" + position + "个按钮",
						Toast.LENGTH_LONG).show();
				// TODO Auto-generated method stub  
                //从集合中删除所删除项的EditText的内容  
				CloseAlarm(arr.get(position));
                arr.remove(position);                
                adapter.notifyDataSetChanged();                 

			}
		});			
		
        //final EditText edit = (EditText) view.findViewById(R.id.edit);  
        
       return view;  
    }  
}  
public class MyData{
	int hour;
	int minute;
	boolean open;
	boolean ring;
	boolean vibrator;
	int arrAlarmNumber;
}
//根据编号关闭闹钟
public void CloseAlarm(MyData data)
{
	Bundle bundle=new Bundle();
	bundle.putBoolean("set_vibrator", data.vibrator);
	bundle.putBoolean("set_ring", data.ring);
	bundle.putInt("hour", data.hour);
	bundle.putInt("minute", data.minute);
	
	Intent intent = new Intent(AlarmMainActivity.this,AlarmActivity.class);
	intent.setAction("com.alarm.action_alarm_on");
	intent.putExtras(bundle);
	PendingIntent pi=PendingIntent.getActivity(AlarmMainActivity.this, data.arrAlarmNumber, intent, 0);	
	alarmManager.cancel(pi);
	Toast.makeText(AlarmMainActivity.this, "Alarm Stop",
			Toast.LENGTH_SHORT).show();
}
//开启闹钟
public void OpenAlarm(MyData data)
{
	
	Bundle bundle=new Bundle();
	bundle.putBoolean("set_vibrator", data.vibrator);
	bundle.putBoolean("set_ring", data.ring);
	bundle.putInt("hour", data.hour);
	bundle.putInt("minute", data.minute);	
	Intent intent = new Intent(AlarmMainActivity.this,AlarmActivity.class);
	intent.setAction("com.alarm.action_alarm_on");
	intent.putExtras(bundle);
	
	long timemillis=60*1000*(data.hour*60+data.minute);
	PendingIntent pi=PendingIntent.getActivity(AlarmMainActivity.this, data.arrAlarmNumber, intent, 0);
	
	alarmManager.set(AlarmManager.RTC_WAKEUP,
			System.currentTimeMillis()+timemillis, pi);	
	
	Toast.makeText(AlarmMainActivity.this, "alarm_nuber="+alarm_nuber+",vibrator="+data.vibrator+",ring="+data.ring,
			Toast.LENGTH_SHORT).show();
}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
