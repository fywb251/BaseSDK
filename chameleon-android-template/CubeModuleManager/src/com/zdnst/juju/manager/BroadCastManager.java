package com.zdnst.juju.manager;

import android.content.Context;
import android.content.Intent;

import com.zdnst.bsl.util.BroadcastConstans;
import com.zdnst.juju.model.CubeModule;


public class BroadCastManager {
	//刷新自动下载进度
	public static void sentModuleDownloadCount(Context context) {
		Intent it = new Intent(BroadcastConstans.MODULE_AUTODOWNLOAD_PROGERSS);
		context.sendBroadcast(it);
	}
	
	
	public static void sentModuleDownloadStart(Context context){
		Intent it = new Intent(BroadcastConstans.MODULE_AUTODOWNLOAD_START);
		context.sendBroadcast(it);
	}
	public static void sentModuleDownloadFinsh(Context context){
		Intent it = new Intent(BroadcastConstans.MODULE_AUTODOWNLOAD_FINISH);
		context.sendBroadcast(it);
	}
	
	
	
	public static void refreshMainPage(Context context, CubeModule module) {
		Intent it = new Intent(BroadcastConstans.RefreshMainPage);
//		refreshMainPage(identifier, type, module);
		it.putExtra("identifier", module.getIdentifier());
		it.putExtra("type","main");
		context.sendBroadcast(it);
	}
	
	
	/**
	 * [功能详细描述]模块增减，刷新页面
	 * @param context
	 * @param module
	 */
	public static void refreshModule(Context context,String type, CubeModule module) {
		Intent it = new Intent(BroadcastConstans.RefreshModule);
		it.putExtra("identifier", module.getIdentifier());
		it.putExtra("type", type);
		context.sendBroadcast(it);
//		refreshModule(identifier, type, module);
	}
	
	/**
	 * [功能详细描述]下载模块时刷新进度条
	 * @param context
	 * @param module
	 * @param progress
	 */
	public static void updateProgress(Context context, CubeModule module, int progress) {
		Intent it = new Intent(BroadcastConstans.UpdateProgress);
		it.putExtra("identifier", module.getIdentifier());
		it.putExtra("progress", progress);
		context.sendBroadcast(it);
	}
	
	
	
	/**
	 * [功能详细描述]接收推送时修改图标上的数字
	 * @param context
	 * @param module
	 */
	public static void receiveMessage(Context context, CubeModule module) {
//		receiveMessage(identifier, count, display);
		Intent it = new Intent(BroadcastConstans.ReceiveMessage);
		it.putExtra("identifier", module.getIdentifier());
		
		
		context.sendBroadcast(it);
	}
	
}
