package com.zdnst.juju.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zdnst.bsl.util.BroadcastConstans;
import com.zdnst.bsl.util.FileCopeTool;
import com.zdnst.bsl.util.FileIntent;
import com.zdnst.bsl.util.PropertiesUtil;
import com.zdnst.chameleon.httputil.DownloadFileAsyncTask;
import com.zdnst.chameleon.httputil.ThreadPlatformUtils;
import com.zdnst.juju.CubeSettingActivity;
import com.zdnst.juju.model.CubeApplication;
import com.zdnst.juju.model.CubeModule;
import com.zdnst.juju.task.CheckDependsTask;
import com.zdnst.juju.task.UnZipTask;
import com.zdnst.juju.view.CubeAndroid;
import com.zdnst.zdnstsdk.config.CubeConstants;
import com.zdnst.zdnstsdk.config.URL;

public class ModuleOperationService extends Service {
	
	private ExecutorService pool = Executors.newFixedThreadPool(3);
	
	public class ModuleOperationServiceBinder extends Binder {
		public ModuleOperationService getService() {
			return ModuleOperationService.this;
		}
	}
  
	@Override
	public IBinder onBind(Intent intent) {
		return new ModuleOperationServiceBinder();
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	public static Intent getIntent(Context context) {
		return new Intent(context, ModuleOperationService.class);
	}

	public void install(final CubeModule module) {
		
		if (!CubeModuleManager.getInstance().getAll_map().get(module.getCategory())
				.contains(module)) {
//			throw new UnsupportedOperationException("你传入的对象不在管理器中");
			Toast.makeText(getApplicationContext(), "你传入的对象不在管理器中", Toast.LENGTH_SHORT).show();
			return;
			
		}

		DownloadFileAsyncTask task = new DownloadFileAsyncTask(this) {

			private int preProgress = -1;

			@Override
			public void doPreExecuteWithoutDialog() {
				super.doPreExecuteWithoutDialog();
				module.setModuleType(CubeModule.INSTALLING);
				BroadCastManager.updateProgress(context, module, -1);
				BroadCastManager.sentModuleDownloadCount(context);
				
			}

			int flag = 0;

			@Override
			protected void onProgressUpdate(Integer... progress) {
				super.onProgressUpdate(progress);
				int p = progress[0];
				module.setProgress(p);
				if (p == 0 || p == 100 || (p - preProgress) >= 5) {
					BroadCastManager.updateProgress(context, module, p);
					preProgress = p;
				}
				if ((flag + p) == -1) {
					flag++;
					BroadCastManager.updateProgress(context, module, p);
				}
			}

			@Override
			protected void doPostExecute(String result) {
				super.doPostExecute(result);
				final CubeApplication cubeApplication = CubeApplication.getInstance(context);
				
				UnZipTask unZipTask = new UnZipTask(
						ModuleOperationService.this, cubeApplication, module) {

					@Override
					protected void onPreExecute() {
						super.onPreExecute();
					}

					@Override
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						if (result) {
							CubeModuleManager.getInstance().add2Main(module);
							if(module.isHidden()) {
								CubeModuleManager.getInstance().removeFormMain(module);
							}	
							module.setModuleType(CubeModule.INSTALLED);
							if(isExist(module,"icon.img")) {
								module.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.img"));
							}else if(isExist(module,"icon.png")) {
								module.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.png"));
							}
							CubeModuleManager.getInstance().removeFormUninstalled(module);
							CubeModuleManager.getInstance().add2Installed(module);
							Set<CubeModule> storedSet = CubeApplication.getInstance(context).getModules();
							storedSet.add(module);
							BroadCastManager.updateProgress(context, module, 101);
							BroadCastManager.refreshModule(context, "install" , module);
							cubeApplication.save(cubeApplication);
							Log.v("Depends", "安装成功，开始检查依赖");
							AutoDownloadHelper.getInstance().finishDownload(module);
							BroadCastManager.sentModuleDownloadCount(context);
							if(AutoDownloadHelper.getInstance().getProgressCount()==0) {
								BroadCastManager.sentModuleDownloadFinsh(context);
								AutoDownloadHelper.getInstance().clear();
							}
							BroadCastManager.refreshMainPage(context, module);
							CheckDependsTask task = new CheckDependsTask() {
								@Override
								protected void onPostExecute(
										ArrayList<CubeModule> result) {
									if (null == result) {
										return;
									} else if (result.size() == 0) {
										Log.v("Depands", "没有依赖模块下载");
									} else {
										for (CubeModule m : result) {
											int type = m.getModuleType();
											if (m.getPrivileges() != null) {
												if (type == CubeModule.UPGRADABLE
														&& type != CubeModule.UPGRADING
														&& type != CubeModule.DELETING) {
													CubeModuleManager.getInstance().upgrade(m);
												} else if (type == CubeModule.UNINSTALL
														&& type != CubeModule.INSTALLING
														&& type != CubeModule.DELETING) {
													CubeModuleManager.getInstance().install(m);
												}
											}
										}
									}
									super.onPostExecute(result);
								}
							};
							String checkPath = Environment
									.getExternalStorageDirectory().getPath()
									+ "/" + URL.APP_PACKAGENAME + "/www/";
							task.execute(module.getIdentifier(), checkPath);
						} else {
							if(module !=null) {
								module.setModuleType(CubeModule.UNINSTALL);
								CubeModuleManager.getInstance().removeFormMain(module);
								AutoDownloadHelper.getInstance().finishDownload(module);
								BroadCastManager.sentModuleDownloadCount(context);
								if(AutoDownloadHelper.getInstance().getProgressCount()==0) {
									BroadCastManager.sentModuleDownloadFinsh(context);
									AutoDownloadHelper.getInstance().clear();
								}
								Toast.makeText(context, "网络状态不稳定!", Toast.LENGTH_SHORT).show();
								BroadCastManager.refreshMainPage(context, module);
								BroadCastManager.updateProgress(context, module, 101);
							}
						}
					};
				};
				unZipTask.execute();
				
			}

			@Override
			protected void doHttpFail(Exception e) {
				super.doHttpFail(e);
				if (module!=null) {
					module.setModuleType(CubeModule.UNINSTALL);
					CubeModuleManager.getInstance().removeFormMain(module);
					AutoDownloadHelper.getInstance().finishDownload(module);
//					ThreadPlatformUtils.SecreaseAutodownLoadTaskCout();//减一
				//	System.out.println("下载模块:  "+module.getName()+"  失败 ,未下载模块数为: "+ThreadPlatformUtils.getAutodownLoadTaskCout());
					BroadCastManager.sentModuleDownloadCount(ModuleOperationService.this);
					if(AutoDownloadHelper.getInstance().getProgressCount()==0) {
						BroadCastManager.sentModuleDownloadFinsh(context);
						AutoDownloadHelper.getInstance().clear();
					}
					BroadCastManager.refreshMainPage(context, module);
					BroadCastManager.updateProgress(context, module, 101);
				}

			}

		};

		task.setShowProgressDialog(false);
		task.setNeedProgressDialog(false);
		ThreadPlatformUtils.executeByPalform(task, 
				new String[] { module.getDownloadUrl(),
						module.getIdentifier() + ".zip",
						DownloadFileAsyncTask.SDCARD, URL.APP_PACKAGENAME });
	}

