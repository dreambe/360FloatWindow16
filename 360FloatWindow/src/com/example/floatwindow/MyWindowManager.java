package com.example.floatwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class MyWindowManager {
	
	/**
	 * 悬浮窗View的实例
	 */
	private static FloatWindowMainView mainWindow;


	/**
	 * 小悬浮窗View的参数
	 */
	private static LayoutParams mainWindowParams;


	/**
	 * 用于控制在屏幕上添加或移除悬浮窗
	 */
	private static WindowManager mWindowManager;
	

	/**
	 * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void createMainWindow(Context context) {
		WindowManager windowManager = getWindowManager(context);
		if (mainWindow == null) {
			mainWindow = new FloatWindowMainView(context);
			if (mainWindowParams == null) {
				mainWindowParams = new LayoutParams();
				mainWindowParams.type = LayoutParams.TYPE_PHONE;
				mainWindowParams.format = PixelFormat.RGBA_8888;
				mainWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
						| LayoutParams.FLAG_NOT_FOCUSABLE;
				mainWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				mainWindowParams.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
				mainWindowParams.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
				mainWindowParams.x = 50;
				mainWindowParams.y = 60;
			}
			mainWindow.setParams(mainWindowParams);
			windowManager.addView(mainWindow, mainWindowParams);
		}
	}


	/**
	 * 将小悬浮窗从屏幕上移除。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void removeMainWindow(Context context) {
		if (mainWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(mainWindow);
			mainWindow = null;
		}
	}


	/**
	 * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
	 * 
	 * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
	 */
	public static boolean isWindowShowing() {
		return mainWindow != null;
	}

	/**
	 * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
	 */
	private static WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}
}
