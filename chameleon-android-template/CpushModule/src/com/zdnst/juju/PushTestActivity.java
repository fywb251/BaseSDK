package com.zdnst.juju;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.zdnst.pushmodel.R;
import com.zdnst.push.client.NotificationPacketListener;
import com.zdnst.push.client.NotificationService;
import com.zdnst.push.client.NotificationService.NotificationServiceBinder;
import com.zdnst.push.client.XmppManager;

public class PushTestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		
		TestParse parse = new TestParse(this);
		NotificationPacketListener.register(parse);
		
	}
	
	
	
	protected void onDestroy() {
		super.onDestroy();
		unbindService(notificationServiceConnection);
	};
	
	
	
	
	Context context;
	public void init() {
		context = this.getApplicationContext();
		System.out.println("initPushModule");
		this.bindService(NotificationService.getIntent(context),
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