	public boolean isExist(CubeModule cubeModule,String name) {
		String path = URL.getSdPath(getApplicationContext(), cubeModule.getIdentifier())+"/"+name;
		File f= new File(path);
		if (f.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public void uninstall(final CubeModule cubeModule) {
		if (!CubeModuleManager.getInstance().getAll_map().get(cubeModule.getCategory())
				.contains(cubeModule)) {
			throw new UnsupportedOperationException("你传入的对象不在管理器中");
		}
		cubeModule.setModuleType(CubeModule.DELETING);
//		BroadCastManager.sendModuleBroadcast(cubeModule);
		final  Context context = ModuleOperationService.this;
//		BroadCastManager.refreshModule(context, cubeModule);
		final CubeApplication app =CubeApplication.getInstance(ModuleOperationService.this.getApplicationContext());

		AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {
			private int preProgress = -1;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				ThreadPlatformUtils.addTask2List(this);
				BroadCastManager.updateProgress(context, cubeModule, -1);
			}

			@Override
			protected Boolean doInBackground(String... params) {
				publishProgress(-1);
				String basePath = Environment.getExternalStorageDirectory()
						.getPath() + "/" + URL.APP_PACKAGENAME;
				StringBuilder folderPath = new StringBuilder();
				StringBuilder zipPath = new StringBuilder();
				folderPath.append(basePath).append("/www/")
						.append(cubeModule.getIdentifier());
				zipPath.append(basePath + "/")
						.append(cubeModule.getIdentifier()).append(".zip");
				app.tool.deleteFolder(folderPath.toString());
				app.tool.deleteFile(zipPath.toString());
				return true;
			}

			int flag = 0;

			@Override
			protected void onProgressUpdate(Integer... progress) {
				super.onProgressUpdate(progress);
				int p = progress[0];
				cubeModule.setProgress(p);
				if (p == 0 || p == 100 || (p - preProgress) >= 5) {
					BroadCastManager.updateProgress(context, cubeModule, p);
					preProgress = p;
				}

				if ((flag + p) == -1) {
					flag++;
					BroadCastManager.updateProgress(context, cubeModule, p);
				}
			}

			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				ThreadPlatformUtils.finishTask(this);
				if (result) {
					cubeModule.setModuleType(CubeModule.UNINSTALL);
					cubeModule.setIcon(cubeModule.getInstallIcon());
					CubeModuleManager.getInstance().removeFormInstalled(cubeModule);
					if (!cubeModule.isHidden()) {
						CubeModuleManager.getInstance().removeFormMain(cubeModule);
					}
					CubeModule newVersion = CubeModuleManager.getInstance()
							.getIdentifier_new_version_map().get(
									cubeModule.getIdentifier());
					if (newVersion != null) {
						newVersion.setModuleType(CubeModule.UNINSTALL);
						newVersion.setUpdatable(false);
						CubeModuleManager.getInstance().removeFormUpdatable(newVersion);
						CubeModuleManager.getInstance().removeFormInstalled(cubeModule);
						CubeModuleManager.getInstance().add2Uninstalled(newVersion);
						CubeModuleManager.getInstance().getIdentifier_new_version_map()
								.remove(newVersion);
						CubeModuleManager.getInstance().getIdentifier_old_version_map()
								.remove(cubeModule);
						CubeModuleManager.getInstance().getAllSet().remove(newVersion);
						app.getOldUpdateModules().remove(
								cubeModule.getIdentifier());
						app.getNewUpdateModules().remove(
								newVersion.getIdentifier());
					} else {
						CubeModuleManager.getInstance().add2Uninstalled(cubeModule);
					}
					
					BroadCastManager.refreshModule(context,"uninstall", cubeModule);
					Set<CubeModule> storedSet = CubeApplication.getInstance(context)
							.getModules();
					storedSet.remove(cubeModule);
					app.save(app);
					BroadCastManager.updateProgress(context, cubeModule, 101);
				} else {
					Toast.makeText(ModuleOperationService.this, "删除失败!",
							Toast.LENGTH_SHORT).show();
					BroadCastManager.updateProgress(context, cubeModule, 101);
				}

			};

		};

		ThreadPlatformUtils.executeByPalform(task, new String[] {});
	}

