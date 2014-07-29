package com.example.commit;

import com.example.floatwindow.R;
import com.jzby.AutoScroll;

import android.R.integer;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class MyService extends Service
{
		private boolean viewAdded = false;// ͸�����Ƿ��Ѿ���ʾ
	    private boolean remoallview = false;//撤销全部的view;
	    
	 	private View view;// ͸����
	 	private View view1;
	 	private View view2;
	   
	    private WindowManager windowManager;
	    private WindowManager windowManager1;
	    private WindowManager windowManager2;
	    
	    private WindowManager.LayoutParams layoutParams;
	    private WindowManager.LayoutParams layoutParams1;
	    private WindowManager.LayoutParams layoutParams2;
	    
        private AutoScroll auto;
        private AutoScroll auto1;
        private AutoScroll auto2;
        
        private static int num = 1;
        private int changetext = -1; 
        private String TAG = "MyService";
        private final IBinder mBinder = new LocalBinder();
        public class LocalBinder  extends Binder {
        	MyService getService() {
                // Return this instance of LocalService so clients can call public methods
                return MyService.this;
            }
        	void refresh() {
                MyService.this.refresh();
            } 
        }

        @Override
        public IBinder onBind(Intent intent) {
            return mBinder;
        }

	    @Override
	    public void onCreate() 
	    {
	        super.onCreate();
	        Log.e(TAG, "onCreate");
	        Log.e(TAG, "onCreate 1");
	        view = LayoutInflater.from(this).inflate(R.layout.autoscroll, null);
	        Log.e(TAG, "onCreate 2");
	        view1 = LayoutInflater.from(this).inflate(R.layout.autoscroll1, null);
	        Log.e(TAG, "onCreate 3");
	        view2 = LayoutInflater.from(this).inflate(R.layout.autoscroll2, null);
	        Log.e(TAG, "onCreate 4");

	        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
	        layoutParams = new LayoutParams(LayoutParams.FILL_PARENT,
	                LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
	                LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
	        layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
	        layoutParams.y=200;
	        layoutParams.x=0;
	        
	        windowManager1 = (WindowManager) this.getSystemService(WINDOW_SERVICE);
	        layoutParams1 = new LayoutParams(LayoutParams.FILL_PARENT,
	                LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
	                LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT); 
	        layoutParams1.gravity = Gravity.RIGHT | Gravity.TOP;
	        layoutParams1.y=275;
	        layoutParams1.x=0;
	        
	        windowManager2 = (WindowManager) this.getSystemService(WINDOW_SERVICE);
	        layoutParams2 = new LayoutParams(LayoutParams.FILL_PARENT,
	                LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
	                LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
	        layoutParams2.gravity = Gravity.RIGHT | Gravity.TOP;
	        layoutParams2.y=350;
	        layoutParams2.x=0;
	        
	        
	        auto=(AutoScroll)view.findViewById(R.id.TextViewNotice);
	        auto1=(AutoScroll)view1.findViewById(R.id.TextViewNotice1);
	        auto2=(AutoScroll)view2.findViewById(R.id.TextViewNotice2);
	        
	        auto.setText("");
	        auto1.setText("");
	        auto2.setText("");
//	        auto.setFocusable(false);
//	        auto.setFocusableInTouchMode(false);
	        auto.setTextColor(Color.rgb(0, 255, 255));  
	        
	        auto.setBackgroundColor(Color.argb(0, 0, 255, 0)); //透明背景
	        auto1.setBackgroundColor(Color.argb(0, 0, 255, 0)); //透明背景
	        auto2.setBackgroundColor(Color.argb(0, 0, 255, 0)); 
//	        System.out.println("1111111");
	    }
	    /**
	     * �������߸����� ������û�������� ����Ѿ�����������λ��
	     */
	    private void refresh()
	    {
	    	Log.e(TAG, "refresh");
	        if (viewAdded) 
	        {
	        	System.out.print("refresh()  if (viewAdded)");
	            windowManager.updateViewLayout(view, layoutParams);
	            windowManager1.updateViewLayout(view1, layoutParams1);
	            windowManager2.updateViewLayout(view2, layoutParams2);
	        } else {
	        	System.out.print("refresh()  else ");
	        	//layoutParams.y = 300;
	            windowManager.addView(view, layoutParams); 
	            windowManager1.addView(view1, layoutParams1); 
	            windowManager2.addView(view2, layoutParams2); 
	            viewAdded = true;
	            AutoScroll autoScrollTextView = (AutoScroll)view.findViewById(R.id.TextViewNotice);
	            autoScrollTextView.init(windowManager);
	            autoScrollTextView.startScroll();
	            AutoScroll autoScrollTextView1 = (AutoScroll)view1.findViewById(R.id.TextViewNotice1);
	            autoScrollTextView1.init(windowManager1);
	            autoScrollTextView1.startScroll();
	            AutoScroll autoScrollTextView2 = (AutoScroll)view2.findViewById(R.id.TextViewNotice2);
	            autoScrollTextView2.init(windowManager2);
	            autoScrollTextView2.startScroll();
	        }
	    }
	    @Override
	    public void onStart(Intent intent, int startId) {
	        super.onStart(intent, startId);
	        refresh();
	    }
	    public void resetText(String str)
	    {
	    	
	    	changetext = num%3;
	    	Log.e(TAG, "resetText"+changetext);
	    	num++;
	    	//myremoveView();
	    	//removeView();
	    	myrestartscroll(changetext,str);
	    	
//	    	removeView();
//	    	 auto=(AutoScroll)view.findViewById(R.id.TextViewNotice);
//		     auto.setText(str);
//		     //auto.setTextColor(Color.GREEN);
//		     auto.setBackgroundColor(Color.argb(0, 0, 255, 0)); //����͸��� 
//		     refresh();
	    }
	    /**
	     *撤销滚动条;
	     */
	    private void myrestartscroll (int i,String str) 
	    {
			switch (i) 
			{
			case 0:
				Log.e(TAG, "resetText  0");
//				windowManager.removeView(view);
				auto=(AutoScroll)view.findViewById(R.id.TextViewNotice);
			    auto.setText(str);
			    auto.setBackgroundColor(Color.argb(0, 0, 255, 0));
//			    windowManager.addView(view, layoutParams);
			    AutoScroll autoScrollTextView = (AutoScroll)view.findViewById(R.id.TextViewNotice);
	            autoScrollTextView.init(windowManager);
	            autoScrollTextView.startScroll();
	            windowManager.updateViewLayout(view, layoutParams);
	            Log.e(TAG, "resetText  end 0");
				break;
			case 1:
				Log.e(TAG, "resetText  1");
//				windowManager1.removeView(view1);
				auto1=(AutoScroll)view1.findViewById(R.id.TextViewNotice1);
			    auto1.setText(str);
			    auto1.setBackgroundColor(Color.argb(0, 0, 255, 0));
//			    windowManager1.addView(view1, layoutParams1); 
			    AutoScroll autoScrollTextView1 = (AutoScroll)view1.findViewById(R.id.TextViewNotice1);
	            autoScrollTextView1.init(windowManager1);
	            autoScrollTextView1.startScroll();
	            windowManager1.updateViewLayout(view1, layoutParams1);
	            Log.e(TAG, "resetText  end 1");
	            break;
			case 2:
				Log.e(TAG, "resetText  2");
//				windowManager2.removeView(view2);
				auto2=(AutoScroll)view2.findViewById(R.id.TextViewNotice2);
			    auto2.setText(str);
			    auto2.setBackgroundColor(Color.argb(0, 0, 255, 0));
//			    windowManager2.addView(view2, layoutParams2); 
			    AutoScroll autoScrollTextView2 = (AutoScroll)view2.findViewById(R.id.TextViewNotice2);
	            autoScrollTextView2.init(windowManager2);
	            autoScrollTextView2.startScroll();
	            windowManager2.updateViewLayout(view2, layoutParams2);
	            Log.e(TAG, "resetText  end 2");
				break;
			default:
				break;
			}
			//refresh();
		}
	    public void myremoveView() 
	    {
	        if (viewAdded) 
	        {
	        	switch (changetext) 
	        	{
				case 0:
					windowManager.removeView(view);
					break;
				case 1:
					windowManager1.removeView(view1);
					break;
				case 2:
					 windowManager.removeView(view2);
					break;
				default:
					break;
				}
		    }
	    }
	    
	    public void removeView() 
	    {
	    	Log.e(TAG, "removeView");
	    	if (viewAdded) 
	        {
	    	windowManager.removeView(view);
            windowManager1.removeView(view1);
            windowManager.removeView(view2);
            viewAdded = false;
	        }
	    }
	    /**
	     * 撤销全部添加的view在推出游戏界面的时候调用这个函数;
	     * **/
	    public void removeallview() 
	    {
	    	windowManager.removeView(view);
            windowManager1.removeView(view1);
            windowManager2.removeView(view2);
            viewAdded = false;
		}
	    /**
	     * 添加全部的view,在回到游戏界面时添加弹窗调用这个函数；
	     * **/
	    public void addallview() 
	    {
	    	windowManager.addView(view, layoutParams); 
            windowManager1.addView(view1, layoutParams1); 
            windowManager2.addView(view2, layoutParams2); 
            viewAdded = true;
		}
	    
	    @Override
	    public void onDestroy()
	    {
	        super.onDestroy();
	        removeView();
	        Log.e(TAG, "onDestroy");
	        System.out.print("onDestroy() ");
	    }
}
