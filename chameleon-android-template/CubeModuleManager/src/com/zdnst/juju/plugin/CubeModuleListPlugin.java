package com.zdnst.juju.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.google.gson.Gson;
import com.zdnst.juju.manager.CubeModuleManager;


/**
 * <BR>
 * [功能详细描述] 获取模块信息插件
 * 
 * @author Amberlo
 * @version [CubeAndroid , 2013-6-13]
 */
public class CubeModuleListPlugin extends CordovaPlugin {

	@Override
	public boolean execute(final String action, final JSONArray args,
			
			final CallbackContext callbackContext) throws JSONException {
//				Log.d("execute action {} in backgrund thread!", action);
				String result = null;
				if (action.equals("upgradableList")) {
					result = new Gson().toJson(CubeModuleManager.getInstance().getUpdatable_map());
				} else if (action.equals("installList")) {
					result = new Gson().toJson(CubeModuleManager.getInstance().getInstalled_map());
				} else if (action.equals("uninstallList")) {
					result = new Gson().toJson(CubeModuleManager.getInstance().getUninstalled_map());
				} else if (action.equals("mainList")) {
					Log.d("CubeModuleListPlugin","obtain main  list...");
					result = new Gson().toJson(CubeModuleManager.getInstance().getMain_map());
				} else {
					Log.d("CubeModuleListPlugin","action {} is not been proccessed!");
				}
				if (result != null) {
					System.out.println("rererere "+result);
					callbackContext.success(result);
				}
				return true;
	}
//	/**
//	 * [一句话功能简述]<BR>
//	 * [功能详细描述] 2013-9-16 上午10:47:40
//	 */
//	class MyRunnable implements Runnable {
//		private String result;
//		private CallbackContext callbackContext;
//
//		public MyRunnable(CallbackContext callbackContext, String result) {
//			this.callbackContext = callbackContext;
//			this.result = result;
//		}
//
//
//		@Override
//		public void run() {
//			callbackContext.success(result);
//		}
//
//	}
}