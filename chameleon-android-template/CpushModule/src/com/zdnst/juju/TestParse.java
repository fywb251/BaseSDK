package com.zdnst.juju;

import org.jivesoftware.smack.packet.Packet;

import android.content.Context;
import android.widget.Toast;

import com.zdnst.push.client.BaseParser;

public class TestParse extends BaseParser {
	
	public TestParse(Context context) {
		super(context);
	}

	@Override
	public void onReceive(Packet packet) {
		Toast.makeText(context, packet.toXML(), Toast.LENGTH_SHORT).show();
	}
	
}
