package com.example.floatwindow;

import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class MyAnimations{
	
	private static int	xOffset		= 16;
	private static int	yOffset		= -13;
	
//	public static Animation getScaleAnimation(float fromX,float  toX,float  fromY,float  toY,int durationMillis){
//		ScaleAnimation scale = new ScaleAnimation(fromX, toX, fromY, toY, 
//				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		scale.setDuration(durationMillis);
//		scale.setFillAfter(true);
//		return scale;
//	}

	public static void initOffset(FloatWindowMainView floatWindowMainView){
		xOffset		= (int) (10.667 *floatWindowMainView.getResources().getDisplayMetrics().density);
		yOffset		= -(int) (8.667 *floatWindowMainView.getResources().getDisplayMetrics().density);
	}
	
	public static Animation getRotateAnimation(float fromDegrees ,float toDegrees,int durationMillis){
		RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(durationMillis);
		rotate.setFillAfter(true);
		return rotate;
	}

	public static void startAnimationsOut(ViewGroup viewgroup,int durationMillis) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
			ImageView inoutimagebutton = (ImageView) viewgroup.getChildAt(i);
				inoutimagebutton.setVisibility(0);
				inoutimagebutton.setFocusable(true);
				inoutimagebutton.setClickable(true);
				
				MarginLayoutParams mlp = (MarginLayoutParams) inoutimagebutton.getLayoutParams();
				Animation animation = new TranslateAnimation
						(Animation.ABSOLUTE,mlp.rightMargin-5*xOffset,Animation.RELATIVE_TO_SELF,0F,
					     Animation.ABSOLUTE,0F,Animation.RELATIVE_TO_SELF, 0F);
				
				animation.setFillAfter(true);
				animation.setDuration(durationMillis);
				animation.setStartOffset(((i+1) * 100)
						/ (-1 + viewgroup.getChildCount()));
				animation.setInterpolator(new OvershootInterpolator(3.5F));
				inoutimagebutton.startAnimation(animation);
			
		}
	}
	public static void startAnimationsIn(ViewGroup viewgroup,int durationMillis) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
				final ImageView inoutimagebutton = (ImageView) viewgroup.getChildAt(i);
				
				MarginLayoutParams mlp = (MarginLayoutParams) inoutimagebutton.getLayoutParams();
//				Animation animation = new TranslateAnimation(0F,mlp.rightMargin-5*xOffset, 0F,0F);
				Animation animation = new TranslateAnimation
						(Animation.ABSOLUTE,0F,Animation.RELATIVE_TO_SELF,mlp.rightMargin-5*xOffset,
						 Animation.ABSOLUTE,0F,Animation.RELATIVE_TO_SELF, 0F);
				
				animation.setFillAfter(true);
				animation.setDuration(durationMillis);
				animation.setStartOffset(((viewgroup.getChildCount()-i) * 100)
						/ (-1 + viewgroup.getChildCount()));
//				animation.setStartOffset((i * 100)
//						/ (-1 + viewgroup.getChildCount()));
				animation.setInterpolator(new AnticipateInterpolator(2F));
				inoutimagebutton.startAnimation(animation);
				inoutimagebutton.setVisibility(8);
			}
		
	}

	
}