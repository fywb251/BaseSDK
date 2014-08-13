package com.zdnst.push.client;

import com.zdnst.chameleon.manager.R;
import com.zdnst.chameleonsdk.config.CubeConstants;
import com.zdnst.push.tool.Pool;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


/**
 * [通知服务]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-7-16]
 */
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
		
//		chatManager = new XmppManager(this,CubeConstants.CUBE_CONFIG,XmppManager.Type.ChAT);
		pushManager = new XmppManager(this,CubeConstants.CUBE_CONFIG,XmppManager.Type.PUSH);
		
//		rosterManager = chatManager.new RosterManager(new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				super.handleMessage(msg);
//				if (msg.what == 0) {
//					sendBroadcast(new Intent("push.model.change"));
//				}
//				sendBroadcast(new Intent("com.csair.cubeModelChange").putExtra(
//						"identifier", msg.getData().getString("identifier")));
//			}
//
//		});
//		Log.d("NotificationService","prepair connect for xmpp...");
		
//		chatManager.prepairConnect();
		pushManager.prepairConnect();
		
//		registerConnectivityReceiver();
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
			pushManager = new XmppManager(this,CubeConstants.CUBE_CONFIG,XmppManager.Type.PUSH);
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

	private String previousServiceName = null;

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
