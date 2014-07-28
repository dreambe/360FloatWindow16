package com.example.killprocess;

import com.example.floatwindow.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class JiaSuActivity extends Activity {

	ImageView image;
	TextView jiasuTV;
	Handler handler;
	private static final int MSG_JIASU_END = 1;  // 加速结束
	private static final int MSG_DESTROY = 2;    // 加速完成图画显示1秒钟结束

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.jiasu_activity); 
        image=(ImageView)findViewById(R.id.imageView);
        jiasuTV=(TextView)findViewById(R.id.jiasuTV);
       
        handler = new Handler(){
        	 @Override
             public void handleMessage(Message msg) {
        		 switch(msg.what)
        		 {
        		 case MSG_JIASU_END:
        			 image.clearAnimation();
            		 image.setImageDrawable(getResources().getDrawable(R.drawable.accelerate_end));
            		 jiasuTV.setVisibility(View.INVISIBLE);
            		 handler.postDelayed(new FinishThread(), 1500);
        			 break;
        		 case MSG_DESTROY:
        			 finish();
        			 break;
        		 }
        		
        	 }        	
        };
        
        
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.jiasu_rotate);  
        LinearInterpolator lin = new LinearInterpolator();  
        operatingAnim.setInterpolator(lin);  
        image.startAnimation(operatingAnim); 
        handler.postDelayed(new KillThread(), 3000);
        
    }
    
    
    
    class FinishThread extends Thread{
    	public void run(){
    		handler.sendEmptyMessage(MSG_DESTROY);
    	}
    }
    
    class KillThread extends Thread{
    	public void run(){
            KillProcess mykill = new KillProcess();
            mykill.killAll(JiaSuActivity.this);
            handler.sendEmptyMessage(MSG_JIASU_END);
        }	
    }
    
    
    
}