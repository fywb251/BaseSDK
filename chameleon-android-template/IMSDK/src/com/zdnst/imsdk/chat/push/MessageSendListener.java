package com.zdnst.imsdk.chat.push;

public interface MessageSendListener {
	
	public void onSuccess(String string);
	
	public void onError(Exception e);
	
	public void onPreExecute(String str);
	
	public void onProgress(String str);
	

}
