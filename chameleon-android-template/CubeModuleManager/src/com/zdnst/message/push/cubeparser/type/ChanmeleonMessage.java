/**
 * 
 */
package com.zdnst.message.push.cubeparser.type;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.zdnst.module.MessageInfo;

public class ChanmeleonMessage implements Delayed {

	public ChanmeleonMessage() {

	}

	/**
	 * [包裹对象]
	 */
	private MessageInfo packedMessage = null;

	public void setPackedMessage(MessageInfo packedMessage) {
		this.packedMessage = packedMessage;
	}

	private long startTime = 0l;

	public MessageInfo getPackedMessage() {
		return packedMessage;
	}

	public ChanmeleonMessage(MessageInfo packedMessage) {
		this.packedMessage = packedMessage;
	}

	@Override
	public int compareTo(Delayed another) {
		return 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(startTime - System.currentTimeMillis(),
				TimeUnit.MILLISECONDS);
	}

	public void delay() {
		startTime = System.currentTimeMillis() + 200;
	}

	public boolean savable() {
		return packedMessage != null;
	}
}