	public void upgrade(final CubeModule module) {
		if (!CubeModuleManager.getInstance().getAll_map().get(module.getCategory())
				.contains(module)) {
			throw new UnsupportedOperationException("你传入的对象不在管理器中");
		}

		DownloadFileAsyncTask task = new DownloadFileAsyncTask(this) {

			private int preProgress = -1;

			@Override
			public void doPreExecuteWithoutDialog() {
				super.doPreExecuteWithoutDialog();
				module.setModuleType(CubeModule.UPGRADING);
				if (!module.isHidden()) {
					CubeModule oldCubeModule = CubeModuleManager.getInstance()
							.getIdentifier_old_version_map().get(
									module.getIdentifier());
					CubeModuleManager.getInstance().removeFormMain(oldCubeModule);
					CubeModuleManager.getInstance().add2Main(module);
					if(isExist(oldCubeModule,"icon.img")) {
						module.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.img"));
					}else if(isExist(oldCubeModule,"icon.png")) {
						module.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.png"));
					}
				}
			//	System.out.println("这是更新模块doPreExecuteWithoutDialog方法调用");
//				ThreadPlatformUtils.addAutodownLoadTaskCout();
				BroadCastManager.sentModuleDownloadCount(context);
//				sendModuleBroadcast(module);
			}

			int flag = 0;

			@Override
			protected void onProgressUpdate(Integer... progress) {
				int p = progress[0];
				module.setProgress(p);
			//	System.out.println("当前更新的模块是:"+module+"进度为:"+p);
				Log.v("downloadTask", " module " + module.getIdentifier()
						+ " is download :" + p);
				
				if (p == 0 || p == 100 || (p - preProgress) >= 5) {
					BroadCastManager.updateProgress(context, module, p);
					preProgress = p;
				}
				if ((flag + p) == -1) {
					flag++;
//					BroadcastConstans.sendModuleBroadcast(module);
//					sendProcessBroadcast(module, p);
					BroadCastManager.updateProgress(context, module, p);
				}
			}

			@Override
			protected void doPostExecute(String result) {
				super.doPostExecute(result);
				final CubeApplication cubeApplication = CubeApplication.getInstance(context);
				
				BroadCastManager.sentModuleDownloadCount(context);
				UnZipTask unZipTask = new UnZipTask(
						ModuleOperationService.this, cubeApplication, module) {

					@Override
					protected Boolean doInBackground(String... params) {
						// 先删除旧文件
						String basePath = Environment
								.getExternalStorageDirectory().getPath()
								+ "/"
								+ URL.APP_PACKAGENAME;
						StringBuilder sb2 = new StringBuilder();
						sb2.append(basePath).append("/www/")
								.append(module.getIdentifier());
						cubeApplication.tool.deleteFolder(sb2.toString());
						return super.doInBackground(params);
					}

					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						CubeModule oldModule = CubeModuleManager.getInstance()
								.getIdentifier_old_version_map().get(
										module.getIdentifier());
						if (null == oldModule) {
							return;
						}
						if (result) {
							module.setModuleType(CubeModule.INSTALLED);
							module.setUpdatable(false);
							if(isExist(module,"icon.img")) {
								module.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.img"));
							}else if(isExist(module,"icon.png")) {
								module.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.png"));
							}
							oldModule.setModuleType(CubeModule.INSTALLED);
							if(isExist(module,"icon.img")) {
								oldModule.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.img"));
							}else if(isExist(module,"icon.png")) {
								oldModule.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.png"));
							}
							oldModule.setUpdatable(false);
							CubeModuleManager.getInstance().removeFormUpdatable(module);
							CubeModuleManager.getInstance().removeFormInstalled(oldModule);
							CubeModuleManager.getInstance().add2Installed(module);
							CubeModuleManager.getInstance().getIdentifier_new_version_map()
									.remove(module.getIdentifier());
							CubeModuleManager.getInstance().getIdentifier_old_version_map()
									.remove(oldModule.getIdentifier());
							CubeModuleManager.getInstance().getAllSet().remove(oldModule);

							Set<CubeModule> storedSet = cubeApplication
									.getModules();
							cubeApplication.getOldUpdateModules().remove(
									oldModule.getIdentifier());
							storedSet.remove(oldModule);
							storedSet.add(module);
							cubeApplication.save(cubeApplication);
//							BroadcastConstans.sendWebBroadCast(module, "upgrade");
//							BroadcastConstans.sendModuleBroadcast(module);
//							sendProcessBroadcast(module, 101);
							BroadCastManager.refreshModule(context,"upgrade", module);
							BroadCastManager.updateProgress(context, module, 101);
							Log.v("Depends", "升级成功，开始检查依赖");
							AutoDownloadHelper.getInstance().finishDownload(module);
							BroadCastManager.sentModuleDownloadCount(context);
							if(AutoDownloadHelper.getInstance().getProgressCount()==0) {
								BroadCastManager.sentModuleDownloadFinsh(context);
								AutoDownloadHelper.getInstance().clear();
							}
							CheckDependsTask task = new CheckDependsTask() {
								@Override
								protected void onPostExecute(
										ArrayList<CubeModule> result) {
									if (null == result) {
										return;
									} else if (result.size() == 0) {
										Log.v("Depands", "没有依赖模块下载");
									} else {
										for (CubeModule m : result) {
											int type = m.getModuleType();
											if (m.getPrivileges() != null) {
												if (type == CubeModule.UPGRADABLE
														&& type != CubeModule.UPGRADING
														&& type != CubeModule.DELETING) {
													CubeModuleManager.getInstance().upgrade(m);
												} else if (type == CubeModule.UNINSTALL
														&& type != CubeModule.INSTALLING
														&& type != CubeModule.DELETING) {
													CubeModuleManager.getInstance().install(m);
												}
											}
										}
									}
									super.onPostExecute(result);
								}
							};
							String checkPath = Environment
									.getExternalStorageDirectory().getPath()
									+ "/" + URL.APP_PACKAGENAME + "/www/";
							task.execute(module.getIdentifier(), checkPath);
						} else {
							module.setModuleType(CubeModule.UPGRADABLE);
							module.setUpdatable(true);
							oldModule.setModuleType(CubeModule.INSTALLED);
							oldModule.setUpdatable(true);
							if (!module.isHidden()) {
								CubeModuleManager.getInstance().removeFormMain(module);
								CubeModuleManager.getInstance().add2Main(oldModule);
								if(isExist(module,"icon.img")) {
									oldModule.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.img"));
								}else if(isExist(module,"icon.png")) {
									oldModule.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.png"));
								}
							}
							AutoDownloadHelper.getInstance().finishDownload(module);
							BroadCastManager.sentModuleDownloadCount(context);
							if(AutoDownloadHelper.getInstance().getProgressCount()==0) {
								BroadCastManager.sentModuleDownloadFinsh(context);
							}
							Toast.makeText(context, "网络状态不稳定!", Toast.LENGTH_SHORT).show();
						}
						BroadCastManager.refreshMainPage(context, module);
//						BroadcastConstans.sendModuleBroadcast(module);
						BroadCastManager.updateProgress(context, module, 101);
					};
				};
				unZipTask.execute();
//				ThreadPlatformUtils.executeByPalform(unZipTask, pool,
//						new String[] {});
			}

			@Override
			protected void doHttpFail(Exception e) {
				super.doHttpFail(e);
				CubeModule oldModule = CubeModuleManager.getInstance()
						.getIdentifier_old_version_map().get(
								module.getIdentifier());
				if (oldModule == null) {
					return;
				}
				module.setModuleType(CubeModule.UPGRADABLE);
				module.setUpdatable(true);
				oldModule.setModuleType(CubeModule.INSTALLED);
				oldModule.setUpdatable(true);
				if (!module.isHidden()) {
					CubeModuleManager.getInstance().removeFormMain(module);
					CubeModuleManager.getInstance().add2Main(oldModule);
					if(isExist(module,"icon.img")) {
						oldModule.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.img"));
					}else if(isExist(module,"icon.png")) {
						oldModule.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.png"));
					}
				}
				BroadCastManager.sentModuleDownloadCount(context);
				AutoDownloadHelper.getInstance().finishDownload(module);
				if(AutoDownloadHelper.getInstance().getProgressCount()==0) {
					BroadCastManager.sentModuleDownloadFinsh(context);
				}
				BroadCastManager.refreshMainPage(context, module);
				Toast.makeText(context, "网络状态不稳定!", Toast.LENGTH_SHORT).show();
				BroadCastManager.updateProgress(context, module, 101);
			}

		};

