package com.zdnst.router;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

public class RoutingParserHelper {
	public int r;
	public Context context;
	
	public RoutingParserHelper(Context context,int r) {
		this.r = r;
		this.context = context;
	}
	public RoutingParserHelper() {
	}
	
	public static ArrayList<RoutingModel> routings = new ArrayList<RoutingModel>();
	
	public ArrayList<RoutingModel> getRoutings() {
		return routings;
	}


	public void setRoutings(ArrayList<RoutingModel> routings) {
		this.routings = routings;
	}


	public  void  addList(RoutingModel model) {
		routings.add(model);
	}
		
	//读取配置文件
	public void readConfig()  {

		InputStream is = context.getResources().openRawResource(r);
		String content = null;
		try {
			byte buffer[] = new byte[is.available()];
			is.read(buffer);
			content = new String(buffer);
			JSONArray jsonArray = new JSONArray(content);
			
			for(int i =0;i<jsonArray.length();i++) {
				RoutingModel routingModel = new RoutingModel(jsonArray.getString(i));
				getRoutings().add(routingModel);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
	//将字符串 转成 数组
	public String []  deatWithUrl(String url) {
		String []moduleUrl = null;
		if(url!=null&&!url.equals("")) {
			String []value =url.split("\\{"); 
			if(value.length!=0) {
				moduleUrl = value[0].substring(1).split("/");
			}
		}
		return moduleUrl;
	}
	
	
	//传入一个 url 返回 一个MappingModel对象，打开指定的界面
	public MappingModel redirectToPage(String url,String moduleIdentifer) {
		int maxIndex = 0;
		MappingModel mappingModel = null;
		String[] moduleUrl = deatWithUrl(url);
		if(moduleUrl == null) {
			return  mappingModel = null;
		}
		for(int i =0;i<routings.size();i++) {
			if(routings.get(i).getName().equals(moduleIdentifer)) {
				Iterator iter = routings.get(i).getMappings().entrySet().iterator();
				while (iter.hasNext()) {
					Entry entry = (Entry) iter.next();
					MappingModel val = (MappingModel) entry.getValue();
					int  similarityRatio = comparison(moduleUrl,val.getLinkURL());
					if(maxIndex<similarityRatio) {
						maxIndex = similarityRatio;
						mappingModel  = val;
					}
				}
			}
		}
		
		return mappingModel;
	}
	//比较两个url 相似度
	public int comparison(String []url, String []localUrl) {
		int sameCount=0;
		String [] count ;
		if(url.length < localUrl.length) {
			count = url;
		}else {
			count = localUrl;
		}
		for(int i =0;i<count.length;i++) {
			if(url[i].equals(localUrl[i])) {
				sameCount++;
			}else {
				return sameCount;
			}
		}
		return sameCount;
	}
	
	
	
}
