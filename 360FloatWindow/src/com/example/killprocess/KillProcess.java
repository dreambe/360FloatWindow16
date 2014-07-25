//-----作者：赵晓彪
//-----功能:杀进程

package com.example.killprocess;

import com.example.killprocess.ReKillInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.util.Log;


public class KillProcess {
	public ReKillInfo killAll(Context context) 
	{
		long firstmem = getAvailMemory(context);
		//		获取一个ActivityManager 对象
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
      //获取系统中所有正在运行的进程
        List<RunningAppProcessInfo> appProcessInfos = activityManager
                .getRunningAppProcesses();
        int firstpronum = appProcessInfos.size();
        System.out.println("ApplicationInfo-->后台进程数 ："+firstpronum);
      //获取当前activity所在的进程
	    String currentProcess=context.getApplicationInfo().processName;
	  //维护一个不杀进程白名单；
	    ArrayList<String> baimindanList = new ArrayList<String>();
	    baimindanList.add("com.huawei.android.launcher");
	    baimindanList.add(currentProcess);
      //对系统中所有正在运行的进程进行迭代，如果进程名不是白名单进程，则Kill掉
        for (RunningAppProcessInfo appProcessInfo : appProcessInfos)
        {
        	String processName=appProcessInfo.processName;
        	// 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了  
            // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着  
        	String strname = appProcessInfo.processName;
        	if(appProcessInfo.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE 
        		 && !(-1!= baimindanList.indexOf(strname)))
        	 {
        		activityManager.killBackgroundProcesses(processName);
        		//forceStopAPK(processName);
	        	 System.out.println("Killed -->PID:"+appProcessInfo.pid+"--ProcessName:"+processName);
        	 }
        }
        appProcessInfos.clear();
        appProcessInfos = activityManager
                .getRunningAppProcesses();
        int lastpronum = appProcessInfos.size();
        System.out.println("ApplicationInfo-->后台进程数 ："+lastpronum);
        
        long lastmem = getAvailMemory(context);
        ReKillInfo ret = new ReKillInfo();
        ret.m_killpronum = firstpronum - lastpronum;
        ret.m_freememsize =lastmem - firstmem;
        System.out.println("ApplicationInfo-->杀死后台进程数 ："+ret.m_killpronum);
        System.out.println("ApplicationInfo-->释放内存 ："+ret.m_freememsize);
        
		return ret;
	}
	

	private long getAvailMemory(Context context) 
    {
        // 获取android当前可用内存大小 
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存 
        System.out.println("ApplicationInfo-->当前可用内存 "+mi.availMem/(1024*1024)+"MB");
        //return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化 
        return mi.availMem/(1024*1024);
    }

    private long getTotalMemory(Context context) 
    {
        String str1 = "/proc/meminfo";// 系统内存信息文件 
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try 
        {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
            localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小 

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte 
            localBufferedReader.close();

        } catch (IOException e) {
        }
        //return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化 
        return initial_memory/(1024*1024);
    }
 }

