package com.zdnst.push.client;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

public class NotificationPacketListener implements PacketListener {
	
	public static List<BaseParser> parses = new ArrayList<BaseParser>();
	@Override
	public void processPacket(Packet packet) {
		for(BaseParser parse:parses){
			parse.onReceive(packet);
		}
	}
	
	public static void register(BaseParser parser){
		parses.add(parser);
	}
}
