package com.zdnst.push.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * [一句话功能简述]<BR>
 * [xmpp监听网络状态]
 * 
 * @author fenweili
 * @version [CubeAndroid, 2013-9-20]
 */
public class ConnectivityReceiver extends BroadcastReceiver {


	private NotificationService notificationService;
	private Context context ;

	public ConnectivityReceiver(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("connectivityReceiver","xmpp ConnectivityReceiver.onReceive()...");
		this.context = context;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		XmppManager manager = notificationService.getPushManager();
		if (networkInfo != null && networkInfo.isConnected()&& notificationService != null) {
			if (!notificationService.isConnected( manager)) { 
				Log.i("ConncetivityReceiver","Network connected,begin reconnect to xmpp");
				notificationService.reconnect(manager);
			} else {
				Log.i("ConncetivityReceiver","Network unavailable,begin to close xmpp");
				notificationService.virtualDisconnect();
			}
		} else {
			if (notificationService != null) {
				notificationService.disconnect(manager);
				notificationService.virtualDisconnect();
			}
		}
	}

}
