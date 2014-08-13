package com.zdnst.juju.plugin;

import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.zdnst.bsl.util.Preferences;
import com.zdnst.bsl.util.PropertiesUtil;
import com.zdnst.chameleon.AppStatus;
import com.zdnst.juju.CmanagerModuleActivity;
import com.zdnst.juju.manager.ApplicationSyncListener;
import com.zdnst.juju.manager.CubeModuleManager;
import com.zdnst.juju.model.CubeApplication;
import com.zdnst.juju.model.CubeModule;
import com.zdnst.juju.settings.SettingsActivity;
import com.zdnst.juju.view.AppDetailActivity;
import com.zdnst.push.tool.PadUtils;
import com.zdnst.zdnstsdk.config.CubeConstants;


/**
 * <BR>
 * [功能详细描述] 模块操作插件
 * 
 * @author Amberlo
 * @version [CubeAndroid , 2013-6-9]
 */
public class CubeModuleOperatorPlugin extends CordovaPlugin {
//	private final String LOG = CubeModuleOperatorPlugin.class.getSimpleName();
//	private Boolean fristTimeDownload = true;
	private AlertDialog needDownloadDialog;
	private AlertDialog needUpdateDialog;
//	private List<CubeModule> unInstalledModules;// 自动下载list
//	private List<CubeModule> updateModules;// 自动更新list
//	private List<CubeModule> isAutoShowModules;// 自动弹出模块list
//	private List<AutoDownloadRecord> autoDownloadRecord;// 数据库记录
	private boolean isLocalContent;
	
	public Dialog dialog;

