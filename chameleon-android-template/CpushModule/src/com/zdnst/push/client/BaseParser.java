package com.zdnst.push.client;

import org.jivesoftware.smack.packet.Packet;

import android.content.Context;

public abstract class BaseParser {
	
	public Context context;
	
	public BaseParser(Context context) {
		this.context=context;
	}
	
	public abstract void onReceive(Packet packet);
		
}
