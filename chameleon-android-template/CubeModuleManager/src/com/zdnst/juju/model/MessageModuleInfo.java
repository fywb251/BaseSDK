package com.zdnst.juju.model;

import java.util.ArrayList;

import com.zdnst.module.MessageInfo;

public class MessageModuleInfo {

	private String identifier;
	private String moduleName;
	private int count;
	private int unread;
	private ArrayList<MessageData> message = new ArrayList<MessageData>();

	public MessageModuleInfo(ArrayList<MessageInfo> list) {
		copyNeededProps(list);
	}

	public MessageModuleInfo(MessageInfo info) {
		copyNeededProps(info);
	}
	public MessageModuleInfo() {}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getUnread() {
		return unread;
	}

	public void setUnread(int unread) {
		this.unread = unread;
	}

	public ArrayList<MessageData> getMessageData() {
		return message;
	}

	public void setMessageData(ArrayList<MessageData> messageData) {
		this.message = message;
	}

	public void copyNeededProps(ArrayList<MessageInfo> list) {
		int unreadCount = 0;
		for (int i = 0; i < list.size(); i++) {
			MessageData data = new MessageData(list.get(i));
			getMessageData().add(data);
			if (!list.get(i).isHasread()) {
				unreadCount++;
			}
		}
		if (list.size() != 0) {
			setCount(list.size());
			setIdentifier(list.get(0).getIdentifier());
			setModuleName(list.get(0).getGroupBelong());
			setUnread(unreadCount);
		}
	}

	public void copyNeededProps(MessageInfo info) {
		int unreadCount = 0;
		if (info != null) {
			MessageData data = new MessageData(info);
			getMessageData().add(data);
			if (!info.isHasread()) {
				unreadCount++;
			}
			setCount(1);
			setIdentifier(info.getIdentifier());
			setModuleName(info.getGroupBelong());
			setUnread(unreadCount);
		}
	}

}
