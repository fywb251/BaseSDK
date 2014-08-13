package com.zdnst.imsdk.chat;

public interface IMCallback {
	
	public void onError(int code,String msg);
	
	public void onProgress(int code,String msg);
	
	public void onSuccess(int code,String msg);

}
