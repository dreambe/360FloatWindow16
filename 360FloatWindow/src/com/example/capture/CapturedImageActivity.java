package com.example.capture;


import com.example.floatwindow.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class CapturedImageActivity extends Activity{
	
	private static Bitmap mBitmap;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_image);
              
//        ImageView imgView = (ImageView)findViewById(R.id.imageView);
//        imgView.setImageBitmap(mBitmap);
        ImageButton imgBtn = (ImageButton)findViewById(R.id.imageButton);
        imgBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				Toast.makeText(CapturedImageActivity.this, "share", Toast.LENGTH_SHORT).show();
			}
		});
    }
    
    public static void setBitmap(Bitmap bitmap)
    {
    	mBitmap = bitmap;
    }

}
