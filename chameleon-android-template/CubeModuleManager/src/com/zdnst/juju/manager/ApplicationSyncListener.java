package com.zdnst.juju.manager;

public interface ApplicationSyncListener {

	public void syncStart();
	
	public void syncFinish();
	
	public void syncFail();
	
	public void syncFinish(String result);
}
