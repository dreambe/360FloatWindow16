package com.example.capture;



import pkg.screenshot.Screenshot;
import pkg.screenshot.ScreenshotPolicy;
import pkg.screenshot.ScreenshotPolicy.OnAcquireRootPermissionCallback;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class CaptureService extends Service implements OnAcquireRootPermissionCallback{
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
//		  Intent inten2t = new Intent();
//		//  intent.putExtra("image", map);
//		  inten2t.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//		  inten2t.setClass(this,CapturedImageActivity.class);
//		  startActivity(inten2t);
//		  Log.e("dddd","dsdsd");
//		  return super.onStartCommand(intent, flags, startId);
		
		if (ScreenshotPolicy.isCurrentAppAcquiredRoot()) {
			Log.e("root", "already have root");
			new CaptureThread().start();
		}else{
			ScreenshotPolicy.acquireRootPermission(CaptureService.this);
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
    @Override
    public void onAcquireRootPermission(boolean acquired) {
    	
    	if(acquired)
    	{
    		new CaptureThread().start();
    	}
    	else
    	{
    		Log.e("root", "get root permission error");
    	}

    }
	
	
	
    class CaptureThread extends Thread{
    	
    	@Override
		public void run(){   		 
    		
    		  Screenshot screenshot = Screenshot.getInstance();

              int result = screenshot.takeScreenshot();
              Log.i("CapureThread", "screen shot result:" + result);
              if (result == Screenshot.SCREENSHOT_OK || result == Screenshot.SCREENSHOT_ARE_SAME) {
                  try {
                	  Bitmap map = screenshot.generateScreenshotBitmap();
                	  Intent intent = new Intent();
                	  Bitmap map2 = Bitmap.createBitmap(map); 
                	  CapturedImageActivity.setBitmap(map2);
					  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					  intent.setClass(CaptureService.this,CapturedImageActivity.class);
					  startActivity(intent);
                  } catch (OutOfMemoryError e) {
                      Log.e("CapureThread", "Screenshot Thread generate Screenshot error:", e);
                  }
              }
              screenshot.release();
    	}
    	

    }

}
