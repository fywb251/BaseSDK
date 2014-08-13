package com.zdnst.zdnstsdk.config;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;


public class URL {


	//cube.properties
	public static String ANNOUNCE = null;
	public static String BASE_WEB = null;
	public static String MUC_BASE = null;
	public static String BASE_WS = null;
	
	//cube.properties
	public static String PAD_MAIN_URL = null;
	public static String PAD_LOGIN_URL = null;
	public static String PHONE_MAIN_URL = null;
	public static String PHONE_LOGIN_URL = null;
	
	
	public static String UPLOAD_URL = null;
	public static String SYNC= null;
	public static String UPLOAD = null;
	public static String LOGIN = null;
	public static String LOGOUT = null;
	public static String UPDATE = null;
	public static String SNAPSHOT =  null;
	public static String PUSH_BASE_URL = null;
	public static String CHECKIN_URL =null;
	public static String CHECKOUT_URL = null;
	public static String FEEDBACK_URL  = null;
	public static String AUTH = null;
	//
	public static String UPDATE_RECORD= null;
	public static String GETPUSHMESSAGE = null;

	public static String APP_VERSION = null;
	public static int APP_BUILD = 0;
	public static String APP_PACKAGENAME =null;
	public static String APPKEY = null;
	
	public static void initUrl (Context context) {
		PropertiesUtil propertiesUtil = PropertiesUtil.readProperties(context,
				CubeConstants.CUBE_CONFIG);
		URL.APPKEY = propertiesUtil.getString("appKey", "");
		URL.ANNOUNCE = propertiesUtil.getString("ANNOUNCE", "");
		URL.BASE_WEB = propertiesUtil.getString("BASE_WEB", "");
		URL.MUC_BASE = propertiesUtil.getString("MUC_BASE", "");
		URL.BASE_WS = propertiesUtil.getString("BASE_WS", "");

//		URL.PAD_MAIN_URL = propertiesUtil.getString("PAD_MAIN_URL", "");
//		URL.PAD_LOGIN_URL = propertiesUtil.getString("PAD_LOGIN_URL", "");
//		URL.PHONE_MAIN_URL = propertiesUtil.getString("PHONE_MAIN_URL", "");
//		URL.PHONE_LOGIN_URL = propertiesUtil.getString("PHONE_LOGIN_URL", "");
		
		URL.UPLOAD_URL = URL.BASE_WEB + "mam/attachment/clientUpload";
		URL.SYNC = URL.BASE_WS + "mam/api/mam/clients/android/";
		URL.UPLOAD = URL.BASE_WS + "mam/api/mam/attachment/upload";
		URL.LOGIN = URL.BASE_WS + "system/api/system/mobile/accounts/login";
		URL.LOGOUT = URL.BASE_WS + "system/api/system/mobile/accounts/logout";
		URL.UPDATE = URL.BASE_WS + "mam/api/mam/clients/update/android";
		URL.UPDATE_RECORD = URL.BASE_WS
				+ "mam/api/mam/clients/update/appcount/android/";
		URL.SNAPSHOT = URL.BASE_WS + "mam/api/mam/clients/widget/";
		URL.PUSH_BASE_URL = URL.BASE_WS+ "push/api/";
		URL.GETPUSHMESSAGE =URL.PUSH_BASE_URL+"push-msgs/none-receipts/";
		URL.CHECKIN_URL = URL.PUSH_BASE_URL + "checkinservice/checkins";
		URL.CHECKOUT_URL = URL.PUSH_BASE_URL + "checkinservice/checkout";
		URL.FEEDBACK_URL = URL.PUSH_BASE_URL + "receipts";
		
		
		URL.APP_PACKAGENAME = context.getPackageName();
		URL.APP_VERSION = getAppVersion(context);
		URL.APP_BUILD = getAppVersionCode(context);
		URL.AUTH = URL.BASE_WS +"mam/api/mam/clients/apps/android/"+ URL.APP_PACKAGENAME + "/"+URL.APP_VERSION+"/validate";
		
		// 请在cube.properties中配置
		URL.PAD_MAIN_URL = "file://" + getPackagePath(context)+"/www/"+ propertiesUtil.getString("PAD_MAIN_URL", "");
		URL.PAD_LOGIN_URL = "file://" + getPackagePath(context)+"/www/"+ propertiesUtil.getString("PAD_LOGIN_URL", "");
		URL.PHONE_MAIN_URL = "file://" + getPackagePath(context)+"/www/"+ propertiesUtil.getString("PHONE_MAIN_URL", "");
		URL.PHONE_LOGIN_URL = "file://" + getPackagePath(context)+"/www/"+ propertiesUtil.getString("PHONE_LOGIN_URL", "");
//		http://10.108.1.217:18860/mam/api /mam/clients/apps/{platform}/{identifier}/{version}/validate
	}
	
	public static String getDownloadUrl(Context context, String bundle) {
		String DOWNLOAD = BASE_WS + "mam/api/mam/clients/files/";
		return DOWNLOAD + bundle + "?" + "appKey="+ APPKEY;
	}
	
	public static String getUpdateAppplicationUrl(Context context, String bundle) {
		String DOWNLOAD = BASE_WS + "mam/api/mam/clients/files/";
//		String appKey = Application.class.cast(context.getApplicationContext()).getCubeApplication().getAppKey();
		return DOWNLOAD + bundle + "?appKey="+ APPKEY;
	}
	
	
	public static String getSessionKey() {
//		String sessionKey = Preferences.getSESSION(Application.sharePref);
		String sessionKey = "";
		return sessionKey;
	}
	
	public static String getSdPath(Context context,String identifier) {
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/" + context.getPackageName();
		String url = path + "/www/" + identifier;
		return url;
	}
	
	public static String getAppVersion(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static int getAppVersionCode(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String getPackagePath(Context context) {
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/" + context.getPackageName();
		return path;
	}
	
}
