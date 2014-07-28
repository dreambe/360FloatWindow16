
package com.example.floatwindow;

import com.example.alarmclock.AlarmMainActivity;
import com.example.capture.CaptureService;
import com.example.capture.CapturedImageActivity;
import com.example.floatwindow.R;
import com.example.floatwindow.MyAnimations;
import com.example.gameguide.ViewGameGuideActivity;
import com.example.killprocess.KillProcess;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class FloatWindowMainView extends LinearLayout {

    private boolean areButtonsShowing;

    private RelativeLayout buttonGroup;

    private ImageView floatMainButton;

    ImageButton btn_skin;

    SharedPreferences sp_skin;

    /**
     * 记录悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录悬浮窗的高度
     */
    public static int viewHeight;


    /**
     * 用于更新悬浮窗的位置
     */
    private WindowManager windowManager;

    /**
     * 悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;


    public FloatWindowMainView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //TODO-------------
        LayoutInflater.from(context).inflate(R.layout.float_window, this);

        View view = findViewById(R.id.relate);
        
        areButtonsShowing = false;
        initPath();
    }


    /**
     * 显示其他按钮
     */
    public void initPath() {
        MyAnimations.initOffset(FloatWindowMainView.this);

        buttonGroup = (RelativeLayout) findViewById(R.id.button_Group);
        floatMainButton = (ImageView) findViewById(R.id.float_main_button);

        floatMainButton.setOnTouchListener(new OnTouchListener() {
            int lastX, lastY;

            int paramX, paramY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = mParams.x;
                        paramY = mParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        mParams.x = paramX + dx;
                        mParams.y = paramY + dy;
                        // 更新悬浮窗位置
                        updateViewPosition();
                        break;
                }
                return false;
            }
        });

        floatMainButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (areButtonsShowing == true) {
//                	buttonGroup.setVisibility(8);
                    floatMainButton.startAnimation(MyAnimations.getRotateAnimation(0, -360, 300));
                    MyAnimations.startAnimationsIn(buttonGroup, 1000);
//                    buttonGroup.setVisibility(8);
                } 
                else if(areButtonsShowing == false || buttonGroup.getVisibility() == 8) {
                	buttonGroup.setVisibility(0);
                    floatMainButton.startAnimation(MyAnimations.getRotateAnimation(-360, 0, 300));
                    MyAnimations.startAnimationsOut(buttonGroup, 300);
                }
                areButtonsShowing = !areButtonsShowing;
            }
        });

        //响应各个图标的点击事件
        for (int i = 0; i < buttonGroup.getChildCount(); i++) {
            final int position = i;
            buttonGroup.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    switch (position) {
                        case 0:
                        	buttonGroup.setVisibility(8);
                            KillProcess mykill = new KillProcess();
                            mykill.killAll(getContext());
                            Log.i("0", "------0-----");
                            break;
                        case 1:
                        	//闹钟
                        	buttonGroup.setVisibility(8);
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //需要传入游戏的package name
                            intent.putExtra("target_pkgname", "com.UCMobile");
                            intent.setClass(FloatWindowMainView.this.getContext(), AlarmMainActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity  
                            FloatWindowMainView.this.getContext().startActivity(intent);//开始界面的跳转函数  
                            Log.i("1", "------1-----");
                            break;
                        case 2: 
                        	//吐槽
                            
                            Log.i("2", "------2----");
                            break;
                        
                        case 3:
                        	//截屏分享
                        	buttonGroup.setVisibility(8);
                        	Intent intent2 = new Intent(FloatWindowMainView.this.getContext(), CaptureService.class);
                            FloatWindowMainView.this.getContext().startService(intent2);
                            Log.i("3", "------3----");
                            break;
                        case 4:
                        	//攻略
                        	buttonGroup.setVisibility(8);
                            Intent intent4 = new Intent();
                            intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent4.setClass(FloatWindowMainView.this.getContext(), ViewGameGuideActivity.class);//前面一个是一个Activity后面一个是要跳转的Activity  
                            FloatWindowMainView.this.getContext().startActivity(intent4);//开始界面的跳转函数  
                            Log.i("4", "------4-----");
                            break;
                        case 5:
                            Log.i("5", "------5-----");
                            break;
                    }
                }
            });
        }
    }

    /**
     * 将悬浮窗的参数传入，用于更新悬浮窗的位置。
     * 
     * @param params 悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        windowManager.updateViewLayout(this, mParams);
    }

}
