package com.zdnst.imsdk.chat.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class IMMessage implements Parcelable {
	
	public enum MessageType
	{
		TXT,MOTIONS,FILE,LOCATION,AUDIO,VEDIO
	}
	
	private String msgId;
	
	private String from;
	
	private String to;
	
	private String content;
	
	private int msgType;
	
	private int isReceived;
	
	private int isRead;
	
	public IMMessage() {
	}

	public IMMessage(Parcel source) {
		this.msgId = source.readString();
		this.from = source.readString();
		this.to = source.readString();
		this.content = source.readString();
		this.msgType = source.readInt();
		this.isReceived = source.readInt();
		this.isRead = source.readInt();
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public int getIsReceived() {
		return isReceived;
	}

	public void setIsReceived(int isReceived) {
		this.isReceived = isReceived;
	}

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.msgId);
		dest.writeString(this.from);
		dest.writeString(this.to);
		dest.writeString(this.content);
		dest.writeInt(this.isRead);
		dest.writeInt(this.isReceived);
		dest.writeInt(this.msgType);
		
	}
	public final static Parcelable.Creator<IMMessage> CREATOR = new Parcelable.Creator<IMMessage>() {
		 
        @Override
        public IMMessage createFromParcel(Parcel source) {
            return new IMMessage(source);
        }
 
        @Override
        public IMMessage[] newArray(int size) {
            return new IMMessage[size];
        }
    };
	
	

}
