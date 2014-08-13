package com.zdnst.zdnstsdk;

import com.zdnst.router.RoutingParserHelper;

import android.content.res.Configuration;

public class CModule {

	private String identifier;
	private String name;
	private String packageName;
	private CApplication cApplication;
	private boolean firstStart;
	
	public CModule() {
		// TODO Auto-generated constructor stub
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public CApplication getcApplication() {
		return cApplication;
	}

	public void setcApplication(CApplication cApplication) {
		this.cApplication = cApplication;
	}
	
	public boolean isFirstStart() {
		return firstStart;
	}

	public void setFirstStart(boolean firstStart) {
		this.firstStart = firstStart;
	}

	//life cycle
	public void onConfigurationChanged(Configuration newConfig) {
		
	}

	public void onCreate(CModule cModule) {
		
	}
	
	public void onExit(CModule cModule){
		
	}
	
	public void onLowMemory() {
		
	}

	public void onTerminate() {
		
	}
	
	public String getActivity(){
		return null;
	}
	public void readConfig(int R) {
		RoutingParserHelper r = new RoutingParserHelper(cApplication.getmContext(),R);
		r.readConfig();
	}

}
