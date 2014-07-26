package com.example.capture;

import java.io.File;

import com.example.floatwindow.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class CapturedImageActivity extends Activity{
	
	
	private static Bitmap mBitmap;
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_image);
                     
        ImageView imgView = (ImageView)findViewById(R.id.imageView);
        imgView.setImageBitmap(mBitmap);

        ImageButton imgBtn = (ImageButton)findViewById(R.id.imageButton);
        imgBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent intent=new Intent(Intent.ACTION_SEND);   
                intent.setType("image/*");   
                intent.putExtra(Intent.EXTRA_SUBJECT, "·ÖÏí");  
                File file = new File(CaptureService.imageFile); 
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));              
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
                startActivity(Intent.createChooser(intent, getTitle()));  

			}
		});
    }
    
    public static void setBitmap(Bitmap bitmap)
    {
    	mBitmap = bitmap;
    }
    

}
