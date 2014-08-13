package com.zdnst.template;

import java.io.InputStream;
import java.util.HashMap;

import org.acra.CrashReport;

import android.app.Application;

import com.zdnst.bsl.util.Preferences;
import com.zdnst.zdnstsdk.CApplication;
import com.zdnst.zdnstsdk.CModule;
import com.zdnst.zdnstsdk.config.URL;

public class TemplateApplication extends Application {

	public CApplication cApplication;
	
	@Override
	public void onCreate() {
		super.onCreate();
		getCApplication();
		Preferences.getInstance(getApplicationContext());
		URL.initUrl(getApplicationContext());
		initModule();
		CrashReport crashReport = new CrashReport();
		crashReport.start(this);
	}
	
	public  CApplication getCApplication(){
		if (cApplication == null){
			InputStream is = getFromAssets("bsl.json");
			cApplication = new CApplication(this , is);
		}
		return cApplication;
	}
	
	private void initModule(){
		HashMap<String, CModule> modules = cApplication.getModules();
		for (CModule module : modules.values()) {
			module.onCreate(module);
		}
	}
	
	
	public InputStream getFromAssets(String fileName) {
		InputStream is = null;
		try {
			is = this.getResources().getAssets().open(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}
	
	@Override
	public void onTerminate() {
	
		super.onTerminate();
	}
	
	public void exitApp(){
		HashMap<String, CModule> modules = cApplication.getModules();
		for (CModule module : modules.values()) {
			module.onExit(module);
		}
	}
}
