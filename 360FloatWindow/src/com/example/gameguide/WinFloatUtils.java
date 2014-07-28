package com.example.gameguide;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

			while ((line = br.readLine()) != null)
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
				if (is != null)
				{
					is.close();
				}
			}
			catch (Exception e)
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
		try
		{
			List<Guide> list = new ArrayList<Guide>();
			
			JsonObject rootObj = new JsonParser().parse(jsonStr).getAsJsonObject();
			JsonElement dataElement = rootObj.get("data");
			JsonArray dataArray = dataElement.getAsJsonArray();
			JsonElement communityElement = dataArray.get(0).getAsJsonObject()
					.get("community");
			JsonElement glElement = communityElement.getAsJsonObject().get("gl");

			JsonObject glObj = glElement.getAsJsonObject();
			Set<Entry<String, JsonElement>> set = glObj.entrySet();
			for (Iterator<Entry<String, JsonElement>> iter = set.iterator(); iter
					.hasNext();)
			{
				Entry<String, JsonElement> entry = iter.next();
				// System.out.println(entry.getKey());

				JsonObject obj = entry.getValue().getAsJsonObject();
				JsonElement elementTitle = obj.get("title");
				System.out.println(elementTitle.getAsString());

				JsonElement elementURL = obj.get("url");
				System.out.println(elementURL.getAsString());

				// map.put(elementTitle.getAsString(), elementURL.getAsString());
				Guide guide = new Guide();
				guide.setGuideTitle(elementTitle.getAsString());
				guide.setUrl(elementURL.getAsString());

				list.add(guide);
			}
			return list;
		}
		catch(Throwable e)
		{
			return null;
		}
	}

	/**
	 * 获取Json中data下的第一层的数据
	 */
	private static String firstLevelJsonOfData(String jsonStr, String name)
	{
		JsonObject rootObj = new JsonParser().parse(jsonStr).getAsJsonObject();
		JsonElement dataElement = rootObj.get("data");
		JsonArray dataArray = dataElement.getAsJsonArray();
		JsonElement typeElement = dataArray.get(0).getAsJsonObject()
				.get(name);

		String result = typeElement.getAsString();
		return result;
	}

	
	/**
	 * 判断当前的应用是否是游戏
	 */
	public static int isGame(String jsonStr)
	{
		String type = firstLevelJsonOfData(jsonStr, "type");
		if (type.trim().equalsIgnoreCase("game"))
		{
			return 0;
		}
		return 1;
	}

	/**
	 * 判断当前的应用是否有广告
	 */
	public static boolean isAd(String jsonStr)
	{
		String is_ad = firstLevelJsonOfData(jsonStr, "is_ad");
		if (Integer.valueOf(is_ad) <= 0)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * 获取当前App的网络图标地址
	 */
	public static String getAppIcon(String jsonStr)
	{
		String appIconUrl = firstLevelJsonOfData(jsonStr, "logo_512");
		return appIconUrl;
	}
	
	/**
	 * 下载图片
	 * @param imgUrl
	 * @param fileURL
	 */
	public static void downloadImg(String imgUrl, String fileUrl)
	{
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try
		{
			in = new BufferedInputStream(new URL(imgUrl).openStream());

			// 生成图片名
			int index = imgUrl.lastIndexOf("/");
			String sName = imgUrl.substring(index + 1, imgUrl.length());
			System.out.println(sName);
			
			// 存放地址
			File img = new File(fileUrl + sName);
			String path = img.getAbsolutePath();
			out = new BufferedOutputStream(new FileOutputStream(path));
			System.out.println(path);
			byte[] buf = new byte[2048];
			int length = in.read(buf);
			while (length != -1)
			{
				out.write(buf, 0, length);
				length = in.read(buf);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(in != null)
				{
					in.close();
				}
				if(out != null)
				{
					out.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
