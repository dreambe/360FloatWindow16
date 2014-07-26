package com.jzby;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AutoScroll extends TextView {
	 private float textLength = 0f;//�ı�����
	 private float viewWidth = 0f;
	 private float step = 0f;//���ֵĺ����
	 private float y = 0f;//���ֵ������
	 private float temp_view_plus_text_length = 0.0f;//���ڼ������ʱ��
	 private float temp_view_plus_two_text_length = 0.0f;//���ڼ������ʱ��
	 public boolean isStarting = false;//�Ƿ�ʼ��
	 private Paint paint = null;//��ͼ��ʽ
	 private String text = "";//�ı�����
	 Canvas acanvas;
	 
	 
	 private Handler handler = new Handler()
	    {
	    	@Override
			public void handleMessage(Message msg) 
	    	{
	    		invalidate();
			}
	    	
	    };
	public AutoScroll(Context context)
	{
		super(context);
//		initView();
	}
	public AutoScroll(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
//		initView();
	}
	public AutoScroll(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
//		initView();
	}
	
//	public void onClick(View v) {
//		 if(isStarting)
//	            stopScroll();
//	        else
//	            startScroll();
//	}
//	 private void initView()
//	    {
//	        setOnClickListener(this);
//	    }
	 public void init(WindowManager windowManager)
	    {
	        paint = getPaint();
	        text = getText().toString();
	        textLength = paint.measureText(text);//textview������ĳ���
	        viewWidth = getWidth();
	        if(viewWidth == 0)
	        {
	            if(windowManager != null)
	            {
	                Display display = windowManager.getDefaultDisplay();
	                viewWidth = display.getWidth();
	            }
	        }
	        step = textLength;
	        temp_view_plus_text_length = viewWidth + textLength;
	        temp_view_plus_two_text_length = viewWidth + textLength * 2;
	        y = getTextSize() + getPaddingTop();
	    }
	    public void startScroll()
	    {
	        isStarting = true;
	        invalidate();
	    }
	   
	   
	    public void stopScroll()
	    {
	        isStarting = false;
	        invalidate();
	    }
	    public void onDraw(Canvas canvas) {
	    	acanvas = canvas;
	        canvas.drawText(text, temp_view_plus_text_length - step, y, paint);
	        if(!isStarting)
	        {
	            return;
	        }
	        step += 3;//0.5Ϊ���ֹ��ٶȡ�
	        if(step > temp_view_plus_two_text_length)
	            step = textLength;
	        Message msg = new Message();
			handler.sendMessage(msg);
//	        try {
//				Thread.sleep(20);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	    }

}
