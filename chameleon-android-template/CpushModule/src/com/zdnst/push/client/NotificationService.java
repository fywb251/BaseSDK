package com.zdnst.push.client;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.zdnst.push.tool.Pool;
import com.zdnst.zdnstsdk.config.CubeConstants;


public class NotificationService extends Service {

	private TelephonyManager telephonyManager;

	private BroadcastReceiver connectivityReceiver;

	private PhoneStateListener phoneStateListener;

//	private XmppManager chatManager;
	
	private XmppManager pushManager;

	public class NotificationServiceBinder extends Binder {
		public NotificationService getService() {
			return NotificationService.this;
		}
	}

	public NotificationService() {
		connectivityReceiver = new ConnectivityReceiver(this);
		phoneStateListener = new PhoneStateChangeListener(this);
//		EventBus.getEventBus(TmpConstants.EVENTBUS_MUTIPLEACCOUNT_BROADCAST,
//				ThreadEnforcer.MAIN).register(this);
	}

	@Override
	public void onCreate() {
		Log.d("NotificationService","NotificationService  onCreate()...");
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		pushManager = new XmppManager(this,CubeConstants.CUBE_CONFIG);
		
		pushManager.prepairConnect();
		
	}

	@Override
	public void onDestroy() {
		Log.d("NotificationService","notificationService onDestroy()...");
		// EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON).unregister(this);
		stop();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("NotificationService","notificationService onBind()...");
		return new NotificationServiceBinder();
	}

	public static Intent getIntent(Context context) {
		return new Intent(context, NotificationService.class);
	}

//	public XmppManager getChatManager() {
//		if (chatManager == null) {
//			chatManager = new XmppManager(this,CubeConstants.CUBE_CONFIG,XmppManager.Type.ChAT);
//		}
//		return chatManager;
//	}
	
	public XmppManager getPushManager() {
		if (pushManager == null) {
			pushManager = new XmppManager(this,CubeConstants.CUBE_CONFIG);
		}
		return pushManager;
	}
	
	public void connect(final XmppManager manager,final String username, final String password) {

		// timer.schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		Log.d("NotificationService","start connect to xmpp for user {}"+ username);
		Pool.run(new Runnable() {
			public void run() {
				if (manager != null && !manager.isAuthenticated()) {
					manager.submitConnectReq(username, password);
				}
			}
		});
		// }
		// }, application.getRemainTimes() * 1000);

	}

	public void disconnect(final XmppManager manager) {
		Log.d("NotificationService","start disconnect to xmpp");

		Pool.run(new Runnable() {
			public void run() {
				if (manager.isConnected()) {
					manager.disconnect();

				}
			}
		});
	}

	public void virtualDisconnect() {
//		sendBroadcastWithStatus(ConnectStatusChangeEvent.CONN_CHANNEL_CHAT,
//				ConnectStatusChangeEvent.CONN_STATUS_OFFLINE);
//		sendBroadcastWithStatus(ConnectStatusChangeEvent.CONN_CHANNEL_OPENFIRE,
//				ConnectStatusChangeEvent.CONN_STATUS_OFFLINE);
		
	}




	public void onConnectEvent(XmppConnectEvent connectEvent) {
		if (connectEvent.isConnected()) {
			registerConnectivityReceiver();
		} else {
			unregisterConnectivityReceiver();

		}
	}

	public boolean isConnected(XmppManager manager) {
		return manager.isConnected();
	}

	private void registerConnectivityReceiver() {
		Log.d("NotificationService","registerConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
		IntentFilter filter = new IntentFilter();
		filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(connectivityReceiver, filter);
	}

	private void unregisterConnectivityReceiver() {
		
		Log.d("NotificationService","unregisterConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(connectivityReceiver);
	}

	public void reconnect(XmppManager manager) {
		// timer.schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		Log.d("NotificationService","notification reconnect()...");
		manager.reconnect();
		// }
		// }, application.getRemainTimes() * 1000);

	}

	private void stop() {
		Log.d("NotificationService","notification stop()...");
		unregisterConnectivityReceiver();
//		chatManager.disconnect();
//		pushManager.disconnect();
	}

//	public void online() {
//		chatManager.online();
//	}
//
//	public void offline() {
//		chatManager.offline();
//	}

	public boolean isOnline(XmppManager manager) {
		return manager.isOnline();
	}

//	private String previousServiceName = null;

//	public String getChatServiceName() {
//
//		if (isOnline(chatManager)) {
//			return previousServiceName = chatManager.getXmppServiceName();
//		} else {
//			return previousServiceName == null ? "" : previousServiceName;
//		}
//
//	}

//	private RosterManager rosterManager;

//	public RosterManager getRosterManager() {
//		return rosterManager;
//	}

}
