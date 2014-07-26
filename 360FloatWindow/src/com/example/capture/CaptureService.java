package com.example.capture;



import java.io.File;
import java.io.IOException;


import pkg.screenshot.Screenshot;
import pkg.screenshot.ScreenshotPolicy;
import pkg.screenshot.ScreenshotPolicy.OnAcquireRootPermissionCallback;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class CaptureService extends Service implements OnAcquireRootPermissionCallback{
	
	static final String BASE_PATH = Environment.getExternalStorageDirectory() + "/screens_shot";
    public static String imageFile = "";
	File mFile;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
//		  Intent inten2t = new Intent();
//		  inten2t.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//		  inten2t.setClass(this,CapturedImageActivity.class);
//		  startActivity(inten2t);
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
                      String path = getPath();
                      if (map != null) {
                          ScreenshotUtils.saveBitmap2File(map, path);
                          Log.i("sssss", "saveBitmap2File path[" + path + "] OK");
                    	  Intent intent = new Intent();
                    	  Bitmap map2 = Bitmap.createBitmap(map); 
                    	  CapturedImageActivity.setBitmap(map2);
    					  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
    					  intent.setClass(CaptureService.this,CapturedImageActivity.class);
    					  startActivity(intent);
                          // map.recycle();
                          map = null;
                      }
                	  
                	  

                  } catch (OutOfMemoryError e) {
                      Log.e("CapureThread", "Screenshot Thread generate Screenshot error:", e);
                  } catch (IOException e) {
                  Log.e("IOException", "EXCEPTION:", e);
              }
              }
              screenshot.release();
    	}
    	
    	
        private String getPath() {
            if (mFile == null) {
                mFile = new File(BASE_PATH);
            }
            if (!mFile.exists()) {
                mFile.mkdirs();
            }
            // get system time
            imageFile = BASE_PATH + "/" + System.currentTimeMillis() + ".jpg";
            return imageFile;
        }
    	

    }

}
