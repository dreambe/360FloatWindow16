package com.example.commit;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.TypeVariable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Types;


import android.app.Activity;
import android.content.ComponentName;

import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import org.apache.commons.logging.Log;

//import com.android.socket.utils.StreamTool;
//import com.android.upload.IOException;
//import com.android.upload.PrintWriter;
//import com.android.upload.UnknownHostException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.commit.MarqueeText;
import com.example.commit.MyService;
import com.example.commit.MyService.LocalBinder;
import com.example.floatwindow.R;


import com.jzby.AutoScroll;


public class CommitActivity extends Activity 
{
    private Socket socket =null;
    private Thread mThread = null;
    private byte [] recive_buffer = new byte[1024];
    private OutputStream outputStream=null;
    private InputStream inputStream=null;
    private String str_recive;
    private EditText ip_server =null;
    private EditText port_server =null;
    private EditText socket_send =null;
    private EditText socket_recive =null;
    private MarqueeText paomadengtTextView = null;
    private MarqueeText paomadengtTextView1 = null;
    private boolean is_connect = false;

    private Button btn_connect = null;
    private Button btn_close=null;
    private Button btn_send=null;
    int temp_connect;
    private MyReadThread myReadThread = null;
    private Handler mHandler;
    public MyService m_mMyService = null;
    boolean mBound = false; 

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit);
        ip_server =(EditText)findViewById(R.id.editText);
        port_server =(EditText)findViewById(R.id.editText2);
        socket_send =(EditText)findViewById(R.id.editText3);
        socket_recive =(EditText)findViewById(R.id.editText4);
        paomadengtTextView1 = (MarqueeText)findViewById(R.id.textView6);
        paomadengtTextView = (MarqueeText)findViewById(R.id.textView5);
        mHandler = new Handler();
        btn_connect = (Button)findViewById(R.id.button);
        btn_close=(Button)findViewById(R.id.buttonSTOP);
        btn_send=(Button)findViewById(R.id.button3);
        btn_connect.setEnabled(true);
        btn_close.setEnabled(false);
        btn_send.setEnabled(false);
        paomadengtTextView.setBackgroundColor(Color.argb(0, 0, 255, 0));
        paomadengtTextView1.setBackgroundColor(Color.argb(0, 0, 255, 0));
        //socket_recive.setBackgroundColor(Color.argb(0, 0, 255, 0)); //锟斤拷锟斤拷透锟斤拷锟�
