package com.zdnst.juju.manager;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.zdnst.juju.model.CubeModule;


public class AutoDownloadHelper {

	private static AutoDownloadHelper instance;
	
	//当前自动下载的任务列表
	private static Map<String,CubeModule> downloadList = new HashMap<String, CubeModule>();
	
	//当前自动下载的任务列表
	private static Map<String,CubeModule> totalList = new HashMap<String, CubeModule>();
	
//	//自动下载的总数
//	private static int autoDownloadCount = 0;
	

	/**
	 * @return
	 */
	public static AutoDownloadHelper getInstance(){
		if(instance ==null){
			instance = new AutoDownloadHelper();
		}
		return instance;
	}
	
//	public static int getAutoDownloadCount() {
//		return autoDownloadCount;
//	}
//	
//	//设置自动下载任务的总数
//	public void setAutoDownloadCount(int autoDownloadCount) {
//		AutoDownloadHelper.autoDownloadCount = autoDownloadCount;
//	}
	
	public int getProgressCount(){
		return downloadList.size();
	}
	
	public int getTotalCount(){
		return totalList.size();
	}
	
	public boolean addDownloadTask(CubeModule module){
		boolean isSuccess = false;
		if(!downloadList.containsKey(module.getIdentifier())){
			synchronized (downloadList) {
				downloadList.put( module.getIdentifier() , module);
				Log.i("lanjianlong","addDownloadTask(CubeModule module),!downloadList.cont..,  identifier = "+module.getIdentifier()+": module = "+module);
				isSuccess=true;
			}
		}else{
			Log.i("lanjianlong","addDownloadTask(CubeModule module),!downloadList.cont.., false; identifier = "+module.getIdentifier()+": module = "+module);
			isSuccess=false;
			/**
			 * lanjianlong 
			 */
//			downloadList.remove(module);
		}
		
		if(!totalList.containsKey(module.getIdentifier())){
			synchronized (totalList) {
				totalList.put( module.getIdentifier() , module);
				isSuccess=true;
				Log.i("lanjianlong","addDownloadTask(CubeModule module),!totalList.cont; identifier = "+module.getIdentifier()+": module = "+module);
			}
		}else{
			isSuccess=false;
			/**
			 * lanjianlong
			 */
			Log.i("lanjianlong","addDownloadTask(CubeModule module),!totalList.cont, false; identifier = "+module.getIdentifier()+": module = "+module);
//			totalList.remove(module);
		}
		return isSuccess;
	}
	
	public void finishDownload(CubeModule module){
		if(downloadList.containsKey(module.getIdentifier())){
			synchronized (downloadList) {
				Log.i("lanjianlong","finishDownload(CubeModule module), identifier = "+module.getIdentifier());
				downloadList.remove(module.getIdentifier());
			}
		}
		
	}
	
	public void clear(){
		synchronized (downloadList) {
			downloadList.clear();
//			autoDownloadCount=0;
			totalList.clear();
		}
	}
}
