package com.test.activity;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.model.Guide;
import com.test.util.WinFloatUtils;

/**
 * 
 * @author dangqi
 */
public class GuideActivity extends Activity
{
	private TextView zhankai1 = null;
	private TextView zhankai2 = null;
	private TextView zhankai3 = null;
	
	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			
			List<Guide> guides = (List<Guide>)msg.obj;
			
			/*
			Set<Entry<String, String>> entryGuides = guides.entrySet();
			for(Iterator<Entry<String, String>> iter = entryGuides.iterator(); iter.hasNext();)
			{
				Entry<String, String> entry = iter.next();
				System.out.println(entry.getKey());
				System.out.println(entry.getValue());
			}
			
			//
			Iterator<Entry<String, String>> iter = entryGuides.iterator();
			final Entry<String, String> entry1 = iter.next();
			final Entry<String, String> entry2 = iter.next();
			final Entry<String, String> entry3 = iter.next();
			*/
			
			final Guide guide1 = guides.get(0);
			final Guide guide2 = guides.get(1);
			final Guide guide3 = guides.get(2);
			
			final TextView tv1 = (TextView)findViewById(R.id.guideText1);
			tv1.setText(guide1.getGuideTitle());
			
			tv1.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					//
					Intent intent = new Intent(GuideActivity.this, WebViewActivity.class);
					intent.putExtra("url", guide1.getUrl());
					startActivity(intent);
				}
			});

			tv1.post(new Runnable()
			{
				@Override
				public void run()
				{
					final int lineCount = tv1.getLineCount();
					
					if(lineCount > 2)
					{
						tv1.setMaxLines(2);
						if(zhankai1 != null)
						{
							zhankai1.setVisibility(View.VISIBLE);
							
							zhankai1.setOnClickListener(new OnClickListener()
							{
								@SuppressLint("NewApi")
								@Override
								public void onClick(View view)
								{
									int maxLines = tv1.getMaxLines();
									if(maxLines <= 2)
									{
										tv1.setMaxLines(lineCount);
									}
									else if(maxLines > 2)
									{
										tv1.setMaxLines(2);
									}
								}
							});
						}
					}	
				}
			});
			
			final TextView tv2 = (TextView)findViewById(R.id.guideText2);
			tv2.setText(guide2.getGuideTitle());
			
			tv2.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					//
					Intent intent = new Intent(GuideActivity.this, WebViewActivity.class);
					intent.putExtra("url", guide2.getUrl());
					startActivity(intent);
				}
			});
			
			tv2.post(new Runnable()
			{
				@Override
				public void run()
				{
					final int lineCount = tv2.getLineCount();
					
					if(lineCount > 2)
					{
						tv2.setMaxLines(2);
						if(zhankai2 != null)
						{
							zhankai2.setVisibility(View.VISIBLE);
							
							zhankai2.setOnClickListener(new OnClickListener()
							{
								@SuppressLint("NewApi")
								@Override
								public void onClick(View view)
								{
									int maxLines = tv2.getMaxLines();
									if(maxLines <= 2)
									{
										tv2.setMaxLines(lineCount);
									}
									else if(maxLines > 2)
									{
										tv2.setMaxLines(2);
									}
								}
							});
						}
					}	
				}
			});
			
			final TextView tv3 = (TextView)findViewById(R.id.guideText3);
			tv3.setText(guide3.getGuideTitle());
			
			tv3.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					//
					Intent intent = new Intent(GuideActivity.this, WebViewActivity.class);
					intent.putExtra("url", guide3.getUrl());
					startActivity(intent);
				}
			});
			
			tv3.post(new Runnable()
			{
				@Override
				public void run()
				{
					final int lineCount = tv3.getLineCount();
					
					if(lineCount > 2)
					{
						tv3.setMaxLines(2);
						if(zhankai3 != null)
						{
							zhankai3.setVisibility(View.VISIBLE);
							
							zhankai3.setOnClickListener(new OnClickListener()
							{
								@SuppressLint("NewApi")
								@Override
								public void onClick(View view)
								{
									int maxLines = tv3.getMaxLines();
									if(maxLines <= 2)
									{
										tv3.setMaxLines(lineCount);
									}
									else if(maxLines > 2)
									{
										tv3.setMaxLines(2);
									}
								}
							});
						}
					}	
				}
			});
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_activity);
		
		zhankai1 = (TextView)findViewById(R.id.zhankai1);
		zhankai1.setVisibility(View.GONE);
		
		zhankai2 = (TextView)findViewById(R.id.zhankai2);
		zhankai2.setVisibility(View.GONE);
		
		zhankai3 = (TextView)findViewById(R.id.zhankai3);
		zhankai3.setVisibility(View.GONE);
		
		ImageView imageView = (ImageView)findViewById(R.id.imageView);
		imageView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				finish();
			}
		});
		
		Thread t = new Thread(downloadThread);
		t.start();
	}
	
	Runnable downloadThread = new Runnable()
	{
		public void run()
		{
			//
			String packageId = "com.gameloft.android.ANMP.GloftDMHM";
			String jsonStr = WinFloatUtils.download(packageId);
			if(jsonStr == null)
			{
				return;
			}
			//
			List<Guide> guides = WinFloatUtils.getGuide(jsonStr);
			if(guides == null || guides.size() <= 0)
			{
				return;
			}
			//
			Message msg = handler.obtainMessage();
			
			msg.obj = guides;
			handler.sendMessage(msg);
		}
	};
}
