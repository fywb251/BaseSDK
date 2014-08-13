package com.zdnst.juju;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.foreveross.chameleon.manager.R;
import com.zdnst.chameleon.AppStatus;
import com.zdnst.data.DataProvider;
import com.zdnst.juju.manager.CubeModuleManager;
import com.zdnst.juju.manager.ModuleOperationService;
import com.zdnst.juju.manager.ModuleOperationService.ModuleOperationServiceBinder;
import com.zdnst.juju.model.CubeApplication;
import com.zdnst.juju.model.UserPrivilege;
import com.zdnst.push.client.NotificationPacketListener;
import com.zdnst.push.client.NotificationService;
import com.zdnst.push.client.NotificationService.NotificationServiceBinder;
import com.zdnst.push.client.XmppManager;
import com.zdnst.push.tmp.OriginalParser;
import com.zdnst.zdnstsdk.CModule;
import com.zdnst.zdnstsdk.config.URL;
//import com.zdnst.chamelonsdk.CModule;

public class CmanagerModule	extends CModule{

	private static CmanagerModule cmanagerModule; 
	
	public static SharedPreferences sharePref;
	
	private Context context;
	
	CubeApplication cubeApplication;
	ModuleOperationService moduleOperationService = null;

	private NotificationService notificationService = null;
	private NotificationCallback notificationCallback;

	private interface NotificationCallback {
		public void doStuff();
	}

	@Override
	public void onCreate(CModule cModule) {
		super.onCreate(cModule);
		AppStatus.USERLOGIN = false;
		AppStatus.FROMLOGIN = false;
		cmanagerModule = (CmanagerModule)cModule;
		init();
		readConfig(R.raw.pushmodule);
		NotificationPacketListener.register(new OriginalParser(context));
		UserPrivilege.getInstance();
	}
	
	public static CmanagerModule getCmanagetModule(){
		return cmanagerModule;
	}
	
	private void init() {
		context =getcApplication().getmContext(); 
		sharePref = PreferenceManager.getDefaultSharedPreferences(context);
		URL.initUrl(context);
		cubeApplication = CubeApplication.getInstance(this.getcApplication().getmContext());
		cubeApplication.loadApplication();
		
		CubeModuleManager.getInstance().init(cubeApplication);
		DataProvider.getInstance(context, "cube");
		context.bindService(ModuleOperationService.getIntent(context),
				moduleServiceConnection, Context.BIND_AUTO_CREATE);
		context.bindService(NotificationService.getIntent(context),
				notificationServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	
	private ServiceConnection moduleServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			moduleOperationService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			moduleOperationService = ((ModuleOperationServiceBinder) service)
					.getService();
			CubeModuleManager.getInstance().setModuleOperationService(moduleOperationService);
		}
	};
	
	
	public String getActivity(){
		return "com.zdnst.bsl.CAdminActivity";
//		return "com.zdnst.bsl.CmanagerModuleActivity";
	}
	
	private ServiceConnection notificationServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			notificationService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			notificationService = ((NotificationServiceBinder) service).getService();
			if (notificationCallback != null) {
				notificationCallback.doStuff();
			}
			
//			String deviceId = DeviceInfoUtil.getDeviceId(Application.this);
			String token = CheckInUtil.createMD5Token(context);
			loginXmppClient(getPushManager(), token , token );
//			String appKey = Application.this.getCubeApplication().getAppKey();
//			loginXmppClient(getPushManager(), deviceId+"_"+appKey , deviceId+"_"+appKey );
//			loginXmppClient(getPushManager(), appKey , appKey );
		}
	};
	public void loginXmppClient(final XmppManager manager,final String username, final String password) {
		if (notificationService == null) {
			context.bindService(NotificationService.getIntent(context),notificationServiceConnection, Context.BIND_AUTO_CREATE);
			notificationCallback = new NotificationCallback() {

				@Override
				public void doStuff() {
					notificationService.connect(manager,username, password);
				}
			};
		} else {
			notificationService.connect(manager,username, password);
		}

	}
	public XmppManager getPushManager() {
		if (null != notificationService) {
			return notificationService.getPushManager();
		} else {
			Log.e("notificationService====", "notificationServiceConnection=" + notificationServiceConnection);
			Log.e("notificationService====", "BeforeBindnotificationService=" + notificationService);

			context.bindService(NotificationService.getIntent(context),notificationServiceConnection, Context.BIND_AUTO_CREATE);
			Log.e("notificationService====", "AffterBindnotificationService=" + notificationService);
			if (notificationService != null) {
				return notificationService.getPushManager();
			}
			return null;
		}
	}
	
	
	@Override
	public void onExit(CModule cModule) {
		super.onExit(cModule);
		CubeModuleManager.getInstance().stopTask();
	}
}
