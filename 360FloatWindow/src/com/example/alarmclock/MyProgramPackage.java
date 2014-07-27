package com.example.alarmclock;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class MyProgramPackage {
	// 根据包名获得应用程序名
			public static String getProgramNameByPackageName(Context context,
					String packageName) {
				PackageManager pm = context.getPackageManager();
				String name = null;
				try {
					name = pm.getApplicationLabel(
							pm.getApplicationInfo(packageName,
									PackageManager.GET_META_DATA)).toString();
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				return name;
			}
			
		//根据包名启动游戏程序
			public static void StartProgramByPackageName(Context context,
					String packageName) 
			{
				PackageManager packageManager = context.getPackageManager();
				Intent intent = new Intent();
				intent = packageManager.getLaunchIntentForPackage(packageName);
				context.startActivity(intent);
			}
			
			//根据包名判断当前游戏是否打开
			public static boolean isProgramRunning(Context context,String packageName)
			{
				return true;
			}
			
			//根据包名关闭游戏
			public static void CloseProgramByPackageName(Context context,
					String packageName)
			{
				
			}
}