	@Override
	public boolean execute(String action, JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		if (action.equals("sync")) {
			
			final CubeApplication cubeApp = CubeApplication.getInstance(cordova.getActivity());
			ApplicationSyncListener callback1 =  new ApplicationSyncListener() {

				@Override
				public void syncStart() {
//					callbackContext.success("sync start");
				}

				@Override
				public void syncFinish() {

					ApplicationSyncListener callback2 =  new ApplicationSyncListener() {

						@Override
						public void syncStart() {
//							callbackContext.success("sync start");
						}

						@Override
						public void syncFinish() {
							
						}

						@Override
						public void syncFail() {
							callbackContext.success("sync failed");
						}

						@Override
						public void syncFinish(String result) {
							callbackContext.success("sync success");
							String userName = Preferences.getUserName( );
							Log.i("lanjianlong","callback2, syncFinish(), userName = "+userName);
							List<CubeModule> autoDownloadModules = CubeModuleManager.getInstance().checkAutoDownload(userName);
							Log.i("lanjianlong","callback2, sycFinish(), autoDownloadModules.size() = "+autoDownloadModules.size());
							if(autoDownloadModules.size()!=0){
								showDownloadAlert(autoDownloadModules , userName);
							}
							
							List<CubeModule> upgradeModuel = CubeModuleManager.getInstance().checkUpgrade();
							if(upgradeModuel.size()!=0){
								showUpdateAlert(upgradeModuel);
							}
						}
						
					};
					 
					ApplicationSyncListener callback3 =  new ApplicationSyncListener() {

						@Override
						public void syncStart() {
//							callbackContext.success("sync start");
							

//							callbackContext.success("sync start");
//							dialog = new Dialog(cordova.getActivity(),R.style.common_dialog);
//							LayoutInflater mInflater = (LayoutInflater)cordova.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//							View v = mInflater.inflate(R.layout.dialog_layout, null);
//							TextView	tv = (TextView) v.findViewById(R.id.dialog_text);
//							tv.setText("正在同步");
//							dialog.setContentView(v);
//							dialog.show();
						}

						@Override
						public void syncFinish() {
							
						}

						@Override
						public void syncFail() {
							callbackContext.success("sync failed");
						}

						@Override
						public void syncFinish(String result) {
							callbackContext.success("sync success");
//							if (null != dialog && dialog.isShowing()) {
//								try {
//									dialog.cancel();
//								} catch (Exception e) {
//									// TODO: handle exception
//								}
//							}
							
							Preferences.savePrivileges(result);
							String userName = Preferences.getUserName( );
							Log.i("lanjianlong","callback3, syncFinish(), userName = "+userName);
							List<CubeModule> autoDownloadModules = CubeModuleManager.getInstance().checkAutoDownload(userName);
							Log.i("lanjianlong","callback3, sycFinish(), autoDownloadModules.size() = "+autoDownloadModules.size());
							if(autoDownloadModules.size()!=0){
								showDownloadAlert(autoDownloadModules , userName);
							}
							
							List<CubeModule> upgradeModuel = CubeModuleManager.getInstance().checkUpgrade();
							if(upgradeModuel.size()!=0){
								showUpdateAlert(upgradeModuel);
							}
						}
						
					};
					if (AppStatus.USERLOGIN){
						String username = Preferences.getUserName( );
						cubeApp.syncPrivilege(true,cordova.getActivity(), username, callback2, "正在同步");
					} else {
						cubeApp.syncPrivilege(true,cordova.getActivity(), "guest", callback3, "正在同步");
					}
				}

				@Override
				public void syncFail() {
					callbackContext.success("sync failed");
				}

				@Override
				public void syncFinish(String result) {
				}
				
			};
			
			// 只有同步不需要用到identifier
			
//			CubeApplication cubeApp = app.getCubeApplication();
			cubeApp.sync(callback1,cubeApp,cordova.getActivity(),true, "正在同步");
		} else if (action.equals("setting")) {
			Intent i = new Intent();
			i.setClass(cordova.getActivity(), SettingsActivity.class);
			cordova.getActivity().startActivity(i);
		} else if (action.equals("manager")) {
//			cordova.getActivity().sendBroadcast(new Intent(BroadcastConstans.JumpToCubeManager));
			
		} else if (action.equals("setTheme")) {// 设置皮肤
//			Intent i = new Intent(BroadcastConstans.CHANGE_SKIN);
//			cordova.getActivity().sendBroadcast(i);

		} else {
			String identifier = args.getString(0);

			CubeModule module = CubeModuleManager.getInstance()
					.getModuleByIdentify(identifier);
			if (module == null)
				return false;
			CubeModuleManager manager = CubeModuleManager.getInstance(); 
			if (action.equals("upgrade")) {
				// 升级模块，先取出新版本的模块
				module = manager.getIdentifier_new_version_map().get(identifier);
				manager.upgrade(module);
			} else if (action.equals("install")) {
				// 安装模块
				manager.install(module);
			} else if (action.equals("uninstall")) {
				// 卸载模块
				Log.i("AAA", "进入删除");
				manager.uninstall(module);
			} else if (action.equals("checkDepends")) {
				// 检查依赖
			} else if (action.equals("showModule")) {
				String type = args.getString(1);
				// 效能监控点击模块保存数据
				/*
				 * <!--
				 * Application.class.cast(cordova.getActivity().getApplication
				 * ()).saveModulerRecord(module);
				 * 
				 * -->
				 */
				
				if (type.equals("main")) {
					showModuleInMainPage(module);
				}
				else {
					showModuleInDetail(module,type);
				}
			}
		}
		return true;
	}

	// 弹出更新提示
	public void showUpdateAlert(final List<CubeModule> updateModules) {
		StringBuffer sb = new StringBuffer();
		for (CubeModule module : updateModules) {
			sb.append("[" + module.getName() + " " + module.getVersion() + "]"
					+ "\n");
		}

		needUpdateDialog = new AlertDialog.Builder(cordova.getActivity())
				.setTitle("检测到有以下模块需要更新：")
				.setMessage(sb.toString())

				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						CubeModuleManager.getInstance().autoUpgrade(updateModules);
						Toast.makeText(cordova.getActivity(), "正在更新模块",
								Toast.LENGTH_LONG).show();
					}
				}).show();
		needUpdateDialog.setCancelable(false);
