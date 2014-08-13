package com.zdnst.juju.manager;

public interface DownloadCubeJsonSyncListener {

	public void downloadStart();
	
	public void downloadFinish();
	
	public void downloadFail();
}
