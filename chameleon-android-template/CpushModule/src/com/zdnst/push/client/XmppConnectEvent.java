package com.zdnst.push.client;

public class XmppConnectEvent {

	private final boolean connected;

	public boolean isConnected() {
		return connected;
	}

	public XmppConnectEvent(final boolean connected) {
		this.connected = connected;
	}

}
