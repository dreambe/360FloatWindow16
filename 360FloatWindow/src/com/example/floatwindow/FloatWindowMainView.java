
package com.example.floatwindow;

import java.util.Timer;
import java.util.TimerTask;

import com.example.alarmclock.AlarmMainActivity;
import com.example.capture.CaptureService;
import com.example.capture.CapturedImageActivity;
import com.example.commit.CommitActivity;
import com.example.cutInternet.CutInternet;
import com.example.floatwindow.R;
import com.example.floatwindow.MyAnimations;
import com.example.gameguide.ViewGameGuideActivity;
import com.example.killprocess.JiaSuActivity;
import com.example.killprocess.KillProcess;
import com.example.floatwindow.MyPackageName;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
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
    
    private CutInternet cut;

    ImageButton btn_skin;

    SharedPreferences sp_skin;

    /**
     * 璁板綍鎮诞绐楃殑瀹藉害
     */
    public static int viewWidth;

    /**
     * 璁板綍鎮诞绐楃殑楂樺害
     */
    public static int viewHeight;


    /**
     * 鐢ㄤ簬鏇存柊鎮诞绐楃殑浣嶇疆
     */
    private WindowManager windowManager;

    /**
     * 鎮诞绐楃殑鍙傛暟
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
     * 鏄剧ず鍏朵粬鎸夐挳
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
                        // 鏇存柊鎮诞绐椾綅缃�
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
                    floatMainButton.startAnimation(MyAnimations.getRotateAnimation(0, -360, 300));
                    MyAnimations.startAnimationsIn(buttonGroup, 1000);
                } 
                else if(areButtonsShowing == false || buttonGroup.getVisibility() == 8) {
                	buttonGroup.setVisibility(0);
                    floatMainButton.startAnimation(MyAnimations.getRotateAnimation(-360, 0, 300));
                    MyAnimations.startAnimationsOut(buttonGroup, 300);
                }
                areButtonsShowing = !areButtonsShowing;
            }
        });

        //鍝嶅簲鍚勪釜鍥炬爣鐨勭偣鍑讳簨浠�
        for (int i = 0; i < buttonGroup.getChildCount(); i++) {
            final int position = i;
            buttonGroup.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    switch (position) {
                        case 0:
                        	buttonGroup.setVisibility(8);
                        	areButtonsShowing = !areButtonsShowing;
                        	
                            Intent jiasuIntent = new Intent();
                            jiasuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            jiasuIntent.setClass(FloatWindowMainView.this.getContext(), JiaSuActivity.class);//鍓嶉潰涓�涓槸涓�涓狝ctivity鍚庨潰涓�涓槸瑕佽烦杞殑Activity  
                            FloatWindowMainView.this.getContext().startActivity(jiasuIntent);//寮�濮嬬晫闈㈢殑璺宠浆鍑芥暟  
                            Log.i("0", "------0-----");
                            break;
                        case 1:
                        	//闂归挓
                        	buttonGroup.setVisibility(8);
                        	areButtonsShowing = !areButtonsShowing;
                        	
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //闇�瑕佷紶鍏ユ父鎴忕殑package name
                            String packageName = MyPackageName.getRunningPackageName(FloatWindowMainView.this.getContext());
                            intent.putExtra("target_pkgname", packageName);
                            intent.setClass(FloatWindowMainView.this.getContext(), AlarmMainActivity.class);//鍓嶉潰涓�涓槸涓�涓狝ctivity鍚庨潰涓�涓槸瑕佽烦杞殑Activity  
                            FloatWindowMainView.this.getContext().startActivity(intent);//寮�濮嬬晫闈㈢殑璺宠浆鍑芥暟  
                            Log.i("1", "------1-----");
                            break;
                        case 2: 
                        	//鍚愭Ы
                        	buttonGroup.setVisibility(8);
                        	areButtonsShowing = !areButtonsShowing;
                        	
                        	Intent intent5 = new Intent();
                            intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent5.setClass(FloatWindowMainView.this.getContext(), CommitActivity.class);
                            FloatWindowMainView.this.getContext().startActivity(intent5);
                            Log.i("2", "------2----");
                            break;
                        
                        case 3:
                        	//鎴睆鍒嗕韩
                        	buttonGroup.setVisibility(8);
                        	areButtonsShowing = !areButtonsShowing;
                        	
                        	Intent intent2 = new Intent(FloatWindowMainView.this.getContext(), CaptureService.class);
                            FloatWindowMainView.this.getContext().startService(intent2);
                            Log.i("3", "------3----");
                            break;
                            
                        case 4:
                        	//鏀荤暐
                        	buttonGroup.setVisibility(8);
                        	areButtonsShowing = !areButtonsShowing;
                        	
                        	Intent intent4 = new Intent();
                            packageName = MyPackageName.getRunningPackageName(FloatWindowMainView.this.getContext());
                            intent4.putExtra("target_pkgname", packageName);
                            intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent4.setClass(FloatWindowMainView.this.getContext(), ViewGameGuideActivity.class);//鍓嶉潰涓�涓槸涓�涓狝ctivity鍚庨潰涓�涓槸瑕佽烦杞殑Activity  
                            FloatWindowMainView.this.getContext().startActivity(intent4);//寮�濮嬬晫闈㈢殑璺宠浆鍑芥暟  
                            Log.i("4", "------4-----");
                            break;
                            
                        case 5:
                        	buttonGroup.setVisibility(8);
                        	areButtonsShowing = !areButtonsShowing;
                        	
                        	Intent intent55 = new Intent();
                            intent55.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent55.setClass(FloatWindowMainView.this.getContext(), CutInternetActivity.class);
                            FloatWindowMainView.this.getContext().startActivity(intent55);
                        	//cut = new CutInternet();
                        	//cut.toggleMobileData(FloatWindowMainView.this.getContext(), false);
                        	//cut.toggleWiFi(FloatWindowMainView.this.getContext(), false);
                            Log.i("5", "------5-----");
                            break;

                    }
                }
            });
        }        
    }

    /**
     * 灏嗘偓娴獥鐨勫弬鏁颁紶鍏ワ紝鐢ㄤ簬鏇存柊鎮诞绐楃殑浣嶇疆銆�
     * 
     * @param params 鎮诞绐楃殑鍙傛暟
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 鏇存柊鎮诞绐楀湪灞忓箷涓殑浣嶇疆銆�
     */
    private void updateViewPosition() {
        windowManager.updateViewLayout(this, mParams);
    }

}
