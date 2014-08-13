package com.zdnst.juju;

//import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
//import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

//import com.zdnst.bsl.util.BroadcastConstans;
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
	
//	BroadcastReceiver receiver = new BroadcastReceiver() {
//		
//		@Override
//		public void onReceive(Context context, Intent intent) {
////			if(intent.getAction().equals(BroadcastConstans.PushCheckIn)){
////				CheckInUtil.registerPush(context, intent.getStringExtra("serviceName"));
////			}
//		}
//	};
	
	@Override
	public void onCreate(CModule module) {
		cpushModule = (CpushModule) module;
		this.context = cpushModule.getcApplication().getmContext();
		init();

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
			String token = CheckInUtil.createMD5Token(context);
			loginXmppClient(notificationService.getPushManager(), token,token);
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
	
	
}
