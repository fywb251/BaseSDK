package com.zdnst.message.push.cubeparser.type;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class NoticesDeletedInMessageEvent implements Delayed {

	private Set<String> patch = new HashSet<String>();

	public boolean isEmpty() {
		return patch.isEmpty();
	}

	public void addDeletedNoticeModuleMessage(String deletedId) {
		patch.add(deletedId);
	}

	public Set<String> getPatch() {
		return patch;
	}

	public void setPatch(Set<String> patch) {
		this.patch = patch;
	}

	public  boolean contain(String deletedId){
		return patch.contains(deletedId);
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
