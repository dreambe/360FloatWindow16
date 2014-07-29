package com.example.cutInternet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;

public class CutInternet {
	
	private static final Context Context = null;

	public void toggleWiFi(Context context, boolean enabled) 
	{  
        WifiManager wm = (WifiManager) context  
                .getSystemService(Context.WIFI_SERVICE);  
        wm.setWifiEnabled(enabled);  
    }  
	
	public void toggleMobileData(Context context, boolean enabled)
	{    
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);    
        Class<?> conMgrClass = null; // ConnectivityManager  
        Field iConMgrField = null; // ConnectivityManager 
        Object iConMgr = null; // IConnectivityManager   
        Class<?> iConMgrClass = null; // IConnectivityManager   
        Method setMobileDataEnabledMethod = null; // setMobileDataEnabled   
        try {     
   
        conMgrClass = Class.forName(conMgr.getClass().getName());     
 
        iConMgrField = conMgrClass.getDeclaredField("mService");     
   
            iConMgrField.setAccessible(true);     

        iConMgr = iConMgrField.get(conMgr);     
  
        iConMgrClass = Class.forName(iConMgr.getClass().getName());     
  
        setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);     
   
        setMobileDataEnabledMethod.setAccessible(true);     
  
        setMobileDataEnabledMethod.invoke(iConMgr, enabled);    
        } catch (ClassNotFoundException e) {     
            e.printStackTrace();    
        } catch (NoSuchFieldException e) {     
            e.printStackTrace();    
        } catch (SecurityException e) {     
            e.printStackTrace();    
        } catch (NoSuchMethodException e) {     
            e.printStackTrace();    
        } catch (IllegalArgumentException e) {     
            e.printStackTrace();    
        } catch (IllegalAccessException e) {     
            e.printStackTrace();    
        } catch (InvocationTargetException e) {     
            e.printStackTrace();    
        }   
    }  
	
	public void toggleGPS()
	{    
        Intent gpsIntent = new Intent();    
        gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");    
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");  gpsIntent.setData(Uri.parse("custom:3"));    
        
        try 
        {     
            PendingIntent.getBroadcast(Context, 0, gpsIntent, 0).send();    
        } 
        catch (CanceledException e) 
        {     
            e.printStackTrace();    
        }   
    }  
}
