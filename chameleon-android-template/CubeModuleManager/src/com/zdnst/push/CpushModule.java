package com.zdnst.push;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.foreveross.chameleon.manager.R;
import com.zdnst.juju.CheckInUtil;
import com.zdnst.push.client.NotificationService;
import com.zdnst.push.client.NotificationService.NotificationServiceBinder;
import com.zdnst.push.client.XmppManager;
import com.zdnst.zdnstsdk.CModule;

public class CpushModule extends CModule{
	
	private static CpushModule cpushModule;

	private Context context;
	
	public static CpushModule getCpushModule(){
		return cpushModule;
	}
	
	@Override
	public void onCreate(CModule module) {
		cpushModule = (CpushModule) module;
		this.context = cpushModule.getcApplication().getmContext();
		init();
		readConfig(R.raw.pushmodule);
	}
	
	public void init() {
		
		System.out.println("initPushModule");
		this.context.bindService(NotificationService.getIntent(context),
				notificationServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
		private NotificationService notificationService = null;
		private NotificationCallback notificationCallback;

		private interface NotificationCallback {
			public void doStuff();
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
			
			//����ㄩ����
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
			// String username = Preferences.getUserName(Application.sharePref);
			// loginChatClient(username, username);
			Log.e("notificationService====", "notificationServiceConnection=" + notificationServiceConnection);
			Log.e("notificationService====", "BeforeBindnotificationService=" + notificationService);

			context.bindService(NotificationService.getIntent(context),notificationServiceConnection, Context.BIND_AUTO_CREATE);
//			loginXmppClient(getPushManager(), DeviceInfoUtil.getDeviceId(this) , DeviceInfoUtil.getDeviceId(this));
			Log.e("notificationService====", "AffterBindnotificationService=" + notificationService);
			if (notificationService != null) {
				return notificationService.getPushManager();
			}
			return null;
		}
	}
	
	
	
}
