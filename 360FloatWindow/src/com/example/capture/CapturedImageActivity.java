package com.example.capture;


import com.example.floatwindow.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class CapturedImageActivity extends Activity{
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_image);
              
        ImageView imgView = (ImageView)findViewById(R.id.imageView);
        Bitmap img = (Bitmap) savedInstanceState.get("productImg");  
        imgView.setImageBitmap(img);
    }

}
