package com.zdnst.juju.model;

import com.zdnst.module.MessageInfo;

public class MessageData {
	
	private long sendtime;
	private String messageId;
	private String title;
	private String content;
	private Boolean hasread;
	
	
	public MessageData (MessageInfo info) {
		copyNeededProps(info);
	}
	
	public long getSendtime() {
		return sendtime;
	}
	public void setSendtime(long sendtime) {
		this.sendtime = sendtime;
	}
	
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
	public Boolean getHasread() {
		return hasread;
	}

	public void setHasread(Boolean hasread) {
		this.hasread = hasread;
	}

	public void copyNeededProps(MessageInfo info) {
		setContent(info.getContent());
		setHasread(info.isHasread());
		setMessageId(info.getMesssageid());
		setSendtime(info.getSendtime());
		setTitle(info.getTitle());
	}

}
