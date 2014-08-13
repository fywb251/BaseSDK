package com.zdnst.juju.model;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zdnst.bsl.util.FileCopeTool;
import com.zdnst.bsl.util.Preferences;
import com.zdnst.bsl.util.PropertiesUtil;
import com.zdnst.juju.CheckInUtil;
import com.zdnst.juju.manager.ApplicationSyncListener;
import com.zdnst.juju.manager.CubeModuleManager;
import com.zdnst.zdnstsdk.config.CubeConstants;
import com.zdnst.zdnstsdk.config.URL;
import com.zdnst.zillasdk.Zilla;
import com.zdnst.zillasdk.ZillaDelegate;

public class CubeApplication implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1698650844370589053L;

	private String name = null;

	private String releaseNote = null;

	private String icon = null;

	private String bundle = null;

	private String platform = null;

	private String version = null;

	private int build = 0;

	private String identifier = null;
	
	// 已安装的模块
	private Set<CubeModule> modules = new HashSet<CubeModule>();
	// 模块可升级时，旧版本和新版本的容器
	private Map<String, CubeModule> oldUpdateModules = new HashMap<String, CubeModule>();
	private Map<String, CubeModule> newUpdateModules = new HashMap<String, CubeModule>();

	private static CubeApplication instance;

	public transient FileCopeTool tool;

	private transient Context context;

	private static Context mContext;
	private String appKey = "";

	public static CubeApplication getInstance(Context context) {
		if (instance == null) {
			instance = new CubeApplication(context);
		}
		return instance;
	}

	public static CubeApplication resetInstance(Context context) {
		instance = null;
		return getInstance(context);
	}

	public CubeApplication(Context context) {

		this.context = context;
		mContext = context;
		tool = new FileCopeTool(context);

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReleaseNote() {
		return releaseNote;
	}

	public void setReleaseNote(String releaseNote) {
		this.releaseNote = releaseNote;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Set<CubeModule> getModules() {
		return modules;
	}

	public void setModules(Set<CubeModule> modules) {
		this.modules = modules;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getBuild() {
		return build;
	}

	public void setBuild(int build) {
		this.build = build;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void copyProperties(CubeApplication a) {
		this.identifier = a.getPackageName();
		this.build = a.getVersionCode();
		this.icon = a.getIcon();
		this.modules = a.getModules();
		this.name = a.getName();
		this.releaseNote = a.getReleaseNote();
		this.version = a.getVersionName();
		this.platform = a.getPlatform();
		this.appKey = a.getAppKey();
	}
 
	public void loadApplication() {

		if (this.isInstalled()) {
			// 运行目录读取
			System.out.println("运行时目录读取");
			String path = Environment.getExternalStorageDirectory().getPath();
//			if(AppStatus.USERLOGIN){
//				String userName = Preferences.getUserName( );
//				name = "Cube" + "_" + userName + ".json"; 
//			} else {
//				name = "Cube" + ".json";
//			}
			name = "Cube" + ".json";
			String results = tool.readerFile(path + "/" + context.getPackageName(), name);
			CubeApplication app = buildApplication(results);

			app.context = this.context;
//			// 同步预置模块
			copyProperties(app);

		} else {
			// assets目录读取
			System.out.println("assets目录读取");
			String results = tool.getFromAssets("Cube.json");
			CubeApplication app = buildApplication(results);
			app.context = this.context;

			copyProperties(app);

			/** 安装应用信息 */
			try {
				install();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 判断运行时目录是否存在Cube.json 文件。
	 * 
	 * @return
	 */
	public boolean isInstalled() {
		String path = Environment.getExternalStorageDirectory().getPath();
		Boolean isExist = tool.isfileExist(
				path + "/" + URL.APP_PACKAGENAME, "Cube.json");
		return isExist;
	}

	/**
	 * 判断当前设备是否存在登陆用户的数据
	 * 
	 * @return
	 */
	public boolean isUserExist(String name) {
		String path = Environment.getExternalStorageDirectory().getPath();
		Boolean isExist = tool
				.isfileExist(path + "/" + URL.APP_PACKAGENAME, "Cube-"
						+ name + ".json");
		return isExist;
	}

	public boolean isUserFileNull() {

		return true;
	}

	/**
	 * 安装应用，将文件复制到运行时目录
	 * 
	 */
	public void install() throws IOException {
		// 复制Assets文件夹中的Cube.json 文件到运行时目录。
		tool.copyOneFileToSDCard("Cube.json",
				Environment.getExternalStorageDirectory().getPath() + "/"
						+ URL.APP_PACKAGENAME + "/", "Cube.json");
		// add by zhoujun begin;
		// 安装时将cordova.js复制到sdcard中
		tool.copyOneFileToSDCard("www/cordova.js",
				Environment.getExternalStorageDirectory().getPath() + "/"
						+ URL.APP_PACKAGENAME + "/www/", "cordova.js");

	}
	
	/**
	 * 与服务器同步应用状态，获取模块更新
	 */
	public void sync(final ApplicationSyncListener listener,
			final CubeApplication app, final Context context, boolean dialogNeed , final String dialogContent) {
		
		ZillaDelegate callback = new ZillaDelegate() {
			
			@Override
			public void requestStart() {
				listener.syncStart();
			}
			
			@Override
			public void requestSuccess(String result) {
				if (result == null) {
					listener.syncFail();
					return;
				}
				
				try {
					JSONObject jb = new JSONObject(result);
					
					if (jb.has("result") && jb.getString("result").equals("error")) {
						listener.syncFail();
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				CubeApplication remote_app = CubeApplication
						.buildApplication(result);
				if (remote_app.getModules() == null) {
					listener.syncFail();
					return;
				}
				CubeApplication comparedCubeApp = compareAndSetApp(app,
						remote_app);
				CubeModuleManager.getInstance().init(comparedCubeApp);
//				Application.class.cast(context.getApplicationContext())
//						.setCubeApplication(comparedCubeApp);
//				context.sendBroadcast(new Intent("com.csair.cubeModelChange")
//						.putExtra("identifier", "none"));
				save(app);
				listener.syncFinish();
			}
			
			@Override
			public void requestFailed(String errorMessage) {
				Log.v("sync", "同步失败");
				listener.syncFail();
			}
			
		};
		Zilla.getZilla().syncModule(context, callback, dialogNeed, dialogContent);
	}

	public void syncPrivilege(boolean dialogNeed,final Context context,final String username,final ApplicationSyncListener callback 
			, final String dialogContent){
		ZillaDelegate delegate = new ZillaDelegate() {
			
			@Override
			public void requestSuccess(String result) {
				Log.d("syncPrivilege",result);
				
				try {
					Preferences.saveUser(username);
					JSONObject jb = new JSONObject(result);
			 		String role = jb.getString("rolesTag");
					// 签到
					CheckInUtil.pushSecurity(context, role);
					JSONArray jay = jb.getJSONArray("priviliges");
					ArrayList<String> getList = UserPrivilege.getInstance().getGetList();
					ArrayList<String> deleteList = UserPrivilege.getInstance().getDeleteList();
					ArrayList<String> privilegeList = UserPrivilege.getInstance().getPrivilegeList();
					deleteList.clear();
					getList.clear();
					privilegeList.clear();
					int len = jay.length();
					for(int i = 0;i < len; i ++){
						JSONArray jay2 = jay.getJSONArray(i);
						if (!privilegeList.contains(jay2.getString(1))){
							privilegeList.add(jay2.getString(1));
						}
						
						if(UserPrivilege.GET.equals(jay2.getString(0))){
							getList.add(jay2.getString(1));
						}
						
						if (UserPrivilege.DELETE.equals(jay2.getString(0))){
							deleteList.add(jay2.getString(1));
						}

					}
					CubeApplication cubeApplication = CubeApplication.getInstance(context);
					
					Set<CubeModule> modules = cubeApplication.getModules();
					for (CubeModule cubeModule : modules) {
						if (privilegeList.contains(cubeModule.getIdentifier())){
							List<Privilege> prList = new ArrayList<Privilege>();
							cubeModule.setPrivileges(prList);
						} else {
							cubeModule.setPrivileges(null);
						}
					}
					CubeModuleManager.getInstance().init(cubeApplication);
					cubeApplication.save(cubeApplication);
					callback.syncFinish();
					callback.syncFinish(result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			
			@Override
			public void requestStart() {
				callback.syncStart();
				
			}
			
			@Override
			public void requestFailed(String errorMessage) {
				callback.syncFail();
				
			}
		};

		Zilla.getZilla().syncPrivilege(context, delegate,username,dialogNeed, dialogContent);
	}
	
	public synchronized CubeApplication compareAndSetApp(
			CubeApplication oldOne, CubeApplication newOne) {
		if (oldOne == null) {
			return newOne;
		}

		Set<CubeModule> oldSet = new HashSet<CubeModule>(oldOne.getModules());
		Set<CubeModule> newSet = new HashSet<CubeModule>(newOne.getModules());
		// Map<String, CubeModule> oldHash = bulidMap(oldSet);
		Map<String, CubeModule> newHash = bulidMap(newSet);

		Map<String, CubeModule> unstallMap = new HashMap<String, CubeModule>();
		Map<String, CubeModule> installedMap = new HashMap<String, CubeModule>();
		Map<String, CubeModule> updatableMap = new HashMap<String, CubeModule>();

		oldOne.getOldUpdateModules().clear();
		oldOne.getNewUpdateModules().clear();
		for (CubeModule cubeModule : oldSet) {
			for (CubeModule module : newSet) {
				// 同步本地已存在模块和服务器模块信息
				if (cubeModule.getIdentifier().equals(module.getIdentifier())) {
					// cubeModule.setVersion(module.getVersion());
					cubeModule.setCategory(module.getCategory());
					cubeModule.setAutoDownload(module.isAutoDownload());
					cubeModule.setAutoShow(module.isAutoShow());
					cubeModule.setTimeUnit(module.getTimeUnit());
					cubeModule.setName(module.getName());
					cubeModule.setReleaseNote(module.getReleaseNote());
					cubeModule.setShowIntervalTime(module.getShowIntervalTime());
					cubeModule.setPrivileges(module.getPrivileges());
					cubeModule.setHidden(module.isHidden());
					cubeModule.setDownloadUrl(URL.getDownloadUrl(context,
							module.getBundle()));
					cubeModule.setSortingWeight(module.getSortingWeight());
					cubeModule.setInstallIcon(URL.getDownloadUrl(context,module.getIcon()));
					
					
					if (cubeModule.getLocal() == null ) {
						if(cubeModule.getModuleType()!=CubeModule.INSTALLED){
							
							cubeModule.setIcon(URL.getDownloadUrl(context,module.getIcon()));
						}else{
							if(!isExist(module,"icon.img")) {
								if(isExist(module,"icon.png")) {
									cubeModule.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.png"));
									
								}else {
									cubeModule.setIcon(URL.getDownloadUrl(context,module.getIcon()));
								}
							}
							//设置默认图标
						}
					}else {
						PropertiesUtil propertiesUtil = PropertiesUtil
								.readProperties(mContext, CubeConstants.CUBE_CONFIG);
						// 判断本地模块是否存在
						String icon = propertiesUtil.getString(
								"icon_"+cubeModule.getIdentifier(), "");
						cubeModule.setIcon(icon);
					}
					// +"?sessionKey="
					// + Preferences.getSESSION(Application.sharePref)
					// + "?appKey="
					// + oldOne.getAppKey());
					break;
				}
			}
			switch (cubeModule.getModuleType()) {
			case CubeModule.DELETING: {
				installedMap.put(cubeModule.getIdentifier(), cubeModule);
				break;
			}
			case CubeModule.INSTALLED: {
				installedMap.put(cubeModule.getIdentifier(), cubeModule);
				break;
			}
			case CubeModule.UNINSTALL: {
				unstallMap.put(cubeModule.getIdentifier(), cubeModule);
				break;
			}
			case CubeModule.INSTALLING: {
				unstallMap.put(cubeModule.getIdentifier(), cubeModule);
				break;
			}
			case CubeModule.UPGRADABLE: {
				updatableMap.put(cubeModule.getIdentifier(), cubeModule);
				break;
			}
			case CubeModule.UPGRADING: {
				updatableMap.put(cubeModule.getIdentifier(), cubeModule);
				break;
			}
			}
		}

		for (CubeModule cubeNew : newSet) {
			if(cubeNew.getIdentifier().contains("chat")) {
				System.out.println("nm");
			}
			String identify = cubeNew.getIdentifier();
			int build = cubeNew.getBuild();
			if (unstallMap.get(identify) == null
					&& installedMap.get(identify) == null) {

				cubeNew.setDownloadUrl(URL.getDownloadUrl(context,
						cubeNew.getBundle()));
				// cubeNew.setIcon(URL.DOWNLOAD +
				// cubeNew.getIcon()+"?sessionKey="
				// + Preferences.getSESSION(Application.sharePref)
				// + "?appKey="
				// + oldOne.getAppKey());
				if(cubeNew.getLocal()!=null) {
					PropertiesUtil propertiesUtil = PropertiesUtil
							.readProperties(mContext, CubeConstants.CUBE_CONFIG);
					// 判断本地模块是否存在
					String icon = propertiesUtil.getString(
							"icon_"+cubeNew.getIdentifier(), "");
					cubeNew.setIcon(icon);
				}else {
					cubeNew.setIcon(URL.getDownloadUrl(context, cubeNew.getIcon()));
					
				}
				unstallMap.put(cubeNew.getIdentifier(), cubeNew);

			} else if (unstallMap.get(identify) != null
					&& build != unstallMap.get(identify).getBuild()) {
				cubeNew.setDownloadUrl(URL.getDownloadUrl(context,
						cubeNew.getBundle()));
				// cubeNew.setIcon(URL.DOWNLOAD +
				// cubeNew.getIcon()+"?sessionKey="
				// + Preferences.getSESSION(Application.sharePref)
				// + "?appKey="
				// + oldOne.getAppKey());
				cubeNew.setIcon(URL.getDownloadUrl(context, cubeNew.getIcon()));
				unstallMap.remove(identify);
				unstallMap.put(cubeNew.getIdentifier(), cubeNew);

			} else if (installedMap.get(identify) != null) {
				CubeModule x = installedMap.get(identify);
				
				if (build <= x.getBuild()) {
					x.setUpdatable(false);
					updatableMap.remove(identify);
					
					// CubeModuleManager.getIdentifier_old_version_map().remove(x.getIdentifier());
					// CubeModuleManager.getIdentifier_new_version_map().remove(cubeNew.getIdentifier());
					// oldOne.getOldUpdateModules().remove(x.getIdentifier());
					// oldOne.getNewUpdateModules().remove(cubeNew.getIdentifier());

				} else if (build > x.getBuild()) {
					if(!"".equals(Preferences.getToken()))
					{
						Preferences.saveToken("", "");
					}
					
					x.setUpdatable(true);
					cubeNew.setModuleType(CubeModule.UPGRADABLE);
					cubeNew.setUpdatable(true);
					// cubeNew.setDownloadUrl(URL.DOWNLOAD +
					// cubeNew.getBundle());
					// cubeNew.setIcon(URL.DOWNLOAD +
					// cubeNew.getIcon()+"?sessionKey="
					// + Preferences.getSESSION(Application.sharePref)
					// + "?appKey="
					// + oldOne.getAppKey());
					cubeNew.setDownloadUrl(URL.getDownloadUrl(context,
							cubeNew.getBundle()));
					cubeNew.setIcon(URL.getDownloadUrl(context,
							cubeNew.getIcon()));
					updatableMap.put(cubeNew.getIdentifier(), cubeNew);
					// CubeModuleManager.getIdentifier_old_version_map().put(x.getIdentifier(),
					// x);
					// CubeModuleManager.getIdentifier_new_version_map().put(cubeNew.getIdentifier(),
					// cubeNew);
					oldOne.getOldUpdateModules().put(x.getIdentifier(), x);
					oldOne.getNewUpdateModules().put(cubeNew.getIdentifier(),
							cubeNew);

				}
			}

		}

		CopyOnWriteArraySet<CubeModule> set = new CopyOnWriteArraySet<CubeModule>(
				oldSet);
		for (CubeModule module : set) {
			if (!newSet.contains(module)
					&& newHash.get(module.getIdentifier()) == null) {
				unstallMap.remove(module.getIdentifier());
				installedMap.remove(module.getIdentifier());
				updatableMap.remove(module.getIdentifier());
			}
		}
		// 1.未安装：有新旧对象，则取新对象
		// 2.已安装: 看是否有更新对象，如果没有再设置为UPGRADABLE,再设置updatable为true
		// 如果有更新对象，则比较两版本的区别，取最新版

		oldOne.getModules().clear();
		oldOne.getModules().addAll(installedMap.values());
		oldOne.getModules().addAll(updatableMap.values());
		oldOne.getModules().addAll(unstallMap.values());
		// oldOne.getModules().add(oldHash.get("com.foss.voice"));
		// oldOne.getModules().add(oldHash.get("com.foss.message.record"));
		// oldOne.getModules().add(oldHash.get("com.foss.feedback"));
		// oldOne.getModules().add(oldHash.get("com.foss.announcement"));
		// oldOne.getModules().add(oldHash.get("com.foss.chat"));
		// oldOne.getModules().add(oldHash.get("com.foss.settings"));
		return oldOne;
	}

	
	public boolean isExist(CubeModule cubeModule,String name) {
		String path = URL.getSdPath(context, cubeModule.getIdentifier())+"/"+name;
		File f= new File(path);
		if (f.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public Map<String, CubeModule> bulidMap(Set<CubeModule> list) {
		Map<String, CubeModule> hash = new HashMap<String, CubeModule>();
		for (CubeModule cubeModule : list) {
			if (cubeModule == null) {
				continue;
			}
			hash.put(cubeModule.getIdentifier(), cubeModule);
		}
		return hash;
	}

	public void copyNeededProps(CubeModule src, CubeModule dest) {

		dest.setCategory(src.getCategory());
		// dest.setDownloading(src.isDownloading());
		dest.setDownloadUrl(src.getDownloadUrl());
		dest.setIcon(src.getIcon());
		dest.setIdentifier(src.getIdentifier());
		dest.setName(src.getName());
		dest.setProgress(src.getProgress());
		dest.setReleaseNote(src.getReleaseNote());
		dest.setUpdatable(src.getBuild() > dest.getBuild() ? true : false);
		dest.setVersion(src.getVersion());
		dest.setBuild(src.getBuild());
	}

	public static CubeApplication buildApplication(String json) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		CubeApplication result = gson.fromJson(json, CubeApplication.class);
		if (result.getModules() != null) {
			for (CubeModule each : result.getModules()) {
				if (each.getLocal() != null) {
					each.setModuleType(CubeModule.INSTALLED);
				} else if (each.getModuleType() == -1) {
					each.setModuleType(CubeModule.UNINSTALL);
				} else if ( each.getModuleType() == CubeModule.INSTALLING ){
					each.setModuleType(CubeModule.UNINSTALL);
				} else if(each.getModuleType() == CubeModule.UPGRADING ){
					each.setModuleType(CubeModule.INSTALLED);
				} else if(each.getModuleType() == CubeModule.DELETING ){
					each.setModuleType(CubeModule.INSTALLED);
				}
			}
		}
		return result;
	}
	
	public static CubeApplication buildGuestApplication(String json) {
		CubeModuleManager manager = CubeModuleManager.getInstance();
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		CubeApplication result = gson.fromJson(json, CubeApplication.class);
		if (result.getModules() != null) {
			for (CubeModule each : result.getModules()) {
				if (manager.getCubeModuleByIdentifier(each.getIdentifier()).getLocal() != null) {
					each.setModuleType(CubeModule.INSTALLED);
				} else if (manager.getCubeModuleByIdentifier(each.getIdentifier()).getModuleType() == -1) {
					each.setModuleType(CubeModule.UNINSTALL);
				} else if ( manager.getCubeModuleByIdentifier(each.getIdentifier()).getModuleType() == CubeModule.INSTALLING ){
					each.setModuleType(CubeModule.UNINSTALL);
				} else if(manager.getCubeModuleByIdentifier(each.getIdentifier()).getModuleType() == CubeModule.UPGRADING ){
					each.setModuleType(CubeModule.INSTALLED);
				} else if(manager.getCubeModuleByIdentifier(each.getIdentifier()).getModuleType() == CubeModule.DELETING ){
					each.setModuleType(CubeModule.INSTALLED);
				}
			}
		}
		return result;
	}

	public void save(CubeApplication app) {
//		if(AppStatus.USERLOGIN){
//			String userName = Preferences.getUserName( );
//			name = "Cube" + "_" + userName; 
//		} else {
//			name = "Cube";
//		}
		name = "Cube";
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String json = gson.toJson(app);
		try {
			tool.writeToJsonFile(name,
					Environment.getExternalStorageDirectory().getPath() + "/"
							+ URL.APP_PACKAGENAME + "/", json);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@SuppressWarnings("static-access")
	public void removeModule(CubeModule module) {
		String identify = module.getIdentifier();
		tool.deleteFile(Environment.getExternalStorageDirectory().getPath()
				+ "/" + URL.APP_PACKAGENAME + "/" + identify + ".zip");
		tool.deleteFolder(Environment.getExternalStorageDirectory().getPath()
				+ "/" + URL.APP_PACKAGENAME + "/www/" + identify);
		instance.getModules().remove(module);
		save(instance);
	}

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getPackageName() {
		return URL.APP_PACKAGENAME;
	}

	public String getVersionName() {
		return URL.APP_VERSION;
	}

	public int getVersionCode() {
		return URL.APP_BUILD;
	}

	public Map<String, CubeModule> getOldUpdateModules() {
		return oldUpdateModules;
	}

	public void setOldUpdateModules(Map<String, CubeModule> oldUpdateModules) {
		this.oldUpdateModules = oldUpdateModules;
	}

	public Map<String, CubeModule> getNewUpdateModules() {
		return newUpdateModules;
	}

	public void setNewUpdateModules(Map<String, CubeModule> newUpdateModules) {
		this.newUpdateModules = newUpdateModules;
	}

	public String getAppKey() {
		return PropertiesUtil.readProperties(mContext, CubeConstants.CUBE_CONFIG).getString(
				"appKey", "");

	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public static Context getmContext() {
		return mContext;
	}
}
