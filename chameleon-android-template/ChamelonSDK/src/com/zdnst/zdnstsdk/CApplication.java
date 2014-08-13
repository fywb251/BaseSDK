package com.zdnst.zdnstsdk;

import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.webkit.WebView.FindListener;

public class CApplication {

	private HashMap<String, CModule> modules = new HashMap<String, CModule>();
	private Context mContext;
	
	private Activity activity;

	public CApplication(Context context , InputStream is) {
		mContext = context;
		initModulesConfig(is);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		for (CModule m : modules.values()) {
			m.onConfigurationChanged(newConfig);
		}
	}

	public void onCreate() {
		for (CModule m : modules.values()) {
			m.onCreate(m);
		}
	}

	public void onExit(){
		for (CModule m : modules.values()) {
			m.onExit(m);
		}
	}
	
	public void onLowMemory() {
		for (CModule m : modules.values()) {
			m.onLowMemory();
		}
	}

	public void onTerminate() {
		for (CModule m : modules.values()) {
			m.onTerminate();
		}
	}

	public CModule getModule(String identifier){
		return modules.get(identifier);
	}

	public HashMap<String, CModule> getModules() {
		return modules;
	}

	public void setModules(HashMap<String, CModule> modules) {
		this.modules = modules;
	}

	public Context getmContext() {
		return mContext;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	public void initModulesConfig(InputStream is) {
		
		String result = "";
		try {
			int lenght = is.available();
			byte[] buffer = new byte[lenght];
			is.read(buffer);
			result = EncodingUtils.getString(buffer, "ENCODING");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			JSONObject json = new JSONObject(result);
			JSONArray jay = json.getJSONArray("modules");
			for (int i = 0; i < jay.length(); i++) {
				JSONObject jb = (JSONObject) jay.get(i);
				String identifier = (String) jb.get("identifier");
				String name = (String) jb.get("name");
				String packagename = (String) jb.get("package");
				boolean firstStart = jb.getBoolean("firststart");
				Class<?> clazz = null;
				try {
					clazz = Class.forName(packagename + "."
							+ name);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}finally{
					if (clazz != null) {
						CModule cModule = (CModule) clazz.newInstance();
						cModule.setcApplication(this);
						cModule.setFirstStart(firstStart);
						cModule.setPackageName(packagename);
						cModule.setName(name);
						modules.put(identifier, cModule);
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
