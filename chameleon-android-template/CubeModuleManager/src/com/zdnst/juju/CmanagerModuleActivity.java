package com.zdnst.juju;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.cordova.CordovaWebViewClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.zdnst.bsl.util.BroadcastConstans;
import com.zdnst.bsl.util.Preferences;
import com.zdnst.chameleon.AppStatus;
import com.zdnst.chameleon.base.BaseFragmentActivity;
import com.zdnst.data.table.MessageDataModel;
import com.zdnst.juju.manager.ApplicationSyncListener;
import com.zdnst.juju.manager.AutoDownloadHelper;
import com.zdnst.juju.manager.CubeModuleManager;
import com.zdnst.juju.model.CubeApplication;
import com.zdnst.juju.model.CubeModule;
import com.zdnst.juju.model.Privilege;
import com.zdnst.juju.model.UserPrivilege;
import com.zdnst.message.update.AutoCheckUpdateListener;
import com.zdnst.message.update.CheckUpdateTask;
import com.zdnst.push.tool.PadUtils;
import com.zdnst.push.url.MessageConstants;
import com.zdnst.zdnstsdk.config.URL;
//import android.view.LayoutInflater;
//import android.widget.TextView;
//import com.zdnst.chameleon.manager.R;

public class CmanagerModuleActivity extends BaseFragmentActivity {
	String url = "";
	Boolean isLocalContent = true;
	IntentFilter intentFilter = new IntentFilter();
	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (BroadcastConstans.ReceiveMessage.equals(intent.getAction())) {
				new RefreshThread().start();

			} else if (BroadcastConstans.ReceiveMessages.equals(intent
					.getAction())) {
				if (PadUtils.isPad(context)) {
					String json = intent.getExtras().getString("message");
					receiveMessage(json);
				}
			}

			else if (BroadcastConstans.RefreshMainPage.equals(intent
					.getAction())) {
				String identifier = intent.getStringExtra("identifier");
				String type = intent.getStringExtra("type");
				CubeModule module = CubeModuleManager.getInstance()
						.getIdentifier_new_version_map().get(identifier);
				if (module == null) {
					module = CubeModuleManager.getInstance()
							.getCubeModuleByIdentifier(identifier);
					if (module != null) {
						if (module.isAutoDownload()) {
							// endAnimAutoDownload();
						}
					}
				}
				refreshMainPage(identifier, type, module);
			} else if (BroadcastConstans.RefreshModule.equals(intent
					.getAction())) {

				String identifier = intent.getStringExtra("identifier");
				String type = intent.getStringExtra("type");
				CubeModule module = CubeModuleManager.getInstance()
						.getIdentifier_new_version_map().get(identifier);
				if (module == null) {
					module = CubeModuleManager.getInstance()
							.getCubeModuleByIdentifier(identifier);
				}
				refreshModule(identifier, type, module);

			} else if (BroadcastConstans.UpdateProgress.equals(intent
					.getAction())) {
				String identifier = intent.getStringExtra("identifier");
				int progress = intent.getIntExtra("progress", 0);
				updateProgress(identifier, progress);
			} else if (BroadcastConstans.MODULE_AUTODOWNLOAD_FINISH
					.equals(intent.getAction())) {
				// 显示自动下载
				autodownloadlayout.setVisibility(View.GONE);
				// AutoDownloadHelper.getInstance().setAutoDownloadCount(0);
				progress.setText("已下载：" + "0" + "，总计需下载：" + "0");
			} else if (BroadcastConstans.MODULE_AUTODOWNLOAD_START
					.equals(intent.getAction())) {
				// 关闭自动下载
				autodownloadlayout.setVisibility(View.VISIBLE);
			} else if (BroadcastConstans.MODULE_AUTODOWNLOAD_PROGERSS
					.equals(intent.getAction())) {
				// int count =
				// AutoDownloadHelper.getInstance().getAutoDownloadCount();
				int count = AutoDownloadHelper.getInstance().getTotalCount();
				int downLoadCount = AutoDownloadHelper.getInstance()
						.getProgressCount();
				progress.setText("已下载：" + (count - downLoadCount) + "，总计需下载："
						+ count);
			} else if (BroadcastConstans.SecurityChange.equals(intent
					.getAction())) {
				stopTimer(timer);
				syncPrivilege();
			} else if (BroadcastConstans.SecurityChangeForFile.equals(intent
					.getAction())) {
				syncPrivilegeFile();
			}

