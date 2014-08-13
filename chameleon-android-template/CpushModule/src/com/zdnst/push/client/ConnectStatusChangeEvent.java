/**
 * 
 */
package com.zdnst.push.client;

public class ConnectStatusChangeEvent {
	
	public static final String CONN_CHANNEL_CHAT = "CONN_CHANNEL_CHAT";
	public static final String CONN_CHANNEL_MINA = "CONN_CHANNEL_MINA";
	public static final String CONN_CHANNEL_OPENFIRE = "CONN_CHANNEL_OPENFIRE";
	public static final String CONN_STATUS_OFFLINE = "CONN_STATUS_OFFLINE";
	public static final String CONN_STATUS_ONLINE = "CONN_STATUS_ONLINE";
	
	public static final String SHOW_TOAST = "SHOW_TOAST";
	private final String channel;

	public String getChannel() {
		return channel;
	}

	private final String status;

	public String getStatus() {
		return status;
	}

	public ConnectStatusChangeEvent(String channel, String status) {
		this.channel = channel;
		this.status = status;
	}

}
