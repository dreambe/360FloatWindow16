package com.test.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.test.model.Guide;

public class WinFloatUtils
{
	private static String ZHUSHOU = "http://openbox.mobilem.360.cn/mintf/getAppInfoByIds?pname=";
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String download(String packageName)
	{
		String temp = ZHUSHOU + packageName;
		HttpGet httpGet = new HttpGet(temp);
		HttpClient client = new DefaultHttpClient();
		InputStream is = null;
		
		try
		{
			HttpResponse response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String result = "";
			String line = "";
			
			while((line = br.readLine()) != null)
			{
				result = result + line;
			}
			return result;
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(is != null)
				{
					is.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
		
	/**
	 * 获取攻略
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("all")
	public static List<Guide> getGuide(String jsonStr)
	{
		List<Guide> list = new ArrayList<Guide>();
		//Map<String, String> map = new HashMap<String, String>();
		
		Gson gson = new Gson();
        
        JsonObject rootObj = new JsonParser().parse(jsonStr).getAsJsonObject();
        JsonElement dataElement = rootObj.get("data");
        JsonArray dataArray = dataElement.getAsJsonArray();
        JsonElement communityElement = dataArray.get(0).getAsJsonObject().get("community");
        JsonElement glElement = communityElement.getAsJsonObject().get("gl");
        
        JsonObject glObj = glElement.getAsJsonObject();
        Set<Entry<String, JsonElement>> set = glObj.entrySet();
        for(Iterator<Entry<String, JsonElement>> iter = set.iterator(); iter.hasNext(); )
        {
     	   Entry<String, JsonElement> entry = iter.next();
     	   //System.out.println(entry.getKey());
     	   
     	   JsonObject obj = entry.getValue().getAsJsonObject();
    	   JsonElement elementTitle = obj.get("title");
    	   System.out.println(elementTitle.getAsString());
    	   
    	   JsonElement elementURL = obj.get("url");
    	   System.out.println(elementURL.getAsString());

     	   //map.put(elementTitle.getAsString(), elementURL.getAsString());
    	   Guide guide = new Guide();
    	   guide.setGuideTitle(elementTitle.getAsString());
    	   guide.setUrl(elementURL.getAsString());
    	   
    	   list.add(guide);
     	}
        return list;
	}
	
	/**
	 * 判断当前的应用是否是游戏 
	 */
	public static boolean isGame(String jsonStr)
	{
		Gson gson = new Gson();
		
		/**
		 * 检测packageName
		 */
		//..........

		
        JsonObject rootObj = new JsonParser().parse(jsonStr).getAsJsonObject();
        JsonElement dataElement = rootObj.get("data");
        JsonArray dataArray = dataElement.getAsJsonArray();
        JsonElement typeElement = dataArray.get(0).getAsJsonObject().get("type");
        
        String type = typeElement.getAsString();
        if(type.equalsIgnoreCase("game"))
        {
        	return true;
        }
		return false;
	}
	
	
	/**
	 * 判断当前的应用是否有广告 
	 */
	public static boolean isAd(String jsonStr)
	{
		Gson gson = new Gson();
		
		/**
		 * 检测packageName
		 */
		//..........

        JsonObject rootObj = new JsonParser().parse(jsonStr).getAsJsonObject();
        JsonElement dataElement = rootObj.get("data");
        JsonArray dataArray = dataElement.getAsJsonArray();
        JsonElement typeElement = dataArray.get(0).getAsJsonObject().get("is_ad");
        
        String is_ad = typeElement.getAsString();
        if(Integer.valueOf(is_ad) <= 0)
        {
        	return false;
        }
		return true;
	}
}