//        
//        Intent intent = new Intent(MainActivity.this, MyService.class);
//    	//startService(intent);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
        //杩欓噷璧蜂竴涓嚎绋嬪仛寮瑰箷锛�
        Thread newThread = new Thread(new Runnable() {
            @Override
                    public void run() {
                    //锟斤拷锟斤拷写锟斤拷锟斤拷锟竭筹拷锟斤拷要锟斤拷墓锟斤拷锟�
            	 Intent intent = new Intent(CommitActivity.this, MyService.class);
            	//startService(intent);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
                
                    }
                });
            newThread.start();
            //debug鍒拌繖閲屽彂鐜板凡缁忓惎鍔紝浣嗘槸寮瑰箷娌℃湁鍑烘潵銆�
        port_server.setText("1000");
        ip_server.append("192.168.16.2");
        btn_connect.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {
                String Ip_address=ip_server.getText().toString();
                String Ip_port=port_server.getText().toString();
                Ip_address.trim();
                Ip_port.trim();
                if(Ip_address.length()==0||Ip_port.length()==0)
                    System.out.println("ip_server edit is empty or port_server is empty!");
                else
                {
                	MyConnectThread thread = new MyConnectThread();
                	thread.start();
                	try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	if (is_connect)
                	{
						btn_close.setEnabled(true);
               	 		btn_connect.setEnabled(false);
               	 		btn_send.setEnabled(true);
               	 		myReadThread = new MyReadThread();
               	 		myReadThread.start();
					}             
                }
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) 
            {              
                try
                {
	                is_connect = false;
	                socket.shutdownInput();
	                socket.shutdownOutput();
	                socket.close();
	                btn_close.setEnabled(false);
	                btn_connect.setEnabled(true);
	                btn_send.setEnabled(false);
	                socket_recive.setText("");
	                socket_send.setText("");
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener()
        {
	        public void onClick(View v)
	        {
	        	String sendstr = socket_send.getText().toString();
	        	sendstr +='\n';
	           	MySendThread thread = new MySendThread(sendstr);
	        	thread.start();
	            socket_send.setText("");
	        }
        });
    }
    
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
    
    void settext(String str_rec)
    {
		socket_recive.setText(str_rec);
		String str_bef = "                                                         ";
		String str_end = "";
		str_rec = str_bef+str_rec+str_end;
		paomadengtTextView.setText(str_rec);
		paomadengtTextView1.setText(str_rec);
		//m_mMyService.resetText(str_rec);
		paomadengtTextView.setFocusable(true);
		paomadengtTextView.setEllipsize(TruncateAt.MARQUEE);
    }
    //end of onCreate;
    //锟竭筹拷:锟斤拷锟斤拷锟斤拷锟斤拷锟�锟斤拷锟斤拷息
    class MyReadThread  extends Thread
    {
    	public String str_rec;
    	public  MyReadThread() 
    	{
			
		}
        public void run()
        {
        	//socket_recive = (EditText)findViewById(R.id.editText4);
            while (true)
            {
                try
                {
                    if(is_connect)
                    {
                        temp_connect = inputStream.read(recive_buffer,0,recive_buffer.length);
                        if(temp_connect!=-1)
                        {
                            str_recive = new String(recive_buffer,0,temp_connect);
                            //str_recive.trim();
                            str_rec = str_recive;
                            //settext(str_rec);
                            //鍦ㄨ繖閲屽凡缁忔帴鍒版湇鍔＄杩斿洖鐨勬秷鎭�
                            mHandler.post(new Runnable() {

								@Override
								public void run()
								{
									settext(str_rec);
									//鍙湁qq杩欎釜鍔熻兘鐨勬椂鍊欒繖閲屾槸鍙互鏄剧ず鎺ユ敹鍒扮殑娑堟伅鐨勩�
									//socket_recive.setText(str_rec);
//									paomadengtTextView.setText(str_rec);
									//鍔犱笂杩欏彞涔嬪悗灏变笉琛屼簡锛屽脊骞曞嚭涓嶆潵銆�
									//m_mMyService.resetText(str_rec);
								}});  
                        }
                        else 
                        {
                            socket.shutdownInput();
                            socket.shutdownOutput();
                            inputStream.close();
                            outputStream.close();
                            socket.close();
                            is_connect=false;
                        }        
                   }
                    Thread.sleep(100);  
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };

    class MySendThread extends Thread
     {
    	 private String str_send;
    	 private OutputStream outputStream=null;
 	   	 public MySendThread (String str) 
    	 {
			this.str_send = str;
		}
    	 public void run()
      	{
      		try 
            {
            	str_send.trim();
                if(str_send.length()==0)
                    System.out.println("send edit is empty!");
                else
                    {
                    byte [] send_buffer = str_send.getBytes(); 
                    try{
                    	if(socket.isConnected()==true)
                    	{
                    	  outputStream = socket.getOutputStream();
                          inputStream = socket.getInputStream();
                          outputStream.write(send_buffer, 0, send_buffer.length);
                          outputStream.flush();
                         }
                       }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
      		} catch (Exception e) 
      		{
      			System.out.println("Send Exception!");
      			Toast toast = Toast.makeText(CommitActivity.this,"l锟斤拷锟斤拷锟斤拷斐�锟斤拷锟斤拷锟角凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷址锟斤拷锟斤拷",Toast.LENGTH_LONG);
      		} 
         }
     }
    
    class MyConnectThread extends Thread
    {
   
	   	private String Ip_address=ip_server.getText().toString();
	   	private String Ip_port=port_server.getText().toString();
    	
    	public MyConnectThread()
    	{
    		
    	}
    	public void run()
    	{
    		//实锟斤拷Socket  
            try 
            {
           	 int port= Integer.parseInt(Ip_port);
           	 socket = new Socket(Ip_address, port);
           	 is_connect = true;
             } catch (Exception e) 
    		{
    			System.out.println("connect Exception!");
    			Toast toast = Toast.makeText(CommitActivity.this,"l锟斤拷锟斤拷锟斤拷斐�锟斤拷锟斤拷锟角凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷址锟斤拷锟斤拷",Toast.LENGTH_LONG);
    		} 
       }
    }

}