//				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						
//					}
//				}).show();
	}

	
	// 弹出下载提示窗口
	public void showDownloadAlert(final List<CubeModule> modules , final String userName) {
		StringBuffer sb = new StringBuffer();
		// 提示需要下载的模块
		for (CubeModule module : modules) {
			sb.append("[" + module.getName() + " " + " " + module.getVersion()
					+ "]" + "\n");
		}
		needDownloadDialog = new AlertDialog.Builder(cordova.getActivity())

				.setTitle("检测到有以下模块需要下载：")
				.setMessage(sb.toString())
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						CubeModuleManager.getInstance().autoDownload(modules , userName);
						Toast.makeText(cordova.getActivity(), "正在下载模块",
								Toast.LENGTH_LONG).show();

					}
				}).show();
		needDownloadDialog.setCancelable(false);
//				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface arg0, int arg1) {
//						CubeModuleManager.getInstance().cancelAutoDownload(modules , userName);
//					}
//				}).show();
	}

	// 弹出下载提示窗口
		public void showDependAlert(final List<CubeModule> modules) {
			StringBuffer sb = new StringBuffer();
			// 提示需要下载的模块
			for (CubeModule module : modules) {
				sb.append("[" + module.getName() + " " + " " + module.getVersion()
						+ "]" + "\n");
			}
			needDownloadDialog = new AlertDialog.Builder(cordova.getActivity())

					.setTitle("缺少依赖模块：")
					.setMessage(sb.toString())
					.setPositiveButton("安装", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							CubeModuleManager.getInstance().autoDownload(modules,Preferences.getUserName( ));
							Toast.makeText(cordova.getActivity(), "正在下载模块",Toast.LENGTH_LONG).show();

						}
					}).show();
			needDownloadDialog.setCancelable(false);
//					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface arg0, int arg1) {
//							
//						}
//					}).show();
		}

	
	public void showModuleInMainPage(CubeModule module) {
		List<CubeModule> dependModules = CubeModuleManager.getInstance().checkDepends(module.getIdentifier());
		if(dependModules.size()!=0){
			showDependAlert(dependModules);
		}else{
			
			if(PadUtils.isPad(cordova.getActivity())){
				final String moduleUrl = CubeModuleManager.getInstance().getModuleUrl(cordova.getActivity(), module);
				if (module.getLocal() == null) {
					isLocalContent = false;
				}else{
					isLocalContent = true;
				}
				
//			不存在页面 NoticeFragment  即时通讯没有 
				if(moduleUrl != null && !moduleUrl .equals("") && cordova.getActivity() instanceof CmanagerModuleActivity){
					cordova.getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							((CmanagerModuleActivity)cordova.getActivity()).loadDetailContent(moduleUrl,isLocalContent,null);									
						}
					});
				}
				
			}else{
				Intent moduleIntent =  CubeModuleManager.getInstance().showModule(cordova.getActivity(),module);
				cordova.getActivity().startActivity(moduleIntent);
			}
		}
	}

	
	
	public void showModuleInDetail(CubeModule module,String type) {
		if(PadUtils.isPad(cordova.getActivity())){
			PropertiesUtil propertiesUtil = PropertiesUtil
					.readProperties(
							CubeModuleOperatorPlugin.this.cordova
									.getActivity(), CubeConstants.CUBE_CONFIG);
			
			if(cordova.getActivity() instanceof CmanagerModuleActivity){
				
				final String moduleDetailFragment = propertiesUtil.getString("moduleDetailFragment", "");
				final Bundle bundle = new Bundle();
				if (type.equals("upgrade")||type.equals("upgradable")) {
					bundle.putString("FROM_UPGRAGE", "FROM_UPGRAGE");
				}
				bundle.putString("identifier",  module.getIdentifier());
				cordova.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						((CmanagerModuleActivity)cordova.getActivity()).loadDetailContent(moduleDetailFragment,true,bundle);									
					}
				});
			}
		}else{
			Intent intent = new Intent();
			intent.putExtra("identifier", module.getIdentifier());
			intent.putExtra("version", module.getVersion());
			intent.putExtra("build", module.getBuild());
			if (type.equals("upgrade")||type.equals("upgradable")) {
				intent.putExtra("FROM_UPGRAGE", "FROM_UPGRAGE");
			}
			intent.setClass(cordova.getActivity(),
					AppDetailActivity.class);
			cordova.getActivity().startActivity(intent);
		}
	}

}