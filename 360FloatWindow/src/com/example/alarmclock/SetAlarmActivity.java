package com.example.alarmclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.floatwindow.R;
public class SetAlarmActivity extends Activity {
    public static final String EXTRA_STRING_TARGET_PKG_NAME = "target_pkgname";
	private NumberPicker numberPicker1=null;
	private NumberPicker numberPicker2=null;
	private TimePicker tp=null;
	private Button btn_setalarm=null;
	private AlarmManager alarmManager = null;
	private Switch switch_vibrate=null;
	private Switch switch_ring=null;
	private ImageView mAppIconImage = null;
	private AsyncTask<String, Void, Drawable> mLoadIconTask;
	private Animation mExpandAlphaInAnim;
	
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
		
		mAppIconImage = (ImageView) findViewById(R.id.app_icon);
		
		//NumberPicker �ؼ�����
		numberPicker1=(NumberPicker)findViewById(R.id.numberPicker1);
		numberPicker1.setMaxValue(23);
		numberPicker1.setMinValue(0);
		numberPicker1.setValue(0);
		numberPicker2=(NumberPicker)findViewById(R.id.numberPicker2);
		numberPicker2.setMaxValue(59);
		numberPicker2.setMinValue(0);
		numberPicker2.setValue(0);

		//Switch�ؼ�����
		switch_vibrate=(Switch)findViewById(R.id.switch_vibrate);
		switch_vibrate.setChecked(true);		
		switch_ring=(Switch)findViewById(R.id.switch_ring);
		switch_ring.setChecked(true);
		
		mExpandAlphaInAnim = AnimationUtils.loadAnimation(this, R.anim.expand_alpha_in);
		
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
				Toast.makeText(SetAlarmActivity.this,"��ʱ�������óɹ���",
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
					Toast.makeText(SetAlarmActivity.this,"��Ч�����룬������������Ϊ��ǰʱ�䣡",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		mLoadIconTask = new AsyncTask<String, Void, Drawable>() {

            @Override
            protected Drawable doInBackground(String... arg0) {
                PackageManager pm = getPackageManager();
                String pkgName = arg0[0];
                try {
                    Drawable icon = pm.getApplicationIcon(pkgName);
                    return icon;
                } catch (NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                return null;
            }
            
            @Override
            protected void onPostExecute(Drawable result) {
                if (isFinishing()) {
                    return;
                }
                if (result == null) {
                    mAppIconImage.setImageResource(R.drawable.icon_other_app_default);
                } else {
                    mAppIconImage.setImageDrawable(result);
                }
                mAppIconImage.startAnimation(mExpandAlphaInAnim);
                mLoadIconTask = null;
            }
		};
		mLoadIconTask.execute(getIntent().getStringExtra(EXTRA_STRING_TARGET_PKG_NAME));
	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (mLoadIconTask != null) {
	        mLoadIconTask.cancel(true);
	        mLoadIconTask = null;
	    }
	}
}
