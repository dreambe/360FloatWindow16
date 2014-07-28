package com.example.floatwindow;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;

public class MyPackageName {
	/**
     *返回当前应用的包名
     */
    public static String getRunningPackageName(Context context) {
    	ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        ComponentName topActivity = tasks.get(0).topActivity;
        
        return topActivity.getPackageName();
    }
}
