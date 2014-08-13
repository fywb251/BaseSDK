package com.zdnst.chameleon.httputil;

public interface DownloadCubeJsonSyncListener {

	public void downloadStart();
	
	public void downloadFinish();
	
	public void downloadFail();
}