		task.setShowProgressDialog(false);
		task.setNeedProgressDialog(false);
		ThreadPlatformUtils.executeByPalform(task, 
				new String[] { module.getDownloadUrl(),
						module.getIdentifier() + ".zip",
						DownloadFileAsyncTask.SDCARD, URL.APP_PACKAGENAME });

	}
	@Override
	public void onDestroy() {
		pool.shutdownNow();
	}

	public ArrayList<CubeModule> checkDepends(String identifier) {
		String path = Environment.getExternalStorageDirectory().getPath() + "/"
				+ URL.APP_PACKAGENAME + "/www/";
		ArrayList<CubeModule> result = null;
		CheckDependsTask task = new CheckDependsTask() {

			@Override
			protected void onPostExecute(ArrayList<CubeModule> result) {
				super.onPostExecute(result);
				this.cancel(true);
				Log.e("check_tag", "task canceled");
			}

		};
		task.execute(identifier, path);
		try {
			result = task.get();
		} catch (InterruptedException e) {
			Log.e("checkDepends", "InterruptedException:检查依赖失败");
			try {
				result = task.get();
				return result;
			} catch (InterruptedException e1) {
				Log.e("checkDepends", "InterruptedException:检查依赖失败");
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				Log.e("checkDepends", "ExecutionException:检查依赖失败");
				e1.printStackTrace();
			}
		} catch (ExecutionException e) {
			Log.e("checkDepends", "ExecutionException:检查依赖失败");
			e.printStackTrace();
		}
		return result;
	}

	
	public Intent gotoModule(final Context context,CubeModule module) {
		
		String identifier = module.getIdentifier();

		// 模块是否本地模块
		Log.i("", "module.getLocal() ============== "+module.getLocal());
		if (module.getLocal() == null) {
			String path = Environment.getExternalStorageDirectory().getPath() + "/" + URL.APP_PACKAGENAME;
			String url = path + "/www/" + identifier;
			// 检查文件是否存在
			if (new FileCopeTool(context).isfileExist(url,"index.html")) {
				
				Intent intent = new Intent();
				
				if ("com.foss.setting".equals(identifier)){
					intent.setClass(context.getApplicationContext(),CubeSettingActivity.class);
				} else {
					intent.setClass(context.getApplicationContext(),CubeAndroid.class);
				}
				intent.putExtra("isPad", false);
				intent.putExtra("from", "main");
				intent.putExtra("path", Environment.getExternalStorageDirectory().getPath()+ "/"+ URL.APP_PACKAGENAME);
				intent.putExtra("identify", identifier);
				return intent;

			} else {
				Toast.makeText(context, "文件缺失，请重新下载",Toast.LENGTH_SHORT).show();
				return null;
			}
		} else {
			// 模块为本地模块
			PropertiesUtil propertiesUtil = PropertiesUtil.readProperties(context.getApplicationContext(), CubeConstants.CUBE_CONFIG);
			String className = propertiesUtil.getString("phone_" + module.getIdentifier(), "");
			Log.i("", "module.getLocal() ============== className" + className);
			Intent i = new Intent();
			i.setClassName(context.getApplicationContext(), className);
			return i;
		}
	}
	
	public String getModuleUrl(final Context context,CubeModule module) {
		String identifier = module.getIdentifier();
		// 模块是否本地模块
		if (module.getLocal() == null) {
//			String path = Environment.getExternalStorageDirectory().getPath() + "/" + URL.APP_PACKAGENAME;
			String path = "/data/data" + "/" + URL.APP_PACKAGENAME;
			String url = path + "/www/" + identifier;
			// 检查文件是否存在
			if (new FileCopeTool(context).isfileExist(url,"index.html")) {
				String moduleuUrl =  "file:/" + url + "/index.html";
				return moduleuUrl;
			} else {
				Toast.makeText(context, "文件缺失，请重新下载",Toast.LENGTH_SHORT).show();
				return null;
			}
		} else {
				PropertiesUtil propertiesUtil = PropertiesUtil.readProperties(context.getApplicationContext(),CubeConstants.CUBE_CONFIG);
				String className = propertiesUtil.getString(module.getIdentifier(), "");
				return className;
		}
	}
	
	public List<CubeModule> checkUpgrade() {
		List<CubeModule> isUpgrade = new ArrayList<CubeModule>();
		if (CubeModuleManager.getInstance().getUpdatable_map()
				.size() != 0) {
			for (List<CubeModule> list : CubeModuleManager
					.getInstance().getUpdatable_map().values()) {
				isUpgrade.addAll(list);
			}
		}
		return isUpgrade;
		
	}
	
	public List<CubeModule> checkAutoDownload(String userName){
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		FileCopeTool tool = new FileCopeTool(getApplicationContext());
		ArrayList<String> lists =  new ArrayList<String>();
		String results = tool.readerFile(Environment
				.getExternalStorageDirectory().getPath()
				+ "/"
				+ URL.APP_PACKAGENAME, userName+"_"+"autoDownLoadFile.json");
		if(results != null){
			lists= gson.fromJson(results, ArrayList.class);
			for (int i = 0; i < lists.size(); i++) {
				System.out.println("已下载过的==" + lists.get(i));
			}
		}
		/**
		 * lanjianlong
		 */
//		String results = tool.readerFile(Environment
//				.getExternalStorageDirectory().getPath()
//				+ "/"
//				+ URL.APP_PACKAGENAME, userName+"_"+"downLoadFailFile.json");
//		if(results != null){
//			lists= gson.fromJson(results, ArrayList.class);
//			for (int i = 0; i < lists.size(); i++) {
//				Log.i("lanjianlong", "checkAutoDownload(String userName), 未下载过的==" + lists.get(i));
//			}
//		}
		
		
		
		List<CubeModule> unInstalledModules = new ArrayList<CubeModule>();
		List<CubeModule> isAutoShowModules = new ArrayList<CubeModule>();
		List<CubeModule> AutoShowModules = new ArrayList<CubeModule>();
		
		// 获得自动下载列表
		if (CubeModuleManager.getInstance().getUninstalled_map().size() != 0) {
			for (List<CubeModule> list : CubeModuleManager.getInstance().getUninstalled_map().values()) {
				
				unInstalledModules.addAll(list);
			}
		}
		for(CubeModule module:unInstalledModules){
			if(module.isAutoDownload()){
				isAutoShowModules.add(module);
			}
		}
		for(int j=0;j<isAutoShowModules.size();j++) {
			Boolean isExit = false;
			for (int i =0;i<lists.size();i++) {
				if(isAutoShowModules.get(j).getIdentifier().equals(lists.get(i))) {
					isExit = true;
					break;
				}
			}
			if(!isExit) {
				AutoShowModules.add(isAutoShowModules.get(j));
			}
		}
		
		
		return AutoShowModules;
	}
	public void cancelAutoDownload(List<CubeModule> modules,String userName) {
		saveAutoDownloadFile(modules, userName);
	}
	
	public void autoDownload(List<CubeModule> modules,String userName){
       
		int total = modules.size();
//		AutoDownloadHelper.getInstance().setAutoDownloadCount(total);
		for(CubeModule module : modules){
			if(!AutoDownloadHelper.getInstance().addDownloadTask(module)){
				total = total-1;
				/**
				 * lanjianlong download fail
				 */
				Log.i("lanjianlong","download module fail: module = "+module);
//				modules.remove(module);
//				AutoDownloadHelper.getInstance().setAutoDownloadCount(total);
			}
			/**
			 * lanjianlong 下载成功
			 */
			else{
				Log.i("lanjianlong","download module success, installing...  module = "+module);
				install(module);
			}
//			install(module); 
		}
//		saveAutoDownloadFile(modules,userName);
		Intent intent = new Intent();
		intent.setAction(BroadcastConstans.MODULE_AUTODOWNLOAD_START);
		sendBroadcast(intent);
	}
	
	public List<CubeModule> checkAutoDownload(){
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		FileCopeTool tool = new FileCopeTool(getApplicationContext());
		ArrayList<String> lists =  new ArrayList<String>();
		String results = tool.readerFile(Environment
				.getExternalStorageDirectory().getPath()
				+ "/"
				+ URL.APP_PACKAGENAME, "autoDownLoadFile.json");
		if(results != null){
			lists= gson.fromJson(results, ArrayList.class);
			for (int i = 0; i < lists.size(); i++) {
				System.out.println("已下载过的==" + lists.get(i));
			}
		}
		/**
		 * lanjianlong
		 */
//		String results = tool.readerFile(Environment
//				.getExternalStorageDirectory().getPath()
//				+ "/"
//				+ URL.APP_PACKAGENAME, "downLoadFailFile.json");
//		if(results != null){
//			lists= gson.fromJson(results, ArrayList.class);
//			
//			CubeModuleManager.getInstance().autoDownload(modules , userName);
//			for (int i = 0; i < lists.size(); i++) {
//				Log.i("lanjianlong","checkAutoDownload(), 未下载过的== " + lists.get(i));	
//			}
//		}
		
		
		List<CubeModule> unInstalledModules = new ArrayList<CubeModule>();
		List<CubeModule> isAutoShowModules = new ArrayList<CubeModule>();
		List<CubeModule> AutoShowModules = new ArrayList<CubeModule>();
		
		// 获得自动下载列表
		if (CubeModuleManager.getInstance().getUninstalled_map().size() != 0) {
			for (List<CubeModule> list : CubeModuleManager.getInstance().getUninstalled_map().values()) {
				
				unInstalledModules.addAll(list);
			}
		}
		for(CubeModule module:unInstalledModules){
			if(module.isAutoDownload()){
				isAutoShowModules.add(module);
			}
		}
		for(int j=0;j<isAutoShowModules.size();j++) {
			Boolean isExit = false;
//			for (int i =0;i<lists.size();i++) {
//				if(isAutoShowModules.get(j).getIdentifier().equals(lists.get(i))) {
//					isExit = true;
//					break;
//				}
//			}
			if(!isExit) {
//				AutoShowModules.add(isAutoShowModules.get(j));
			}
		}
		
		
		return AutoShowModules;
	}
	public void saveAutoDownloadFile (List<CubeModule> modules,String userName) {
		ArrayList<String> list = new ArrayList<String>();
		for(CubeModule module : modules){
			list.add(module.getIdentifier());
		}
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String json = gson.toJson(list);
		System.out.println(json);
		FileCopeTool tool = new FileCopeTool(getApplicationContext());
		tool.writeToJsonFile(userName+"_"+"autoDownLoadFile", Environment
				.getExternalStorageDirectory().getPath()
				+ "/"
				+ URL.APP_PACKAGENAME + "/", json);
	}
	
	
	public void autoUpgrade(List<CubeModule> modules) {
//		AutoDownloadHelper.getInstance().setAutoDownloadCount(modules.size());
		for(CubeModule module : modules){
			AutoDownloadHelper.getInstance().addDownloadTask(module);
			upgrade(module);
		}
		Intent intent = new Intent();
		intent.setAction(BroadcastConstans.MODULE_AUTODOWNLOAD_START);
		sendBroadcast(intent);
	}
	
	public void downloadAttachMent(final String attach) {

		new Thread() {
			public void run() {
				downloadAttachMentFile(attach);
			};
		}.start();
	}

	private void downloadAttachMentFile(String attach) {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet post = new HttpGet(URL.getDownloadUrl(
					ModuleOperationService.this, attach));
			HttpResponse response;
			

			response = client.execute(post);
			HttpEntity entity = response.getEntity();
			String fileName = response.getFirstHeader("Content-Disposition")
					.getValue();
			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			if (is != null) {

				fileName = attach
						+ fileName.substring(fileName.indexOf("=") + 1);
				FileCopeTool copeTool = new FileCopeTool(this);
				copeTool.createFile(URL.APP_PACKAGENAME+ "/www/com.foss.announcement");
				String dirpath = Environment.getExternalStorageDirectory()+ "/"
						+URL.APP_PACKAGENAME 
						+ "/www/com.foss.announcement";
				fileOutputStream = new FileOutputStream(new File(dirpath,
						fileName));
				// 开始下载
				Log.i("Environment", dirpath + "/" + fileName);
				byte[] buf = new byte[1024 * 256];
				int ch = -1;

				while ((ch = is.read(buf)) != -1) {
					fileOutputStream.write(buf, 0, ch);
				}
			}
			Log.d("cube", "下载完成");

			if (is != null) {
				is.close();
			}
			
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e("cube", "下载失败");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("cube", "下载失败");
		}
	}

	public void openAttachment(String fileType, String path) {
		Log.i("chencao", "openFile type=" + fileType + " path=" + path);

		Intent intent = null;
		if (FileIntent.FILE_PDF.equals(fileType)) {
			intent = FileIntent.getPdfFileIntent(path);
		} else if (FileIntent.FILE_CHM.equals(fileType)) {
			intent = FileIntent.getChmFileIntent(path);
		} else if (FileIntent.FILE_TEXT_HTML.equals(fileType)) {
			intent = FileIntent.getHtmlFileIntent(path);
		} else if (FileIntent.FILE_WORD.equals(fileType)) {
			intent = FileIntent.getWordFileIntent(path);
		} else if (FileIntent.FILE_EXCEL.equals(fileType)) {
			intent = FileIntent.getExcelFileIntent(path);
		} else if (FileIntent.FILE_PPT.equals(fileType)) {
			intent = FileIntent.getPptFileIntent(path);
		} else if ("txt".equals(fileType)) {
			intent = FileIntent.getTextFileIntent(path, false);

		} else {
			// do nothing...
		}

		if (intent != null) {
			try {
				startActivity(intent);
			} catch (Exception ex) {
				Log.w("chencao", "打开文件出错，没有合适的程序。");
				Toast.makeText(this, "打开文件出错，没有合适的程序。", Toast.LENGTH_LONG)
						.show();
			}
		}

	}
	
	public void stopTask(){
		System.out.println("stopTask");
	}
}
