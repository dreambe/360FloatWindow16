package com.example.capture;


import com.example.floatwindow.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class CapturedImageActivity extends Activity{
	
	private static Bitmap mBitmap;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_image);
              
        ImageView imgView = (ImageView)findViewById(R.id.imageView);
        imgView.setImageBitmap(mBitmap);
    }
    
    public static void setBitmap(Bitmap bitmap)
    {
    	mBitmap = bitmap;
    }

}
