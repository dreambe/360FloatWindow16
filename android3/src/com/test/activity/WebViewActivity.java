package com.test.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity
{
	private WebView webview;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_activity);

		webview = (WebView) findViewById(R.id.webview);
		
		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		
		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadUrl(url);
		webview.setWebViewClient(new HelloWebViewClient());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack())
		{
			webview.goBack();
			return true;
		}
		else if(keyCode == KeyEvent.KEYCODE_BACK && !webview.canGoBack())
		{
			finish();
		}
		return false;
	}
	
	private class HelloWebViewClient extends WebViewClient
	{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			view.loadUrl(url);
			return true;
		}
	}
}