			else if (BroadcastConstans.SecurityRefreshMainPage.equals(intent
					.getAction())) {
				Log.i("", "SecurityRefreshMainPage ================== ");
				CubeApplication apCubeApplication = CubeApplication
						.getInstance(context);
				Set<CubeModule> modules = apCubeApplication.getModules();
				refreshManagerPage();
				for (CubeModule cubeModule : modules) {
					refreshMainPage(cubeModule.getIdentifier(), "main",
							cubeModule);
					break;
				}

				if (PadUtils.isPad(context)) {
					refreshPrivileges();
					System.out.println("调用了 pad  SecurityRefreshModuelDetail");
				}
				// Intent intent2 = new Intent();
				// String title = Preferences.getToken(Application.sharePref);
				// Notifier.notifyInfo(context, R.drawable.about_logo, 123,
				// "权限发生改变", title, intent2);
				// Toast.makeText(application.getApplicationContext(), "权限改变",
				// Toast.LENGTH_SHORT).show();
			} else if (BroadcastConstans.SecurityRoleChange.equals(intent
					.getAction())) {
				AppStatus.USERLOGIN = false;
				// 默认模块权限
				String privileges = Preferences.getPrivileges();
				saveGuestApp(privileges);
				Preferences.saveUser("guest");
				// 重新签到
				CheckInUtil.pushSecurity(context, "guest");
				CmanagerModuleActivity.this.sendBroadcast(new Intent(
						BroadcastConstans.SecurityRefreshModuelDetail));

				CubeApplication apCubeApplication = CubeApplication
						.getInstance(CmanagerModuleActivity.this);
				Set<CubeModule> modules = apCubeApplication.getModules();
				for (CubeModule cubeModule : modules) {
					refreshMainPage(cubeModule.getIdentifier(), "main",
							cubeModule);
					break;
				}
				if (PadUtils.isPad(context)) {
					refreshPrivileges();
					System.out.println("调用了 pad  SecurityRefreshModuelDetail");
				}
				loginOrLogout(false);

				// 弹出提示框提示角色改变
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("提示");
				builder.setMessage("你的帐户角色被改变，请重新登录");
				builder.setPositiveButton("确定", null);
				Dialog dialog = builder.create();
				dialog.show();
			}
		}
	};

	Timer timer = new Timer();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// url = "file:///android_asset/www/phone/index.html";
		// url = URL.PHONE_MAIN_URL;
		if (isPad()) {
			url = URL.PHONE_LOGIN_URL;
		} else {
			url = URL.PHONE_LOGIN_URL;
		}
		loadMainContent(url);
		intentFilter.addAction(BroadcastConstans.MODULE_AUTODOWNLOAD_FINISH);
		intentFilter.addAction(BroadcastConstans.MODULE_AUTODOWNLOAD_START);
		intentFilter.addAction(BroadcastConstans.MODULE_AUTODOWNLOAD_PROGERSS);
		intentFilter.addAction(BroadcastConstans.ReceiveMessage);
		intentFilter.addAction(BroadcastConstans.UpdateProgress);
		intentFilter.addAction(BroadcastConstans.RefreshModule);
		intentFilter.addAction(BroadcastConstans.RefreshMainPage);
		intentFilter.addAction(BroadcastConstans.SecurityChange);
		intentFilter.addAction(BroadcastConstans.ReceiveMessages);
		intentFilter.addAction(BroadcastConstans.SecurityRefreshMainPage);
		intentFilter.addAction(BroadcastConstans.SecurityRoleChange);
		intentFilter.addAction(BroadcastConstans.SecurityChangeForFile);
		registerReceiver(receiver, intentFilter);
		startTimer(timer);

		Intent i = getIntent();
		if (i != null) {
			showModule(i);
		}

		// 设置详情页面的WebViewClient
		setDetailWebViewClient(new CordovaWebViewClient(this) {
			@Override
			public boolean shouldOverrideUrlLoading(WebView webview, String url) {
				if (PadUtils.isPad(getApplicationContext())) {
					if (url.contains("cube-action=push")) {
						url = subUrl(url);
						Intent intent = new Intent(CmanagerModuleActivity.this,
								CmanagerModuleActivity.class);
						intent.putExtra("from", "web");
						intent.putExtra("url", url);
						startActivity(intent);
						return true;
					} else if (url.contains("cube-action=pop")) {
						showDetailContent(false);
						return true;

					} else if (url.endsWith("cube://exit")) {
						showDetailContent(false);
						return true;
					}
				}
				return super.shouldOverrideUrlLoading(webview, url);
			}

		});

	}

	public String subUrl(String url) {
		int start = url.indexOf("cube-action=push");
		int end = start + 16;
		String newUrl = "";
		if (url.indexOf("&") == -1 && start != -1) {
			newUrl = url.substring(0, (url.indexOf("cube-action=push") - 1))
					+ url.subSequence(end, url.length());
		} else if (url.indexOf("&") > -1 && start < url.indexOf("&")) {
			newUrl = url.substring(0, (url.indexOf("cube-action=push")))
					+ url.subSequence(end + 1, url.length());
		} else if (url.indexOf("&") > -1 && start > url.indexOf("&")) {
			newUrl = url.substring(0, url.indexOf("&"))
					+ url.subSequence(end, url.length());
		}
		return newUrl;
	}

	public void showModule(Intent intent) {
		Intent i = intent;
		if (i != null) {
			String moduleIdentifier = i.getStringExtra("moduleIdentifier");
			String className = i.getStringExtra("className");
			// String params = i.getStringExtra("parameters");
			String params[] = i.getStringArrayExtra("parameters");
			CubeModule module = CubeModuleManager.getInstance()
					.getModuleByIdentify(moduleIdentifier);

			if (PadUtils.isPad(this) && module != null) {
				final String moduleUrl = CubeModuleManager.getInstance()
						.getModuleUrl(this, module);
				if (module.getLocal() == null) {
					isLocalContent = false;
				} else {
					isLocalContent = true;
				}

				// 不存在页面 NoticeFragment 即时通讯没有
				if (moduleUrl != null && !moduleUrl.equals("")) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							loadDetailContent(moduleUrl, isLocalContent, null);
						}
					});
				}

			} else {
				if (module != null) {
					Intent moduleIntent = null;
					if (module.getModuleType() != CubeModule.INSTALLED) {
						String identifier = MessageConstants.MESSAGE_IDENTIFIER;
						module = CubeModuleManager.getInstance()
								.getModuleByIdentify(identifier);
					}
					moduleIntent = CubeModuleManager.getInstance().showModule(
							this, module);
					if (moduleIntent != null) {
						if (params != null && params.length != 0) {
							moduleIntent.putExtra("parameters", params);
						}
						if (className != null && !className.equals("")) {

							moduleIntent.setClassName(getApplicationContext(),
									className);
						}
						startActivity(moduleIntent);
					}
				}
			}
		}
		checkUpdate(url);

	}

	private AutoCheckUpdateListener acuListener;
	private CheckUpdateTask updateTask;

	public void checkUpdate(String url) {
		// System.out.println("checkUpdate");
		// && url.contains("login") 修改触发更新监听
		if (url != null) {
			// System.out.println("checkUpdate====!=null");
			acuListener = new AutoCheckUpdateListener(this);
			updateTask = new CheckUpdateTask(CubeApplication.getInstance(this),
					acuListener);
			updateTask.execute();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		showModule(intent);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		if (updateTask.getStatus() != AsyncTask.Status.FINISHED) {
			updateTask.cancel(true);
		}
		stopTimer(timer);
	}

	public void refreshMainPage(String identifier, String type,
			CubeModule module) {
		String moduleMessage = new Gson().toJson(module);
		setMainJavaScript("refreshMainPage('" + identifier + "','" + type
				+ "','" + moduleMessage + "')");
		// setMainJavaScript("refreshManagerPage()");
		// appView.sendJavascript("refreshMainPage('" + identifier + "','" +
		// type
		// + "','" + moduleMessage + "')");
	}

	public void refreshManagerPage() {
		setMainJavaScript("refreshManagerPage()");
		// appView.sendJavascript("refreshMainPage('" + identifier + "','" +
		// type
		// + "','" + moduleMessage + "')");
	}

	public void refreshModule(String identifier, String type, CubeModule module) {
		String moduleMessage = new Gson().toJson(module);
		setMainJavaScript("refreshModule('" + identifier + "','" + type + "','"
				+ moduleMessage + "')");
		// appView.sendJavascript("refreshModule('" + identifier + "','" + type
		// + "','" + moduleMessage + "')");
	}

	public void updateProgress(String identifier, int progress) {
		setMainJavaScript("updateProgress('" + identifier + "','" + progress
				+ "')");
		// appView.sendJavascript("updateProgress('" + identifier + "','"
		// + progress + "')");
	}

	public void receiveMessage(final String identifier, final int count,
			boolean display) {
		setMainJavaScript("receiveMessage('" + identifier + "'," + count + ","
				+ display + ")");
		// appView.sendJavascript("receiveMessage('" + identifier + "'," + count
		// + "," + display + ")");
	}

	public void receiveMessage(String json) {
		setDetailJavaScript("receiveMessages('" + json + "')");
		setMainJavaScript("receiveMessages('" + json + "')");
		// appView.sendJavascript("receiveMessages('" + json + "')");
		// appView.sendJavascript("receiveMessage('" + identifier + "'," + count
		// + "," + display + ")");
	}

	public void refreshPrivileges() {
		setMainJavaScript("refreshPrivileges()");
	}

	public void loginOrLogout(boolean s) {
		setMainJavaScript("loginOrLogout(" + s + ")");
		// appView.sendJavascript("receiveMessage('" + identifier + "'," + count
		// + "," + display + ")");
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Dialog dialog = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("确定退出 ？")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(!"".equals(Preferences.getToken()))
						{
							Preferences.saveToken("", "");
						}
						CmanagerModule.getCmanagetModule();
						AppStatus.USERLOGIN = false;
						String privileges = Preferences.getPrivileges();
						saveGuestApp(privileges);

						Preferences.saveUser("");
						dialog.dismiss();
						finish();
						
						CmanagerModule.getCmanagetModule().getcApplication()
								.onExit();
					}
				}).create();

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			/**
			 *  lanjianlong 修改返回键根据不同页面处理的情况
			 */
			if (event.getAction() == KeyEvent.ACTION_UP) {
				String htmlUrl = getMainWebView().getUrl();
				String pkName = this.getPackageName();
				Log.i("html", "htmlUrl = " + htmlUrl);
				if (htmlUrl.indexOf(pkName) != -1) {
					String addrstr = htmlUrl.substring(htmlUrl.indexOf(pkName));
					String tools = pkName + "/www/com.midea.tools/index.html";
					String login = pkName + "/www/com.midea.login/index.html";
					Log.i("html", "addrstr = " + addrstr);
					if (addrstr.equals(tools) || login.equals(addrstr)) {
						dialog.show();
						return true;
					} else {
						if (getMainWebView().backHistory()) {
							if (dialog.isShowing()) {
								dialog.dismiss();
							}
							return true;
						}
					}
				}
				// if (popFragment()) {
				// return true;
				// }
				if (dialog.isShowing()) {
					dialog.dismiss();
					return true;
				} else {
					dialog.show();
					return true;
				}
			}

		}
		return super.dispatchKeyEvent(event);
	}

	class RefreshThread extends Thread {
		@Override
		public void run() {
			try {
				CubeModuleManager cubeModuleManager = CubeModuleManager
						.getInstance();
				MessageDataModel messageDataModel = new MessageDataModel(
						CmanagerModuleActivity.this);
				for (String identifier : cubeModuleManager.getIdentifier_map()
						.keySet()) {
					if (!identifier.equals(MessageConstants.MESSAGE_IDENTIFIER)) {
						int count = messageDataModel
								.getIdentifierUnReadCount(identifier);
						CubeModule module = CubeModuleManager.getInstance()
								.getCubeModuleByIdentifier(identifier);
						module.setMsgCount(count);
						receiveMessage(identifier, count, true);
					}
				}
				int messageCount = messageDataModel.getUnReadCount();
				CubeModule messageModule = CubeModuleManager.getInstance()
						.getCubeModuleByIdentifier(
								MessageConstants.MESSAGE_IDENTIFIER);
				messageModule.setMsgCount(messageCount);
				receiveMessage(MessageConstants.MESSAGE_IDENTIFIER,
						messageCount, true);
				CubeApplication cubeAPplication = CubeApplication
						.getInstance(CmanagerModuleActivity.this);
				CubeApplication.getInstance(CmanagerModuleActivity.this).save(
						cubeAPplication);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void onResume() {
		new RefreshThread().start();
		CubeApplication apCubeApplication = CubeApplication.getInstance(this);
		Set<CubeModule> modules = apCubeApplication.getModules();
		for (CubeModule cubeModule : modules) {
			refreshMainPage(cubeModule.getIdentifier(), "main", cubeModule);
			break;
		}

		if (AppStatus.FROMLOGIN) {
			AppStatus.FROMLOGIN = false;
			String userName = Preferences.getUserName();
			autoDownLoad(userName);
		}

		super.onResume();

	}

	public void refreshMainPageEvent() {
		CubeApplication apCubeApplication = CubeApplication.getInstance(this);
		Set<CubeModule> modules = apCubeApplication.getModules();
		for (CubeModule cubeModule : modules) {
			refreshMainPage(cubeModule.getIdentifier(), "main", cubeModule);
			break;
		}
	}

	// 弹出更新提示
	public void showUpdateAlert(final List<CubeModule> updateModules) {
		StringBuffer sb = new StringBuffer();
		for (CubeModule module : updateModules) {
			sb.append("[" + module.getName() + " " + module.getVersion() + "]"
					+ "\n");
		}

		AlertDialog needUpdateDialog = new AlertDialog.Builder(this)
				.setTitle("检测到有以下模块需要更新：")
				.setMessage(sb.toString())

				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						CubeModuleManager.getInstance().autoUpgrade(
								updateModules);
					}
				}).show();
//				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//
//					}
//				}).show();
		needUpdateDialog.setCancelable(false);
	}

	// 弹出下载提示窗口
	public void showDownloadAlert(final List<CubeModule> modules) {
		StringBuffer sb = new StringBuffer();
		// 提示需要下载的模块
		for (CubeModule module : modules) {
			sb.append("[" + module.getName() + " " + " " + module.getVersion()
					+ "]" + "\n");
		}
		AlertDialog needDownloadDialog = new AlertDialog.Builder(this)

		.setTitle("检测到有以下模块需要下载：").setMessage(sb.toString())
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						CubeModuleManager.getInstance().autoDownload(modules,
								Preferences.getUserName());
						CubeModuleManager.getInstance().saveAutoDownloadFile(
								modules, Preferences.getUserName());

					}
				}).show();
		// .setNegativeButton("取消", new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface arg0, int arg1) {
		// CubeModuleManager.getInstance().cancelAutoDownload(
		// modules, Preferences.getUserName());
		// }
		// }).show();
		needDownloadDialog.setCancelable(false);
	}

	public void autoDownLoad(String userName) {

		List<CubeModule> autoDownloadModules = CubeModuleManager.getInstance()
				.checkAutoDownload(userName);
		if (autoDownloadModules.size() != 0) {
			showDownloadAlert(autoDownloadModules);
		}

	}

	public void autoUpgrade() {
		List<CubeModule> upgradeModuel = CubeModuleManager.getInstance()
				.checkUpgrade();
		if (upgradeModuel.size() != 0) {
			showUpdateAlert(upgradeModuel);
		}
	}

	private void saveGuestApp(String result) {
		try {
			JSONObject jb = new JSONObject(result);
			JSONArray jay = jb.getJSONArray("priviliges");
			int len = jay.length();
			ArrayList<String> getList = UserPrivilege.getInstance()
					.getGetList();
			ArrayList<String> deleteList = UserPrivilege.getInstance()
					.getDeleteList();
			ArrayList<String> privilegeList = UserPrivilege.getInstance()
					.getPrivilegeList();
			deleteList.clear();
			getList.clear();
			privilegeList.clear();
			for (int i = 0; i < len; i++) {
				JSONArray jay2 = jay.getJSONArray(i);
				if (!privilegeList.contains(jay2.getString(1))) {
					privilegeList.add(jay2.getString(1));
				}

				if (UserPrivilege.GET.equals(jay2.getString(0))) {
					getList.add(jay2.getString(1));
				}

				if (UserPrivilege.DELETE.equals(jay2.getString(0))) {
					deleteList.add(jay2.getString(1));
				}
			}
			CubeApplication cubeApplication = CubeApplication
					.getInstance(CmanagerModuleActivity.this);
			Set<CubeModule> modules = cubeApplication.getModules();
			for (CubeModule cubeModule : modules) {
				if (privilegeList.contains(cubeModule.getIdentifier())) {
					List<Privilege> prList = new ArrayList<Privilege>();
					cubeModule.setPrivileges(prList);
				} else {
					cubeModule.setPrivileges(null);
				}
			}
			CubeModuleManager.getInstance().init(cubeApplication);
			cubeApplication.save(cubeApplication);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startTimer(Timer timer) {
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				CmanagerModuleActivity.this.sendBroadcast(new Intent(
						BroadcastConstans.SecurityChangeForFile));
			}
		}, 10 * 1000 * 60, 10 * 1000 * 60);
	}

	private void stopTimer(Timer timer) {
		System.out.println("stop timer !!");
		timer.cancel();
	}

	public Dialog dialog;

	private void syncPrivilege() {

		final CubeApplication cubeApp = CubeApplication
				.getInstance(CmanagerModuleActivity.this);

		ApplicationSyncListener callback1 = new ApplicationSyncListener() {

			@Override
			public void syncStart() {
				// callbackContext.success("sync start");
				// dialog = new
				// Dialog(CmanagerModuleActivity.this,R.style.common_dialog);
				// LayoutInflater mInflater =
				// (LayoutInflater)CmanagerModuleActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				// View v = mInflater.inflate(R.layout.dialog_layout, null);
				// TextView tv = (TextView) v.findViewById(R.id.dialog_text);
				// tv.setText("正在获取帐户权限");
				// dialog.setContentView(v);
				// dialog.show();
			}

			@Override
			public void syncFinish() {

				ApplicationSyncListener callback2 = new ApplicationSyncListener() {

					@Override
					public void syncStart() {
					}

					@Override
					public void syncFinish() {
						// if (null != dialog && dialog.isShowing()) {
						// try {
						// dialog.cancel();
						// } catch (Exception e) {
						// // TODO: handle exception
						// }
						//
						// }
						refreshManagerPage();
						refreshMainPageEvent();
						CmanagerModuleActivity.this.sendBroadcast(new Intent(
								BroadcastConstans.SecurityRefreshModuelDetail));
						timer = new Timer();
						startTimer(timer);
					}

					@Override
					public void syncFail() {
					}

					@Override
					public void syncFinish(String result) {

					}

				};
				String username = Preferences.getUserName();
				if ("".equals(username)) {
					return;
				} else {
					if (AppStatus.USERLOGIN) {
						cubeApp.syncPrivilege(true,
								CmanagerModuleActivity.this, username,
								callback2, "正在获取账户权限");
					} else {
						cubeApp.syncPrivilege(true,
								CmanagerModuleActivity.this, "guest",
								callback2, "正在获取账户权限");
					}
				}
			}

			@Override
			public void syncFail() {
			}

			@Override
			public void syncFinish(String result) {
			}

		};
		String username = Preferences.getUserName();
		if ("".equals(username)) {
			return;
		} else {
			cubeApp.sync(callback1, cubeApp, CmanagerModuleActivity.this, true,
					"正在获取账户权限");
		}

	}

	private void syncPrivilegeFile() {

		final CubeApplication cubeApp = CubeApplication
				.getInstance(CmanagerModuleActivity.this);

		ApplicationSyncListener callback1 = new ApplicationSyncListener() {

			@Override
			public void syncStart() {
			}

			@Override
			public void syncFinish() {

				ApplicationSyncListener callback2 = new ApplicationSyncListener() {

					@Override
					public void syncStart() {
					}

					@Override
					public void syncFinish() {
						refreshManagerPage();
						refreshMainPageEvent();

					}

					@Override
					public void syncFail() {
					}

					@Override
					public void syncFinish(String result) {

					}

				};

				String username = Preferences.getUserName();
				cubeApp.syncPrivilege(false, CmanagerModuleActivity.this,
						username, callback2, "");
			}

			@Override
			public void syncFail() {
			}

			@Override
			public void syncFinish(String result) {
			}

		};

		cubeApp.sync(callback1, cubeApp, CmanagerModuleActivity.this, false, "");
	}

}
