package com.zdnst.juju.manager;

public class MessageCountChangeEvent {

	private final String identifier;

	public String getIdentifier() {
		return identifier;
	}

	private final int count;
	private final boolean displayBadge;

	public boolean isDisplayBadge() {
		return displayBadge;
	}

	public int getCount() {
		return count;
	}

	public MessageCountChangeEvent(String identifier, int count,
			boolean displayBadge) {
		this.identifier = identifier;
		this.count = count;
		this.displayBadge = displayBadge;
	}

}
