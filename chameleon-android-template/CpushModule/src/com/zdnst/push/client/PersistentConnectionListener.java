package com.zdnst.push.client;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.StreamError;

import android.util.Log;


public class PersistentConnectionListener implements ConnectionListener {


	private final XmppManager xmppManager;

	public PersistentConnectionListener(XmppManager xmppManager) {
		this.xmppManager = xmppManager;
	}

	private void sendBroadcastWithStatus(String channel, String status) {
//		EventBus.getEventBus(TmpConstants.EVENTBUS_PUSH, ThreadEnforcer.MAIN)
//				.post(new ConnectStatusChangeEvent(channel, status));
	}
	@Override
	public void connectionClosed() {
		Log.i("Persistent","xmpp connectionClosed()...");
		sendBroadcastWithStatus(ConnectStatusChangeEvent.CONN_CHANNEL_OPENFIRE,
				ConnectStatusChangeEvent.CONN_STATUS_OFFLINE);
//		if(!NetworkUtil.isNetworkConnected(xmppManager.getNotificationService())){
//			
//			((Application)xmppManager.getNotificationService().getApplication()).caculateHeartBeartRemain();	
//		}
//		

	}

	@Override
	public synchronized void connectionClosedOnError(Exception e) {
		Log.d("PersistentConnectionListener","xmpp connectionClosedOnError()...");

		if (e instanceof XMPPException) {
			Log.d("PersistentConnectionListener","exception is XMPPException");
			XMPPException xmppEx = (XMPPException) e;
			StreamError error = xmppEx.getStreamError();
			String reason = error.getCode();
			if ("conflict".equals(reason)) {
				// 当前账号已在别处登录
				Log.i("Persistent","xmpp冲突，被迫下线", e);
				try {
					if (xmppManager.isConnected()) {
						Log.d("PersistentConnectionListener","xmpp manager is connecting,disconnect it!");
						xmppManager.disconnect();
						
					}
				} catch (Exception e1) {
					Log.e("","close xmpp connection error!"+ e1);
				}
				// 发送广播通知Activity弹窗关闭应用
				
				
//				xmppManager.getNotificationService().sendBroadcast(
//						new Intent("com.xmpp.mutipleAccount"));
			}
		} else {
			Log.d("PersistentConnectionListener","exception is not XMPPException");
			if (xmppManager.isConnected()) {
				Log.d("PersistentConnectionListener","xmpp manager is connecting,disconnect it!");
				xmppManager.disconnect();
				xmppManager.stopReconnectionThread();
			}
			Log.d("PersistentConnectionListener","start reconnect thread to connect...");
			xmppManager.startReconnectionThread();
		}

	}

	@Override
	public synchronized void reconnectingIn(int seconds) {
		Log.i("Persistent","xmpp reconnectingIn()...");
	}
	
	@Override
	public synchronized void reconnectionFailed(Exception e) {
		Log.e("","xmpp reconnectionFailed()..."+ e);
	}

	@Override
	public void reconnectionSuccessful() {
		Log.i("Persistent","xmpp reconnectionSuccessful()...");
		sendBroadcastWithStatus(ConnectStatusChangeEvent.CONN_CHANNEL_OPENFIRE,
				ConnectStatusChangeEvent.CONN_STATUS_ONLINE);
	}

}
