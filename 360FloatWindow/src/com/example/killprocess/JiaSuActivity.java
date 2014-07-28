package com.example.killprocess;

import com.example.floatwindow.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class JiaSuActivity extends Activity {

	Animation rotateAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        
        setContentView(R.layout.jiasu_activity);
        
        ImageView image=(ImageView)findViewById(R.id.imageView);   
       
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.jiasu_rotate);  
        LinearInterpolator lin = new LinearInterpolator();  
        operatingAnim.setInterpolator(lin);  
        if (operatingAnim != null) {  
        	image.startAnimation(operatingAnim);  
        } 
        
    }
    
    
    
}