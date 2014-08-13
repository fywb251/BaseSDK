package com.zdnst.message.push.cubeparser.type;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class PatchMessageModelEvent implements Delayed {
	private List<ChanmeleonMessage> patch = new ArrayList<ChanmeleonMessage>();

	public ChanmeleonMessage lastChanmeleonMessage() {
		return patch.get(patch.size() - 1);
	}

	public boolean isEmpty() {
		return patch.isEmpty();
	}

	public void addChanmeleonMessage(ChanmeleonMessage chanmeleonMessage) {
		patch.add(chanmeleonMessage);
	}

	public List<ChanmeleonMessage> getPatch() {
		return patch;
	}

	public void setPatch(List<ChanmeleonMessage> patch) {
		this.patch = patch;
	}

	@Override
	public int compareTo(Delayed another) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		// TODO Auto-generated method stub
		return 0;
	}
}
