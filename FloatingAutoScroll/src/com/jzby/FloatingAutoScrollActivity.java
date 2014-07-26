package com.jzby;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;



import com.jzby.MyService;
//import com.jzby.MyService1;
//import com.jzby.MyService1.LocalBinder1;
//import com.jzby.MyService.LocalBinder;
import com.jzby.MyService.LocalBinder;



public class FloatingAutoScrollActivity extends Activity 
{
	 MyService m_mMyService = null;
	 //MyService1 m_mMyService1 = null;
	 boolean mBound = false; 
	 boolean mBound1 = false;
	 Button m_bnt_genhuan = null;
	 Button m_bnt_remo = null;
	 Button m_bnt_add = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        m_bnt_genhuan = (Button)findViewById(R.id.buttongenghuan);
        m_bnt_remo = (Button)findViewById(R.id.button1);
        m_bnt_add = (Button)findViewById(R.id.button2);
//        Intent intent = new Intent(FloatingAutoScrollActivity.this, MyService.class);
//    	startService(intent);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
        
        
        Thread newThread = new Thread(new Runnable() {
            @Override
                    public void run() {
                    //����д�����߳���Ҫ��Ĺ���
            	 Intent intent = new Intent(FloatingAutoScrollActivity.this, MyService.class);
            	startService(intent);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
                    }
                });
            newThread.start(); //���߳�
            
            
//         Thread newThread1 = new Thread(new Runnable() {
//                @Override
//                        public void run() {
//                        //����д�����߳���Ҫ��Ĺ���
//                	 Intent intent = new Intent(FloatingAutoScrollActivity.this, MyService1.class);
//                	startService(intent);
//                    bindService(intent, mConnection1, Context.BIND_AUTO_CREATE); 
//                        }
//                    });
//                newThread1.start(); //���߳�
//        startService(intent);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
        
        m_bnt_genhuan.setOnClickListener(new View.OnClickListener()
        {
	        @Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				m_mMyService.resetText("NEW TEXT");
			}
        });
        
        m_bnt_remo.setOnClickListener(new View.OnClickListener()
        {
	        @Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				m_mMyService.removeallview();
			}
        });
        
        m_bnt_add.setOnClickListener(new View.OnClickListener()
        {
	        @Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				m_mMyService.addallview();
			}
        });
     }
    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false; 
        }
        if (mBound1) {
            unbindService(mConnection1);
            mBound1 = false; 
        }
    }
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            binder.refresh();
            m_mMyService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    
    private ServiceConnection mConnection1 = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            //LocalBinder1 binder1 = (LocalBinder1) service;
           // binder1.refresh();
           // m_mMyService1 = binder1.getService();
            mBound1 = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound1 = false;
        }
    };
	
    
}