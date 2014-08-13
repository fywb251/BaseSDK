package com.zdnst.juju.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.foreveross.chameleon.manager.R;
import com.google.gson.Gson;
import com.zdnst.chameleon.util.DeviceInfoUtil;
import com.zdnst.juju.model.CubeApplication;
import com.zdnst.message.update.CheckUpdateTask;
import com.zdnst.message.update.ManualCheckUpdateListener;
import com.zdnst.zdnstsdk.config.URL;

public class CubeSettingPlugin extends CordovaPlugin {


	@Override
	public boolean execute(String action, JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		if (action.equals("getAppInfo")) {
			callbackContext.success(getAppInfo());
		} else if (action.equals("checkAppUpdate")) {
			checkAppUpdate();
		}
		return true;
	}
	
	private String getAppInfo(){
		
		String version = URL.APP_VERSION;
		String deviceId = DeviceInfoUtil.getDeviceId(cordova.getActivity());
		String appName = cordova.getActivity().getResources().getString(R.string.app_name);
		String appKey = URL.APPKEY;
		String appId = URL.APP_PACKAGENAME;
		String loginUrl = URL.LOGIN;
		String logoutUrl = URL.LOGOUT;
		AppInfo info = new AppInfo(version, deviceId, appName, appKey, appId, loginUrl, logoutUrl);
		Gson gson = new Gson();
		return gson.toJson(info);
	}
	
	private void checkAppUpdate(){
		new CheckUpdateTask(
				CubeApplication.getInstance(cordova.getActivity()),
				new ManualCheckUpdateListener(cordova.getActivity()))
		.execute();
	}
	
	class AppInfo{
		private String version;
		private String deviceId;
		private String appName;
		private String appKey;
		private String appId;
		private String loginUrl;
		private String logoutUrl;
		public AppInfo(String version, String deviceId, String appName,
				String appKey, String appId, String loginUrl, String logoutUrl) {
			super();
			this.version = version;
			this.deviceId = deviceId;
			this.appName = appName;
			this.appKey = appKey;
			this.appId = appId;
			this.loginUrl = loginUrl;
			this.logoutUrl = logoutUrl;
		}
	}
}